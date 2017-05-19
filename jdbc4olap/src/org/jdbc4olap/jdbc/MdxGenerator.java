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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.soap.Node;

import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaHelper;
import org.w3c.dom.NodeList;


class MdxGenerator {

	final List<QueryFilter> mdxFilterList = new ArrayList<QueryFilter>();
	final List<String> mdxWhereList = new ArrayList<String>();
	final HashMap<String, HashMap<String, List<String>>> filterMap = new HashMap<String, HashMap<String, List<String>>>();
	final StringBuffer fieldPrefix = new StringBuffer();
	final StringBuffer fieldSuffix = new StringBuffer();
	StringBuffer mdx;
	int measureAxis=-1;
	int axisCount = 0;
	
	MdxGenerator(XmlaConn conn, QueryDefinition qd, QueryMetaData qmd, String measureName)
			throws SQLException, EmptyResultSetException{
		analyzeFilters(conn, qd, qmd, measureName);
		
		// QUERY setup
		mdx = new StringBuffer();
		mdx.append("SELECT ");
		// String lastTable = new String();
		for (String table : qmd.getFieldMap().keySet()) {
			// lastTable=table;
			final List<QueryColumn> list = qmd.getFieldMap().get(table);
			final StringBuffer sb = new StringBuffer(" { ");
			boolean fieldAdded = false;
			String suffix = "";
			if (!table.equals(measureName)) {
				suffix = ".Members";
			}
			final HashMap<String, List<String>> tableFilterMap = filterMap
					.get(table);
			for (QueryColumn field : list) {
				if (!field.getField().endsWith("_ID")) {
					if (tableFilterMap != null && tableFilterMap.size() > 0) {
						sb.append(buildMdxQueryAxis(field, fieldPrefix
								.toString(), fieldSuffix.toString(),
								new ArrayList<String>(),
								new ArrayList<List<String>>(tableFilterMap
										.values())));
					} else {
						sb.append(fieldPrefix).append(field.getField()).append(
								suffix).append(fieldSuffix).append(",");
					}
					fieldAdded = true;
				}
			}
			if (fieldAdded) {
				if (sb.charAt(sb.length() - 1) == ',') {
					sb.deleteCharAt(sb.length() - 1);
				}
				mdx.append(sb).append(" } On Axis(").append(axisCount).append(
						"),");
				if (table.equals(measureName)) {
					measureAxis = axisCount;
				}
				axisCount += 1;
			}
		}
		if (axisCount == 1) {
			/*
			 * if (measureName.equals(lastTable)) { ResultSet lRS =
			 * getColumns(catalog, schema, null, "[(All)]"); if (lRS.first())
			 * mdx
			 * .append(" { ").append(lRS.getString(4)).append(".Members } On Axis("
			 * ).append(axisCount).append(")"); }else{ ResultSet lRS =
			 * getColumns(catalog, schema, measureName, null); if (lRS.first())
			 * mdx
			 * .append(" { ").append(lRS.getString(4)).append(" } On Axis(").append
			 * (axisCount).append(")"); }
			 */
			// throw new
			// SQLException("MDX queries need at least 2 dimensions involved");
		}
		if (mdx.charAt(mdx.length() - 1) == ',') {
			mdx.deleteCharAt(mdx.length() - 1);
		}
		if (qmd.getSchema().trim().startsWith("[")) {
			mdx.append(" FROM ").append(qmd.getSchema());
		} else {
			mdx.append(" FROM [").append(qmd.getSchema()).append("]");
		}

		if (!mdxWhereList.isEmpty()) {
			StringBuffer querySuffix = new StringBuffer(" WHERE (");
			for (String filter : mdxWhereList) {
				querySuffix.append(filter).append(",");
			}
			querySuffix.deleteCharAt(querySuffix.length() - 1);
			querySuffix.append(")");
			mdx.append(querySuffix);
		}
	}

