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

/**
 * @author Dan Rollo
 * Date: Jul 8, 2008
 * Time: 5:47:54 PM
 */
public class StandardPropertyManagerTest extends TestCase {

    public void testCreate() throws Exception {

        final StandardPropertyManager standardPropertyManager = new StandardPropertyManager(
                XmlaConnTest.createMock(XmlaConn.STANDARD_SERVER, null), null, null);
        assertEquals(null, standardPropertyManager.getXmlaProperties().getProperty("dummy"));
        assertEquals(1, standardPropertyManager.getDriverPropertyInfo().length);
        assertEquals(null, standardPropertyManager.getCatalog());
    }
}
