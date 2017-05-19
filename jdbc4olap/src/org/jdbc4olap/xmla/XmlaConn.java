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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
public class XmlaConn {

    private static final Logger LOG = Logger.getLogger(Jdbc4OlapConstants.JDBC4OLAP_LOG);

    public static final int STANDARD_SERVER = 0;
    private static final int MS_SSAS_SERVER = 1;
    public static final int SAP_BW_SERVER = 2;
    public static final int HYPERION_ESSBASE_SERVER = 3;
    private static final int MONDRIAN_SERVER = 4;

    private static final String MEASURE_TYPE = "2";

    private final URL endpoint;
    private final XmlaLogin login;
    private String requestType;
    private final int serverType;
    private String dataSourceInfo;
    private String databaseProductName;
    private boolean hierarchiesSupport=false;
    private final PropertyManager propertyManager;
    private final HashMap<String, String> measuresNamesCache;


    /**
     * Package visible no-arg constructor for unit testing only.
     *
     * @param serverType server type for unit test
     * @param login      mock login object
     */
    XmlaConn(final int serverType, final XmlaLogin login) {
        endpoint = null;
        this.login = login;
        this.serverType = serverType;
        propertyManager = null;
        measuresNamesCache = null;
    }

    public XmlaConn(final URL endpoint, final Properties info) throws SQLException {

        this.endpoint = endpoint;
        login = new XmlaLogin(info);
        if (login.getUserName() == null
                && (endpoint != null && !endpoint.toExternalForm().contains("@"))) {
            LOG.warning("WARNING: Login info missing?");
        }

        measuresNamesCache = new HashMap<String, String>();

        NodeList rows = discoverDatasource();

        Node item = rows.item(0);
        NodeList nl = item.getChildNodes();
        dataSourceInfo = "";
        String dataSourceName= "";
        databaseProductName = "";
        String dataSourceDescription = "";
        XmlaHelper helper = new XmlaHelper();
        for (int i = 0; i < nl.getLength(); i++) {
            org.w3c.dom.Node n = nl.item(i);
            final String nodeName = n.getNodeName();
            final String nodeTextContent = helper.getTextContent(n);
            if (nodeName.equals("DataSourceName")) {
                dataSourceName = nodeTextContent;
            } else if (nodeName.equals("ProviderName")) {
                databaseProductName = nodeTextContent;
            } else if (nodeName.equals("DataSourceDescription")) {
                dataSourceDescription = nodeTextContent;
            } else if (nodeName.equals("DataSourceInfo")) {
                dataSourceInfo = nodeTextContent;
            }
        }

        final String upperProductName = databaseProductName.toUpperCase();
        if (upperProductName.contains("MICROSOFT") || upperProductName.startsWith("MS")) {
            serverType = MS_SSAS_SERVER;
        } else if (upperProductName.contains("MONDRIAN")) {
            serverType = MONDRIAN_SERVER;
        } else if (upperProductName.contains("SAP")) {
            serverType = SAP_BW_SERVER;
        } else if (upperProductName.contains("ESSBASE")) {
            serverType = HYPERION_ESSBASE_SERVER;
        } else {
            throw new SQLException("Unknown XML/A provider");
        }
        if (serverType == SAP_BW_SERVER) {
            dataSourceInfo = "Provider=" + databaseProductName + ";DataSource=" + dataSourceDescription;
        } else if (serverType != HYPERION_ESSBASE_SERVER) {
            dataSourceInfo = dataSourceName;
        }

        propertyManager = new StandardPropertyManager(this, dataSourceInfo, info);

        if (serverType != HYPERION_ESSBASE_SERVER) {
	        rows = discoverSchemaRowsets();
	        for (int i = 0; i < rows.getLength(); i++) {
	            item = rows.item(i);
	            nl = item.getChildNodes();
	
	            for (int j = 0; j < nl.getLength(); j++) {
	                org.w3c.dom.Node node = nl.item(j);
	                final String nodeName = node.getNodeName();
	                final String nodeTextContent = helper.getTextContent(node);
	
	                if (nodeName.equals("SchemaName")
	                        && nodeTextContent.equalsIgnoreCase("MDSCHEMA_HIERARCHIES")) {
	
	                    hierarchiesSupport = true;
	                    break;
	                }
	            }
	
	            // no need to keep looking
	            if (hierarchiesSupport) {
	                break;
	            }
	        }
        }
    }

