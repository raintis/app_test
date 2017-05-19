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

import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 */
interface PropertyManager {

	static final String PROPERTY_STREAM_COMPRESSION = "streamCompression";
	
    DriverPropertyInfo[] getDriverPropertyInfo() throws SQLException;

    XmlaProperties getXmlaProperties();

    String getDatabaseProductName() throws SQLException;

    String getDatabaseProductVersion() throws SQLException;

    void setCatalog(String cat);

    String getCatalog() throws SQLException;

    void setCube(String cube);

    String getCube() throws SQLException;

}
