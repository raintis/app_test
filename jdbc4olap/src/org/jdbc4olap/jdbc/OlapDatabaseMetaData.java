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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaHelper;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 * @author Dan Rollo
 */
class OlapDatabaseMetaData implements DatabaseMetaData {

    private final OlapConnection olapConn;
    private final XmlaConn xmlaConn;
    private ArrayList<String[]> catalogsCache;
    private ArrayList<String[]> schemasCache;

    OlapDatabaseMetaData(final OlapConnection conn) {
        this.olapConn = conn;
        this.xmlaConn = olapConn.getXmlaConn();
    }

    public boolean allProceduresAreCallable() throws SQLException {
        return false;
    }

    public boolean allTablesAreSelectable() throws SQLException {
        return true;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return false;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return true;
    }

    public boolean deletesAreDetected(final int arg0) throws SQLException {
        return false;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return true;
    }

    public ResultSet getAttributes(final String arg0, final String arg1, final String arg2,
                                   final String arg3) throws SQLException {
        return OlapResultSet.createEmptyResultSet();
    }

//    * <LI><B>SCOPE</B> short => actual scope of result
//    *      <UL>
//    *      <LI> bestRowTemporary - very temporary, while using row
//    *      <LI> bestRowTransaction - valid for remainder of current transaction
//    *      <LI> bestRowSession - valid for remainder of current session
//    *      </UL>
//    * <LI><B>COLUMN_NAME</B> String => column name
//    * <LI><B>DATA_TYPE</B> int => SQL data type from java.sql.Types
//    * <LI><B>TYPE_NAME</B> String => Data source dependent type name,
//    *  for a UDT the type name is fully qualified
//    * <LI><B>COLUMN_SIZE</B> int => precision
//    * <LI><B>BUFFER_LENGTH</B> int => not used
//    * <LI><B>DECIMAL_DIGITS</B> short => scale
//    * <LI><B>PSEUDO_COLUMN</B> short => is this a pseudo column
//    *      like an Oracle ROWID
//    *      <UL>
//    *      <LI> bestRowUnknown - may or may not be pseudo column
//    *      <LI> bestRowNotPseudo - is NOT a pseudo column
//    *      <LI> bestRowPseudo - is a pseudo column
//    *      </UL>
    static final String[] ROW_ID_NAMES = new String[]{
            "SCOPE",
            "COLUMN_NAME",
            "DATA_TYPE",
            "TYPE_NAME",
            "COLUMN_SIZE",
            "BUFFER_LENGTH",
            "DECIMAL_DIGITS",
            "PSEUDO_COLUMN"
    };
    private static final int[] ROW_ID_TYPES = new int[]{
            Types.INTEGER, // "SCOPE",
            Types.VARCHAR, // "COLUMN_NAME",
            Types.INTEGER, // "DATA_TYPE",
            Types.VARCHAR, // "TYPE_NAME",
            Types.INTEGER, // "COLUMN_SIZE",
            Types.INTEGER, // "BUFFER_LENGTH",
            Types.INTEGER, // "DECIMAL_DIGITS",
            Types.INTEGER, // "PSEUDO_COLUMN"
    };

    public ResultSet getBestRowIdentifier(final String arg0, final String arg1,
                                          final String arg2, final int arg3, final boolean arg4) throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, ROW_ID_NAMES, ROW_ID_TYPES);
        return rs;
    }

    public ResultSet getCatalogs() throws SQLException {

        final OlapResultSet rsCatalogsCache = new OlapResultSet();
        for (String[] cat : getCatalogsCache()) {
            rsCatalogsCache.add(cat);
        }
        OlapResultSetMetaData.setMetaDataStringCols(rsCatalogsCache, new String[]{"TABLE_CAT"});
        return rsCatalogsCache;
    }

    static final String CATALOG_SEPARATOR = ".";

    public String getCatalogSeparator() throws SQLException {
        return CATALOG_SEPARATOR;
    }

    public String getCatalogTerm() throws SQLException {
        return "catalog";
    }

//    * <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
//    * <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
//    * <LI><B>TABLE_NAME</B> String => table name
//    * <LI><B>COLUMN_NAME</B> String => column name
//    * <LI><B>GRANTOR</B> => grantor of access (may be <code>null</code>)
//    * <LI><B>GRANTEE</B> String => grantee of access
//    * <LI><B>PRIVILEGE</B> String => name of access (SELECT,
//    *      INSERT, UPDATE, REFRENCES, ...)
//    * <LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted
//    *      to grant to others; "NO" if not; <code>null</code> if unknown
    static final String[] COL_PRIV_NAMES = new String[]{
            "TABLE_CAT",
            "TABLE_SCHEM",
            "TABLE_NAME",
            "COLUMN_NAME",
            "GRANTOR",
            "GRANTEE",
            "PRIVILEGE",
            "IS_GRANTABLE"
    };

    public ResultSet getColumnPrivileges(final String arg0, final String arg1, final String arg2,
                                         final String arg3) throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaDataStringCols(rs, COL_PRIV_NAMES);
        return rs;
    }