    public DriverPropertyInfo[] getDriverPropertyInfo() throws SQLException {
        return propertyManager.getDriverPropertyInfo();
    }

    public URL getEndpoint() {
        return endpoint;
    }

    public XmlaLogin getLogin() {
        return login;
    }

    public String getDatabaseProductName() throws SQLException {
        return databaseProductName == null ? propertyManager.getDatabaseProductName() : databaseProductName;
    }

    public String getDatabaseProductVersion() throws SQLException {
        return propertyManager.getDatabaseProductVersion();
    }

    private String getRequestType() {
        return requestType;
    }

    private void setRequestType(final String type) {
        requestType = type;
    }

    private void checkReply(final SOAPMessage reply, final SOAPBody sb) throws SQLException {
        if (sb.hasFault()) {
            SOAPFault sf = sb.getFault();
            StringBuffer errorMsg = new StringBuffer();
            errorMsg.append(sf.getFaultCode()).append(" : ").append(sf.getFaultString());
            if (sf.getFaultActor() != null) {
                errorMsg.append(" caused by ").append(sf.getFaultActor());
            }
            errorMsg.append(".\n");
            Detail detail = sf.getDetail();
            if (detail != null) {
                try {
                    final SOAPPart sp = reply.getSOAPPart();
                    final SOAPEnvelope envelope = sp.getEnvelope();
                    errorMsg.append(" Details : ");
                    final Iterator<SOAPElement> errorsIt;
                    switch (serverType) {
                        case MONDRIAN_SERVER:
                            final Name mondrianErrorName = envelope.createName("error", "XA", "http://mondrian.sourceforge.net");
                            final Name mondrianCodeName = envelope.createName("code", "", "");
                            final Name mondrianDescriptionName = envelope.createName("desc", "", "");
                            errorsIt = detail.getChildElements(mondrianErrorName);
                            while (errorsIt.hasNext()) {
                                SOAPElement error = errorsIt.next();
                                SOAPElement code = (SOAPElement) error.getChildElements(mondrianCodeName).next();
                                errorMsg.append(code.getValue()).append(" : ");
                                SOAPElement desc = (SOAPElement) error.getChildElements(mondrianDescriptionName).next();
                                errorMsg.append(desc.getValue());
                                errorMsg.append(".\n");
                            }
                            break;
                        case MS_SSAS_SERVER:
                        case HYPERION_ESSBASE_SERVER:
                            final Name msErrorName = envelope.createName("Error", "", "");
                            final Name msCodeName = envelope.createName("ErrorCode", "", "");
                            final Name msDescriptionName = envelope.createName("Description", "", "");
                            final Name msSourceName = envelope.createName("Source", "", "");
                            errorsIt = detail.getChildElements(msErrorName);
                            while (errorsIt.hasNext()) {
                                SOAPElement error = errorsIt.next();
                                errorMsg.append(error.getAttributeValue(msCodeName)).append(" : ");
                                errorMsg.append(error.getAttributeValue(msDescriptionName));
                                errorMsg.append(" ( ").append(error.getAttributeValue(msSourceName)).append(" ) ");
                                errorMsg.append(".\n");
                            }
                            break;
                    }
                } catch (SOAPException se) {
                    LOG.info(se.getMessage());
                }
            }
            LOG.info(errorMsg.toString());
            throw new SQLException(errorMsg.toString());
        }
    }


