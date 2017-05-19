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

import junit.framework.TestCase;

import java.util.Properties;

/**
 * @author Dan Rollo
 * Date: Jul 14, 2008
 * Time: 5:08:50 PM
 */
public class XmlaLoginTest extends TestCase {

    public static XmlaLogin createMock(final String userName, final String password) {
        final Properties info = new Properties();

        if (userName != null) {
            info.put(XmlaLogin.PROPERTY_NAME_USER, userName);
        }

        if (password != null) {
            info.put(XmlaLogin.PROPERTY_NAME_PASSWORD, password);
        }

        return new XmlaLogin(info);
    }

    public void testCreateMock() throws Exception {
        createMock(null, null);
        createMock("u", null);
        createMock(null, "p");
        createMock("", "p");
        createMock("", null);
        createMock("u", "");
        createMock("u", null);
    }

    public void testCreate() throws Exception {
        assertEquals(null, (new XmlaLogin(null)).getAuthorization());

        final Properties info = new Properties();
        assertEquals("bnVsbDpudWxs", (new XmlaLogin(info)).getAuthorization());

        info.put(XmlaLogin.PROPERTY_NAME_USER, "");
        assertEquals("Om51bGw=", (new XmlaLogin(info)).getAuthorization());

        info.put(XmlaLogin.PROPERTY_NAME_USER, "dummyUser");
        assertEquals("ZHVtbXlVc2VyOm51bGw=", (new XmlaLogin(info)).getAuthorization());

        info.put(XmlaLogin.PROPERTY_NAME_PASSWORD, "");
        assertEquals("ZHVtbXlVc2VyOg==", (new XmlaLogin(info)).getAuthorization());

        info.put(XmlaLogin.PROPERTY_NAME_PASSWORD, "dummyPassword");
        assertEquals("ZHVtbXlVc2VyOmR1bW15UGFzc3dvcmQ=", (new XmlaLogin(info)).getAuthorization());

        info.put(XmlaLogin.PROPERTY_NAME_USER, "");
        assertEquals("OmR1bW15UGFzc3dvcmQ=", (new XmlaLogin(info)).getAuthorization());

        info.remove(XmlaLogin.PROPERTY_NAME_USER);
        assertEquals("bnVsbDpkdW1teVBhc3N3b3Jk", (new XmlaLogin(info)).getAuthorization());
    }
}
