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

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.jdbc4olap.xmla.XmlaConn;


/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
class OlapConnection implements Connection {

    private XmlaConn xmlaConn;
    private String catalog;
    private int holdability;
    private boolean autocommit;
    private Map<String,Class<?>> typeMap;

    private final DatabaseMetaData metaData;

    /**
     * Package visible constructor for unit testing only.
     *
     * @param mockXmlaConn a fack object.
     */
    OlapConnection(final XmlaConn mockXmlaConn) {
        xmlaConn = mockXmlaConn;
        metaData = new OlapDatabaseMetaData(this);
    }

    OlapConnection(final String url, final Properties info) throws MalformedURLException, SQLException {
        if (url == null || url.length() == 0) {
            throw new MalformedURLException("Connection initialization error");
        }
        xmlaConn = new XmlaConn(new URL(url), info);
        metaData = new OlapDatabaseMetaData(this);
        holdability = ResultSet.HOLD_CURSORS_OVER_COMMIT;
        autocommit = true;
        typeMap = new HashMap<String,Class<?>>();
    }

    XmlaConn getXmlaConn() {
        return xmlaConn;
    }

    public void clearWarnings() throws SQLException {
    }

    public void close() throws SQLException {
        xmlaConn = null;
    }

    public void commit() throws SQLException {
    }

    public Statement createStatement() throws SQLException {
        return new OlapStatement(this);
    }

    public Statement createStatement(final int arg0, final int arg1) throws SQLException {
        return new OlapStatement(this);
    }

    public Statement createStatement(final int arg0, final int arg1, final int arg2) throws SQLException {
        return new OlapStatement(this);
    }

    public boolean getAutoCommit() throws SQLException {
        return autocommit;
    }

    public String getCatalog() throws SQLException {
        return catalog;
    }

    public int getHoldability() throws SQLException {
        return holdability;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return metaData;
    }

    public int getTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    public Map<String,Class<?>> getTypeMap() throws SQLException {
        return typeMap;
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public boolean isClosed() throws SQLException {
        return xmlaConn == null;
    }

    public boolean isReadOnly() throws SQLException {
        return true;
    }

    public String nativeSQL(final String arg0) throws SQLException {
        return arg0;
    }

    public CallableStatement prepareCall(final String arg0) throws SQLException {
        return null;
    }

    public CallableStatement prepareCall(final String arg0, final int arg1, final int arg2) throws SQLException {
        return null;
    }

    public CallableStatement prepareCall(final String arg0, final int arg1, final int arg2, final int arg3) throws SQLException {
        return null;
    }

    public PreparedStatement prepareStatement(final String arg0) throws SQLException {
        return new OlapPreparedStatement(this, arg0);
    }

    public PreparedStatement prepareStatement(final String arg0, final int arg1) throws SQLException {
        return new OlapPreparedStatement(this, arg0);
    }

    public PreparedStatement prepareStatement(final String arg0, final int[] arg1) throws SQLException {
        return new OlapPreparedStatement(this, arg0);
    }

    public PreparedStatement prepareStatement(final String arg0, final String[] arg1) throws SQLException {
        return new OlapPreparedStatement(this, arg0);
    }

    public PreparedStatement prepareStatement(final String arg0, final int arg1, final int arg2) throws SQLException {
        return new OlapPreparedStatement(this, arg0);
    }

    public PreparedStatement prepareStatement(final String arg0, final int arg1, final int arg2, final int arg3) throws SQLException {
        return new OlapPreparedStatement(this, arg0);
    }

    public void releaseSavepoint(final Savepoint arg0) throws SQLException {
    }

    public void rollback() throws SQLException {
    }

    public void rollback(final Savepoint arg0) throws SQLException {
    }

    public void setAutoCommit(final boolean arg0) throws SQLException {
        autocommit = arg0;
    }

    public void setCatalog(final String arg0) throws SQLException {
        catalog = arg0;
        // @todo Implement this???
//        xmlaConn.setCatalog(arg0);
    }

    public void setHoldability(final int arg0) throws SQLException {
        holdability = arg0;
    }

    public void setReadOnly(final boolean arg0) throws SQLException {
    }

    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    public Savepoint setSavepoint(final String arg0) throws SQLException {
        return null;
    }

    public void setTransactionIsolation(final int arg0) throws SQLException {
    }

    public void setTypeMap(final Map<String,Class<?>> arg0) throws SQLException {
        typeMap = arg0;
    }

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValid(int timeout) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	public String getClientInfo(String name) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}


}
