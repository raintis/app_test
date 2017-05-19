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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.soap.SOAPMessage;

import org.jdbc4olap.xmla.Jdbc4OlapConstants;
import org.jdbc4olap.xmla.XmlaConn;

class StatementHelper {

	private static final Logger LOG = Logger.getLogger(Jdbc4OlapConstants.JDBC4OLAP_LOG);

	DatabaseMetaData metadata=null;
	XmlaConn xmlaConn=null;
	QueryDefinition qd=null;
	QueryMetaData qmd = null;
	
	StatementHelper(String sql, DatabaseMetaData olapMetadata, XmlaConn conn)throws SQLException{
		qd = QueryDefinitionExtractor.getQueryDefinition(sql);
		qmd = QueryMetaDataExtractor.getQueryMetaData(qd, olapMetadata, conn);
		xmlaConn=conn;
	}

	ResultSet processQuery() throws SQLException {
		String catalog = qmd.getCatalog();
		String schema = qmd.getSchema();
		final String measureName = xmlaConn.getMeasureName(catalog, schema);
	
		MdxGenerator mdxGenerator;
		try{
			mdxGenerator = new MdxGenerator(xmlaConn, qd, qmd, measureName);
		}catch (EmptyResultSetException erse){
			return OlapResultSet.createEmptyResultSet();
		}
		String mdx = mdxGenerator.getMdx();

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(mdx.toString());
		}

		/******************************/
		/* QUERY PROCESSING */
		/******************************/

		final SOAPMessage reply = xmlaConn.execute(catalog, mdx.toString());

		/******************************/
		/* RESULT PROCESSING */
		/******************************/
		
		return ResultSetProcessor.processResult(reply, xmlaConn, qmd, 
				mdxGenerator.getAxisCount(), measureName, mdxGenerator.getMeasureAxis());
	}

	ResultSetMetaData getMetaData(OlapResultSet owner) throws SQLException {
		return ResultSetProcessor.getResultSetMetaData(xmlaConn, owner, qmd);
	}
	
}