//    * <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
//    * <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
//    * <LI><B>TABLE_NAME</B> String => table name
//    * <LI><B>COLUMN_NAME</B> String => column name
//    * <LI><B>DATA_TYPE</B> int => SQL type from java.sql.Types
//    * <LI><B>TYPE_NAME</B> String => Data source dependent type name,
//    *  for a UDT the type name is fully qualified
//    * <LI><B>COLUMN_SIZE</B> int => column size.  For char or date
//    *     types this is the maximum number of characters, for numeric or
//    *     decimal types this is precision.
//    * <LI><B>BUFFER_LENGTH</B> is not used.
//    * <LI><B>DECIMAL_DIGITS</B> int => the number of fractional digits
//    * <LI><B>NUM_PREC_RADIX</B> int => Radix (typically either 10 or 2)
//    * <LI><B>NULLABLE</B> int => is NULL allowed.
//    *      <UL>
//    *      <LI> columnNoNulls - might not allow <code>NULL</code> values
//    *      <LI> columnNullable - definitely allows <code>NULL</code> values
//    *      <LI> columnNullableUnknown - nullability unknown
//    *      </UL>
//    * <LI><B>REMARKS</B> String => comment describing column (may be <code>null</code>)
//    * <LI><B>COLUMN_DEF</B> String => default value (may be <code>null</code>)
//    * <LI><B>SQL_DATA_TYPE</B> int => unused
//    * <LI><B>SQL_DATETIME_SUB</B> int => unused
//    * <LI><B>CHAR_OCTET_LENGTH</B> int => for char types the
//    *       maximum number of bytes in the column
//    * <LI><B>ORDINAL_POSITION</B> int => index of column in table
//    *      (starting at 1)
//    * <LI><B>IS_NULLABLE</B> String => "NO" means column definitely
//    *      does not allow NULL values; "YES" means the column might
//    *      allow NULL values.  An empty string means nobody knows.
//    *  <LI><B>SCOPE_CATLOG</B> String => catalog of table that is the scope
//    *      of a reference attribute (<code>null</code> if DATA_TYPE isn't REF)
//    *  <LI><B>SCOPE_SCHEMA</B> String => schema of table that is the scope
//    *      of a reference attribute (<code>null</code> if the DATA_TYPE isn't REF)
//    *  <LI><B>SCOPE_TABLE</B> String => table name that this the scope
//    *      of a reference attribure (<code>null</code> if the DATA_TYPE isn't REF)
//    *  <LI><B>SOURCE_DATA_TYPE</B> short => source type of a distinct type or user-generated
//    *      Ref type, SQL type from java.sql.Types (<code>null</code> if DATA_TYPE
//    *      isn't DISTINCT or user-generated REF)
    static final String[] COLUMN_NAMES = new String[]{
            "TABLE_CAT",
            "TABLE_SCHEM",
            "TABLE_NAME",
            "COLUMN_NAME",
            "DATA_TYPE",
            "TYPE_NAME",
            "COLUMN_SIZE",
            "BUFFER_LENGTH",
            "DECIMAL_DIGITS",
            "NUM_PREC_RADIX",
            "NULLABLE",
            "REMARKS",
            "COLUMN_DEF",
            "SQL_DATA_TYPE",
            "SQL_DATETIME_SUB",
            "CHAR_OCTET_LENGTH",
            "ORDINAL_POSITION",
            "IS_NULLABLE",
            "SCOPE_CATLOG",
            "SCOPE_SCHEMA",
            "SCOPE_TABLE",
            "SOURCE_DATA_TYPE"
    };
    static final int[] COLUMN_TYPES = new int[]{
            Types.VARCHAR, // "TABLE_CAT",
            Types.VARCHAR, // "TABLE_SCHEM",
            Types.VARCHAR, // "TABLE_NAME",
            Types.VARCHAR, // "COLUMN_NAME",
            Types.INTEGER, // "DATA_TYPE",
            Types.VARCHAR, // "TYPE_NAME",
            Types.INTEGER, // "COLUMN_SIZE",
            Types.INTEGER, // "BUFFER_LENGTH",
            Types.INTEGER, // "DECIMAL_DIGITS",
            Types.INTEGER, // "NUM_PREC_RADIX",
            Types.INTEGER, // "NULLABLE",
            Types.VARCHAR, // "REMARKS",
            Types.VARCHAR, // "COLUMN_DEF",
            Types.INTEGER, // "SQL_DATA_TYPE",
            Types.INTEGER, // "SQL_DATETIME_SUB",
            Types.INTEGER, // "CHAR_OCTET_LENGTH",
            Types.INTEGER, // "ORDINAL_POSITION",
            Types.VARCHAR, // "IS_NULLABLE",
            Types.VARCHAR, // "SCOPE_CATLOG",
            Types.VARCHAR, // "SCOPE_SCHEMA",
            Types.VARCHAR, // "SCOPE_TABLE",
            Types.INTEGER  // "SOURCE_DATA_TYPE"
    };

    public ResultSet getColumns(final String catalog, String schemaPattern, String tableNamePattern,
                                String columnNamePattern) throws SQLException {

        final OlapResultSet resRS = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(resRS, COLUMN_NAMES, COLUMN_TYPES);
        if (("".equals(catalog)) || ("".equals(schemaPattern)) || ("".equals(tableNamePattern)) || ("".equals(columnNamePattern))) {
            return resRS;
        }

        schemaPattern = translatePattern(schemaPattern);
        tableNamePattern = translatePattern(tableNamePattern);
        columnNamePattern = translatePattern(columnNamePattern);

        final List<String> catalogs = getCatalogsByName(catalog);
        XmlaHelper helper = new XmlaHelper();
        for (String cat : catalogs) {
            if (catalog == null || catalog.equals(cat)) {
                final String measureName = xmlaConn.getMeasureName(cat, schemaPattern);
                if (measureName != null && measureName.equals(tableNamePattern)) {
                    final NodeList measures = xmlaConn.getMembersNodeList(cat, schemaPattern, measureName);

                    final List<String> cubesPked = new ArrayList<String>();

                    for (int i = 0; i < measures.getLength(); i++) {
                        final Node item = measures.item(i);
                        String cube = "";
                        String measure = "";
                        String desc = "";

                        NodeList nl = item.getChildNodes();
                        for (int j = 0; j < nl.getLength(); j++) {
                            org.w3c.dom.Node node = nl.item(j);
                            final String nodeName = node.getNodeName();
                            final String nodeTextContent = helper.getTextContent(node);

                            if (nodeName.equals("CUBE_NAME")) {
                                cube = nodeTextContent;
                            } else if (nodeName.equals("MEMBER_UNIQUE_NAME")) {
                                measure = nodeTextContent;
                            } else if (nodeName.equals("MEMBER_CAPTION")) {
                                desc = nodeTextContent;
                            }
                        }

                        // @todo Determine "real" datatype for virtual key columns (using VARCHAR for now)
                        if (!cubesPked.contains(cube)) {
                            ResultSet lRS = getPrimaryKeys(cat, cube, measureName);
                            lRS.beforeFirst();
                            while (lRS.next()) {
                                addColumnTuple(resRS,
                                        lRS.getString(1), lRS.getString(2), lRS.getString(3), lRS.getString(4),
                                        // Check this, was missing data type before
                                        Types.VARCHAR,
                                        null);
                            }
                            lRS = getImportedKeys(cat, cube, measureName);
                            lRS.beforeFirst();
                            while (lRS.next()) {
                                addColumnTuple(resRS,
                                        lRS.getString(5), lRS.getString(6), lRS.getString(7), lRS.getString(8),
                                        // Check this, was missing data type before
                                        Types.VARCHAR,
                                        null);
                            }
                            cubesPked.add(cube);
                        }

                        addColumnTuple(resRS,
                                cat, cube, measureName, measure,
                                Types.NUMERIC, // @todo Check this
                                desc);
                    }

                } else {
                    // @todo Why Map here, where above a List is used???
                    final HashMap<String, List<String>> cubesPked = new HashMap<String, List<String>>();
                    List<String> tablesPked;

                    final NodeList levels = xmlaConn.getLevelsNodeList(cat, schemaPattern, tableNamePattern, columnNamePattern);

                    for (int i = 0; i < levels.getLength(); i++) {
                        final Node item = levels.item(i);
                        String cube = "";
                        String table = "";
                        String level = "";
                        String desc = "";

                        final NodeList nl = item.getChildNodes();
                        for (int j = 0; j < nl.getLength(); j++) {
                            org.w3c.dom.Node node = nl.item(j);
                            final String nodeName = node.getNodeName();
                            final String nodeTextContent = helper.getTextContent(node);

                            if (nodeName.equals("CUBE_NAME")) {
                                cube = nodeTextContent;
                            } else if (nodeName.equals(xmlaConn.getTableUniqueNameProperty())) {
                                table = nodeTextContent;
                            } else if (nodeName.equals("LEVEL_UNIQUE_NAME")) {
                                level = nodeTextContent;
                            } else if (nodeName.equals("LEVEL_CAPTION")) {
                                desc = nodeTextContent;
                            }
                        }

                        // @todo Determine "real" datatype for virtual key columns (using VARCHAR for now)
                        tablesPked = cubesPked.get(cube);
                        if (tablesPked == null) {
                            tablesPked = new ArrayList<String>();
                        }
                        if (!tablesPked.contains(table)) {
                            final ResultSet lRS = getPrimaryKeys(cat, cube, table);
                            lRS.beforeFirst();
                            while (lRS.next()) {
                                addColumnTuple(resRS,
                                        lRS.getString(1), lRS.getString(2), lRS.getString(3), lRS.getString(4),
                                        // Check this, was missing data type before
                                        Types.VARCHAR,
                                        null);
                            }
                            tablesPked.add(table);
                            cubesPked.put(cube, tablesPked);
                        }

                        addColumnTuple(resRS,
                                cat, cube, table, level, Types.VARCHAR, desc);
                    }
                }
            }
        }
        return resRS;
    }

    List<String> getCatalogsByName(final String namedCatalog) throws SQLException {
        final List<String> catalogs = new ArrayList<String>();
        if (namedCatalog == null) {

            for (String[] cachedCatalog : getCatalogsCache()) {
                catalogs.add(cachedCatalog[0]);
            }

        } else {
            catalogs.add(namedCatalog);
        }
        return catalogs;
    }

    static void addColumnTuple(final OlapResultSet resRS,
                               final String tableCatalog, final String tableSchema, final String tableName,
                               final String columnName, final int dataType,
                               final String remarks) {


        final Object[] val = new Object[22];
        val[0] = tableCatalog;
        val[1] = tableSchema;
        val[2] = tableName;
        val[3] = columnName;

        val[4] = Integer.valueOf(dataType);
        // @todo Check if mappings are valid
        val[5] = OlapResultSetMetaData.TYPE_NAME_MAP.get(Integer.valueOf(dataType));

        val[6] = Integer.valueOf(-1); // "COLUMN_SIZE"
        val[7] = Integer.valueOf(-1); // "BUFFER_LENGTH"
        val[8] = Integer.valueOf(-1); // "DECIMAL_DIGITS"
        val[9] = Integer.valueOf(-1); // "NUM_PREC_RADIX"
        // DatabaseMetaData.columnNoNulls: Indicates that the column might not allow <code>NULL</code> values.
        val[10] = Integer.valueOf(DatabaseMetaData.columnNoNulls); // "NULLABLE"

        val[11] = remarks;

        val[13] = Integer.valueOf(dataType); // same as DATA_TYPE

        val[14] = Integer.valueOf(-1); // "SQL_DATETIME_SUB"
        val[15] = Integer.valueOf(-1); // "CHAR_OCTET_LENGTH"
        val[16] = Integer.valueOf(-1); // "ORDINAL_POSITION"

        // "YES" means the column might allow NULL values.
        val[17] = "YES"; // "IS_NULLABLE"

        val[21] = Integer.valueOf(dataType); // same as DATA_TYPE


        resRS.add(val);
    }

    public Connection getConnection() throws SQLException {
        return olapConn;
    }

    public ResultSet getCrossReference(final String arg0, final String arg1, final String arg2,
                                       final String arg3, final String arg4, final String arg5) throws SQLException {
        return OlapResultSet.createEmptyResultSet();
    }

    public int getDatabaseMajorVersion() throws SQLException {
        return 0;
    }

    public int getDatabaseMinorVersion() throws SQLException {
        return 0;
    }

    public String getDatabaseProductName() throws SQLException {
        return xmlaConn.getDatabaseProductName();
    }

    public String getDatabaseProductVersion() throws SQLException {
        return xmlaConn.getDatabaseProductVersion();
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    public int getDriverMajorVersion() {
        return 1;
    }

    public int getDriverMinorVersion() {
        return 0;
    }

    public String getDriverName() throws SQLException {
        return "jdbc4olap Driver";
    }

    public String getDriverVersion() throws SQLException {
        return "1.0";
    }

//    * <LI><B>PKTABLE_CAT</B> String => primary key table catalog (may be <code>null</code>)
//    * <LI><B>PKTABLE_SCHEM</B> String => primary key table schema (may be <code>null</code>)
//    * <LI><B>PKTABLE_NAME</B> String => primary key table name
//    * <LI><B>PKCOLUMN_NAME</B> String => primary key column name
//    * <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be <code>null</code>)
//    *      being exported (may be <code>null</code>)
//    * <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be <code>null</code>)
//    *      being exported (may be <code>null</code>)
//    * <LI><B>FKTABLE_NAME</B> String => foreign key table name
//    *      being exported
//    * <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
//    *      being exported
//    * <LI><B>KEY_SEQ</B> short => sequence number within foreign key
//    * <LI><B>UPDATE_RULE</B> short => What happens to
//    *       foreign key when primary is updated:
//    *      <UL>
//    *      <LI> importedNoAction - do not allow update of primary
//    *               key if it has been imported
//    *      <LI> importedKeyCascade - change imported key to agree
//    *               with primary key update
//    *      <LI> importedKeySetNull - change imported key to <code>NULL</code> if
//    *               its primary key has been updated
//    *      <LI> importedKeySetDefault - change imported key to default values
//    *               if its primary key has been updated
//    *      <LI> importedKeyRestrict - same as importedKeyNoAction
//    *                                 (for ODBC 2.x compatibility)
//    *      </UL>
//    * <LI><B>DELETE_RULE</B> short => What happens to
//    *      the foreign key when primary is deleted.
//    *      <UL>
//    *      <LI> importedKeyNoAction - do not allow delete of primary
//    *               key if it has been imported
//    *      <LI> importedKeyCascade - delete rows that import a deleted key
//    *      <LI> importedKeySetNull - change imported key to <code>NULL</code> if
//    *               its primary key has been deleted
//    *      <LI> importedKeyRestrict - same as importedKeyNoAction
//    *                                 (for ODBC 2.x compatibility)
//    *      <LI> importedKeySetDefault - change imported key to default if
//    *               its primary key has been deleted
//    *      </UL>
//    * <LI><B>FK_NAME</B> String => foreign key name (may be <code>null</code>)
//    * <LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
//    * <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
//    *      constraints be deferred until commit
//    *      <UL>
//    *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
//    *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition
//    *      <LI> importedKeyNotDeferrable - see SQL92 for definition
    //    *      </UL>
    static final String[] EXPORTED_KEYS_NAMES = new String[]{
            "PKTABLE_CAT",
            "PKTABLE_SCHEM",
            "PKTABLE_NAME",
            "PKCOLUMN_NAME",
            "FKTABLE_CAT",
            "FKTABLE_SCHEM",
            "FKTABLE_NAME",
            "FKCOLUMN_NAME",
            "KEY_SEQ",
            "UPDATE_RULE",
            "DELETE_RULE",
            "FK_NAME",
            "PK_NAME",
            "DEFERRABILITY"
    };
    private static final int[] EXPORTED_KEYS_TYPES = new int[]{
            Types.VARCHAR, // "PKTABLE_CAT",
            Types.VARCHAR, // "PKTABLE_SCHEM",
            Types.VARCHAR, // "PKTABLE_NAME",
            Types.VARCHAR, // "PKCOLUMN_NAME",
            Types.VARCHAR, // "FKTABLE_CAT",
            Types.VARCHAR, // "FKTABLE_SCHEM",
            Types.VARCHAR, // "FKTABLE_NAME",
            Types.VARCHAR, // "FKCOLUMN_NAME",
            Types.INTEGER, // "KEY_SEQ",
            Types.INTEGER, // "UPDATE_RULE",
            Types.INTEGER, // "DELETE_RULE",
            Types.VARCHAR, // "FK_NAME",
            Types.VARCHAR, // "PK_NAME",
            Types.INTEGER  // "DEFERRABILITY"
    };

    public ResultSet getExportedKeys(final String arg0, final String arg1, final String arg2)
            throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, EXPORTED_KEYS_NAMES, EXPORTED_KEYS_TYPES);
        return rs;
    }

    public String getExtraNameCharacters() throws SQLException {
        return null;
    }

    public String getIdentifierQuoteString() throws SQLException {
        return "\"";
    }