    private static final SOAPConnectionFactory SOAP_CONNECTION_FACTORY;

    static {
        try {
            SOAP_CONNECTION_FACTORY = SOAPConnectionFactory.newInstance();
        } catch (SOAPException e) {
            throw new RuntimeException("Error initing SOAPConnectionFactory.", e);
        }
    }

    static final SOAPFactory SOAP_FACTORY;

    static {
        try {
            SOAP_FACTORY = SOAPFactory.newInstance();
        } catch (SOAPException e) {
            throw new RuntimeException("Error initing SOAPFactory.", e);
        }
    }

    private SOAPElement discover(final XmlaProperties properties, final XmlaRestrictions restrictions) throws SQLException {
        try {
            final SOAPConnection connection = SOAP_CONNECTION_FACTORY.createConnection();
            final SOAPMessage message = getSoapMessage();
            final SOAPBody body = message.getSOAPBody();
            final SOAPBodyElement discover = body.addBodyElement(SOAP_FACTORY.createName("Discover", "", "urn:schemas-microsoft-com:xml-analysis"));
            discover.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
            final SOAPElement requestType = discover.addChildElement(SOAP_FACTORY.createName("RequestType"));
            requestType.addTextNode(getRequestType());
            discover.addChildElement(restrictions.getXMLA());
            properties.setProperty("Content", "SchemaData");
            properties.setProperty("Format", "Tabular");
            discover.addChildElement(properties.getXMLA());
            addHeaderUserPwd(login, message);
            
            if ("1".equals(properties.getProperty(PropertyManager.PROPERTY_STREAM_COMPRESSION))){
	            MimeHeaders mh = message.getMimeHeaders();
	            if (mh==null)
	            	mh=new MimeHeaders();
	            mh.setHeader("Accept-Encoding","gzip"); 
            }
            message.saveChanges();
            SOAPMessage reply;
            try {
                reply = connection.call(message, endpoint);
            } catch (SOAPException e) {
                LOG.info("Error in soap call: " + e.getLocalizedMessage());
                throw new SQLException("Error in soap call: " + e.getLocalizedMessage() + addCauseAndTrace(e));
            }
            boolean zipped = false;
            if ("1".equals(properties.getProperty(PropertyManager.PROPERTY_STREAM_COMPRESSION))){
            	MimeHeaders mh = reply.getMimeHeaders();
            	String[] values = mh.getHeader("content-encoding");
            	if (values != null) {
            		for (int i = 0;i < values.length;i++) {
            			if ("gzip".equals(values[i])) zipped=true;
            		}
            	}
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	reply.writeTo(baos);
            	ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            	InputStream is;
            	if(zipped){
            		is = new GZIPInputStream(bais);
            	} else {
            		is = bais;
            	}      
            	MessageFactory mf = MessageFactory.newInstance();
            	reply = mf.createMessage(null, is);
            }
            final SOAPPart sp = reply.getSOAPPart();
            final SOAPEnvelope se = sp.getEnvelope();
            final SOAPBody sb = se.getBody();
            checkReply(reply, sb);
            return sb;
        } catch (Exception e) {
            throw new SQLException(e.getLocalizedMessage() + addCauseAndTrace(e));
        }
    }

    static void addHeaderUserPwd(final XmlaLogin login, final SOAPMessage message) {
        if (login != null
                && login.getUserName() != null
                // don't add http auth header if username = "",
                // this allows only use of saaj style URL login (http://USER:PASSWORDD@HOST:PORT/...)
                && !"".equals(login.getUserName())) {
            final MimeHeaders hd = message.getMimeHeaders();
            hd.addHeader("Authorization", "Basic " + login.getAuthorization());
        }
    }

    public NodeList discoverProperties() throws SQLException {
        setRequestType("DISCOVER_PROPERTIES");
        XmlaProperties properties = new XmlaProperties();
        XmlaRestrictions restrictions = new XmlaRestrictions();
        final SOAPElement reply = discover(properties, restrictions);
        XmlaHelper helper = new XmlaHelper();
        return helper.getElementsByTagName(reply, "row");
    }

