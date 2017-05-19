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

import java.sql.SQLException;
import java.net.URL;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Iterator;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;

/**
 * @author Dan Rollo
 * Date: Jul 8, 2008
 * Time: 5:54:26 PM
 */
public class XmlaConnTest extends TestCase {

    public static XmlaConn createMock(final int serverType, final XmlaLogin login) {
        return new XmlaConn(serverType, login) {

            public XmlaLogin getLogin() {
                return login;
            }

            private final NodeList emptyNodeList = new NodeList() {

                public Node item(final int index) {
                    return null;
                }

                public int getLength() {
                    return 0;
                }
            };

            public NodeList discoverProperties() throws SQLException {
                return emptyNodeList;
            }

            public NodeList getCatalogsNodeList() throws SQLException {
                return emptyNodeList;
            }
        };
    }

    public void testCreateMock() throws Exception {
        final XmlaConn mockXmlaConn = createMock(XmlaConn.STANDARD_SERVER, null);
        assertNotNull(mockXmlaConn);
        final NodeList nl = mockXmlaConn.discoverProperties();
        assertNotNull(nl);
        assertEquals(0, nl.getLength());
    }

    public void testAddHeaderUserPwd() throws Exception {
        XmlaConn.addHeaderUserPwd(null, null);

        final SOAPMessage mockSoapMessage = new SOAPMessage() {
            private final MimeHeaders mimeHeaders = new MimeHeaders();

            public void setContentDescription(final String s) { }
            public String getContentDescription() { return null; }
            public SOAPPart getSOAPPart() { return null; }
            public void removeAllAttachments() { }
            public int countAttachments() { return 0; }
            public Iterator getAttachments() { return null; }
            public Iterator getAttachments(final MimeHeaders mimeHeaders) { return null; }
            public void removeAttachments(final MimeHeaders mimeHeaders) { }
            public AttachmentPart getAttachment(final SOAPElement soapElement) throws SOAPException { return null; }
            public void addAttachmentPart(final AttachmentPart attachmentPart) { }
            public AttachmentPart createAttachmentPart() { return null; }
            public MimeHeaders getMimeHeaders() {
                return mimeHeaders;
            }
            public void saveChanges() throws SOAPException { }
            public boolean saveRequired() { return false; }
            public void writeTo(final OutputStream outputStream) throws SOAPException, IOException { }
        };

        XmlaConn.addHeaderUserPwd(new XmlaLogin(new Properties()), mockSoapMessage);
        //assertEquals(1, mockSoapMessage.getMimeHeaders().getHeader("Authorization").length);
        assertEquals("Should not add login header if 'user' is null",
                false, mockSoapMessage.getMimeHeaders().getAllHeaders().hasNext());

        // check user with no password
        mockSoapMessage.getMimeHeaders().removeAllHeaders();
        final Properties propsUserPwd = new Properties();
        propsUserPwd.setProperty(XmlaLogin.PROPERTY_NAME_USER, "someUser");
        XmlaConn.addHeaderUserPwd(new XmlaLogin(propsUserPwd), mockSoapMessage);
        assertEquals(1, mockSoapMessage.getMimeHeaders().getHeader("Authorization").length);
        assertEquals("Basic c29tZVVzZXI6bnVsbA==", mockSoapMessage.getMimeHeaders().getHeader("Authorization")[0]);

        // check user and password
        mockSoapMessage.getMimeHeaders().removeAllHeaders();
        propsUserPwd.setProperty(XmlaLogin.PROPERTY_NAME_USER, "someUser");
        propsUserPwd.setProperty(XmlaLogin.PROPERTY_NAME_PASSWORD, "somePwd");
        XmlaConn.addHeaderUserPwd(new XmlaLogin(propsUserPwd), mockSoapMessage);
        assertEquals(1, mockSoapMessage.getMimeHeaders().getHeader("Authorization").length);
        assertEquals("Basic c29tZVVzZXI6c29tZVB3ZA==", mockSoapMessage.getMimeHeaders().getHeader("Authorization")[0]);

        // check empty user and password
        mockSoapMessage.getMimeHeaders().removeAllHeaders();
        propsUserPwd.setProperty(XmlaLogin.PROPERTY_NAME_USER, "");
        propsUserPwd.setProperty(XmlaLogin.PROPERTY_NAME_PASSWORD, "somePwd");
        XmlaConn.addHeaderUserPwd(new XmlaLogin(propsUserPwd), mockSoapMessage);
        assertNull(mockSoapMessage.getMimeHeaders().getHeader("Authorization"));
    }

    public void testCreate() throws Exception {
        try {
            new XmlaConn(null, null);
            fail("Null args should fail");
        } catch (SQLException e) {
            assertTrue(e.getMessage().startsWith("Error in soap call: Bad endPoint type null"));
        }

        try {
            new XmlaConn(new URL("http://dummy"), null);
            fail("Bogus endpoint should fail");
        } catch (SQLException e) {
            assertTrue("Wrong err msg: " + e.getMessage(),
                    e.getMessage().contains("com.sun.xml.messaging.saaj.SOAPExceptionImpl: Invalid Content-Type:text/html. Is this an error message instead of a SOAP response?")
                    || e.getMessage().contains("Error in soap call: java.security.PrivilegedActionException: com.sun.xml.messaging.saaj.SOAPExceptionImpl: Message send failed")
                    // different package when running tests under jdk1.6:
                    // Error in soap call: java.security.PrivilegedActionException: com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl: Message send failed
                    || e.getMessage().contains(".messaging.saaj.SOAPExceptionImpl: Message send failed")

                    // PrivilegedActionException can also occur here
                    || e.getMessage().contains("java.security.PrivilegedActionException: com.sun.xml.messaging.saaj.SOAPExceptionImpl: Bad response: (403Forbidden")
                    || e.getMessage().contains("java.security.PrivilegedActionException: com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl: Bad response: (403Forbidden")
            );
        }
    }
}