	private void analyzeFilters(XmlaConn conn, QueryDefinition qd,  QueryMetaData qmd, String measureName)
			throws SQLException, EmptyResultSetException{
		for (QueryFilter filter : qd.getQueryFilterList()) {
			// filter validity check, are accepted: Column Operator (Column | Value)
			final QueryFilterOperand leftOp = filter.getLeftOp();
			final QueryFilterOperand rightOp = filter.getRightOp();
			if (leftOp == null || filter.getOperator() == null
					|| rightOp == null) {
				throw new SQLException("Where clause syntax error.");
			}
			if (leftOp.getCol() != null) {
				leftOp.setCol(QueryMetaDataExtractor.checkField(leftOp.getCol(), qd.getQueryTableList(),	qmd.getTableMap()));
			}
			if (rightOp.getCol() != null) {
				rightOp.setCol(QueryMetaDataExtractor.checkField(rightOp.getCol(), qd.getQueryTableList(), qmd.getTableMap()));
			}

			// where val1=val2
			if (leftOp.getValList() != null && rightOp.getValList() != null) {
				if (!leftOp.getValList().equals(rightOp.getValList())) {
					// @todo Return empty resultset (with metadata) via
					// OlapResultSet.createEmptyResultSet()?
					throw new EmptyResultSetException();
				}
			} else if (leftOp.getCol() != null && rightOp.getCol() != null) {
				// joins are ignored
			} else {
				// v1: column op expr OR expr op column (op can only be '=')
				final QueryColumn field = leftOp.getCol() == null ? rightOp
						.getCol() : leftOp.getCol();
				final List<String> expr = leftOp.getValList() == null ? rightOp
						.getValList() : leftOp.getValList();
				if (!field.getField().endsWith("_ID")) {
					if (measureName.equals(field.getTable())) {
						mdxFilterList.add(filter);
					} else {
						if (!filter.getOperator().equals("=")) {
							throw new SQLException(
									"Only equality filter supported");
						}

						final NodeList members = conn.getMembersNodeList(
								qmd.getCatalog(), qmd.getSchema(), field.getTable());
						Node item;
						XmlaHelper helper = new XmlaHelper();
						final List<String> foundValues = new ArrayList<String>();
						for (int i = 0; i < members.getLength(); i++) {
							item = (Node) members.item(i);
							String member = "";
							String desc = "";
							final NodeList nl = item.getChildNodes();
							for (int j = 0; j < nl.getLength(); j++) {
								org.w3c.dom.Node node = nl.item(j);
								final String nodeName = node.getNodeName();
								final String nodeTextContent = helper
										.getTextContent(node);

								if (nodeName.equals("MEMBER_UNIQUE_NAME")) {
									member = nodeTextContent;
								} else if (nodeName.equals("MEMBER_CAPTION")) {
									desc = nodeTextContent;
								}
							}
							for (String pattern : expr) {
								if (member.equalsIgnoreCase(pattern)
										|| desc.equalsIgnoreCase(pattern)) {
									foundValues.add(member);
								}
							}
						}
						if (foundValues.size() == 0) {
							// @todo Return empty resultset (with metadata) via
							// OlapResultSet.createEmptyResultSet()?
							throw new EmptyResultSetException();
						}
						if (qmd.getFieldMap().get(field.getTable()) != null) {
							HashMap<String, List<String>> tableMap = filterMap
									.get(field.getTable());
							if (tableMap == null) {
								tableMap = new HashMap<String, List<String>>();
							}
							List<String> filterList = tableMap.get(field
									.getField());
							if (filterList == null) {
								filterList = new ArrayList<String>();
								filterList.addAll(foundValues);
							} else {
								filterList.retainAll(foundValues);
							}
							tableMap.put(field.getField(), filterList);
							filterMap.put(field.getTable(), tableMap);
						} else {
							mdxWhereList.addAll(foundValues);
						}
					}
				}
			}
		}
		if (!mdxFilterList.isEmpty()) {
			fieldPrefix.append("Filter( {");
			for (QueryFilter filter : mdxFilterList) {
				if (fieldSuffix.length() > 0) {
					fieldSuffix.append(" and ");
				} else {
					fieldSuffix.append("} , ");
				}
				if (filter.getLeftOp().getCol() != null) {
					fieldSuffix.append(filter.getLeftOp().getCol().getField())
							.append(filter.getOperator()).append(
									filter.getRightOp().getValList().get(0));
				} else {
					fieldSuffix.append(filter.getLeftOp().getValList().get(0)).append(
							filter.getOperator()).append(
							filter.getRightOp().getCol().getField());
				}
			}
			fieldSuffix.append(")");
		}
	}
	
	private static String buildMdxQueryAxis(final QueryColumn field,
			final String fieldPrefix, final String fieldSuffix,
			final List<String> current, final List<List<String>> filterColl) {
		StringBuilder sb = new StringBuilder();
		if (filterColl != null) {
			if (filterColl.size() == current.size()) {
				for (int i = 0; i < current.size(); i++) {
					String col = current.get(i);
					if (i + 1 < current.size()) {
						sb.append("INTERSECT(");
					}
					sb.append(fieldPrefix).append("Union( {Ancestor(").append(
							col).append(",").append(field.getField()).append(
							")}, {Descendants(").append(col).append(",")
							.append(field.getField()).append(")} )").append(
									fieldSuffix);
					if (i > 0) {
						sb.append("),");
					} else {
						sb.append(",");
					}
				}
			} else {
				List<String> firstCol = filterColl.get(current.size());
				for (String elt : firstCol) {
					List<String> tmp = new ArrayList<String>(current);
					tmp.add(elt);
					sb.append(buildMdxQueryAxis(field, fieldPrefix,
							fieldSuffix, tmp, filterColl));
				}
			}
		}
		return sb.toString();
	}

	String getMdx() {
		return mdx.toString();
	}

	int getMeasureAxis() {
		return measureAxis;
	}

	int getAxisCount() {
		return axisCount;
	}
	
}

