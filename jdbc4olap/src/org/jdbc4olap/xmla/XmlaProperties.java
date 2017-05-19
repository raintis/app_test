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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author <a href="mailto:drollo@compositesw.com">Dan Rollo</a>
 */
class XmlaProperties {

    private static final String[] DRIVER_PROPERTIES = {PropertyManager.PROPERTY_STREAM_COMPRESSION.toLowerCase()};
    private static final List<String> DRIVER_LIST = new ArrayList<String>(Arrays.asList(DRIVER_PROPERTIES));
    private static final String[] FORBIDDEN_PROPERTIES = {XmlaLogin.PROPERTY_NAME_USER, XmlaLogin.PROPERTY_NAME_PASSWORD};
    private static final List<String> FORBIDDEN_LIST = new ArrayList<String>(Arrays.asList(FORBIDDEN_PROPERTIES));

    private final Properties properties;

    XmlaProperties() {
        properties = new Properties();
    }

    XmlaProperties(final Properties props) {
        this();
        addProperties(props);
    }

    void addProperties(final Properties props) {
        if (props != null) {
            for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
                String key = (String) e.nextElement();
                String value = props.getProperty(key);
                setProperty(key, value);
            }
        }
    }

    String getProperty(final String key) {
        return properties.getProperty(key);
    }

    void setProperty(final String key, final String value) {
        if (key != null && value != null && !FORBIDDEN_LIST.contains(key.toLowerCase())) {
            properties.setProperty(key, value);
        }
    }

    boolean containsProperty(final String key) {
        return properties.containsKey(key);
    }

    SOAPElement getXMLA() throws SOAPException {
        final SOAPFactory factory = XmlaConn.SOAP_FACTORY;
        final SOAPElement props = factory.createElement("Properties", "", "urn:schemas-microsoft-com:xml-analysis");
        final SOAPElement propertyList = props.addChildElement(factory.createName("PropertyList", "", "urn:schemas-microsoft-com:xml-analysis"));
        for (Enumeration e = properties.propertyNames(); e.hasMoreElements();) {
            final String key = (String) e.nextElement();
            final String value = properties.getProperty(key);
            if (value != null && !"".equals(value)&& !DRIVER_LIST.contains(key.toLowerCase())) {
                final SOAPElement soapProperty = propertyList.addChildElement(factory.createName(key));
                soapProperty.addTextNode(value);
            }
        }
        return props;
    }
}
