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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.jdbc4olap.xmla.XmlaConnTest;
import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaLogin;

/**
 * @author Dan Rollo
 * Date: Jul 9, 2008
 * Time: 1:37:17 AM
 */
public class OlapConnectionTest extends TestCase {

    public static OlapConnection createMock(final int serverType, final XmlaLogin login)  {
        return new OlapConnection(XmlaConnTest.createMock(serverType, login));
    }

    public static OlapConnection createMock(final XmlaLogin login,
                                            final ArrayList<String[]> mockCatalogCache,
                                            final ArrayList<String[]> mockSchemaCache,
                                            final String mockTable, final String mockColumn)  {
        return new OlapConnection(XmlaConnTest.createMock(XmlaConn.STANDARD_SERVER, login)) {

            private final OlapDatabaseMetaData mockDatabaseMetaData = new OlapDatabaseMetaData(this) {

                @Override
                public ResultSet getCatalogs() throws SQLException {
                    final OlapResultSet rsCatalogsCache = new OlapResultSet();
                    for (String[] cat : mockCatalogCache) {
                        rsCatalogsCache.add(cat);
                    }
                    OlapResultSetMetaData.setMetaDataStringCols(rsCatalogsCache, new String[] {"TABLE_CAT"});
                    return rsCatalogsCache;
                }

                @Override
                public ResultSet getSchemas() throws SQLException {

                    final OlapResultSet rsSchemasCache = new OlapResultSet();
                    for (String[] schema : mockSchemaCache) {
                        rsSchemasCache.add(schema);
                    }
                    OlapResultSetMetaData.setMetaDataStringCols(rsSchemasCache, new String[]{"TABLE_SCHEM", "TABLE_CATALOG"});
                    return rsSchemasCache;
                }

                @Override
                public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern,
                                           final String[] types) throws SQLException {

                    final OlapResultSet resRS = new OlapResultSet();
                    OlapResultSetMetaData.setMetaDataStringCols(resRS, TABLE_DESC_NAME);
                    final String[] val = new String[TABLE_DESC_NAME.length];
                    //val[0] = cat;
                    //val[1] = cube;
                    val[2] = mockTable;
                    val[3] = "TABLE";
                    //val[4] = desc;
                    resRS.add(val);

                    return resRS;
                }

                @Override
                public ResultSet getColumns(final String catalog, final String schemaPattern, final String tableNamePattern,
                        final String columnNamePattern) throws SQLException {

                    final OlapResultSet resRS = new OlapResultSet();
                    OlapResultSetMetaData.setMetaData(resRS, COLUMN_NAMES, COLUMN_TYPES);

                    addColumnTuple(resRS,
                            null, //cat,
                            null, // cube,
                            mockTable, // table,
                            mockColumn, // level,
                            Types.VARCHAR,
                            null //desc
                    );

                    return resRS;
                }
            };


            @Override
            public DatabaseMetaData getMetaData() throws SQLException {
                return mockDatabaseMetaData;
            }

        };
    }

    public void testCreateMock() throws Exception {
        final OlapConnection olapConnection = createMock(XmlaConn.STANDARD_SERVER, null);
        assertFalse(olapConnection.isClosed());
        olapConnection.close();
        assertTrue(olapConnection.isClosed());
    }

    public void testCreateMockWithCatalogsSchemasTable() throws Exception {
        final ArrayList<String[]> mockCatalogCache = new ArrayList<String[]>();
        mockCatalogCache.add(new String[] {"mockCatalog"});

        final ArrayList<String[]> mockSchemaCache = new ArrayList<String[]>();
        mockSchemaCache.add(new String[] {"mockSchema"});

        final String mockTable = "mockTable";
        final String mockColumn = "mockColumn";

        final Connection olapConnection = createMock(null,
                mockCatalogCache, mockSchemaCache, mockTable, mockColumn);

        final DatabaseMetaData metaData = olapConnection.getMetaData();
        final ResultSet catalogs = metaData.getCatalogs();
        assertTrue(catalogs.next());
        assertEquals(mockCatalogCache.get(0)[0], catalogs.getString(1));

        final ResultSet schemas = metaData.getSchemas();
        assertTrue(schemas.next());
        assertEquals(mockSchemaCache.get(0)[0], schemas.getString(1));

        final ResultSet tables = metaData.getTables(null, null, mockTable, null);
        assertTrue(tables.next());
        assertEquals(mockTable, tables.getString(3));

        final ResultSet columns = metaData.getColumns(null, null, mockTable, mockColumn);
        assertTrue(columns.next());
        assertEquals(mockTable, columns.getString(3));
        assertEquals(mockColumn, columns.getString(4));

        assertFalse(olapConnection.isClosed());
        olapConnection.close();
        assertTrue(olapConnection.isClosed());
    }

    public void testCreate() throws Exception {
        try {
            new OlapConnection(null, null);
            fail("Null should have failed");
        } catch (Exception e) {
            assertTrue(e.getMessage().startsWith("Connection initialization error"));
        }
    }
}
