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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jdbc4olap.xmla.Jdbc4OlapConstants;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
class OlapResultSetMetaData implements ResultSetMetaData {

    private static final Logger LOG = Logger.getLogger(Jdbc4OlapConstants.JDBC4OLAP_LOG);

    /**
     * Create metadata for the given resultset, all column types are set to Types.VARCHAR.
     *
     * @param rs          the resultset to which metadata will be added
     * @param columnNames the names of columns in the resultset
     */
    static void setMetaDataStringCols(final OlapResultSet rs, final String[] columnNames) {

        final int[] colummTypes = createStringColTypesForNames(columnNames);

        setMetaData(rs, columnNames, colummTypes);
    }

    static int[] createStringColTypesForNames(final String[] columnNames) {
        final int[] colummTypes;
        if (columnNames == null) {
            colummTypes = null;
        } else {
            colummTypes = new int[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                colummTypes[i] = Types.VARCHAR;
            }
        }
        return colummTypes;
    }

    /**
     * Create metadata for the given resultset.
     *
     * @param rs          the resultset to which metadata will be added
     * @param columnNames the names of columns in the resultset
     * @param columnTypes the sql type of each column
     */
    static void setMetaData(final OlapResultSet rs, final String[] columnNames, final int[] columnTypes) {
        setMetaData(rs, columnNames, columnTypes, null);
    }
    /**
     * Create metadata for the given resultset.
     *
     * @param rs          the resultset to which metadata will be added
     * @param columnNames the names of columns in the resultset
     * @param columnTypes the sql type of each column
     * @param columnLabels the display name of each column
     */
    static void setMetaData(final OlapResultSet rs, final String[] columnNames, final int[] columnTypes,
                            final String[] columnLabels) {
        final OlapResultSetMetaData md = new OlapResultSetMetaData(rs);

        if (columnNames != null) {

            if (columnTypes == null) {
                throw new IllegalStateException("Array mismatch: columnNames.length: "
                        + columnNames.length + ", columnTypes is null");
            }
            if (columnNames.length != columnTypes.length) {
                throw new IllegalStateException("Array length mismatch: columnNames.length: "
                        + columnNames.length + ", columnTypes.length: " + columnTypes.length);
            }
            if (columnLabels != null && columnNames.length != columnLabels.length) {
                throw new IllegalStateException("Array length mismatch: columnNames.length: "
                        + columnNames.length + ", columnLabels.length: " + columnTypes.length);
            }

            for (int i = 0; i < columnNames.length; i++) {
                final OlapColumnMetaData cmd = new OlapColumnMetaData();
                cmd.setName(columnNames[i]);
                cmd.setType(columnTypes[i]);
                if (columnLabels != null) {
                    cmd.setLabel(columnLabels[i]);
                }
                md.add(cmd);
            }

        } else if (columnTypes != null) {
            throw new IllegalStateException("Array mismatch: columnNames is null, columnTypes.length: " + columnTypes.length);
        }

        rs.setMetaData(md);
    }

    private final List<OlapColumnMetaData> list;
    // only intended for debugging
    private final OlapResultSet owner;

    OlapResultSetMetaData(final OlapResultSet ownerRS) {
        this.list = new ArrayList<OlapColumnMetaData>();
        this.owner = ownerRS;
    }

    // only intended for debugging
    OlapResultSet getOwner() {
        return owner;
    }

    void add(final OlapColumnMetaData column) {
        list.add(column);
    }

    OlapColumnMetaData getColumn(final int column) {
        return list.get(column - 1);
    }

    public String getCatalogName(final int column) throws SQLException {
        return getColumn(column).getCatalogName();
    }

    public String getColumnClassName(final int column) throws SQLException {
        return getColumn(column).getClassName();
    }

    public int getColumnCount() throws SQLException {
        return list.size();
    }

    static final int COL_DISPLAY_SIZE_PAD = 2;

    public int getColumnDisplaySize(final int column) throws SQLException {
        if (getColumn(column).getDisplaySize() != 0) {
            return getColumn(column).getDisplaySize();
        }

        final String label = getColumnLabel(column);
        if (label != null) {
            return label.length() + COL_DISPLAY_SIZE_PAD;
        }

        return 0;
    }

    public String getColumnLabel(final int column) throws SQLException {
        final OlapColumnMetaData columnMD = getColumn(column);

        final String colLabel;
        if (columnMD.getLabel() == null) {
            colLabel = columnMD.getName();
        } else {
            colLabel = columnMD.getLabel();
        }
        return colLabel;
    }

    public String getColumnName(final int column) throws SQLException {
        return getColumn(column).getName();
    }

    public int getColumnType(final int column) throws SQLException {
        return getColumn(column).getType();
    }


    /**
     * Mapping of java.sql.Types to TypeNames. We may need a more flexible approach?
     */
    static final HashMap<Integer, String> TYPE_NAME_MAP = new HashMap<Integer, String>();

    static {
        TYPE_NAME_MAP.put(Integer.valueOf(Types.VARCHAR), "VARCHAR");
        TYPE_NAME_MAP.put(Integer.valueOf(Types.BOOLEAN), "BOOLEAN");
        TYPE_NAME_MAP.put(Integer.valueOf(Types.INTEGER), "INTEGER");
        TYPE_NAME_MAP.put(Integer.valueOf(Types.NUMERIC), "NUMERIC");
    }

    public String getColumnTypeName(final int column) throws SQLException {
        if (getColumn(column).getTypeName() != null) {
            return getColumn(column).getTypeName();
        }

        final String typeName = TYPE_NAME_MAP.get(Integer.valueOf(getColumnType(column)));
        if (typeName == null) {
            LOG.warning("----WARNING: Missing Name in TypeMap for type: " + getColumnType(column)
                    + "; Catalog: " + getCatalogName(column) + ", Schema: " + getSchemaName(column) + ", Table: " + getTableName(column)
                    + ", Column: " + getColumnName(column) + ", Type: " + getColumnType(column));
        }
        return typeName;
    }

    public int getPrecision(final int column) throws SQLException {
        return getColumn(column).getPrecision();
    }

    public int getScale(final int column) throws SQLException {
        return getColumn(column).getScale();
    }

    public String getSchemaName(final int column) throws SQLException {
        return getColumn(column).getSchemaName();
    }

    public String getTableName(final int column) throws SQLException {
        return getColumn(column).getTableName();
    }

    public boolean isAutoIncrement(final int column) throws SQLException {
        return getColumn(column).isAutoIncrement();
    }

    public boolean isCaseSensitive(final int column) throws SQLException {
        return getColumn(column).isCaseSensitive();
    }

    public boolean isCurrency(final int column) throws SQLException {
        return getColumn(column).isCurrency();
    }

    public boolean isDefinitelyWritable(final int column) throws SQLException {
        return getColumn(column).isDefinitivelyWritable();
    }

    public int isNullable(final int column) throws SQLException {
        return getColumn(column).isNullable();
    }

    public boolean isReadOnly(final int column) throws SQLException {
        return getColumn(column).isReadOnly();
    }

    public boolean isSearchable(final int column) throws SQLException {
        return getColumn(column).isSearchable();
    }

    public boolean isSigned(final int column) throws SQLException {
        return getColumn(column).isSigned();
    }

    public boolean isWritable(final int column) throws SQLException {
        return getColumn(column).isWritable();
    }

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
