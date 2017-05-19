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

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jdbc4olap.parsing.ASTCompleteTableColumnName;
import org.jdbc4olap.parsing.ASTFromAliasItem;
import org.jdbc4olap.parsing.ASTFromItem;
import org.jdbc4olap.parsing.ASTSQLRelOpExpr;
import org.jdbc4olap.parsing.ASTSQLRelationalExpression;
import org.jdbc4olap.parsing.ASTSelectAliasItem;
import org.jdbc4olap.parsing.ASTSelectItem;
import org.jdbc4olap.parsing.ASTSelectList;
import org.jdbc4olap.parsing.ASTSelectStar;
import org.jdbc4olap.parsing.ASTTableColumnName;
import org.jdbc4olap.parsing.ParseException;
import org.jdbc4olap.parsing.SimpleNode;
import org.jdbc4olap.parsing.SqlGrammar;

class QueryDefinitionExtractor {

    private static final String QUOTE_NAME_CHAR = "\"";
    private static final String QUOTE_EXPRESSION_CHAR = "'";

    static QueryDefinition getQueryDefinition (String sql) throws SQLException{
    	SimpleNode root=null;
    	SqlGrammar p = new SqlGrammar(new StringReader(sql));
		try {
			root =  p.QueryStatement();
		} catch (ParseException pe) {
			throw new SQLException(pe.getLocalizedMessage());
		}
		final SimpleNode selectClause = (SimpleNode) root.jjtGetChild(0);
		List<QueryColumn> selectList = new ArrayList<QueryColumn>(extractQueryFields(selectClause));

		final SimpleNode fromClause = (SimpleNode) root.jjtGetChild(1);
		List<QueryTable> fromList = new ArrayList<QueryTable>(extractQueryTables(fromClause));

		List<QueryFilter> whereList = new ArrayList<QueryFilter>();
		if (root.jjtGetNumChildren() > 2) {
			final SimpleNode whereClause = (SimpleNode) root.jjtGetChild(2);
			whereList = extractQueryFilters(whereClause);
		}
		return new QueryDefinition(selectList, fromList, whereList);
    }
    
    private static List<QueryColumn> extractQueryFields(final SimpleNode node) {
        final List<QueryColumn> list = new ArrayList<QueryColumn>();
        if (node instanceof ASTSelectItem) {
            QueryColumn field = new QueryColumn();
            final SimpleNode child = (SimpleNode) node.jjtGetChild(0);
            if (child instanceof ASTSelectStar) {
                //tableName.* or tableAlias.*
                final SimpleNode tableAlias = (SimpleNode) child.jjtGetChild(0);
                field.setField("*");
                field.setTableAlias(getUnquotedName(tableAlias.getText()));
            } else {
                final SimpleNode columnName;
                if (child instanceof ASTSelectAliasItem) {
                    //tableAlias.columnName
                    columnName = (SimpleNode) child.jjtGetChild(0);
                    field.setFieldAlias(((SimpleNode) child.jjtGetChild(1)).getText());
                } else {
                    //columnName
                    columnName = child;
                }
                field = readTableColumn(columnName, field);
            }
            list.add(field);
        } else {
            if (node instanceof ASTSelectList) {
                if ("*".equals(node.getText())) {
                    final QueryColumn field = new QueryColumn();
                    field.setField("*");
                    list.add(field);
                } else {
                    for (int j = 0; j < node.jjtGetNumChildren(); j++) {
                        final SimpleNode item = (SimpleNode) node.jjtGetChild(j);
                        list.addAll(extractQueryFields(item));
                    }
                }
            }
        }
        return list;
    }

