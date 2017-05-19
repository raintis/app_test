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

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.sql.DatabaseMetaData;

import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaLogin;
import org.jdbc4olap.xmla.XmlaLoginTest;

/**
 * @author Dan Rollo
 * Date: Jul 9, 2008
 * Time: 1:13:28 AM
 */
public class OlapDatabaseMetaDataTest extends TestCase {

    public static final String[] EMPTY_STRING_ARRAY = new String[] {};

    private static OlapDatabaseMetaData createMock(final int serverType, final XmlaLogin login)  {
        return new OlapDatabaseMetaData(OlapConnectionTest.createMock(serverType, login));
    }

    public void testCreateMock() throws Exception {
        final OlapDatabaseMetaData md = createMock(XmlaConn.STANDARD_SERVER, null);

        assertMetaCatalogs(md);
        assertMetaTableTypes(md);
        assertMetaSchemas(md);
        assertMetaTypeInfo(md);
        assertMetaUDT(md);
        assertMetaTables(md);
        assertMetaSuperTables(md);
        assertMetaProcedures(md);
        assertMetaColumns(md);
        assertMetaBestRowIdentifier(md);
        assertMetaPrimaryKeys(md);
        assertMetaImportedKeys(md);
        assertMetaIndexInfo(md);
        assertMetaExportedKeys(md);
        assertMetaColumnPrivileges(md);
        assertMetaTablePrivileges(md);
        assertMetaVersionColumns(md);
    }

    private static void assertMetaSchemas(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getSchemas();
        assertEquals("TABLE_SCHEM", rs.getMetaData().getColumnName(1));
        assertEquals("TABLE_CATALOG", rs.getMetaData().getColumnName(2));
        assertFalse(rs.next());
    }

    private static void assertMetaTableTypes(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getTableTypes();
        assertEquals("TABLE_TYPE", rs.getMetaData().getColumnName(1));
        assertTrue(rs.next());
        assertEquals("TABLE", rs.getString(1));
    }

    private static void assertMetaCatalogs(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getCatalogs();
        assertEquals("TABLE_CAT", rs.getMetaData().getColumnName(1));
    }

    private static void assertMetaTypeInfo(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getTypeInfo();

        assertEquals("TYPE_NAME", rs.getMetaData().getColumnName(1));
        assertEquals("DATA_TYPE", rs.getMetaData().getColumnName(2));

        assertEquals(Types.VARCHAR, // "TYPE_NAME",
                rs.getMetaData().getColumnType(1));
        assertEquals(Types.INTEGER, // "DATA_TYPE",
                rs.getMetaData().getColumnType(2));
    }

    private static void assertMetaUDT(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getUDTs(null, null, null, null);

        assertEquals("TYPE_CAT", rs.getMetaData().getColumnName(1));
        assertEquals("TYPE_SCHEM", rs.getMetaData().getColumnName(2));

        assertEquals(Types.VARCHAR, // "TYPE_CAT",
                rs.getMetaData().getColumnType(1));
        assertEquals(Types.VARCHAR, // "TYPE_SCHEM",
                rs.getMetaData().getColumnType(2));
    }