    NodeList discoverDatasource() throws SQLException {
        setRequestType("DISCOVER_DATASOURCES");
        XmlaProperties properties = new XmlaProperties();
        XmlaRestrictions restrictions = new XmlaRestrictions();
        restrictions.setProviderType("MDP");
        final SOAPElement reply = discover(properties, restrictions);
        XmlaHelper helper = new XmlaHelper();
        return helper.getElementsByTagName(reply, "row");
    }

    NodeList discoverSchemaRowsets() throws SQLException {
        setRequestType("DISCOVER_SCHEMA_ROWSETS");
        final XmlaProperties properties = new XmlaProperties();
        final XmlaRestrictions restrictions = new XmlaRestrictions();
        restrictions.setProviderType("MDP");
        final SOAPElement reply = discover(properties, restrictions);
        XmlaHelper helper = new XmlaHelper();
        return helper.getElementsByTagName(reply, "row");
    }

    public NodeList getCatalogsNodeList() throws SQLException {
        setRequestType("DBSCHEMA_CATALOGS");
        final XmlaProperties props = propertyManager.getXmlaProperties();
        props.setProperty("DataSourceInfo", dataSourceInfo);
        final XmlaRestrictions restrictions = new XmlaRestrictions();
        final SOAPElement reply = discover(props, restrictions);
        XmlaHelper helper = new XmlaHelper();
        return helper.getElementsByTagName(reply, "CATALOG_NAME");
    }

    public NodeList getCubesNodeList(final String catalog) throws SQLException {
        setRequestType("MDSCHEMA_CUBES");
        final XmlaProperties props = propertyManager.getXmlaProperties();
        final XmlaRestrictions restrictions = new XmlaRestrictions();
        restrictions.setCatalog(catalog);
        propertyManager.setCatalog(catalog);
        final SOAPElement reply = discover(props, restrictions);
        XmlaHelper helper = new XmlaHelper();
        return helper.getElementsByTagName(reply, "CUBE_NAME");
    }

    public NodeList getTablesNodeList(final String catalog, final String cube, final String table) {
        XmlaRestrictions restrictions = new XmlaRestrictions();
        restrictions.setCatalog(catalog);
        restrictions.setCubeName(cube);
        if (hierarchiesSupport) {
            setRequestType("MDSCHEMA_HIERARCHIES");
            restrictions.setHierarchyUniqueName(table);
        } else {
            setRequestType("MDSCHEMA_DIMENSIONS");
            restrictions.setDimensionUniqueName(table);
        }
        propertyManager.setCatalog(catalog);
        try {
            final SOAPElement reply = discover(propertyManager.getXmlaProperties(), restrictions);
            XmlaHelper helper = new XmlaHelper();
            return helper.getElementsByTagName(reply, "row");
        } catch (SQLException e) {
            LOG.info("Error getting tables for: catalog: " + catalog + ", cube: " + cube
                    + ", table: " + table);
            LOG.log(Level.FINE, "Error getting tables", e);
            return null;
        }
    }

    public NodeList getLevelsNodeList(final String catalog, final String cube, final String table, final String level) throws SQLException {
        setRequestType("MDSCHEMA_LEVELS");
        final XmlaRestrictions restrictions = new XmlaRestrictions();
        restrictions.setCatalog(catalog);
        restrictions.setCubeName(cube);
        restrictions.setLevelUniqueName(level);
        if (hierarchiesSupport) {
            restrictions.setHierarchyUniqueName(table);
        } else {
            restrictions.setDimensionUniqueName(table);
        }
        propertyManager.setCatalog(catalog);

        final SOAPElement reply = discover(propertyManager.getXmlaProperties(), restrictions);
        XmlaHelper helper = new XmlaHelper();
        return helper.getElementsByTagName(reply, "row");
    }

