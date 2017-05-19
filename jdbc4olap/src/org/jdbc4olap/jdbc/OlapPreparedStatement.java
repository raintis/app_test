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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
class OlapPreparedStatement extends OlapStatement implements PreparedStatement {

    private final String preparedStatement;
    private String[] params;

    public OlapPreparedStatement(final OlapConnection c, final String statement) throws SQLException {
        super(c);
        //@TODO: empty query ? Maybe need to override OlapStatement.execute(String) methods to parse params, etc?
        preparedStatement = statement;
        final int nbParams = getStatementParameterCount(statement);
        params = new String[nbParams];
    }

    /**
     * @param statement the sql of a prepared statement
     * @return the number of occurences of the "?" (parameters) in the given statement string
     */
    static int getStatementParameterCount(final String statement) {
        if (statement == null) {
            return 0;
        }

        int lastIndex = 0;
        int nbParams = 0;
        while (lastIndex != -1) {
            lastIndex = statement.indexOf("?", lastIndex + 1);
            if (lastIndex != -1) {
                nbParams++;
            }
        }
        return nbParams;
    }

    public void addBatch() throws SQLException {
    }

    public void clearParameters() throws SQLException {
        params = new String[preparedStatement.split("[?]").length - 1];
    }

    private String prepareSql() throws SQLException {
        String[] queryParts = preparedStatement.split("[?]");
        StringBuffer sql = new StringBuffer();
        for (int i = 0; i < queryParts.length; i++) {
            if (i < params.length && params[i] == null) {
                throw new SQLException("Missing parameter");
            }
            sql.append(queryParts[i]).append(i < params.length ? params[i] : "");
        }
        return sql.toString();
    }

    public boolean execute() throws SQLException {
    	StatementHelper sh = new StatementHelper(prepareSql(), olapMetadata, xmlaConn);
        resultSet = (OlapResultSet) sh.processQuery();
        return true;
    }

    public ResultSet executeQuery() throws SQLException {
    	StatementHelper sh = new StatementHelper(prepareSql(), olapMetadata, xmlaConn);
        resultSet = (OlapResultSet) sh.processQuery();
        return resultSet;
    }

    public int executeUpdate() throws SQLException {
        throw new SQLException("jdbc4olap driver only supports SELECT queries");
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        if (resultSet.getMetaData() == null) {
        	StatementHelper sh = new StatementHelper(prepareSql(), olapMetadata, xmlaConn);
        	resultSet.setMetaData(sh.getMetaData(resultSet));
        }
        return resultSet.getMetaData();
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    public void setArray(final int arg0, final Array arg1) throws SQLException {
    }

    public void setAsciiStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
    }

    public void setBigDecimal(final int arg0, final BigDecimal arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = String.valueOf(arg1);
    }

    public void setBinaryStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
    }

    public void setBlob(final int arg0, final Blob arg1) throws SQLException {
    }

    public void setBoolean(final int arg0, final boolean arg1) throws SQLException {
    }

    public void setByte(final int arg0, final byte arg1) throws SQLException {
    }

    public void setBytes(final int arg0, final byte[] arg1) throws SQLException {
    }

    public void setCharacterStream(final int arg0, final Reader arg1, final int arg2) throws SQLException {
    }

    public void setClob(final int arg0, final Clob arg1) throws SQLException {
    }

    public void setDate(final int arg0, final Date arg1) throws SQLException {
    }

    public void setDate(final int arg0, final Date arg1, final Calendar arg2) throws SQLException {
    }

    public void setDouble(final int arg0, final double arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = Double.toString(arg1);
    }

    public void setFloat(final int arg0, final float arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = Float.toString(arg1);
    }

    public void setInt(final int arg0, final int arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = Integer.toString(arg1);
    }

    public void setLong(final int arg0, final long arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = Long.toString(arg1);
    }

    public void setNull(final int arg0, final int arg1) throws SQLException {
    }

    public void setNull(final int arg0, final int arg1, final String arg2) throws SQLException {
    }

    public void setObject(final int arg0, final Object arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = arg1.toString();
    }

    public void setObject(final int arg0, final Object arg1, final int arg2) throws SQLException {
    }

    public void setObject(final int arg0, final Object arg1, final int arg2, final int arg3) throws SQLException {
    }

    public void setRef(final int arg0, final Ref arg1) throws SQLException {
    }

    public void setShort(final int arg0, final short arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = Short.toString(arg1);
    }

    public void setString(final int arg0, final String arg1) throws SQLException {
        if (arg0 > params.length) {
            throw new SQLException("Incorrect index for parameter.");
        }
        params[arg0 - 1] = "'" + arg1 + "'";
    }

    public void setTime(final int arg0, final Time arg1) throws SQLException {
    }

    public void setTime(final int arg0, final Time arg1, final Calendar arg2) throws SQLException {
    }

    public void setTimestamp(final int arg0, final Timestamp arg1) throws SQLException {
    }

    public void setTimestamp(final int arg0, final Timestamp arg1, final Calendar arg2) throws SQLException {
    }

    public void setURL(final int arg0, final URL arg1) throws SQLException {
    }

    public void setUnicodeStream(final int arg0, final InputStream arg1, final int arg2) throws SQLException {
    }

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNString(int parameterIndex, String value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
