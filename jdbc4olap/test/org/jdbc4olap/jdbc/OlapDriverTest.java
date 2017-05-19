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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;

import org.jdbc4olap.xmla.XmlaLogin;

/**
 * @author Dan Rollo
 * Date: Jul 8, 2008
 * Time: 5:47:54 PM
 */
public final class OlapDriverTest extends TestCase {

    private static final File TEST_PROPS_FILE = new File("test.properties");
    private static final Properties TEST_PROPS = new Properties();
    static {
        final FileInputStream fis;
        try {
            fis = new FileInputStream(TEST_PROPS_FILE);
            try {
                TEST_PROPS.load(fis);
            } finally {
                fis.close();
            }

        } catch (IOException e) {
            System.out.println("Missing unit test properties file: " + TEST_PROPS_FILE.getAbsolutePath());
            System.out.println("Some unit tests will fail or be skipped.");
            e.printStackTrace();
        }
    }

    private static boolean isRunConnectionTests() {
        return !"false".equals(TEST_PROPS.getProperty("test.isRunConnectionTests"));
    }

    private static String getTestUrl() {
        return TEST_PROPS.getProperty("test.url");
    }

    private static String getTestUser() {
        return TEST_PROPS.getProperty("test.user");
    }

    private static String getTestPwd() {
        return TEST_PROPS.getProperty("test.pwd");
    }

    public static String getTestCatalog() {
        return TEST_PROPS.getProperty("test.catalog");
    }

    public static String getTestSchema() {
        return TEST_PROPS.getProperty("test.schema");
    }

    public static String getTestCube() {
        return TEST_PROPS.getProperty("test.cube");
    }

    private static String getTestTableAlias() {
        return TEST_PROPS.getProperty("test.tablealias");
    }

    public static String getOldDriverTestURL() {
        return OlapDriver.JDBC_URL_PREFIX + "http://"
                + getTestUser() + ":" + getTestPwd() + "@"
                + getTestUrl().substring("http://".length());
    }

    public static String getTestColumn() {
        return TEST_PROPS.getProperty("test.column");
    }


    private OlapDriver olapDriver;

    @Override
    protected void setUp() throws Exception {
        olapDriver = new OlapDriver();
    }


    public void testAcceptsURL() throws Exception {
        assertTrue(olapDriver.acceptsURL(OlapDriver.JDBC_URL_PREFIX));
        assertTrue(olapDriver.acceptsURL(OlapDriver.JDBC_URL_PREFIX + "x"));
        assertFalse(olapDriver.acceptsURL(OlapDriver.JDBC_URL_PREFIX.substring(0,
                OlapDriver.JDBC_URL_PREFIX.length() - 1)));
    }

    public void testGetDriverInfo() throws Exception {

        DriverPropertyInfo[] result = olapDriver.getPropertyInfo(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), null);
        assertDriverProps(result);

        result = olapDriver.getPropertyInfo(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), new Properties());
        assertDriverProps(result);

        final Properties info = new Properties();
        final String currentValue = "someUser";
        info.put(XmlaLogin.PROPERTY_NAME_USER, currentValue);
        result = olapDriver.getPropertyInfo(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), info);
        assertDriverProps(result);
        assertEquals(currentValue, result[0].value);

        info.clear();
        info.setProperty(XmlaLogin.PROPERTY_NAME_USER, getTestUser());
        info.setProperty(XmlaLogin.PROPERTY_NAME_PASSWORD, getTestPwd());

        DriverPropertyInfo[] driverInfo;
        try {
            driverInfo = olapDriver.getPropertyInfo(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), info);
        } catch (Exception e) {
            throw new Exception(printTestWarningServerAccesMsg(OlapDriverTest.class, this), e);
        }
        assertNotNull(driverInfo);
        try {
            assertEquals(0, driverInfo.length);
        } catch (Throwable t) {
            if (isRunConnectionTests()) {
                throw new Exception(printTestWarningServerAccesMsg(OlapDriverTest.class, this), t);
            } else {
                // skipping connection tests, so ignore and stop here
                printTestWarningServerAccesTestSkippedMsg(OlapDriverTest.class, this);
                return;
            }
        }
        assertEquals(null, result[1].value); // don't show current value of current password

        //info.setProperty(StandardPropertyManager.CATALOG, "someCatalog");
        try {
            driverInfo = olapDriver.getPropertyInfo(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), info);
        } catch (Exception e) {
            throw new Exception(printTestWarningServerAccesMsg(OlapDriverTest.class, this), e);
        }
        assertNotNull(driverInfo);
        assertEquals(0, driverInfo.length);
    }

    private static void assertDriverProps(final DriverPropertyInfo[] result) {
        int i = 0;
        assertEquals(XmlaLogin.PROPERTY_NAME_USER, result[i++].name);
        assertEquals(XmlaLogin.PROPERTY_NAME_PASSWORD, result[i++].name);
        //assertEquals(StandardPropertyManager.CATALOG, result[i++].name);
        //assertEquals(StandardPropertyManager.CUBE, result[i++].name);

        for (String[] olapVendor : OlapDriver.PROPERTY_VALUE_DRIVER_URL_PATTERNS) {
            assertEquals(OlapDriver.PROPERTY_NAME_DRIVER_URL_PATTERN, result[i].name);
            assertTrue(result[i++].description.contains(olapVendor[0]));
        }

        assertEquals(2 + OlapDriver.PROPERTY_VALUE_DRIVER_URL_PATTERNS.length, result.length);
    }


    private static final String QT = "\"";