    public NodeList getMembersNodeList(final String catalog, final String cube, final String table)
            throws SQLException {

        setRequestType("MDSCHEMA_MEMBERS");
        final XmlaRestrictions restrictions = new XmlaRestrictions();
        restrictions.setCatalog(catalog);
        restrictions.setCubeName(cube);
        
//        restrictions.setLevelUniqueName(level);
//        restrictions.setMemberUniqueName(member);
        if (hierarchiesSupport) {
            restrictions.setHierarchyUniqueName(table);
        } else {
            restrictions.setDimensionUniqueName(table);
        }
        propertyManager.setCatalog(catalog);

        final SOAPElement reply = discover(propertyManager.getXmlaProperties(), restrictions);
        XmlaHelper helper = new XmlaHelper();
        return helper.getElementsByTagName(reply, "row");
    }

    /*
     public NodeList getMeasuresNodeList(final String catalog) throws SQLException {
         setRequestType("MDSCHEMA_MEASURES");
         final XmlaRestrictions restrictions = new XmlaRestrictions();
         restrictions.setCatalog(catalog);
         propertyManager.setCatalog(catalog);
         final SOAPElement reply = discover(propertyManager.getXmlaProperties(), restrictions);
         return null;
 //        XmlaHelper helper = new XmlaHelper();
 //        return helper.getElementsByTagName(reply,"row");
 //        return reply.getElementsByTagName("row");
     }
      */

    public String getTableUniqueNameProperty() {
        if (hierarchiesSupport) {
            return "HIERARCHY_UNIQUE_NAME";
        } else {
            return "DIMENSION_UNIQUE_NAME";
        }
    }

    public String getTableNameProperty() {
        if (hierarchiesSupport) {
            return "HIERARCHY_CAPTION";
        } else {
            return "DIMENSION_NAME";
        }
    }


    public String getMeasureName(final String catalog, final String cube) throws SQLException {
        String measure = measuresNamesCache.get(catalog);
        if (measure == null) {
            final NodeList rows = getTablesNodeList(catalog, cube, null);

            XmlaHelper helper = new XmlaHelper();
            for (int i = 0; i < rows.getLength(); i++) {
                final Node item = rows.item(i);
                String table = "";
                String dimType = "";
                final NodeList nl = item.getChildNodes();
                for (int j = 0; j < nl.getLength(); j++) {
                    org.w3c.dom.Node node = nl.item(j);
                    final String nodeName = node.getNodeName();
                    final String nodeTextContent = helper.getTextContent(node);

                    if (nodeName.equals(getTableUniqueNameProperty())) {
                        table = nodeTextContent;
                    } else if (nodeName.equals("DIMENSION_TYPE")) {
                        dimType = nodeTextContent;
                    }
                }
                if (dimType.equals(MEASURE_TYPE)) {
                    if (measure != null && !measure.equals(table)) {
                        throw new SQLException("Different Measure names found in cubes.");
                    }
                    measure = table;
                }
            }
            measuresNamesCache.put(catalog, measure);
        }
        return measure;
    }