    private static List<QueryTable> extractQueryTables(final SimpleNode node) {
        List<QueryTable> list = new ArrayList<QueryTable>();
        if (node instanceof ASTFromItem) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(0);
            QueryTable qTable = new QueryTable();
            SimpleNode tableReference;
            if (child instanceof ASTFromAliasItem) {
                tableReference = (SimpleNode) child.jjtGetChild(0);
                qTable.setTableAlias(((SimpleNode) child.jjtGetChild(1)).getText());
            } else {
                tableReference = child;
            }
            SimpleNode catalog = (SimpleNode) tableReference.jjtGetChild(0);
            SimpleNode schema = (SimpleNode) tableReference.jjtGetChild(1);
            SimpleNode table = (SimpleNode) tableReference.jjtGetChild(2);
            qTable.setCatalog(getUnquotedName(((SimpleNode) catalog.jjtGetChild(0)).getText()));
            qTable.setSchema(getUnquotedName(((SimpleNode) schema.jjtGetChild(0)).getText()));
            qTable.setTable(getUnquotedName(((SimpleNode) table.jjtGetChild(0)).getText()));
            list.add(qTable);
        } else {
            for (int j = 0; j < node.jjtGetNumChildren(); j++) {
                SimpleNode item = (SimpleNode) node.jjtGetChild(j);
                list.addAll(extractQueryTables(item));
            }
        }
        return list;
    }

    private static List<QueryFilter> extractQueryFilters(final SimpleNode node) {
        List<QueryFilter> list = new ArrayList<QueryFilter>();
        if (node instanceof ASTSQLRelationalExpression) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(0);
            QueryFilter qFilter = new QueryFilter();
            SimpleNode simpleExpr = (SimpleNode) child.jjtGetChild(0);
            if (simpleExpr.jjtGetNumChildren() > 0) {
                QueryColumn field = new QueryColumn();
                field = readTableColumn((SimpleNode) simpleExpr.jjtGetChild(0), field);
                qFilter.setLeftOp(field);
            } else {
                List<String> valList = new ArrayList<String>();
                valList.add(getUnquotedExpression(simpleExpr.getText()));
                qFilter.setLeftOp(valList);
            }
            if (child instanceof ASTSQLRelOpExpr) {
                SimpleNode relExpr = (SimpleNode) child.jjtGetChild(1);
                SimpleNode op = (SimpleNode) relExpr.jjtGetChild(0);
                qFilter.setOperator(op.getText());
                simpleExpr = (SimpleNode) relExpr.jjtGetChild(1);
                if (simpleExpr.jjtGetNumChildren() > 0) {
                    QueryColumn field = new QueryColumn();
                    field = readTableColumn((SimpleNode) simpleExpr.jjtGetChild(0), field);
                    qFilter.setRightOp(field);
                } else {
                    List<String> l = new ArrayList<String>();
                    l.add(getUnquotedExpression(simpleExpr.getText()));
                    qFilter.setRightOp(l);
                }
                list.add(qFilter);
            } else {
                SimpleNode inClause = (SimpleNode) child.jjtGetChild(1);
                SimpleNode exprList = (SimpleNode) inClause.jjtGetChild(0);
                List<String> l = new ArrayList<String>();
                QueryFilter filter = new QueryFilter();
                filter.setLeftOp(qFilter.getLeftOp());
                filter.setOperator("=");
                for (int i = 0; i < exprList.jjtGetNumChildren(); i++) {
                    SimpleNode item = (SimpleNode) exprList.jjtGetChild(i);
                    l.add(getUnquotedExpression(item.getText()));
                }
                filter.setRightOp(l);
                list.add(filter);

            }
        } else {
            for (int j = 0; j < node.jjtGetNumChildren(); j++) {
                SimpleNode item = (SimpleNode) node.jjtGetChild(j);
                list.addAll(extractQueryFilters(item));
            }
        }
        return list;
    }

    private static QueryColumn readTableColumn(final SimpleNode node, final QueryColumn field) {
        SimpleNode child = (SimpleNode) node.jjtGetChild(0);
        if (child instanceof ASTTableColumnName) {
            String fieldName = ((SimpleNode) child.jjtGetChild(1)).getText();
            field.setField(getUnquotedName(fieldName));
            String tableAliasName = ((SimpleNode) child.jjtGetChild(0)).getText();
            field.setTableAlias(getUnquotedName(tableAliasName));
        } else if (child instanceof ASTCompleteTableColumnName) {
            String fieldName = ((SimpleNode) child.jjtGetChild(1)).getText();
            field.setField(getUnquotedName(fieldName));
            SimpleNode tableReference = (SimpleNode) child.jjtGetChild(0);
            SimpleNode catalog = (SimpleNode) tableReference.jjtGetChild(0);
            SimpleNode schema = (SimpleNode) tableReference.jjtGetChild(1);
            SimpleNode table = (SimpleNode) tableReference.jjtGetChild(2);
            field.setCatalog(getUnquotedName(((SimpleNode) catalog.jjtGetChild(0)).getText()));
            field.setSchema(getUnquotedName(((SimpleNode) schema.jjtGetChild(0)).getText()));
            field.setTable(getUnquotedName(((SimpleNode) table.jjtGetChild(0)).getText()));
        } else {
            field.setField(getUnquotedName(child.getText()));
        }
        return field;
    }

    private static String getUnquotedName(final String name) {
        if (name.startsWith(QUOTE_NAME_CHAR) && name.endsWith(QUOTE_NAME_CHAR)) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }

    private static String getUnquotedExpression(final String name) {
        if (name.startsWith(QUOTE_EXPRESSION_CHAR) && name.endsWith(QUOTE_EXPRESSION_CHAR)) {
            return name.substring(1, name.length() - 1);
        }
        return name;
    }


}