    private static void assertMetaTables(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rsNull = md.getTables(null, null, null, null);
        assertEquals(OlapDatabaseMetaData.TABLE_DESC_NAME[0], rsNull.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rsNull.getMetaData().getColumnType(1));

        final ResultSet rs = md.getTables("", "", "", EMPTY_STRING_ARRAY);
        assertEquals(OlapDatabaseMetaData.TABLE_DESC_NAME[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaSuperTables(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rsNull = md.getSuperTables(null, null, null);
        assertEquals(OlapDatabaseMetaData.SUPER_TABLE_COL_NAMES[3], rsNull.getMetaData().getColumnName(4));
        assertEquals(Types.VARCHAR, rsNull.getMetaData().getColumnType(4));

        final ResultSet rs = md.getSuperTables("", "", "");
        assertEquals(OlapDatabaseMetaData.SUPER_TABLE_COL_NAMES[3], rs.getMetaData().getColumnName(4));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(4));
    }

    private static void assertMetaProcedures(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rsNull = md.getProcedures(null, null, null);
        assertEquals(OlapDatabaseMetaData.PROCS_COL_NAMES[0], rsNull.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rsNull.getMetaData().getColumnType(1));

        final ResultSet rs = md.getProcedures("", "", "");
        assertEquals(OlapDatabaseMetaData.PROCS_COL_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaColumns(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rsNull = md.getColumns(null, null, null, null);
        assertEquals(OlapDatabaseMetaData.COLUMN_NAMES[0], rsNull.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rsNull.getMetaData().getColumnType(1));

        final ResultSet rs = md.getColumns("", "", "", "");
        assertEquals(OlapDatabaseMetaData.COLUMN_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaBestRowIdentifier(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getBestRowIdentifier(null, null, null, 0, false);
        assertEquals(OlapDatabaseMetaData.ROW_ID_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.INTEGER, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaPrimaryKeys(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getPrimaryKeys(null, null, null);
        assertEquals(OlapDatabaseMetaData.PRIMARY_KEY_CNAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaImportedKeys(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getImportedKeys(null, null, null);
        assertEquals(OlapDatabaseMetaData.IMPORTED_KEYS_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaIndexInfo(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getIndexInfo(null, null, null, false, false);
        assertEquals(OlapDatabaseMetaData.INDEX_INFO_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaExportedKeys(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getExportedKeys(null, null, null);
        assertEquals(OlapDatabaseMetaData.EXPORTED_KEYS_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaColumnPrivileges(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getColumnPrivileges(null, null, null, null);
        assertEquals(OlapDatabaseMetaData.COL_PRIV_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaTablePrivileges(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getTablePrivileges(null, null, null);
        assertEquals(OlapDatabaseMetaData.TABLE_PRIV_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(Types.VARCHAR, rs.getMetaData().getColumnType(1));
    }

    private static void assertMetaVersionColumns(final OlapDatabaseMetaData md) throws SQLException {
        final ResultSet rs = md.getVersionColumns(null, null, null);
        assertEquals(OlapDatabaseMetaData.VERSION_COL_NAMES[0], rs.getMetaData().getColumnName(1));
        assertEquals(OlapDatabaseMetaData.VERSION_COL_TYPES[0], rs.getMetaData().getColumnType(1));
        assertEquals(Types.INTEGER, rs.getMetaData().getColumnType(1));
    }



    public void testGetCatalogSeparator() throws Exception {
        assertEquals(".", createMock(XmlaConn.STANDARD_SERVER, null).getCatalogSeparator());

        //assertEquals("/", createMock(XmlaConn.SAP_BW_SERVER).getCatalogSeparator());
        assertEquals(".", createMock(XmlaConn.SAP_BW_SERVER, null).getCatalogSeparator());
    }

    public void testCreate() throws Exception {
        try {
            new OlapDatabaseMetaData(null);
            fail("Should have failed w/out XmlaConn");
        } catch (NullPointerException e) {
            assertEquals(null, e.getLocalizedMessage());
        }
    }

    public void testGetUserName() throws Exception {
        assertEquals(null, createMock(XmlaConn.STANDARD_SERVER, XmlaLoginTest.createMock(null, null)).getUserName());
        assertEquals("dummyUser", createMock(XmlaConn.STANDARD_SERVER, XmlaLoginTest.createMock("dummyUser", null)).getUserName());
    }

    public void testAddColumnTuple() throws Exception {
        final OlapResultSet mock = (OlapResultSet) createMock(XmlaConn.STANDARD_SERVER, null).getColumns("", "", "", "");

        final int expectedDataType = Types.BOOLEAN;
        final String expectedDataTypeName = OlapResultSetMetaData.TYPE_NAME_MAP.get(Integer.valueOf(expectedDataType));

        OlapDatabaseMetaData.addColumnTuple(mock, "mockCatalog", "mockSchema", "mockTableName", "mockColumnName",
                expectedDataType, "mockRemarks");

        assertTrue(mock.next());

        assertEquals("mockCatalog", mock.getString("TABLE_CAT"));
        assertEquals("mockSchema", mock.getString("TABLE_SCHEM"));
        assertEquals("mockTableName", mock.getString("TABLE_NAME"));
        assertEquals("mockColumnName", mock.getString("COLUMN_NAME"));

        assertEquals(expectedDataType, mock.getInt("DATA_TYPE"));
        assertEquals(expectedDataTypeName, mock.getString("TYPE_NAME"));
        assertEquals(expectedDataType, mock.getInt("SQL_DATA_TYPE"));
        assertEquals(expectedDataType, mock.getInt("SOURCE_DATA_TYPE"));

        assertEquals("mockRemarks", mock.getString("REMARKS"));

        // ensure numeric metatdata fields have a value to avoid SQuirrel errors reading column metadata
        assertEquals(-1, mock.getInt("COLUMN_SIZE"));
        assertEquals(-1, mock.getInt("BUFFER_LENGTH"));
        assertEquals(-1, mock.getInt("DECIMAL_DIGITS"));
        assertEquals(-1, mock.getInt("NUM_PREC_RADIX"));
        assertEquals(DatabaseMetaData.columnNoNulls, mock.getInt("NULLABLE"));
        assertEquals(-1, mock.getInt("SQL_DATETIME_SUB"));
        assertEquals(-1, mock.getInt("CHAR_OCTET_LENGTH"));
        assertEquals(-1, mock.getInt("ORDINAL_POSITION"));
        assertEquals("YES", mock.getString("IS_NULLABLE"));


        assertFalse(mock.next());
    }

    public void testGetSuperTypes() throws Exception {
        final OlapDatabaseMetaData md = createMock(XmlaConn.STANDARD_SERVER, null);
        OlapResultSetTest.assertEmptyResultSet(md.getSuperTypes(null, null, null));
    }
    public void testGetProcedureColumns() throws Exception {
        final OlapDatabaseMetaData md = createMock(XmlaConn.STANDARD_SERVER, null);
        OlapResultSetTest.assertEmptyResultSet(md.getProcedureColumns(null, null, null, null));
    }
    public void testGetCrossReference() throws Exception {
        final OlapDatabaseMetaData md = createMock(XmlaConn.STANDARD_SERVER, null);
        OlapResultSetTest.assertEmptyResultSet(md.getCrossReference(null, null, null, null, null, null));
    }
    public void testGetAttributes() throws Exception {
        final OlapDatabaseMetaData md = createMock(XmlaConn.STANDARD_SERVER, null);
        OlapResultSetTest.assertEmptyResultSet(md.getAttributes(null, null, null, null));
    }
}