    public SOAPMessage execute(final String catalog, final String request) throws SQLException {
        try {
            final SOAPConnection connection = SOAP_CONNECTION_FACTORY.createConnection();
            final SOAPMessage message = getSoapMessage();
            final SOAPBody body = message.getSOAPBody();
            final SOAPBodyElement execute = body.addBodyElement(SOAP_FACTORY.createName("Execute", "", "urn:schemas-microsoft-com:xml-analysis"));
            execute.setEncodingStyle("http://schemas.xmlsoap.org/soap/encoding/");
            final SOAPElement command = execute.addChildElement(SOAP_FACTORY.createName("Command"));
            final SOAPElement statement = command.addChildElement(SOAP_FACTORY.createName("Statement"));
            statement.addTextNode(request);
            propertyManager.setCatalog(catalog);
            final XmlaProperties properties = propertyManager.getXmlaProperties();
            properties.setProperty("Format", "Multidimensional");
            properties.setProperty("Content", "Data");
            properties.setProperty("AxisFormat", "TupleFormat");
            execute.addChildElement(properties.getXMLA());
            properties.setProperty("AxisFormat", "");
            addHeaderUserPwd(login, message);
            if ("1".equals(properties.getProperty(PropertyManager.PROPERTY_STREAM_COMPRESSION))){
	            MimeHeaders mh = message.getMimeHeaders();
	            if (mh==null)
	            	mh=new MimeHeaders();
	            mh.setHeader("Accept-Encoding","gzip"); 
            }
            message.saveChanges();
    		Long timeBefore= Long.valueOf(System.nanoTime());
    		SOAPMessage reply = connection.call(message, endpoint);
            Long timeAfter = Long.valueOf(System.nanoTime());
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	reply.writeTo(baos);
            if (LOG.isLoggable(Level.INFO)) {
        		long d = timeAfter.longValue() - timeBefore.longValue();
            	LOG.info("Query processed by OLAP server in " + (d / 1000000) +  " ms, returning " + baos.size() +  " bytes.\n");
            }
            boolean zipped = false;
            if ("1".equals(properties.getProperty(PropertyManager.PROPERTY_STREAM_COMPRESSION))){
            	MimeHeaders mh = reply.getMimeHeaders();
            	String[] values = mh.getHeader("content-encoding");
            	if (values != null) {
            		for (int i = 0;i < values.length;i++) {
            			if ("gzip".equals(values[i])) zipped=true;
            		}
            	}
            	ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            	InputStream is;
            	if(zipped){
            		is = new GZIPInputStream(bais);
            	} else {
            		is = bais;
            	}      
            	MessageFactory mf = MessageFactory.newInstance();
            	reply = mf.createMessage(null, is);
            }
            if (LOG.isLoggable(Level.FINE)) {
            	baos = new ByteArrayOutputStream();
            	reply.writeTo(baos);
            	LOG.fine(baos.toString());
            }
            final SOAPPart sp = reply.getSOAPPart();
            final SOAPEnvelope se = sp.getEnvelope();
            final SOAPBody sb = se.getBody();
            checkReply(reply, sb);
            return reply;
        } catch (Exception e) {
            throw new SQLException(e.getMessage() + addCauseAndTrace(e));
        }
    }

    public int getServerType() {
        return serverType;
    }

    private static final MessageFactory MESSAGE_FACTORY;

    static {
        try {
            MESSAGE_FACTORY = MessageFactory.newInstance();
        } catch (SOAPException e) {
            throw new RuntimeException("Error initing MessageFactory.", e);
        }
    }

    private SOAPMessage getSoapMessage() throws SOAPException {
        SOAPMessage message = MESSAGE_FACTORY.createMessage();
        SOAPHeader soapHeader = message.getSOAPHeader();
        soapHeader.detachNode();
        return message;
    }


    /**
     * @param throwable the throwable who's stack trace we want to extract
     * @return the stack trace of the given throwable
     * @deprecated Only exists until a better logging approach is implemented.
     */
    private static String getStackTrace(final Throwable throwable) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(baos);
        try {
            throwable.printStackTrace(pw);
        } finally {
            pw.close();
        }
        return baos.toString();
    }

    /**
     * @param throwable the throwable who's cause we want to extract
     * @return the cause of the given throwable
     * @deprecated Only exists until a better logging approach is implemented.
     */
    private static String addCauseAndTrace(final Throwable throwable) {
        final Throwable cause = throwable.getCause();
        return "\nCaused by: " + (cause == null ? "unknown"
                : cause.getLocalizedMessage() + "\nCause Stack Trace: " + getStackTrace(cause));
    }

}