//    * <LI><B>PKTABLE_CAT</B> String => primary key table catalog//      setCatalog(bckCat);

//    *      being imported (may be <code>null</code>)
//    * <LI><B>PKTABLE_SCHEM</B> String => primary key table schema
//    *      being imported (may be <code>null</code>)
//    * <LI><B>PKTABLE_NAME</B> String => primary key table name
//    *      being imported
//    * <LI><B>PKCOLUMN_NAME</B> String => primary key column name
//    *      being imported
//    * <LI><B>FKTABLE_CAT</B> String => foreign key table catalog (may be <code>null</code>)
//    * <LI><B>FKTABLE_SCHEM</B> String => foreign key table schema (may be <code>null</code>)
//    * <LI><B>FKTABLE_NAME</B> String => foreign key table name
//    * <LI><B>FKCOLUMN_NAME</B> String => foreign key column name
//    * <LI><B>KEY_SEQ</B> short => sequence number within a foreign key
//    * <LI><B>UPDATE_RULE</B> short => What happens to a
//    *       foreign key when the primary key is updated:
//    *      <UL>
//    *      <LI> importedNoAction - do not allow update of primary
//    *               key if it has been imported
//    *      <LI> importedKeyCascade - change imported key to agree
//    *               with primary key update
//    *      <LI> importedKeySetNull - change imported key to <code>NULL</code>
//    *               if its primary key has been updated
//    *      <LI> importedKeySetDefault - change imported key to default values
//    *               if its primary key has been updated
//    *      <LI> importedKeyRestrict - same as importedKeyNoAction
//    *                                 (for ODBC 2.x compatibility)
//    *      </UL>
//    * <LI><B>DELETE_RULE</B> short => What happens to
//    *      the foreign key when primary is deleted.
//    *      <UL>
//    *      <LI> importedKeyNoAction - do not allow delete of primary
//    *               key if it has been imported
//    *      <LI> importedKeyCascade - delete rows that import a deleted key
//    *      <LI> importedKeySetNull - change imported key to NULL if
//    *               its primary key has been deleted
//    *      <LI> importedKeyRestrict - same as importedKeyNoAction
//    *                                 (for ODBC 2.x compatibility)
//    *      <LI> importedKeySetDefault - change imported key to default if
//    *               its primary key has been deleted
//    *      </UL>
//    * <LI><B>FK_NAME</B> String => foreign key name (may be <code>null</code>)
//    * <LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
//    * <LI><B>DEFERRABILITY</B> short => can the evaluation of foreign key
//    *      constraints be deferred until commit
//    *      <UL>
//    *      <LI> importedKeyInitiallyDeferred - see SQL92 for definition
//    *      <LI> importedKeyInitiallyImmediate - see SQL92 for definition
//    *      <LI> importedKeyNotDeferrable - see SQL92 for definition
//    *      </UL>
    static final String[] IMPORTED_KEYS_NAMES = new String[]{
            "PKTABLE_CAT",
            "PKTABLE_SCHEM",
            "PKTABLE_NAME",
            "PKCOLUMN_NAME",
            "FKTABLE_CAT",
            "FKTABLE_SCHEM",
            "FKTABLE_NAME",
            "FKCOLUMN_NAME",
            "KEY_SEQ",
            "UPDATE_RULE",
            "DELETE_RULE",
            "FK_NAME",
            "PK_NAME",
            "DEFERRABILITY"
    };
    private static final int[] IMPORTED_KEYS_TYPES = new int[]{
            Types.VARCHAR, // "PKTABLE_CAT",
            Types.VARCHAR, // "PKTABLE_SCHEM",
            Types.VARCHAR, // "PKTABLE_NAME",
            Types.VARCHAR, // "PKCOLUMN_NAME",
            Types.VARCHAR, // "FKTABLE_CAT",
            Types.VARCHAR, // "FKTABLE_SCHEM",
            Types.VARCHAR, // "FKTABLE_NAME",
            Types.VARCHAR, // "FKCOLUMN_NAME",
            Types.INTEGER, // "KEY_SEQ",
            Types.INTEGER, // "UPDATE_RULE",
            Types.INTEGER, // "DELETE_RULE",
            Types.VARCHAR, // "FK_NAME",
            Types.VARCHAR, // "PK_NAME",
            Types.INTEGER  // "DEFERRABILITY"
    };

    public ResultSet getImportedKeys(final String catalog, final String schema, final String table)
            throws SQLException {

        final OlapResultSet resRS = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(resRS, IMPORTED_KEYS_NAMES, IMPORTED_KEYS_TYPES);

        if (table == null) {
            return resRS;
        }

        final List<String> catalogs = new ArrayList<String>();
        ResultSet lRS;
        if (catalog == null) {
            lRS = getCatalogs();
            lRS.beforeFirst();
            while (lRS.next()) {
                catalogs.add(lRS.getString(1));
            }
        } else {
            catalogs.add(catalog);
        }

        String tableTag = xmlaConn.getTableUniqueNameProperty();
        XmlaHelper helper = new XmlaHelper();
        for (String cat : catalogs) {
            if ((catalog == null || catalog.equals(cat)) && table.equals(xmlaConn.getMeasureName(cat, schema))) {
                final NodeList rows = xmlaConn.getTablesNodeList(cat, schema, null);

                for (int i = 0; i < rows.getLength(); i++) {
                    final Node item = rows.item(i);
                    String cube = "";
                    String tableName = "";
                    final NodeList nl = item.getChildNodes();
                    for (int j = 0; j < nl.getLength(); j++) {
                        final Node node = nl.item(j);
                        final String nodeName = node.getNodeName();
                        final String nodeTextContent = helper.getTextContent(node);
                        if (nodeName.equals("CUBE_NAME")) {
                            cube = nodeTextContent;
                        } else if (nodeName.equals(tableTag)) {
                            tableName = nodeTextContent;
                        }
                    }
                    if (!table.equals(tableName)) {
                        lRS = getPrimaryKeys(cat, cube, tableName);
                        lRS.beforeFirst();
                        while (lRS.next()) {
                            final Object[] val = new Object[14];
                            val[0] = cat;
                            val[1] = cube;
                            val[2] = tableName;
                            val[3] = lRS.getString(4);
                            val[4] = cat;
                            val[5] = cube;
                            val[6] = table;
                            val[7] = lRS.getString(4);
                            val[8] = Integer.valueOf(lRS.getInt(5));
                            val[9] = Integer.valueOf(DatabaseMetaData.importedKeyNoAction); // "importedKeyNoAction";
                            val[10] = Integer.valueOf(DatabaseMetaData.importedKeyNoAction); //  "importedKeyNoAction";
                            val[11] = null;
                            val[12] = null;
                            val[13] = Integer.valueOf(DatabaseMetaData.importedKeyNotDeferrable); // "importedKeyNotDeferrable";
                            resRS.add(val);
                        }
                    }
                }
            }
        }
        return resRS;
    }

