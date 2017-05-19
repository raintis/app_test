/*
 * PRELYTIS.
 * Copyright 2007, PRELYTIS S.A., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jdbc4olap.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdbc4olap.xmla.XmlaConn;

class QueryMetaDataExtractor {
	
	static QueryMetaData getQueryMetaData(QueryDefinition queryDef, DatabaseMetaData olapMetadata, 
			XmlaConn conn) throws SQLException{
		QueryMetaData qmd = new QueryMetaData();
		checkCatSchTab(qmd, queryDef, olapMetadata, conn);
		checkFields(qmd, queryDef, olapMetadata, conn);
		return qmd;
	}

	private static void checkCatSchTab(QueryMetaData qmd, QueryDefinition queryDef, 
			DatabaseMetaData olapMetadata, XmlaConn conn)throws SQLException{
		HashMap<String, List<String>> tableMap = new HashMap<String, List<String>>();
		String catalog="", schema="";
		final List<String> tableAliases = new ArrayList<String>();
		for (QueryTable table : queryDef.getQueryTableList()) {
			catalog = checkCatalog(catalog, table.getCatalog(), olapMetadata);
			schema = checkSchema(schema, table.getSchema(), olapMetadata);
			
			// table alias unicity check
			if (table.getTableAlias() != null) {
				if (tableAliases.contains(table.getTableAlias())) {
					throw new SQLException("Table alias '"
							+ table.getTableAlias()
							+ "' defined more than once.");
				} else {
					tableAliases.add(table.getTableAlias());
				}
			}
			// table check and cache feeding
			final ResultSet tableRS = olapMetadata.getTables(catalog, schema,
					table.getTable(), null);
			if (tableRS.first()) {
				final ResultSet colRS = olapMetadata.getColumns(catalog,
						schema, table.getTable(), null);
				final List<String> colList = new ArrayList<String>();
				colRS.beforeFirst();
				while (colRS.next()) {
					colList.add(colRS.getString(4));
				}
				tableMap.put(table.getTable(), colList);
			} else {
				throw new SQLException("Unknown table : " + table.getTable());
			}
		}
		for (String alias : tableAliases) {
			if (tableMap.containsKey(alias)) {
				throw new SQLException(
						"Can't use a table name as a table alias");
			}
		}
		qmd.setTableMap(tableMap);
		qmd.setCatalog(catalog);
		qmd.setSchema(schema);
	}
	
	private static String checkCatalog(String catalog, String lCat, DatabaseMetaData olapMetadata) throws SQLException{
		if (lCat == null || lCat.equals("")) {
			throw new SQLException("Catalog must be specified");
		}
		if (!catalog.equals("") && !catalog.equals(lCat)) {
			throw new SQLException("More than one catalog requested");
		}
		if (catalog.equals("")) {
			final ResultSet catRS = olapMetadata.getCatalogs();
			boolean catFound = false;
			catRS.beforeFirst();
			while (catRS.next()) {
				if (catRS.getString(1).equals(lCat)) {
					catFound = true;
				}
			}
			if (!catFound) {
				throw new SQLException("Unknown catalog : " + catalog);
			}
		}
		return lCat;
	}
	
	private static String checkSchema(String schema, String lSch, DatabaseMetaData olapMetadata) throws SQLException{
		if (lSch == null || lSch.equals("")) {
			throw new SQLException("Schema must be specified");
		}
		if (!schema.equals("") && !schema.equals(lSch)) {
			throw new SQLException("More than one schema requested");
		}
		if (schema.equals("")) {
			ResultSet schRS = olapMetadata.getSchemas();
			boolean schFound = false;
			schRS.beforeFirst();
			while (schRS.next()) {
				if (schRS.getString(1).equals(lSch)) {
					schFound = true;
				}
			}
			if (!schFound) {
				throw new SQLException("Unknown schema : " + schema);
			}
		}
		return lSch;
	}
	
	private static void checkFields(QueryMetaData qmd, QueryDefinition queryDef, 
			DatabaseMetaData olapMetadata, XmlaConn conn) throws SQLException{
		// Fields verification and Table - Field association
		HashMap<String, List<QueryColumn>> fieldMap = new HashMap<String, List<QueryColumn>>();
		List<QueryColumn> fieldList;
		int rank = 0;
		final List<String> fieldAliases = new ArrayList<String>();
		for (QueryColumn field : queryDef.getQueryColumnList()) {
			// field alias unicity check
			if (field.getFieldAlias() != null) {
				if (fieldAliases.contains(field.getFieldAlias())) {
					throw new SQLException("Column alias '"
							+ field.getFieldAlias()
							+ "' defined more than once.");
				} else {
					fieldAliases.add(field.getFieldAlias());
				}
			}
			final List<QueryColumn> subList = new ArrayList<QueryColumn>();
			if ("*".equals(field.getField())) {
				final List<String> tableList = new ArrayList<String>();
				if (field.getTableAlias() == null
						|| "".equals(field.getTableAlias())) {
					tableList.addAll(qmd.getTableMap().keySet());
				} else {
					boolean found = false;
					String alias = field.getTableAlias();
					for (QueryTable table : queryDef.getQueryTableList()) {
						if (alias.equals(table.getTable())
								|| alias.equals(table.getTableAlias())) {
							found = true;
							tableList.add(table.getTable());
						}
					}
					if (!found) {
						throw new SQLException("Unknown field '"
								+ field.getField());
					}
				}
				for (String table : tableList) {
					for (String fieldName : qmd.getTableMap().get(table)) {
						QueryColumn qField = new QueryColumn();
						qField.setField(fieldName);
						qField.setTable(table);
						subList.add(qField);
					}
				}
			} else {
				subList.add(checkField(field, queryDef.getQueryTableList(), qmd.getTableMap()));
			}
			for (QueryColumn qField : subList) {
				// Fields classification by table
				fieldList = fieldMap.get(qField.getTable());
				if (fieldList == null) {
					fieldList = new ArrayList<QueryColumn>();
				}
				qField.setRank(rank);
				rank += 1;
				fieldList.add(qField);
				fieldMap.put(qField.getTable(), fieldList);
			}
		}
		qmd.setFieldMap(fieldMap);
	}
	
	static QueryColumn checkField(final QueryColumn field,
			final List<QueryTable> tableList,
			final HashMap<String, List<String>> tablesFieldsCache)
			throws SQLException {
		boolean fieldFound = false;
		// if the field has a table alias we use it for verification
		if (field.getTableAlias() != null) {
			// && tableAlias.length()>0 ?
			for (QueryTable table : tableList) {
				if (field.getTableAlias().equals(table.getTableAlias())
						|| field.getTableAlias().equals(table.getTable())) {
					List<String> colList = tablesFieldsCache.get(table.getTable());
					if (colList.contains(field.getField())) {
						field.setTable(table.getTable());
						fieldFound = true;
					} else {
						throw new SQLException("Unknown column '"
								+ field.getField() + "' for table '"
								+ table.getTable() + "'.");
					}
				}
			}
		} else {
			// without table alias we search the field in each table in the from
			// clause
			for (QueryTable table : tableList) {
				List<String> colList = tablesFieldsCache.get(table.getTable());
				if (colList.contains(field.getField())) {
					// we may find the field in different tables
					if (fieldFound) {
						throw new SQLException("Ambiguous column definition : "
								+ field.getField());
					} else {
						field.setTable(table.getTable());
						fieldFound = true;
					}
				}
			}
		}
		if (!fieldFound) {
			throw new SQLException("Unknown field '" + field.getField());
		}
		return field;
	}

	
}
