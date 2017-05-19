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
package org.jdbc4olap.xmla;

import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;

import javax.xml.soap.Node;

import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 */
class StandardPropertyManager implements PropertyManager {

    //properties cache is updated when a property is set
    private boolean upToDate = false;
    //a server's properties are divided in writable and read-only properties
    private List<DriverPropertyInfo> driverProperties;
    private XmlaProperties writableProperties;
    private Properties readOnlyProperties;
    private final XmlaConn conn;
    private final String dataSourceInfo;
    private final Properties info;
    //XMLA properties names
    private static final String CATALOG = "Catalog";
    private static final String CUBE = "Cube";
    private static final String DATABASE_PRODUCT_NAME = "ProviderName";
    private static final String DATABASE_PRODUCT_VERSION = "ProviderVersion";
    //mapped or deprecated properties
    private static final String[] FORBIDDEN_PROPERTIES = {"AxisFormat", "Catalog", "Content", "Cube", "DataSourceInfo", "Format", "Password", "UserName"};
    private static final List<String> FORBIDDEN_LIST = new ArrayList<String>(Arrays.asList(FORBIDDEN_PROPERTIES));

    public StandardPropertyManager(final XmlaConn conn, final String dataSourceInfo, final Properties info) throws SQLException {
        this.conn = conn;
        this.dataSourceInfo = dataSourceInfo;
        this.info = info;
        refreshProperties();
    }

    private String getPropertyValue(final String key) throws SQLException {
        if (!upToDate) {
            refreshProperties();
        }
        if (writableProperties.containsProperty(key)) {
            return writableProperties.getProperty(key);
        } else {
            return readOnlyProperties.getProperty(key);
        }
    }

    public DriverPropertyInfo[] getDriverPropertyInfo() throws SQLException {
        if (!upToDate) {
            refreshProperties();
        }

        final DriverPropertyInfo[] res = new DriverPropertyInfo[driverProperties.size()];
        for (int k = 0; k < driverProperties.size(); k++) {
            res[k] = driverProperties.get(k);
        }
        return res;
    }

    private void refreshProperties() throws SQLException {

        final NodeList rows = conn.discoverProperties();

        writableProperties = new XmlaProperties();
        readOnlyProperties = new Properties();
        driverProperties = new ArrayList<DriverPropertyInfo>();
        final DriverPropertyInfo compressionProperty = new DriverPropertyInfo(PROPERTY_STREAM_COMPRESSION, "0");
        compressionProperty.required = false;
        compressionProperty.description = "Use compressed XMLA streams";
        driverProperties.add(compressionProperty);

        for (int i = 0; i < rows.getLength(); i++) {
            final NodeList attributes = rows.item(i).getChildNodes();
            String name = "";
            String description = "";
            String value = "";
            boolean required = false;
            boolean writable = false;

            for (int j = 0; j < attributes.getLength(); j++) {
                final Node attribute = (Node) attributes.item(j);
                final String nodeName = attribute.getNodeName();
                final String nodeValue = attribute.getValue();
                if ("PropertyName".equals(nodeName)) {
                    name = nodeValue;
                } else if ("PropertyAccessType".equals(nodeName)) {
                    writable = !nodeValue.equals("Read");
                } else if ("PropertyDescription".equals(nodeName)) {
                    description = nodeValue;
                } else if ("IsRequired".equals(nodeName)) {
                    required = nodeValue.equals("true");
                } else if ("Value".equals(nodeName)) {
                    value = nodeValue;
                }
            }

            if (name != null && !name.equals("") && !FORBIDDEN_LIST.contains(name)) { // && value!=null
                final DriverPropertyInfo driverProperty = new DriverPropertyInfo(name, value);
                driverProperty.required = required;
                driverProperty.description = description;
                if (writable) {
                    driverProperties.add(driverProperty);
                }
                if (value != null && !value.equals("")) {
                    if (writable) {
                        writableProperties.setProperty(name, value);
                    } else {
                        readOnlyProperties.setProperty(name, value);
                    }
                }
            }
        }
        writableProperties.addProperties(info);
        writableProperties.setProperty("DataSourceInfo", dataSourceInfo);
        upToDate = true;
    }

    public void setCatalog(final String cat) {
        writableProperties.setProperty(CATALOG, cat);
    }

    public String getCatalog() throws SQLException {
        return getPropertyValue(CATALOG);
    }

    public void setCube(final String cube) {
        writableProperties.setProperty(CUBE, cube);
    }

    public String getCube() throws SQLException {
        return getPropertyValue(CUBE);
    }

    public XmlaProperties getXmlaProperties() {
        return writableProperties;
    }

    public String getDatabaseProductName() throws SQLException {
        return getPropertyValue(DATABASE_PRODUCT_NAME);
    }

    public String getDatabaseProductVersion() throws SQLException {
        return getPropertyValue(DATABASE_PRODUCT_VERSION);
    }


}
