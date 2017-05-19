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

import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdbc4olap.xmla.Jdbc4OlapConstants;
import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaLogin;

/**
 * Must be "public" to allow for loading/introspection.
 *
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
public class OlapNonRegisteringDriver implements Driver {

    private static final Logger LOG = Logger.getLogger(Jdbc4OlapConstants.JDBC4OLAP_LOG);

    /**
     * The required URL prefix for this driver.
     */
    public static final String JDBC_URL_PREFIX = "jdbc:jdbc4olap:";
    public static final String PROPERTY_NAME_DRIVER_URL_PATTERN = "urlPattern";
    public static final String[][] PROPERTY_VALUE_DRIVER_URL_PATTERNS = new String[][]{
            new String[]{"MS SQL Server", JDBC_URL_PREFIX + "http://server:port/OLAP/msmdpump.dll"},
            new String[]{"Mondrian", JDBC_URL_PREFIX + "http://server:port/mondrian/xmla"},
            new String[]{"SAP BW", JDBC_URL_PREFIX + "http://server:port/sap/bw/xml/soap/xmla?sap-client=number"},
            new String[]{"ESSBASE", JDBC_URL_PREFIX + "http://server:port/aps/xmla"}
    };

    public boolean acceptsURL(final String url) throws SQLException {
        return url != null && (url.indexOf(JDBC_URL_PREFIX) >= 0);
    }

    public Connection connect(final String url, final Properties info) throws SQLException {
        try {
            String location = url.substring(JDBC_URL_PREFIX.length());
            if (location.indexOf("?") >= 0) {
                final String params = location.substring(location.indexOf("?") + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                String param;
                while (st.hasMoreElements()) {
                    param = (String) st.nextElement();
                    int pos = param.indexOf("=");
                    if (pos >= 0) {
                        info.put(param.substring(0, pos), param.substring(pos + 1));
                    }
                }
            }
            return new OlapConnection(location, info);
        } catch (Exception e) {
            LOG.info("Connect error");
            LOG.log(Level.FINE, "Connect error", e);
            throw new SQLException("Connect error: " + e.getLocalizedMessage());
        }
    }

    public int getMajorVersion() {
        return 1;
    }

    public int getMinorVersion() {
        return 0;
    }

    public DriverPropertyInfo[] getPropertyInfo(final String arg0, final Properties info) throws SQLException {
        try {
            String location = arg0.substring(JDBC_URL_PREFIX.length());
            XmlaConn conn = new XmlaConn(new URL(location), info);
            return conn.getDriverPropertyInfo();
        } catch (Exception e) {
            // response with missing props needed to connect
            final ArrayList<DriverPropertyInfo> driverProps = new ArrayList<DriverPropertyInfo>();

            final String currentUser;
            if (info != null) {
                currentUser = info.getProperty(XmlaLogin.PROPERTY_NAME_USER);
            } else {
                currentUser = null;
            }
            driverProps.add(createDriverPropInfo(XmlaLogin.PROPERTY_NAME_USER, currentUser, null,
                    "Login user id"));

            driverProps.add(createDriverPropInfo(XmlaLogin.PROPERTY_NAME_PASSWORD, null, null,
                    "Login password"));

            /* These need more work - see OlapConnection.setCatalog(), that would be a better way I think
            if (info == null || (info.getProperty(StandardPropertyManager.CATALOG) == null)) {
                driverProps.add(createDriverPropInfo(StandardPropertyManager.CATALOG, null, null,
                        StandardPropertyManager.CATALOG + " to load.  NOT IMPLEMENTED!!!", false));
            }

            if (info == null || (info.getProperty(StandardPropertyManager.CUBE) == null)) {
                driverProps.add(createDriverPropInfo(StandardPropertyManager.CUBE, null, null,
                        StandardPropertyManager.CUBE + " (schema) to load. NOT IMPLEMENTED!!!", false));
            }
            //*/

            if (info == null || (info.getProperty(PROPERTY_NAME_DRIVER_URL_PATTERN) == null)) {
                for (String[] olapVendor : PROPERTY_VALUE_DRIVER_URL_PATTERNS) {
                    driverProps.add(createDriverPropInfo(PROPERTY_NAME_DRIVER_URL_PATTERN, olapVendor[1], null,
                            "URL Pattern for " + olapVendor[0]));
                }
            }

            return driverProps.toArray(new DriverPropertyInfo[driverProps.size()]);
            //throw new SQLException("Driver properties retrieval error:" + e.getLocalizedMessage() + addCauseAndTrace(e));
        }
    }

    private static DriverPropertyInfo createDriverPropInfo(final String name, final String value, final String[] choices,
                                                           final String description) {

        final DriverPropertyInfo prop = new DriverPropertyInfo(name, value);
        prop.choices = choices;
        prop.description = description;
        prop.required = false;
        return prop;
    }

    public boolean jdbcCompliant() {
        return false;
    }

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

}