//    * <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
//    * <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
//    * <LI><B>TABLE_NAME</B> String => table name
//    * <LI><B>NON_UNIQUE</B> boolean => Can index values be non-unique.
//    *      false when TYPE is tableIndexStatistic
//    * <LI><B>INDEX_QUALIFIER</B> String => index catalog (may be <code>null</code>);
//    *      <code>null</code> when TYPE is tableIndexStatistic
//    * <LI><B>INDEX_NAME</B> String => index name; <code>null</code> when TYPE is
//    *      tableIndexStatistic
//    * <LI><B>TYPE</B> short => index type:
//    *      <UL>
//    *      <LI> tableIndexStatistic - this identifies table statistics that are
//    *           returned in conjuction with a table's index descriptions
//    *      <LI> tableIndexClustered - this is a clustered index
//    *      <LI> tableIndexHashed - this is a hashed index
//    *      <LI> tableIndexOther - this is some other style of index
//    *      </UL>
//    * <LI><B>ORDINAL_POSITION</B> short => column sequence number
//    *      within index; zero when TYPE is tableIndexStatistic
//    * <LI><B>COLUMN_NAME</B> String => column name; <code>null</code> when TYPE is
//    *      tableIndexStatistic
//    * <LI><B>ASC_OR_DESC</B> String => column sort sequence, "A" => ascending,
//    *      "D" => descending, may be <code>null</code> if sort sequence is not supported;
//    *      <code>null</code> when TYPE is tableIndexStatistic
//    * <LI><B>CARDINALITY</B> int => When TYPE is tableIndexStatistic, then
//    *      this is the number of rows in the table; otherwise, it is the
//    *      number of unique values in the index.
//    * <LI><B>PAGES</B> int => When TYPE is  tableIndexStatisic then
//    *      this is the number of pages used for the table, otherwise it
//    *      is the number of pages used for the current index.
//    * <LI><B>FILTER_CONDITION</B> String => Filter condition, if any.
//    *      (may be <code>null</code>)
    static final String[] INDEX_INFO_NAMES = new String[]{
            "TABLE_CAT",
            "TABLE_SCHEM",
            "TABLE_NAME",
            "NON_UNIQUE",
            "INDEX_QUALIFIER",
            "INDEX_NAME",
            "TYPE",
            "ORDINAL_POSITION",
            "COLUMN_NAME",
            "ASC_OR_DESC",
            "CARDINALITY",
            "PAGES",
            "FILTER_CONDITION"
    };
    private static final int[] INDEX_INFO_TYPES = new int[]{
            Types.VARCHAR, // "TABLE_CAT",
            Types.VARCHAR, // "TABLE_SCHEM",
            Types.VARCHAR, // "TABLE_NAME",
            Types.BOOLEAN, // "NON_UNIQUE",
            Types.VARCHAR, // "INDEX_QUALIFIER",
            Types.VARCHAR, // "INDEX_NAME",
            Types.INTEGER, // "TYPE",
            Types.INTEGER, // "ORDINAL_POSITION",
            Types.VARCHAR, // "COLUMN_NAME",
            Types.VARCHAR, // "ASC_OR_DESC",
            Types.INTEGER, // "CARDINALITY",
            Types.INTEGER, // "PAGES",
            Types.VARCHAR  // "FILTER_CONDITION"
    };

    public ResultSet getIndexInfo(final String arg0, final String arg1, final String arg2,
                                  final boolean arg3, final boolean arg4) throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, INDEX_INFO_NAMES, INDEX_INFO_TYPES);
        return rs;
    }

    public int getJDBCMajorVersion() throws SQLException {
        return 4;
    }

    public int getJDBCMinorVersion() throws SQLException {
        return 0;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        return 0;
    }

    public int getMaxCatalogNameLength() throws SQLException {
        return 0;
    }

    public int getMaxCharLiteralLength() throws SQLException {
        return 0;
    }

    public int getMaxColumnNameLength() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInIndex() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInSelect() throws SQLException {
        return 0;
    }

    public int getMaxColumnsInTable() throws SQLException {
        return 0;
    }

    public int getMaxConnections() throws SQLException {
        return 0;
    }

    public int getMaxCursorNameLength() throws SQLException {
        return 0;
    }

    public int getMaxIndexLength() throws SQLException {
        return 0;
    }

    public int getMaxProcedureNameLength() throws SQLException {
        return 0;
    }

    public int getMaxRowSize() throws SQLException {
        return 0;
    }

    public int getMaxSchemaNameLength() throws SQLException {
        return 0;
    }

    public int getMaxStatementLength() throws SQLException {
        return 0;
    }

    public int getMaxStatements() throws SQLException {
        return 0;
    }

    public int getMaxTableNameLength() throws SQLException {
        return 0;
    }

    public int getMaxTablesInSelect() throws SQLException {
        return 0;
    }

    public int getMaxUserNameLength() throws SQLException {
        return 0;
    }

    public String getNumericFunctions() throws SQLException {
        return "";
    }

