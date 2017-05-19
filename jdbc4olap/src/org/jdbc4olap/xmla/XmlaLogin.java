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

import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

/**
 * Holds http basic auth login info to allow login to Xmla webservice web server.
 *
 * @author Dan Rollo
 *         Date: Jul 14, 2008
 *         Time: 4:46:40 PM
 * @author Kasper Sørensen
 *      use org.apache.commons.codec.binary.Base64 instead of non-spec'd Sun internal class sun.misc.BASE64Encoder.
 */
public final class XmlaLogin {

    /**
     * Property name used as login user.
     */
    public static final String PROPERTY_NAME_USER = "user";
    /**
     * Property name used as login password.
     */
    public static final String PROPERTY_NAME_PASSWORD = "password";


    private final String userName;
    private final String authorization;

    XmlaLogin(final Properties info) {
        if (info != null) {
            userName = info.getProperty(XmlaLogin.PROPERTY_NAME_USER);
            final String usernameString = userName + ":" + info.getProperty(XmlaLogin.PROPERTY_NAME_PASSWORD);
            authorization = new String(new Base64().encode(usernameString.getBytes()));
        } else {
            userName = null;
            authorization = null;
        }
    }

    public String getUserName() {
        return userName;
    }

    String getAuthorization() {
        return authorization;
    }
}
