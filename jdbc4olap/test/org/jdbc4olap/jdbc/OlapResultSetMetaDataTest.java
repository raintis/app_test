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

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Dan Rollo
 * Date: Jul 9, 2008
 * Time: 12:29:56 PM
 */
public class OlapResultSetMetaDataTest extends TestCase {

    private static final int[] EMPTY_INT_ARRAY = new int[]{};


    public void testAddMetaData() throws Exception {
        final OlapResultSet rs = new OlapResultSet();

        assertMD(rs, null, null);
        try {
            assertMD(rs, OlapDatabaseMetaDataTest.EMPTY_STRING_ARRAY, null);
            fail("Bad args should fail");
        } catch (IllegalStateException e) {
            assertEquals("Array mismatch: columnNames.length: 0, columnTypes is null", e.getMessage());
        }
        try {
            assertMD(rs, null, EMPTY_INT_ARRAY);
            fail("Bad args should fail");
        } catch (IllegalStateException e) {
            assertEquals("Array mismatch: columnNames is null, columnTypes.length: 0", e.getMessage());
        }

        assertMD(rs, new String[] {"uno"}, new int[] {Types.VARCHAR});

        assertMD(rs, new String[] {"uno", "DOS"}, new int[] {Types.VARCHAR , Types.INTEGER});

        assertMD(rs, new String[] {"uno", "DOS", "tres"},  new int[] {Types.VARCHAR , Types.INTEGER, Types.BOOLEAN});
    }

    private static void assertMD(final OlapResultSet rs, final String[] colNames, final int[] colTypes) throws SQLException {
        OlapResultSetMetaData.setMetaData(rs, colNames, colTypes);
        assertResultSetMD(rs, colNames, colTypes);
    }

    private static void assertResultSetMD(final OlapResultSet rs, final String[] colNames, final int[] colTypes) throws SQLException {
        final ResultSetMetaData md = rs.getMetaData();

        if (colNames == null) {
            assertEquals(0, md.getColumnCount());
            return;
        }

        assertEquals(colNames.length, md.getColumnCount());

        for (int i = 0; i < colNames.length; i++) {
            assertEquals(colNames[i], md.getColumnName(i + 1));
            assertEquals(colNames[i], md.getColumnLabel(i + 1));
        }

        for (int i = 0; i < colTypes.length; i++) {

            final int actualColType = md.getColumnType(i + 1);
            assertEquals(colTypes[i], actualColType);

            final String msgMissingTypeMap = "Column Type with unknown mapping: " + actualColType + ", Should we add it?";
            assertEquals(msgMissingTypeMap, OlapResultSetMetaData.TYPE_NAME_MAP.get(Integer.valueOf(actualColType)), md.getColumnTypeName(i + 1));
            assertTrue(msgMissingTypeMap, OlapResultSetMetaData.TYPE_NAME_MAP.containsKey(Integer.valueOf(actualColType)));

            assertEquals(md.getColumnLabel(i + 1).length() + OlapResultSetMetaData.COL_DISPLAY_SIZE_PAD,
                    md.getColumnDisplaySize(i + 1));

            // @todo Check this
            md.getCatalogName(i + 1);
            md.getColumnClassName(i + 1);

            md.getPrecision(i + 1);
            md.getScale(i + 1);
            md.getSchemaName(i + 1);
            md.getTableName(i + 1);
        }
    }

    public void testAddMetaDataStringCols() throws Exception {
        final OlapResultSet rs = new OlapResultSet();

        assertMDStringCols(rs, null);
        assertMDStringCols(rs, OlapDatabaseMetaDataTest.EMPTY_STRING_ARRAY);
        assertMDStringCols(rs, new String[] {"uno"});
        assertMDStringCols(rs, new String[] {"uno", "DOS"});
        assertMDStringCols(rs, new String[] {"uno", "DOS", "tres"});
    }

    private static void assertMDStringCols(final OlapResultSet rs, final String[] colNames) throws SQLException {
        OlapResultSetMetaData.setMetaDataStringCols(rs, colNames);
        assertResultSetMD(rs, colNames, OlapResultSetMetaData.createStringColTypesForNames(colNames));
    }

    public void testCreate() throws Exception {
        final OlapResultSetMetaData md = new OlapResultSetMetaData(null);
        assertEquals(0, md.getColumnCount());
    }

    public void testGetColumnLabel() throws Exception {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, new String[] {"uno", "DOS"}, new int[] {Types.VARCHAR, Types.VARCHAR},
                new String[] {"unoLabel", null});
        final OlapResultSetMetaData md = (OlapResultSetMetaData) rs.getMetaData();
        assertEquals("uno", md.getColumnName(1));
        assertEquals("unoLabel", md.getColumnLabel(1));
        assertEquals("DOS", md.getColumnName(2));
        assertEquals("DOS", md.getColumnLabel(2));
    }
}