//    * <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
//    * <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
//    * <LI><B>TABLE_NAME</B> String => table name
//    * <LI><B>COLUMN_NAME</B> String => column name
//    * <LI><B>KEY_SEQ</B> short => sequence number within primary key
//    * <LI><B>PK_NAME</B> String => primary key name (may be <code>null</code>)
    static final String[] PRIMARY_KEY_CNAMES = new String[]{
            "TABLE_CAT",
            "TABLE_SCHEM",
            "TABLE_NAME",
            "COLUMN_NAME",
            "KEY_SEQ",
            "PK_NAME"
    };
    private static final int[] PRIMARY_KEY_CTYPES = new int[]{
            Types.VARCHAR, // "TABLE_CAT",
            Types.VARCHAR, // "TABLE_SCHEM",
            Types.VARCHAR, // "TABLE_NAME",
            Types.VARCHAR, // "COLUMN_NAME",
            Types.INTEGER, // "KEY_SEQ",
            Types.VARCHAR  // "PK_NAME"
    };

    public ResultSet getPrimaryKeys(final String catalog, final String schema, final String table) throws SQLException {
        final OlapResultSet resRS = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(resRS, PRIMARY_KEY_CNAMES, PRIMARY_KEY_CTYPES);
        if (table == null) {
            return resRS;
        }

        final List<String> catalogs = getCatalogsByName(catalog);

        XmlaHelper helper = new XmlaHelper();
        for (String cat : catalogs) {
            if (catalog == null || catalog.equals(cat)) {
                final NodeList rows = xmlaConn.getTablesNodeList(cat, schema, table);

                for (int i = 0; i < rows.getLength(); i++) {
                    final Node item = rows.item(i);
                    String cube = "";
                    String tableUniqueName = "";
                    String tableName = "";

                    final NodeList nl = item.getChildNodes();
                    for (int j = 0; j < nl.getLength(); j++) {
                        org.w3c.dom.Node node = nl.item(j);
                        final String nodeName = node.getNodeName();
                        final String nodeTextContent = helper.getTextContent(node);
                        if (nodeName.equals("CUBE_NAME")) {
                            cube = nodeTextContent;
                        } else if (nodeName.equals(xmlaConn.getTableUniqueNameProperty())) {
                            tableUniqueName = nodeTextContent;
                        } else if (nodeName.equals(xmlaConn.getTableNameProperty())) {
                            tableName = nodeTextContent;
                        }
                    }
                    final Object[] val = new Object[6];
                    val[0] = cat;
                    val[1] = cube;
                    val[2] = tableUniqueName;
                    val[3] = tableName + "_ID";
                    val[4] = Integer.valueOf(1);
                    val[5] = null;
                    resRS.add(val);
                }
            }
        }
        return resRS;
    }

    public ResultSet getProcedureColumns(final String arg0, final String arg1, final String arg2,
                                         final String arg3) throws SQLException {
        return OlapResultSet.createEmptyResultSet();
    }

    public String getProcedureTerm() throws SQLException {
        return null;
    }

