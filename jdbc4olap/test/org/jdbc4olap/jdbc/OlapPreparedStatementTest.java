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
import org.jdbc4olap.xmla.XmlaLogin;
import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaLoginTest;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Dan Rollo
 * Date: Jul 17, 2008
 * Time: 12:53:41 AM
 */
public class OlapPreparedStatementTest extends TestCase {

    private static OlapPreparedStatement createMock(final XmlaLogin login, final String statement,
                                                    final ArrayList<String[]> mockCatalogCache,
                                                    final ArrayList<String[]> mockSchemaCache,
                                                    final String mockTable, final String mockColumn)
            throws SQLException {

        return new OlapPreparedStatement(OlapConnectionTest.createMock(login, mockCatalogCache, mockSchemaCache, mockTable, mockColumn), statement);
    }

    public void testGetStatementParameterCount() throws Exception {
        assertEquals(0, OlapPreparedStatement.getStatementParameterCount(null));
        assertEquals(0, OlapPreparedStatement.getStatementParameterCount(""));
        assertEquals(0, OlapPreparedStatement.getStatementParameterCount("T"));
        assertEquals(0, OlapPreparedStatement.getStatementParameterCount("select * from T"));

        assertEquals(1, OlapPreparedStatement.getStatementParameterCount("select * from T where F=?"));

        assertEquals(2, OlapPreparedStatement.getStatementParameterCount("F=?, F2>?"));
    }

    public void testCreate() throws Exception {
        try {
            new OlapPreparedStatement(null, null);
            fail("Null connection and statement should fail");
        } catch (NullPointerException e) {
            assertNull(e.getMessage());
        }

        // @todo Do we need to override OlapStatement#execute(String) methods in OlapPreparedStatement?
        assertNotNull(new OlapPreparedStatement(OlapConnectionTest.createMock(XmlaConn.STANDARD_SERVER, null), null));
    }

    public void testCreateMock() throws Exception {

        final ArrayList<String[]> mockCatalogCache = new ArrayList<String[]>();
        final String mockCatalog = "cat";
        mockCatalogCache.add(new String[] {mockCatalog});

        final ArrayList<String[]> mockSchemaCache = new ArrayList<String[]>();
        final String mockSchema = mockCatalog + "/schema";
        mockSchemaCache.add(new String[] {mockSchema});

        final String mockTable = "table";
        final String mockColumn = "column";

        final String mockStatement = "select * from " + mockCatalog + ".\"" + mockSchema + "\".\"" + mockTable + "\" where \"" + mockTable + "." + mockColumn + "\" = ?";
        final OlapPreparedStatement olapPreparedStatement = createMock(null, mockStatement,
                mockCatalogCache, mockSchemaCache, mockTable, mockColumn);
        assertNotNull(olapPreparedStatement);
        assertNotNull(olapPreparedStatement.getConnection());
        assertNull(((OlapConnection) olapPreparedStatement.getConnection()).getXmlaConn().getLogin());

        olapPreparedStatement.setString(1, "mockParam");
        // @todo Implement getParameterMetaData()?
        //assertNotNull(olapPreparedStatement.getParameterMetaData());
        // @todo Why is OlapPreparedStatemt.prepareSql() never called?
    }

    public void testExecuteEmptySql() throws Exception {
        final OlapPreparedStatement ps = createMock(XmlaLoginTest.createMock("auser", null),
                "", null, null, null, null);
        try {
            ps.executeQuery();
            fail("invalid statement sql should have failed.");
        } catch (SQLException e) {
            assertTrue(e.getMessage().startsWith("Encountered \"<EOF>\" at line 0, column 0."));
        }

        // @todo Do we need to override OlapStatement#execute(String) methods in OlapPreparedStatement?
        OlapResultSetTest.assertEmptyResultSet(ps.executeQuery(""));
    }

}
