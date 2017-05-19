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

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class OlapDataSourceFactory implements ObjectFactory {

    public Object getObjectInstance(final Object obj, final Name name, final Context ctx, final Hashtable env)
            throws Exception {

        final String odsClass = "org.jdbc4olap.jdbc.olapDataSource";
        final Reference ref = (Reference) obj;
        final String className = ref.getClassName();

        if (className != null && className.equals(odsClass)) {
            final OlapDataSource ods = new OlapDataSource();
            ods.setUrl((String) ref.get("url").getContent());
            ods.setUser((String) ref.get("user").getContent());
            ods.setPassword((String) ref.get("password").getContent());
            return ods;
        } else {
            return null;
        }
    }

}