//    * <LI><B>PROCEDURE_CAT</B> String => procedure catalog (may be <code>null</code>)
//    * <LI><B>PROCEDURE_SCHEM</B> String => procedure schema (may be <code>null</code>)
//    * <LI><B>PROCEDURE_NAME</B> String => procedure name
//    *  <LI> reserved for future use
//    *  <LI> reserved for future use
//    *  <LI> reserved for future use
//    * <LI><B>REMARKS</B> String => explanatory comment on the procedure
//    * <LI><B>PROCEDURE_TYPE</B> short => kind of procedure:
//    *      <UL>
//    *      <LI> procedureResultUnknown - May return a result
//    *      <LI> procedureNoResult - Does not return a result
//    *      <LI> procedureReturnsResult - Returns a result
//    *      </UL>
    static final String[] PROCS_COL_NAMES = new String[]{
            "PROCEDURE_CAT",
            "PROCEDURE_SCHEM",
            "PROCEDURE_NAME",
            "REMARKS",
            "PROCEDURE_TYPE",
    };
    private static final int[] PROCS_COL_TYPES = new int[]{
            Types.VARCHAR, // "PROCEDURE_CAT",
            Types.VARCHAR, // "PROCEDURE_SCHEM",
            Types.VARCHAR, // "PROCEDURE_NAME",
            Types.VARCHAR, // "REMARKS",
            Types.INTEGER  // "PROCEDURE_TYPE",
    };

    public ResultSet getProcedures(final String arg0, final String arg1, final String arg2)
            throws SQLException {

        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, PROCS_COL_NAMES, PROCS_COL_TYPES);
        return rs;
    }

    public int getResultSetHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    public String getSQLKeywords() throws SQLException {
        return null;
    }

    public int getSQLStateType() throws SQLException {
        return 0;
    }

    public String getSchemaTerm() throws SQLException {
        return "cube";
    }

    public ResultSet getSchemas() throws SQLException {

        // @todo Maybe remove schemaCache?
        // @todo Test reset/clear of schema cache after catalog changed
        if (schemasCache == null) {

            schemasCache = new ArrayList<String[]>();
            XmlaHelper helper = new XmlaHelper();
            for (String[] catalog : getCatalogsCache()) {
                final String cat = catalog[0];
                // setCatalog(cat);
                NodeList rows = xmlaConn.getCubesNodeList(cat);

                for (int i = 0; i < rows.getLength(); i++) {
                    final String[] val = new String[2];
                    //final String[] val = new String[3];
                    final Node item = rows.item(i);
                    val[0] = helper.getTextContent(item);
                    val[1] = cat;

                    /* @todo Attempt to get friendly names, but may not be useful?...
                    // get CUBE_CAPTION sybling if available
                    final NodeList captions = item.getParentElement().getElementsByTagName("CUBE_CAPTION");
                    if (captions != null && captions.getLength() > 0) {
                        // @todo Can captions.getLength() be > 1, if so, what should we do?
                        final Node ndCaption = (Node) captions.item(0);
                        val[2] = ndCaption.getValue();
                    }
                    */

                    schemasCache.add(val);
                }
            }

            // Ensure cache ordered by schema name.
            /*
            Collections.sort(schemasCache,
                    new Comparator<String[]>() {
                        public int compare(String[] o1, String[] o2) {
                            return o1[0].compareTo(o2[0]);
                        }
                    });
            //*/
        }

        final OlapResultSet rsSchemasCache = new OlapResultSet();
        for (String[] schema : schemasCache) {
            rsSchemasCache.add(schema);
        }
        OlapResultSetMetaData.setMetaDataStringCols(rsSchemasCache, new String[]{"TABLE_SCHEM", "TABLE_CATALOG"});

        //  setCatalog(bckCat);
        return rsSchemasCache;
    }

    private ArrayList<String[]> getCatalogsCache() throws SQLException {
        // ensure catalogs cache is populated
        if (catalogsCache == null) {
            catalogsCache = new ArrayList<String[]>();

            final NodeList nodeListCatalogs = xmlaConn.getCatalogsNodeList();
            XmlaHelper helper = new XmlaHelper();
            for (int i = 0; i < nodeListCatalogs.getLength(); i++) {
                final String[] val = new String[1];
                Node node = nodeListCatalogs.item(i);
                val[0] = helper.getTextContent(node);
                catalogsCache.add(val);
            }

            // Ensure cache ordered by catalog name.
            /*
            Collections.sort(catalogsCache,
                    new Comparator<String[]>() {
                        public int compare(String[] o1, String[] o2) {
                            return o1[0].compareTo(o2[0]);
                        }
                    });
            //*/
        }

        return catalogsCache;
    }

    public String getSearchStringEscape() throws SQLException {
        return null;
    }

    public String getStringFunctions() throws SQLException {
        return "";
    }

//    *  <LI><B>TABLE_CAT</B> String => the type's catalog (may be <code>null</code>)
//    *  <LI><B>TABLE_SCHEM</B> String => type's schema (may be <code>null</code>)
//    *  <LI><B>TABLE_NAME</B> String => type name
//    *  <LI><B>SUPERTABLE_NAME</B> String => the direct super type's name
    static final String[] SUPER_TABLE_COL_NAMES = new String[]{
            "TABLE_CAT",
            "TABLE_SCHEM",
            "TABLE_NAME",
            "SUPERTABLE_NAME"
    };

    public ResultSet getSuperTables(final String arg0, final String arg1, final String arg2) throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaDataStringCols(rs, SUPER_TABLE_COL_NAMES);
        return rs;
    }

    public ResultSet getSuperTypes(final String arg0, final String arg1, final String arg2)
            throws SQLException {
        return OlapResultSet.createEmptyResultSet();
    }

    public String getSystemFunctions() throws SQLException {
        return "";
    }

//    * <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
//    * <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
//    * <LI><B>TABLE_NAME</B> String => table name
//    * <LI><B>GRANTOR</B> => grantor of access (may be <code>null</code>)
//    * <LI><B>GRANTEE</B> String => grantee of access
//    * <LI><B>PRIVILEGE</B> String => name of access (SELECT,
//    *      INSERT, UPDATE, REFRENCES, ...)
//    * <LI><B>IS_GRANTABLE</B> String => "YES" if grantee is permitted
//    *      to grant to others; "NO" if not; <code>null</code> if unknown
    static final String[] TABLE_PRIV_NAMES = new String[]{
            "TABLE_CAT",
            "TABLE_SCHEM",
            "TABLE_NAME",
            "GRANTOR",
            "GRANTEE",
            "PRIVILEGE",
            "IS_GRANTABLE"
    };

    public ResultSet getTablePrivileges(final String arg0, final String arg1, final String arg2)
            throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaDataStringCols(rs, TABLE_PRIV_NAMES);
        return rs;
    }

    public ResultSet getTableTypes() throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        rs.add(new String[]{"TABLE"});

        OlapResultSetMetaData.setMetaDataStringCols(rs, new String[]{"TABLE_TYPE"});
        return rs;
    }

