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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.jdbc4olap.xmla.XmlaLogin;

public class OlapDataSource implements DataSource {

    private String password = "";
    private String user = "";
    private PrintWriter printWriter;
    private String url;

    public Connection getConnection() throws SQLException {
        return getConnection(user, password);
    }

    public Connection getConnection(final String user, final String password)
            throws SQLException {
        final Properties properties = new Properties();
        if (user != null) {
            properties.put(XmlaLogin.PROPERTY_NAME_USER, user);
        }
        if (password != null) {
            properties.put(XmlaLogin.PROPERTY_NAME_PASSWORD, password);
        }
        final OlapNonRegisteringDriver driver = new OlapNonRegisteringDriver();
        return driver.connect(url, properties);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return printWriter;
    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public void setLogWriter(final PrintWriter out) throws SQLException {
        printWriter = out;
    }

    public void setLoginTimeout(final int seconds) throws SQLException {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
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
