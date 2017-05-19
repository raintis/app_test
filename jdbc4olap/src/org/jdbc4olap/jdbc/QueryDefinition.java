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

import java.util.List;

class QueryDefinition {

	private List<QueryColumn> queryColumnList;
	
	private List<QueryTable> queryTableList;
	
	private List<QueryFilter> queryFilterList;

	QueryDefinition(List<QueryColumn> queryColumnList,
			List<QueryTable> queryTableList, List<QueryFilter> queryFilterList) {
		super();
		this.queryColumnList = queryColumnList;
		this.queryTableList = queryTableList;
		this.queryFilterList = queryFilterList;
	}

	List<QueryColumn> getQueryColumnList() {
		return queryColumnList;
	}

	void setQueryColumnList(List<QueryColumn> queryColumnList) {
		this.queryColumnList = queryColumnList;
	}

	List<QueryTable> getQueryTableList() {
		return queryTableList;
	}

	void setQueryTableList(List<QueryTable> queryTableList) {
		this.queryTableList = queryTableList;
	}

	List<QueryFilter> getQueryFilterList() {
		return queryFilterList;
	}

	void setQueryFilterList(List<QueryFilter> queryFilterList) {
		this.queryFilterList = queryFilterList;
	}
	
	
}