//    * <LI><B>TABLE_CAT</B> String => table catalog (may be <code>null</code>)
//    * <LI><B>TABLE_SCHEM</B> String => table schema (may be <code>null</code>)
//    * <LI><B>TABLE_NAME</B> String => table name
//    * <LI><B>TABLE_TYPE</B> String => table type.  Typical types are "TABLE",
//    *         "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
//    *         "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
//    * <LI><B>REMARKS</B> String => explanatory comment on the table
//    * <LI><B>TYPE_CAT</B> String => the types catalog (may be <code>null</code>)
//    * <LI><B>TYPE_SCHEM</B> String => the types schema (may be <code>null</code>)
//    * <LI><B>TYPE_NAME</B> String => type name (may be <code>null</code>)
//    * <LI><B>SELF_REFERENCING_COL_NAME</B> String => name of the designated
//    *                 "identifier" column of a typed table (may be <code>null</code>)
//    * <LI><B>REF_GENERATION</B> String => specifies how values in
//    *                  SELF_REFERENCING_COL_NAME are created. Values are
//    *                  "SYSTEM", "USER", "DERIVED". (may be <code>null</code>)
    static final String[] TABLE_DESC_NAME = new String[]{
            "TABLE_CAT",
            "TABLE_SCHEM",
            "TABLE_NAME",
            "TABLE_TYPE",
            "REMARKS",
            "TYPE_CAT",
            "TYPE_SCHEM",
            "TYPE_NAME",
            "SELF_REFERENCING_COL_NAME",
            "REF_GENERATION"
    };

    public ResultSet getTables(final String catalog, final String schemaPattern, final String tableNamePattern,
                               final String[] types) throws SQLException {
        final OlapResultSet resRS = new OlapResultSet();
        OlapResultSetMetaData.setMetaDataStringCols(resRS, TABLE_DESC_NAME);

        if (("".equals(catalog)) || ("".equals(schemaPattern)) || ("".equals(tableNamePattern))) {
            return resRS;
        }

        final List<String> catalogs = getCatalogsByName(catalog);
        XmlaHelper helper = new XmlaHelper();
        for (String cat : catalogs) {
            if (catalog == null || catalog.equals(cat)) {
                final NodeList rows = xmlaConn.getTablesNodeList(cat, translatePattern(schemaPattern), translatePattern(tableNamePattern));

                for (int i = 0; i < rows.getLength(); i++) {
                    final Node item = rows.item(i);
                    String cube = "";
                    String table = "";
                    String desc = "";
                    final NodeList nl = item.getChildNodes();

                    for (int j = 0; j < nl.getLength(); j++) {
                        org.w3c.dom.Node node = nl.item(j);
                        final String nodeName = node.getNodeName();
                        final String nodeTextContent = helper.getTextContent(node);

                        if (nodeName.equals("CUBE_NAME")) {
                            cube = nodeTextContent;
                        } else if (nodeName.equals(xmlaConn.getTableUniqueNameProperty())) {
                            table = nodeTextContent;
                        } else if (nodeName.equals("DESCRIPTION")) {
                            desc = nodeTextContent;
                        }
                    }
                    final String[] val = new String[10];
                    val[0] = cat;
                    val[1] = cube;
                    val[2] = table;
                    val[3] = "TABLE";
                    val[4] = desc;
                    resRS.add(val);
                }
            }
        }
        return resRS;
    }

    public String getTimeDateFunctions() throws SQLException {
        return "";
    }

//     * <LI><B>TYPE_NAME</B> String => Type name
//     * <LI><B>DATA_TYPE</B> int => SQL data type from java.sql.Types
//     * <LI><B>PRECISION</B> int => maximum precision
//     * <LI><B>LITERAL_PREFIX</B> String => prefix used to quote a literal
//     *      (may be <code>null</code>)
//     * <LI><B>LITERAL_SUFFIX</B> String => suffix used to quote a literal
//     (may be <code>null</code>)
//     * <LI><B>CREATE_PARAMS</B> String => parameters used in creating
//     *      the type (may be <code>null</code>)
//     * <LI><B>NULLABLE</B> short => can you use NULL for this type.
//     *      <UL>
//     *      <LI> typeNoNulls - does not allow NULL values
//     *      <LI> typeNullable - allows NULL values
//     *      <LI> typeNullableUnknown - nullability unknown
//     *      </UL>
//     * <LI><B>CASE_SENSITIVE</B> boolean=> is it case sensitive.
//     * <LI><B>SEARCHABLE</B> short => can you use "WHERE" based on this type:
//     *      <UL>
//     *      <LI> typePredNone - No support
//     *      <LI> typePredChar - Only supported with WHERE .. LIKE
//     *      <LI> typePredBasic - Supported except for WHERE .. LIKE
//     *      <LI> typeSearchable - Supported for all WHERE ..
//     *      </UL>
//     * <LI><B>UNSIGNED_ATTRIBUTE</B> boolean => is it unsigned.
//     * <LI><B>FIXED_PREC_SCALE</B> boolean => can it be a money value.
//     * <LI><B>AUTO_INCREMENT</B> boolean => can it be used for an
//     *      auto-increment value.
//     * <LI><B>LOCAL_TYPE_NAME</B> String => localized version of type name
//     *      (may be <code>null</code>)
//     * <LI><B>MINIMUM_SCALE</B> short => minimum scale supported
//     * <LI><B>MAXIMUM_SCALE</B> short => maximum scale supported
//     * <LI><B>SQL_DATA_TYPE</B> int => unused
//     * <LI><B>SQL_DATETIME_SUB</B> int => unused
//     * <LI><B>NUM_PREC_RADIX</B> int => usually 2 or 10

    private static final String[] TYPE_INFO_COL_NAMES = new String[]{
            "TYPE_NAME",
            "DATA_TYPE",
            "PRECISION",
            "LITERAL_PREFIX",
            "LITERAL_SUFFIX",
            "CREATE_PARAMS",
            "NULLABLE",
            "CASE_SENSITIVE",
            "SEARCHABLE",
            "UNSIGNED_ATTRIBUTE",
            "FIXED_PREC_SCALE",
            "AUTO_INCREMENT",
            "LOCAL_TYPE_NAME",
            "MINIMUM_SCALE",
            "MAXIMUM_SCALE",
            "SQL_DATA_TYPE",
            "SQL_DATETIME_SUB",
            "NUM_PREC_RADIX"
    };
    private static final int[] TYPE_INFO_COL_TYPES = new int[]{
            Types.VARCHAR, // "TYPE_NAME",
            Types.INTEGER, // "DATA_TYPE",
            Types.INTEGER, // "PRECISION",
            Types.VARCHAR, // "LITERAL_PREFIX",
            Types.VARCHAR, // "LITERAL_SUFFIX",
            Types.VARCHAR, // "CREATE_PARAMS",
            Types.INTEGER, // "NULLABLE",
            Types.BOOLEAN, // "CASE_SENSITIVE",
            Types.INTEGER, // "SEARCHABLE",
            Types.BOOLEAN, // "UNSIGNED_ATTRIBUTE",
            Types.BOOLEAN, // "FIXED_PREC_SCALE",
            Types.BOOLEAN, // "AUTO_INCREMENT",
            Types.VARCHAR, // "LOCAL_TYPE_NAME",
            Types.INTEGER, // "MINIMUM_SCALE",
            Types.INTEGER, // "MAXIMUM_SCALE",
            Types.INTEGER, // "SQL_DATA_TYPE",
            Types.INTEGER, // "SQL_DATETIME_SUB",
            Types.INTEGER, // "NUM_PREC_RADIX"
    };

    public ResultSet getTypeInfo() throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, TYPE_INFO_COL_NAMES, TYPE_INFO_COL_TYPES);
        return rs;
    }

