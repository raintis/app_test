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

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
class OlapColumnMetaData {
    private String catalogName;

    private String className;

    private int displaySize;

    private String label;

    private String name;

    private int type;

    private String typeName;

    private int precision;

    private int scale;

    private String schemaName;

    private String tableName;

    private boolean autoIncrement;

    private boolean caseSensitive;

    private boolean currency;

    private boolean definitivelyWritable;

    private int nullable;

    private boolean readOnly;

    private boolean searchable;

    private boolean signed;

    private boolean writable;

    boolean isAutoIncrement() {
        return autoIncrement;
    }

    void setAutoIncrement(final boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    boolean isCaseSensitive() {
        return caseSensitive;
    }

    void setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    String getCatalogName() {
        return catalogName;
    }

    void setCatalogName(final String catalogName) {
        this.catalogName = catalogName;
    }

    String getClassName() {
        return className;
    }

    void setClassName(final String className) {
        this.className = className;
    }

    boolean isCurrency() {
        return currency;
    }

    void setCurrency(final boolean currency) {
        this.currency = currency;
    }

    boolean isDefinitivelyWritable() {
        return definitivelyWritable;
    }

    void setDefinitivelyWritable(final boolean definitivelyWritable) {
        this.definitivelyWritable = definitivelyWritable;
    }

    int getDisplaySize() {
        return displaySize;
    }

    void setDisplaySize(final int displaySize) {
        this.displaySize = displaySize;
    }

    String getLabel() {
        return label;
    }

    void setLabel(final String label) {
        this.label = label;
    }

    String getName() {
        return name;
    }

    void setName(final String name) {
        this.name = name;
    }

    int isNullable() {
        return nullable;
    }

    void setNullable(final int nullable) {
        this.nullable = nullable;
    }

    int getPrecision() {
        return precision;
    }

    void setPrecision(final int precision) {
        this.precision = precision;
    }

    boolean isReadOnly() {
        return readOnly;
    }

    void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    int getScale() {
        return scale;
    }

    void setScale(final int scale) {
        this.scale = scale;
    }

    String getSchemaName() {
        return schemaName;
    }

    void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }

    boolean isSearchable() {
        return searchable;
    }

    void setSearchable(final boolean searchable) {
        this.searchable = searchable;
    }

    boolean isSigned() {
        return signed;
    }

    void setSigned(final boolean signed) {
        this.signed = signed;
    }

    String getTableName() {
        return tableName;
    }

    void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    int getType() {
        return type;
    }

    void setType(final int type) {
        this.type = type;
    }

    String getTypeName() {
        return typeName;
    }

    void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    boolean isWritable() {
        return writable;
    }

    void setWritable(final boolean writable) {
        this.writable = writable;
    }
}
