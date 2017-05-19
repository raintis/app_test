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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Dan Rollo
 * Date: Jul 9, 2008
 * Time: 12:36:17 PM
 */
public class OlapResultSetTest extends TestCase {

    public void testCreate() throws Exception {
        final OlapResultSet rs = new OlapResultSet();
        assertEquals(true, rs.isBeforeFirst());
        // @todo Restore when fixed
        //assertNull(rs.getMetaData());
    }

    public void testCreateEmptyResultSet() throws Exception {
        OlapResultSetTest.assertEmptyResultSet(OlapResultSet.createEmptyResultSet());
    }

    static void assertEmptyResultSet(final ResultSet rs) throws SQLException {
        assertNotNull("ResultSet should be empty, but it is <null>", rs);
        assertNotNull("Empty ResultSet is missing metadata.", rs.getMetaData());
        assertEquals("Empty ResultSet has incorrent columnCount in metadata.", 0, rs.getMetaData().getColumnCount());
    }
}
