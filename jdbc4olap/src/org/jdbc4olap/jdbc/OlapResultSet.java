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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdbc4olap.xmla.Jdbc4OlapConstants;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
class OlapResultSet implements ResultSet {

    private static final Logger LOG = Logger.getLogger(Jdbc4OlapConstants.JDBC4OLAP_LOG);

    private ResultSetMetaData metaData;
    private List<Object[]> data;
    private int position;
    private Statement statement;

    OlapResultSet() {
        this.data = new ArrayList<Object[]>();
        position = -1;
    }

    /**
     * Exists for unit testing only.
     *
     * @return the data structure of this result set
     */
    List<Object[]> getData() {
        return data;
    }

    void add(final Object[] row) {
        data.add(row);
    }

    public boolean absolute(final int row) throws SQLException {
        return false;
    }

    public void afterLast() throws SQLException {
        position = data.size() + 1;
    }

    public void beforeFirst() throws SQLException {
        position = -1;
    }

    public void cancelRowUpdates() throws SQLException {
    }

    public void clearWarnings() throws SQLException {
    }

    public void close() throws SQLException {
        data = null;
        metaData = null;
        position = -1;
        statement = null;
    }

    public void deleteRow() throws SQLException {
    }

    public int findColumn(final String columnName) throws SQLException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String name = metaData.getColumnName(i);
            if (name.equals(columnName)) {
                return i;
            }
        }
        throw new SQLException("Unknown column name");
    }

    public boolean first() throws SQLException {
        position = 0;
        return data.size() > 0;
    }

    public Array getArray(final int i) throws SQLException {
        return null;
    }

    public Array getArray(final String colName) throws SQLException {
        return null;
    }

    public InputStream getAsciiStream(final int columnIndex) throws SQLException {
        return null;
    }

    public InputStream getAsciiStream(final String columnName) throws SQLException {
        return null;
    }

    public BigDecimal getBigDecimal(final int columnIndex) throws SQLException {
        Object value = getObject(columnIndex);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return new BigDecimal(((Number) value).doubleValue());
        }
        return new BigDecimal(value.toString());
    }

    public BigDecimal getBigDecimal(final String columnName) throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    public BigDecimal getBigDecimal(final int columnIndex, final int scale) throws SQLException {
        return getBigDecimal(columnIndex);
    }

    public BigDecimal getBigDecimal(final String columnName, final int scale) throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    public InputStream getBinaryStream(final int columnIndex) throws SQLException {
        return null;
    }

    public InputStream getBinaryStream(final String columnName) throws SQLException {
        return null;
    }

    public Blob getBlob(final int i) throws SQLException {
        return null;
    }

    public Blob getBlob(final String colName) throws SQLException {
        return null;
    }

    public boolean getBoolean(final int columnIndex) throws SQLException {
        Object value = getObject(columnIndex);
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        }
        return Boolean.getBoolean(value.toString());
    }

    public boolean getBoolean(final String columnName) throws SQLException {
        return getBoolean(findColumn(columnName));
    }

    public byte getByte(final int columnIndex) throws SQLException {
        return 0;
    }

    public byte getByte(final String columnName) throws SQLException {
        return 0;
    }

    public byte[] getBytes(final int columnIndex) throws SQLException {
        return null;
    }

    public byte[] getBytes(final String columnName) throws SQLException {
        return null;
    }

    public Reader getCharacterStream(final int columnIndex) throws SQLException {
        return null;
    }

    public Reader getCharacterStream(final String columnName) throws SQLException {
        return null;
    }

    public Clob getClob(final int i) throws SQLException {
        return null;
    }

    public Clob getClob(final String colName) throws SQLException {
        return null;
    }

    public int getConcurrency() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    public String getCursorName() throws SQLException {
        return null;
    }

    public Date getDate(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return null;
        }
        if (value instanceof java.util.Date) {
            return new Date(((java.util.Date) value).getTime());
        }
        throw new SQLException("Conversion Error");
    }

    public Date getDate(final String columnName) throws SQLException {
        return getDate(findColumn(columnName));
    }

    public Date getDate(final int columnIndex, final Calendar cal) throws SQLException {
        return getDate(columnIndex);
    }

    public Date getDate(final String columnName, final Calendar cal) throws SQLException {
        return getDate(findColumn(columnName));
    }

    public double getDouble(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    public double getDouble(final String columnName) throws SQLException {
        return getDouble(findColumn(columnName));
    }

    public int getFetchDirection() throws SQLException {
        return ResultSet.FETCH_FORWARD;
    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public float getFloat(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return Float.parseFloat(value.toString());
    }

    public float getFloat(final String columnName) throws SQLException {
        return getFloat(findColumn(columnName));
    }

    public int getInt(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    public int getInt(final String columnName) throws SQLException {
        return getInt(findColumn(columnName));
    }

    public long getLong(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    public long getLong(final String columnName) throws SQLException {
        return getLong(findColumn(columnName));
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        if (metaData == null) {
            final String msg = "Jdbc4Olap: Missing ResultSetMetaData - Please report as driver bug.\n";
            final IllegalStateException e = new IllegalStateException(msg);
            LOG.severe(msg);
            LOG.log(Level.FINE, "StackTrace", e);
            throw e;
        }
        return metaData;
    }

    public Object getObject(final int columnIndex) throws SQLException {
        if (position < 0) {
            throw new SQLException("Before first row");
        }
        if (position >= data.size()) {
            throw new SQLException("After last row");
        }

        final Object[] row = data.get(position);
        return row[columnIndex - 1];
    }

    public Object getObject(final String columnName) throws SQLException {
        return getObject(findColumn(columnName));
    }

    public Object getObject(final int i, final Map map) throws SQLException {
        return getObject(i);
    }

    public Object getObject(final String colName, final Map map) throws SQLException {
        return getObject(colName);
    }

    public Ref getRef(final int i) throws SQLException {
        return null;
    }

    public Ref getRef(final String colName) throws SQLException {
        return null;
    }

    public int getRow() throws SQLException {
        return position + 1;
    }

    public short getShort(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return 0;
        }
        if (value instanceof Short) {
            return ((Number) value).shortValue();
        }
        return Short.parseShort(value.toString());
    }

    public short getShort(final String columnName) throws SQLException {
        return getShort(findColumn(columnName));
    }

    public Statement getStatement() throws SQLException {
        return statement;
    }

    public String getString(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    public String getString(final String columnName) throws SQLException {
        return getString(findColumn(columnName));
    }

    public Time getTime(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return null;
        }
        if (value instanceof java.util.Date) {
            return new Time(((java.util.Date) value).getTime());
        }
        throw new SQLException("Conversion Error");
    }

    public Time getTime(final String columnName) throws SQLException {
        return getTime(findColumn(columnName));
    }

    public Time getTime(final int columnIndex, final Calendar cal) throws SQLException {
        return getTime(columnIndex);
    }

    public Time getTime(final String columnName, final Calendar cal) throws SQLException {
        return getTime(findColumn(columnName));
    }

    public Timestamp getTimestamp(final int columnIndex) throws SQLException {
        final Object value = getObject(columnIndex);
        if (value == null) {
            return null;
        }
        if (value instanceof java.util.Date) {
            return new Timestamp(((java.util.Date) value).getTime());
        }
        throw new SQLException("Conversion Error");
    }

    public Timestamp getTimestamp(final String columnName) throws SQLException {
        return getTimestamp(findColumn(columnName));
    }

    public Timestamp getTimestamp(final int columnIndex, final Calendar cal) throws SQLException {
        return getTimestamp(columnIndex);
    }

    public Timestamp getTimestamp(final String columnName, final Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnName));
    }

    public int getType() throws SQLException {
        return ResultSet.TYPE_FORWARD_ONLY;
    }

    public URL getURL(final int columnIndex) throws SQLException {
        return null;
    }

    public URL getURL(final String columnName) throws SQLException {
        return null;
    }

    public InputStream getUnicodeStream(final int columnIndex) throws SQLException {
        return null;
    }

    public InputStream getUnicodeStream(final String columnName) throws SQLException {
        return null;
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void insertRow() throws SQLException {
    }

    public boolean isAfterLast() throws SQLException {
        return position > data.size();
    }

    public boolean isBeforeFirst() throws SQLException {
        return position < 0;
    }

    public boolean isFirst() throws SQLException {
        return position == 0;
    }

    public boolean isLast() throws SQLException {
        return position == data.size();
    }

    public boolean last() throws SQLException {
        position = data.size();
        return true;
    }

    public void moveToCurrentRow() throws SQLException {
    }

    public void moveToInsertRow() throws SQLException {
    }

    public boolean next() throws SQLException {
        if (position <= data.size()) {
            position++;
        }
        return position < data.size();
    }

    public boolean previous() throws SQLException {
        if (position >= 0) {
            position--;
        }
        return position < 0;
    }

    public void refreshRow() throws SQLException {
    }

    public boolean relative(final int rows) throws SQLException {
        return false;
    }

    public boolean rowDeleted() throws SQLException {
        return false;
    }

    public boolean rowInserted() throws SQLException {
        return false;
    }

    public boolean rowUpdated() throws SQLException {
        return false;
    }

    public void setFetchDirection(final int direction) throws SQLException {
    }

    public void setFetchSize(final int rows) throws SQLException {
    }

    public void updateArray(final int columnIndex, final Array x) throws SQLException {
    }

    public void updateArray(final String columnName, final Array x) throws SQLException {
    }

    public void updateAsciiStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
    }

    public void updateAsciiStream(final String columnName, final InputStream x, final int length) throws SQLException {
    }

    public void updateBigDecimal(final int columnIndex, final BigDecimal x) throws SQLException {
    }

    public void updateBigDecimal(final String columnName, final BigDecimal x) throws SQLException {
    }

    public void updateBinaryStream(final int columnIndex, final InputStream x, final int length) throws SQLException {
    }

    public void updateBinaryStream(final String columnName, final InputStream x, final int length) throws SQLException {
    }

    public void updateBlob(final int columnIndex, final Blob x) throws SQLException {
    }

    public void updateBlob(final String columnName, final Blob x) throws SQLException {
    }

    public void updateBoolean(final int columnIndex, final boolean x) throws SQLException {
    }

    public void updateBoolean(final String columnName, final boolean x) throws SQLException {
    }

    public void updateByte(final int columnIndex, final byte x) throws SQLException {
    }

    public void updateByte(final String columnName, final byte x) throws SQLException {
    }

    public void updateBytes(final int columnIndex, final byte[] x) throws SQLException {
    }

    public void updateBytes(final String columnName, final byte[] x) throws SQLException {
    }

    public void updateCharacterStream(final int columnIndex, final Reader x, final int length) throws SQLException {
    }

    public void updateCharacterStream(final String columnName, final Reader reader, final int length) throws SQLException {
    }

    public void updateClob(final int columnIndex, final Clob x) throws SQLException {
    }

    public void updateClob(final String columnName, final Clob x) throws SQLException {
    }

    public void updateDate(final int columnIndex, final Date x) throws SQLException {
    }

    public void updateDate(final String columnName, final Date x) throws SQLException {
    }

    public void updateDouble(final int columnIndex, final double x) throws SQLException {
    }

    public void updateDouble(final String columnName, final double x) throws SQLException {
    }

    public void updateFloat(final int columnIndex, final float x) throws SQLException {
    }

    public void updateFloat(final String columnName, final float x) throws SQLException {
    }

    public void updateInt(final int columnIndex, final int x) throws SQLException {
    }

    public void updateInt(final String columnName, final int x) throws SQLException {
    }

    public void updateLong(final int columnIndex, final long x) throws SQLException {
    }

    public void updateLong(final String columnName, final long x) throws SQLException {
    }

    public void updateNull(final int columnIndex) throws SQLException {
    }

    public void updateNull(final String columnName) throws SQLException {
    }

    public void updateObject(final int columnIndex, final Object x) throws SQLException {
    }

    public void updateObject(final String columnName, final Object x) throws SQLException {
    }

    public void updateObject(final int columnIndex, final Object x, final int scale) throws SQLException {
    }

    public void updateObject(final String columnName, final Object x, final int scale) throws SQLException {
    }

    public void updateRef(final int columnIndex, final Ref x) throws SQLException {
    }

    public void updateRef(final String columnName, final Ref x) throws SQLException {
    }

    public void updateRow() throws SQLException {
    }

    public void updateShort(final int columnIndex, final short x) throws SQLException {
    }

    public void updateShort(final String columnName, final short x) throws SQLException {
    }

    public void updateString(final int columnIndex, final String x) throws SQLException {
    }

    public void updateString(final String columnName, final String x) throws SQLException {
    }

    public void updateTime(final int columnIndex, final Time x) throws SQLException {
    }

    public void updateTime(final String columnName, final Time x) throws SQLException {
    }

    public void updateTimestamp(final int columnIndex, final Timestamp x) throws SQLException {
    }

    public void updateTimestamp(final String columnName, final Timestamp x) throws SQLException {
    }

    public boolean wasNull() throws SQLException {
        return false;
    }

    void setMetaData(final ResultSetMetaData rsMetaData) {
        this.metaData = rsMetaData;
    }

    void setStatement(final Statement owningStatement) {
        this.statement = owningStatement;
    }

    /**
     * @return A new, valid, empty resultset (with metadata).
     */
    static OlapResultSet createEmptyResultSet() {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, null, null);
        return rs;
    }

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public RowId getRowId(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateNString(int columnIndex, String nString) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNString(String columnLabel, String nString) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public NClob getNClob(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public String getNString(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
