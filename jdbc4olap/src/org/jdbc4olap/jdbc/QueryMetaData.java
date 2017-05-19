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

import java.util.HashMap;
import java.util.List;

class QueryMetaData {

	private HashMap<String, List<String>> tableMap;
	private HashMap<String, List<QueryColumn>> fieldMap;
	private String catalog="";
	private String schema="";
	
	HashMap<String, List<String>> getTableMap() {
		return tableMap;
	}
	
	void setTableMap(HashMap<String, List<String>> tableMap) {
		this.tableMap = tableMap;
	}
	
	HashMap<String, List<QueryColumn>> getFieldMap() {
		return fieldMap;
	}
	
	void setFieldMap(HashMap<String, List<QueryColumn>> fieldMap) {
		this.fieldMap = fieldMap;
	}
	
	String getCatalog() {
		return catalog;
	}
	
	void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	String getSchema() {
		return schema;
	}
	
	void setSchema(String schema) {
		this.schema = schema;
	}
	

}