//    public static final String SQL_SELECT_WITH_TBL_ALIAS = "SELECT "
//            + getTestTableAlias() + OlapDatabaseMetaData.CATALOG_SEPARATOR + QT + getTestCube() + OlapDatabaseMetaData.CATALOG_SEPARATOR + getTestColumn() + QT
//            + " FROM "
//            + QT + getTestCatalog() + QT + OlapDatabaseMetaData.CATALOG_SEPARATOR
//                    + QT + getTestCatalog() + "/" + getTestSchema() + QT + OlapDatabaseMetaData.CATALOG_SEPARATOR
//            + QT + getTestCube() + QT
//            + " " + getTestTableAlias();

    public static final String SQL_SELECT_WITH_TBL_ALIAS = "SELECT "
            + QT + getTestCube() + OlapDatabaseMetaData.CATALOG_SEPARATOR + getTestColumn() + QT
            + " FROM "
            + QT + getTestCatalog() + QT + OlapDatabaseMetaData.CATALOG_SEPARATOR
                    + QT + getTestCatalog() + "/" + getTestSchema() + QT + OlapDatabaseMetaData.CATALOG_SEPARATOR
            + QT + getTestCube() + QT
            + " " + getTestTableAlias(); // parse works with or w/out this alias here

    // @todo finish join sql
    public static final String SQL_SELECT_JOIN = "SELECT "
            + QT + getTestCube() + OlapDatabaseMetaData.CATALOG_SEPARATOR + getTestColumn() + QT
            + " FROM "
            + QT + getTestCatalog() + QT + OlapDatabaseMetaData.CATALOG_SEPARATOR
                    + QT + getTestCatalog() + "/" + getTestSchema() + QT + OlapDatabaseMetaData.CATALOG_SEPARATOR
            + QT + getTestCube() + QT;
            //+ " " + getTestTableAlias();

    // NOTE: This test attempts to hit a real olap database
    public void testTableAlias() throws Exception {
        final Properties info = new Properties();
        info.setProperty(XmlaLogin.PROPERTY_NAME_USER, getTestUser());
        info.setProperty(XmlaLogin.PROPERTY_NAME_PASSWORD, getTestPwd());

        final Connection conn;
        try {
            conn = olapDriver.connect(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), info);
        } catch (Exception e) {
            if (isRunConnectionTests()) {
                throw new Exception(printTestWarningServerAccesMsg(OlapDriverTest.class, this), e);
            } else {
                // skipping connection tests, so ignore and stop here
                printTestWarningServerAccesTestSkippedMsg(OlapDriverTest.class, this);
                return;
            }
        }

        try {
            assertNotNull(conn);
            assertFalse(conn.isClosed());

            final OlapStatement st = (OlapStatement) conn.createStatement();
            final OlapResultSet rs;
            try {
                rs = (OlapResultSet) st.executeQuery(SQL_SELECT_WITH_TBL_ALIAS);
            } catch (Throwable throwable) {
                throw new Exception("Error parsing SQL: " + SQL_SELECT_WITH_TBL_ALIAS, throwable);
            }
            assertTrue(rs.getData().size() > 0);
        } finally {
            assert conn != null;
            conn.close();
            assertTrue(conn.isClosed());
        }

    }

    // NOTE: This test attempts to hit a real olap database
    public void testConnect() throws Exception {
        final Properties info = new Properties();
        info.setProperty(XmlaLogin.PROPERTY_NAME_USER, getTestUser());
        info.setProperty(XmlaLogin.PROPERTY_NAME_PASSWORD, getTestPwd());

        Connection conn;
        try {
            conn = olapDriver.connect(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), info);
            //conn = olapDriver.connect(getOldDriverTestURL(), new Properties());
        } catch (Exception e) {
            if (isRunConnectionTests()) {
                throw new Exception(printTestWarningServerAccesMsg(OlapDriverTest.class, this), e);
            } else {
                // skipping connection tests, so ignore and stop here
                printTestWarningServerAccesTestSkippedMsg(OlapDriverTest.class, this);
                return;
            }
        }

