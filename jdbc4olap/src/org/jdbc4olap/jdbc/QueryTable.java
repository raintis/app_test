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

class QueryTable {

    private String tableAlias;
    private String table;
    private String catalog;
    private String schema;


    QueryTable() {
        this.tableAlias = null;
        this.table = null;
        this.catalog = null;
        this.schema = null;
    }

    String getTable() {
        return table;
    }

    void setTable(final String table) {
        this.table = table;
    }

    String getTableAlias() {
        return tableAlias;
    }

    void setTableAlias(final String tableAlias) {
        this.tableAlias = tableAlias;
    }

    String getCatalog() {
        return catalog;
    }

    void setCatalog(final String catalog) {
        this.catalog = catalog;
    }

    String getSchema() {
        return schema;
    }

    void setSchema(final String schema) {
        this.schema = schema;
    }
}

