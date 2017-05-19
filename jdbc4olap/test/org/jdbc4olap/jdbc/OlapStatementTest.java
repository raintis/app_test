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

import junit.framework.TestCase;
import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaLogin;
import org.jdbc4olap.xmla.XmlaLoginTest;

import java.sql.SQLException;

/**
 * @author Dan Rollo
 * Date: Jul 16, 2008
 * Time: 11:48:23 PM
 */
public class OlapStatementTest extends TestCase {

    private static OlapStatement createMock(final XmlaLogin login) throws SQLException {
        return new OlapStatement(OlapConnectionTest.createMock(XmlaConn.STANDARD_SERVER, login));
    }

    public void testCreate() throws Exception {
        try {
            new OlapStatement(null);
            fail("Null connection should fail");
        } catch (NullPointerException e) {
            assertNull(e.getMessage());
        }
    }

    public void testCreateMock() throws Exception {
        final XmlaLogin mockLogin = XmlaLoginTest.createMock("dummyUser", "dummyPwd");
        final OlapStatement olapStatement = createMock(mockLogin);
        assertNotNull(olapStatement);
        assertNotNull(olapStatement.getConnection());
        assertEquals(mockLogin.getUserName(), ((OlapConnection) olapStatement.getConnection()).getXmlaConn().getLogin().getUserName());

        olapStatement.close();
        // ensure multiple calls to close() are allowed, as per spec
        olapStatement.close();
    }

    public void testExecuteEmptySql() throws Exception {
        final OlapStatement st = createMock(null);
        OlapResultSetTest.assertEmptyResultSet(st.getResultSet());
        OlapResultSetTest.assertEmptyResultSet(st.executeQuery("select * from cat.\"cat/schema\".\"table\" where \"table.column\" = ?"));
        OlapResultSetTest.assertEmptyResultSet(st.executeQuery(""));
    }

    public void testGetGeneratedKeys() throws Exception {
        final OlapStatement st = createMock(null);
        OlapResultSetTest.assertEmptyResultSet(st.getGeneratedKeys());
    }
}