//        final List<Object[]> allCatalogs;
//        final List<Object[]> allSchemas;
        try {
            assertNotNull(conn);

//            allCatalogs = ((OlapResultSet) conn.getMetaData().getCatalogs()).getData();
//            assertTrue("Does the test db have only 1 catalog? count: " + allCatalogs, allCatalogs.size() > 1);
//
//            allSchemas = ((OlapResultSet) conn.getMetaData().getSchemas()).getData();
//            assertTrue("Does the test db have only 1 cube? count: " + allSchemas, allSchemas.size() > 1);
//
//            assertTrue("Expected more schemas: " + allSchemas.size() + " that catalogs: " + allCatalogs.size(),
//                    allCatalogs.size() < allSchemas.size());

            assertFalse(conn.isClosed());
        } finally {
            assert conn != null;
            conn.close();
            assertTrue(conn.isClosed());
        }

        // try w/ a catalog
//        final List<Object[]> schemasInCatalog;
//        try {
//            info.setProperty(StandardPropertyManager.CATALOG, getTestCatalog());
//            conn = olapDriver.connect(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), info);
//            assertEquals(null, conn.getCatalog()); // @todo fix this?
//            final OlapResultSet cats = (OlapResultSet) conn.getMetaData().getCatalogs();
//            assertEquals(1, cats.getData().size());
//
//            schemasInCatalog = ((OlapResultSet) conn.getMetaData().getSchemas()).getData();
////            assertTrue("Does the test catalog have only 1 or no schemas? count: " + schemasInCatalog, schemasInCatalog.size() > 1);
//        } catch (Throwable t) {
//            throw new Exception(printTestWarningServerAccesMsg(OlapDriverTest.class, this), t);
//        }

        // try w/ a catalog and cube
//        try {
//            info.setProperty(StandardPropertyManager.CUBE, getTestCube());
//            conn = olapDriver.connect(OlapDriver.JDBC_URL_PREFIX + getTestUrl(), info);
//            final OlapResultSet schemas = (OlapResultSet) conn.getMetaData().getSchemas();
//            assertEquals(1, schemas.getData().size());
//        } catch (Throwable t) {
//            throw new Exception(printTestWarningServerAccesMsg(OlapDriverTest.class, this), t);
//        }

        // @todo Test reset/clear of schema cache after catalog changed with same connection object
    }

    private static String printTestWarningServerAccesMsg(final Class testClass, final TestCase test) {
        final String msg = "**** WARNING: The unit test: " + testClass.getName() + "." + test.getName()
                + "() \n\trequires access to a server. \nBe sure: "
                + TEST_PROPS_FILE.getAbsolutePath()
                + " \n\tis setup correctly AND the server is available on the network (vpn).\n"
                + "Be sure you can connect a web browser to: " + getTestUrl();
        System.out.println(msg + "\n");
        return msg;
    }

    private static void printTestWarningServerAccesTestSkippedMsg(final Class testClass, final TestCase test) {
        final String msg = "**** WARNING: The unit test: " + testClass.getName() + "." + test.getName()
                + "() \n\t is being skipped. This test requires access to a server. \nTo run these tests, be sure: "
                + TEST_PROPS_FILE.getAbsolutePath()
                + " \n\tis setup correctly (and set test.isRunConnectionTests=true) AND the server is available on the network (vpn).\n"
                + "Be sure you can connect a web browser to: " + getTestUrl();
        System.out.println(msg + "\n");
    }
}
