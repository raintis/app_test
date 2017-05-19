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

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdbc4olap.xmla.Jdbc4OlapConstants;

public class OlapDriver extends OlapNonRegisteringDriver implements Driver {

    private static final Logger LOG = Logger.getLogger(Jdbc4OlapConstants.JDBC4OLAP_LOG);

    static {
        try {
            DriverManager.registerDriver(new OlapDriver());
        } catch (SQLException e) {
            LOG.info("Can't register driver!");
            LOG.log(Level.FINE, "Can't register driver", e);
            throw new RuntimeException("Can't register driver!");
        }
    }

}