//        * <LI><B>TYPE_CAT</B> String => the type's catalog (may be <code>null</code>)
//        * <LI><B>TYPE_SCHEM</B> String => type's schema (may be <code>null</code>)
//        * <LI><B>TYPE_NAME</B> String => type name
//        * <LI><B>CLASS_NAME</B> String => Java class name
//        * <LI><B>DATA_TYPE</B> int => type value defined in java.sql.Types.
//        *     One of JAVA_OBJECT, STRUCT, or DISTINCT
//        * <LI><B>REMARKS</B> String => explanatory comment on the type
//        * <LI><B>BASE_TYPE</B> short => type code of the source type of a
//        *     DISTINCT type or the type that implements the user-generated
//        *     reference type of the SELF_REFERENCING_COLUMN of a structured
//        *     type as defined in java.sql.Types (<code>null</code> if DATA_TYPE is not
//        *     DISTINCT or not STRUCT with REFERENCE_GENERATION = USER_DEFINED)

    private static final String[] UDT_COL_NAMES = new String[]{
            "TYPE_CAT",
            "TYPE_SCHEM",
            "TYPE_NAME",
            "CLASS_NAME",
            "DATA_TYPE",
            "REMARKS",
            "BASE_TYPE"
    };
    private static final int[] UDT_COL_TYPES = new int[]{
            Types.VARCHAR, // "TYPE_CAT",
            Types.VARCHAR, // "TYPE_SCHEM",
            Types.VARCHAR, // "TYPE_NAME",
            Types.VARCHAR, // "CLASS_NAME",
            Types.INTEGER, // "DATA_TYPE",
            Types.VARCHAR, // "REMARKS",
            Types.INTEGER, // "BASE_TYPE"
    };

    public ResultSet getUDTs(final String arg0, final String arg1, final String arg2, final int[] arg3)
            throws SQLException {

        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, UDT_COL_NAMES, UDT_COL_TYPES);
        return rs;
    }

    public String getURL() throws SQLException {
        return xmlaConn.getEndpoint().toString();
    }

    public String getUserName() throws SQLException {
        return xmlaConn.getLogin().getUserName();
    }

//    * <LI><B>SCOPE</B> short => is not used
//    * <LI><B>COLUMN_NAME</B> String => column name
//    * <LI><B>DATA_TYPE</B> int => SQL data type from <code>java.sql.Types</code>
//    * <LI><B>TYPE_NAME</B> String => Data source-dependent type name
//    * <LI><B>COLUMN_SIZE</B> int => precision
//    * <LI><B>BUFFER_LENGTH</B> int => length of column value in bytes
//    * <LI><B>DECIMAL_DIGITS</B> short => scale
//    * <LI><B>PSEUDO_COLUMN</B> short => whether this is pseudo column
//    *      like an Oracle ROWID
//    *      <UL>
//    *      <LI> versionColumnUnknown - may or may not be pseudo column
//    *      <LI> versionColumnNotPseudo - is NOT a pseudo column
//    *      <LI> versionColumnPseudo - is a pseudo column
//    *      </UL>
    static final String[] VERSION_COL_NAMES = new String[]{
            "SCOPE",
            "COLUMN_NAME",
            "DATA_TYPE",
            "TYPE_NAME",
            "COLUMN_SIZE",
            "BUFFER_LENGTH",
            "DECIMAL_DIGITS",
            "PSEUDO_COLUMN"
    };
    static final int[] VERSION_COL_TYPES = new int[]{
            Types.INTEGER, // "SCOPE",
            Types.VARCHAR, // "COLUMN_NAME",
            Types.INTEGER, // "DATA_TYPE",
            Types.VARCHAR, // "TYPE_NAME",
            Types.INTEGER, // "COLUMN_SIZE",
            Types.INTEGER, // "BUFFER_LENGTH",
            Types.INTEGER, // "DECIMAL_DIGITS",
            Types.INTEGER  // "PSEUDO_COLUMN"
    };

    public ResultSet getVersionColumns(final String arg0, final String arg1, final String arg2)
            throws SQLException {
        final OlapResultSet rs = new OlapResultSet();
        OlapResultSetMetaData.setMetaData(rs, VERSION_COL_NAMES, VERSION_COL_TYPES);
        return rs;
    }

    public boolean insertsAreDetected(final int arg0) throws SQLException {
        return false;
    }

    public boolean isCatalogAtStart() throws SQLException {
        return true;
    }

    public boolean isReadOnly() throws SQLException {
        return true;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        return false;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {
        return false;
    }

    public boolean othersDeletesAreVisible(final int arg0) throws SQLException {
        return false;
    }

    public boolean othersInsertsAreVisible(final int arg0) throws SQLException {
        return false;
    }

    public boolean othersUpdatesAreVisible(final int arg0) throws SQLException {
        return false;
    }

    public boolean ownDeletesAreVisible(final int arg0) throws SQLException {
        return false;
    }

    public boolean ownInsertsAreVisible(final int arg0) throws SQLException {
        return false;
    }

    public boolean ownUpdatesAreVisible(final int arg0) throws SQLException {
        return false;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        return false;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return false;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        return false;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return false;
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return false;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return true;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return true;
    }

    public boolean supportsColumnAliasing() throws SQLException {
        return true;
    }

    public boolean supportsConvert() throws SQLException {
        return false;
    }

    public boolean supportsConvert(final int arg0, final int arg1) throws SQLException {
        return false;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        return false;
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions()
            throws SQLException {
        return false;
    }

    public boolean supportsDataManipulationTransactionsOnly()
            throws SQLException {
        return false;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        return false;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        return false;
    }

    public boolean supportsGroupBy() throws SQLException {
        return false;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        return false;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return false;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        return false;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        return false;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return true;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return true;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        return false;
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        return false;
    }

    public boolean supportsNamedParameters() throws SQLException {
        return false;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        return false;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return false;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        return false;
    }

    public boolean supportsOuterJoins() throws SQLException {
        return false;
    }

    public boolean supportsPositionedDelete() throws SQLException {
        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        return false;
    }

    public boolean supportsResultSetConcurrency(final int arg0, final int arg1)
            throws SQLException {
        return (arg0 == ResultSet.TYPE_FORWARD_ONLY
                && arg1 == ResultSet.CONCUR_READ_ONLY);
    }

    public boolean supportsResultSetHoldability(final int arg0) throws SQLException {
        return false;
    }

    public boolean supportsResultSetType(final int arg0) throws SQLException {
        return arg0 == ResultSet.TYPE_FORWARD_ONLY;
    }

    public boolean supportsSavepoints() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        return true;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return false;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        return false;
    }

    public boolean supportsStatementPooling() throws SQLException {
        return false;
    }

    public boolean supportsStoredProcedures() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        return false;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return false;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        return false;
    }

    public boolean supportsTransactionIsolationLevel(final int arg0)
            throws SQLException {
        return false;
    }

    public boolean supportsTransactions() throws SQLException {
        return false;
    }

    public boolean supportsUnion() throws SQLException {
        return false;
    }

    public boolean supportsUnionAll() throws SQLException {
        return false;
    }

    public boolean updatesAreDetected(final int arg0) throws SQLException {
        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        return false;
    }

    public boolean usesLocalFiles() throws SQLException {
        return false;
    }

    private String translatePattern(final String pattern) {
        if ("%".equals(pattern)) {
            return null;
        } else {
            return pattern;
        }

        /*
        StringBuffer motif = new StringBuffer();
        if (pattern==null)
            return null;
        for (String part: pattern.split("[%]"))
            motif.append(part);
        return motif.toString();
        */
    }

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public RowIdLifetime getRowIdLifetime() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public ResultSet getClientInfoProperties() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern,
			String columnNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern,
			String columnNamePattern) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean generatedKeyAlwaysReturned() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}


}
