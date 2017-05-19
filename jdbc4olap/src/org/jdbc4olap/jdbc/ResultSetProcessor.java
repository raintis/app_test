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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.jdbc4olap.xmla.XmlaConn;
import org.jdbc4olap.xmla.XmlaTuple;
import org.w3c.dom.NodeList;

class ResultSetProcessor {

	private static final String MDD_URI = "urn:schemas-microsoft-com:xml-analysis:mddataset";
	static final String ROWS_URI = "urn:schemas-microsoft-com:xml-analysis:rowset";
	private static final String XMLA_URI = "urn:schemas-microsoft-com:xml-analysis";
	static final String XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
	
	static ResultSet processResult(SOAPMessage reply, XmlaConn xmlaConn, QueryMetaData qmd, 
			int axisCount, String measureName, int measureAxis){
		try {
			final SOAPPart sp = reply.getSOAPPart();
			final SOAPEnvelope envelope = sp.getEnvelope();
			final SOAPBody body = envelope.getBody();
			Name name;
			if (xmlaConn.getServerType() == XmlaConn.SAP_BW_SERVER) {
				name = envelope.createName("ExecuteResponse", "", XMLA_URI);
			} else {
				name = envelope.createName("ExecuteResponse", "m", XMLA_URI);
			}
			final SOAPElement eResponse = selectSingleNode(body, name);
			if (xmlaConn.getServerType() == XmlaConn.SAP_BW_SERVER) {
				name = envelope.createName("return", "", XMLA_URI);
			} else {
				name = envelope.createName("return", "m", XMLA_URI);
			}
			final SOAPElement eReturn = selectSingleNode(eResponse, name);
			name = envelope.createName("root", "", MDD_URI);
			final SOAPElement rootNode = selectSingleNode(eReturn, name);
			name = envelope.createName("Axes", "", MDD_URI);
			final SOAPElement axesNode = (SOAPElement) rootNode.getChildElements(name).next();

			// 1st pass to determine the resultsetSize multiplying the number of columns of each dimension
			name = envelope.createName("Axis", "", MDD_URI);
			Iterator axis = axesNode.getChildElements(name);
			final int axisSize = axesNode.getElementsByTagName("Axis").getLength();
			int nbCells = 1;
			int nbRows = 1;
			int currentAxis = 0;
			while (axis.hasNext()) {
				SOAPElement lAxis = (SOAPElement) axis.next();
				// don't count measure axis and slicer axis
				if (currentAxis != axisSize - 1) {
					name = envelope.createName("Tuples", "", MDD_URI);
					SOAPElement tuplesNode = (SOAPElement) lAxis
							.getChildElements(name).next();
					NodeList tuple = tuplesNode.getElementsByTagName("Tuple");
					nbCells *= tuple.getLength();
					if (currentAxis != measureAxis) {
						nbRows *= tuple.getLength();
					}
				}
				currentAxis += 1;
			}

			// preparation of the map containing results by field and of the ordered list of queried fields
			final HashMap<String, Object[]> resultData = new HashMap<String, Object[]>();
			// @todo Check this: contents of 'resultTypeMap' are updated, but never used
			final HashMap<String, Integer> resultTypeMap = new HashMap<String, Integer>();
			for (List<QueryColumn> list : qmd.getFieldMap().values()) {
				for (QueryColumn field : list) {
					resultData.put(field.getField(), new Object[nbRows]);
				}
			}

			// 2nd pass: for each dimension we extract the results and put them at the right location of the result array
			name = envelope.createName("Axis", "", MDD_URI);
			axis = axesNode.getChildElements(name);
			int dimCount = 1;
			currentAxis = 0;
			String[] measures;
			final Name tuplesName = envelope.createName("Tuples", "", MDD_URI);
			final Name tupleName = envelope.createName("Tuple", "", MDD_URI);
			final Name memberName = envelope.createName("Member", "", MDD_URI);
	        int totalTuplesSize = 1;

			while (axis.hasNext()) {
				final SOAPElement lAxis = (SOAPElement) axis.next();
				final SOAPElement tuplesNode = (SOAPElement) lAxis.getChildElements(tuplesName).next();
				final Iterator tuple = tuplesNode.getChildElements(tupleName);
				final int tuplesSize = tuplesNode.getElementsByTagName("Tuple").getLength();
				// SKIP SLICER AXIS
				if (currentAxis != axisSize - 1 && currentAxis < axisCount) {
					if (currentAxis == measureAxis) {
						// GESTION DES MESURES
						measures = new String[tuplesSize];
						int index = 0;
						while (tuple.hasNext()) {
							final SOAPElement lTuple = (SOAPElement) tuple.next();
							final SOAPElement lMember = (SOAPElement) lTuple.getChildElements(memberName).next();
							String uName = "";
							final Iterator memberIt = lMember.getChildElements();
							while (memberIt.hasNext()) {
								final Node n = (Node) memberIt.next();
								if (n instanceof SOAPElement) {
									SOAPElement el = (SOAPElement) n;
									if (el.getNodeName().equals("UName")) {
										uName = el.getValue();
									}
								}
							}
							measures[index] = uName;
							index += 1;
						}
						name = envelope.createName("CellData", "", MDD_URI);
						final SOAPElement cellData = (SOAPElement) rootNode.getChildElements(name).next();
						name = envelope.createName("Cell", "", MDD_URI);
						final Iterator cells = cellData.getChildElements(name);
						final Name cellOrdinal = envelope.createName("CellOrdinal", "", "");
						final Name valueName = envelope.createName("Value", "",	MDD_URI);
						final Name xsiType = envelope.createName("xsi:type", "", "");
						Integer ordinal;
						int fact, mod;
						String measure;
						int fieldType = -1;

						// FORMAT
						while (cells.hasNext()) {
							final SOAPElement cell = (SOAPElement) cells.next();
							ordinal = new Integer(cell.getAttributeValue(cellOrdinal));
							if (ordinal.intValue() < nbCells) {
			                    mod = (ordinal.intValue() / totalTuplesSize) % tuplesSize;
			                    fact = (ordinal.intValue() / (totalTuplesSize * tuplesSize)) * totalTuplesSize + ordinal.intValue() % totalTuplesSize;
								measure = measures[mod];
								final Object[] data = resultData.get(measure);
								try {
									SOAPElement valueCell = (SOAPElement) cell.getChildElements(valueName).next();
									if (fieldType == -1) {
										String type = valueCell.getAttributeValue(xsiType);
										if (type != null) {
											if (type.equalsIgnoreCase("xsd:boolean")) {
												fieldType = Types.BOOLEAN;
											} else if (type.equalsIgnoreCase("xsd:double")) {
												fieldType = Types.DOUBLE;
											} else if (type.equalsIgnoreCase("xsd:float")) {
												fieldType = Types.FLOAT;
											} else if (type.equalsIgnoreCase("xsd:int")) {
												fieldType = Types.INTEGER;
											} else {
												fieldType = Types.NUMERIC;
											}
										} else {
											fieldType = Types.NUMERIC;
										}
									}
									switch (fieldType) {
									case Types.BOOLEAN:
										Boolean b = Boolean.valueOf(valueCell.getValue());
										data[fact] = Integer.valueOf(b.equals(Boolean.TRUE) ? 1 : 0);
										break;
									case Types.DOUBLE:
										data[fact] = new Double(valueCell.getValue());
										break;
									case Types.FLOAT:
										data[fact] = new Float(valueCell.getValue());
										break;
									case Types.INTEGER:
										data[fact] = new Integer(valueCell.getValue());
										break;
									default:
										String val = valueCell.getValue();
										val = val.replace(" ", "");
										if (val.matches("[\\d]+\\,[\\d]+")) {
											val = val.replace(",", ".");
										}
										try {
											data[fact] = Double.valueOf(val);
										} catch (Exception e) {
											data[fact] = null;
										}
									}
								} catch (Exception e) {
									data[fact] = null;
								}
								resultTypeMap.put(measure, Integer.valueOf(fieldType));
								resultData.put(measure, data);
							}
						}
					} else {
			            totalTuplesSize *= tuplesSize;
						final HashMap<String, XmlaTuple[]> dimData = new HashMap<String, XmlaTuple[]>();
						int index = 0;
						while (tuple.hasNext()) {
							final SOAPElement lTuple = (SOAPElement) tuple.next();
							final SOAPElement lMember = (SOAPElement) lTuple.getChildElements(memberName).next();
							String lName = "";
							String uName = "";
							String caption = "";
							final Iterator memberIt = lMember.getChildElements();
							while (memberIt.hasNext()) {
								final Node n = (Node) memberIt.next();
								if (n instanceof SOAPElement) {
									SOAPElement lChild = (SOAPElement) n;
									if (lChild.getNodeName().equals("UName")) {
										uName = lChild.getValue();
									} else if (lChild.getNodeName().equals("LName")) {
										lName = lChild.getValue();
									} else if (lChild.getNodeName().equals("Caption")) {
										caption = lChild.getValue();
									}
								}
							}
							XmlaTuple[] tab = dimData.get(lName);
							if (tab == null) {
								tab = new XmlaTuple[tuplesSize];
							}
							final XmlaTuple tup = new XmlaTuple();
							tup.setField(lName);
							tup.setHierarchy(uName);
							tup.setValue(caption);
							tab[index] = tup;
							dimData.put(lName, tab);
							index++;
						}
						// if different levels from a dimension are querried, we have to identify hierarchies and fix them
						if (dimData.keySet().size() > 1) {
							// a map of ascendancy is maintained to avoid redundancy in the search process
							final HashMap<String, List<String>> parentMap = new HashMap<String, List<String>>();
							String bottomField = "";
							// for each field, we look for descendants
							for (String fieldA : dimData.keySet()) {
								final XmlaTuple[] tupA = dimData.get(fieldA);
								// list of non-empty/empty cell indexes for the processed field
								final List<Integer> listA = new ArrayList<Integer>();
								final List<Integer> listB = new ArrayList<Integer>();
								for (int i = 0; i < tupA.length; i++) {
									if (tupA[i] != null) {
										listA.add(Integer.valueOf(i));
									} else {
										listB.add(Integer.valueOf(i));
									}
								}
								List<String> parentListA = parentMap.get(fieldA);
								if (parentListA == null) {
									parentListA = new ArrayList<String>();
								}
								boolean isBottom = true;
								for (Integer indexA : listA) {
									final String hierarchyA = tupA[indexA.intValue()].getHierarchy();

									for (String fieldB : dimData.keySet()) {
										final XmlaTuple[] tupB = dimData.get(fieldB);
										boolean found = false;
										if (!fieldA.equals(fieldB)
												&& !parentListA.contains(fieldB)) {
											for (Integer indexB : listB) {
												if (tupB[indexB.intValue()] != null
														&& tupB[indexB.intValue()].getHierarchy().startsWith(hierarchyA)) {
													tupA[indexB.intValue()] = tupA[indexA.intValue()];
													found = true;
												}
											}
										}
										if (found) {
											List<String> parentListB = parentMap.get(fieldB);
											if (parentListB == null) {
												parentListB = new ArrayList<String>();
											}
											parentListB.add(fieldA);
											parentMap.put(fieldB, parentListB);
											isBottom = false;
										}
									}
								}
								if (isBottom) {
									bottomField = fieldA;
								}
							}
							if (!bottomField.equals("")) {
								final List<Integer> emptyCells = new ArrayList<Integer>();
								final XmlaTuple[] tupBottom = dimData.get(bottomField);
								for (int i = 0; i < tupBottom.length; i++) {
									if (tupBottom[i] == null) {
										emptyCells.add(Integer.valueOf(i));
									}
								}
								for (String field : dimData.keySet()) {
									final XmlaTuple[] tup = dimData.get(field);
									for (Integer i : emptyCells) {
										tup[i.intValue()] = null;
									}
								}
							}
						}

						// copy the results in the resultData Map
						for (String table : dimData.keySet()) {
							final Object[] data = resultData.get(table);
							final XmlaTuple[] tupTab = dimData.get(table);
							String value;
							final int max = data.length;
							// if (serverType==SAP_BW_SERVER)
							// max-=1;
							for (int i = 0; i < max; i++) {
								final int k = (i / dimCount) % tupTab.length;
								if (tupTab[k] != null) {
									value = tupTab[k].getValue();
									data[i] = value;
								}
							}
							resultData.put(table, data);
						}
						dimCount *= tuplesSize;
					}
				}
				currentAxis += 1;
			}
			int fieldCount = 0;
			for (List<QueryColumn> l : qmd.getFieldMap().values()) {
				fieldCount += l.size();
			}
			final String[] orderedFieldTable = new String[fieldCount];
			for (List<QueryColumn> list : qmd.getFieldMap().values()) {
				for (QueryColumn field : list) {
					orderedFieldTable[field.getRank()] = field.getField();
				}
			}

			final List<List<Integer>> fieldIndexByTable = new ArrayList<List<Integer>>();
			for (String table : qmd.getFieldMap().keySet()) {
				if (!table.equals(measureName)) {
					final List<Integer> tableList = new ArrayList<Integer>();
					for (QueryColumn field : qmd.getFieldMap().get(table)) {
						tableList.add(Integer.valueOf(field.getRank()));
					}
					fieldIndexByTable.add(tableList);
				}
			}

			final OlapResultSet rs = new OlapResultSet();
			Object[] dataList;
			for (int i = 0; i < nbRows; i++) {
				dataList = new Object[fieldCount];
				for (int j = 0; j < fieldCount; j++) {
					String field = orderedFieldTable[j];
					final Object[] data = resultData.get(field);
					dataList[j] = data[i];
				}
				boolean copy = true;
				for (List<Integer> tableIndex : fieldIndexByTable) {
					boolean hasData = false;
					for (Integer index : tableIndex) {
						if (dataList[index.intValue()] != null) {
							hasData = true;
						}
					}
					if (!hasData) {
						copy = false;
					}
				}
				if (copy) {
					rs.add(dataList);
				}
			}
			rs.setMetaData(getResultSetMetaData(xmlaConn, (OlapResultSet) rs, qmd));
			return rs;
		} catch (Exception e) {
			return null;
		}
	}

	static ResultSetMetaData getResultSetMetaData(XmlaConn xmlaConn, OlapResultSet owner, 
			QueryMetaData qmd) throws SQLException {

		final OlapResultSetMetaData md = new OlapResultSetMetaData(owner);

		// Integer type;
		int fieldCount = 0;
		for (List<QueryColumn> l : qmd.getFieldMap().values()) {
			fieldCount += l.size();
		}

		final QueryColumn[] orderedQueryFields = new QueryColumn[fieldCount];
		for (List<QueryColumn> list : qmd.getFieldMap().values()) {
			for (QueryColumn field : list) {
				orderedQueryFields[field.getRank()] = field;
			}
		}

		for (QueryColumn field : orderedQueryFields) {
			final OlapColumnMetaData column = new OlapColumnMetaData();
			column.setName(field.getField());
			column.setCatalogName(qmd.getCatalog());
			column.setSchemaName(qmd.getSchema());
			column.setTableName(field.getTable());
			// type = field.getType();
			// if (type==null)
			// type = new Integer(Types.VARCHAR);
			if (xmlaConn.getMeasureName(qmd.getCatalog(), qmd.getSchema()).equals(
					field.getTable())) {
				column.setType(Types.NUMERIC);
			} else {
				column.setType(Types.VARCHAR);
			}

			md.add(column);
		}
		return md;
	}
	private static SOAPElement selectSingleNode(final SOAPElement contextNode,
			final Name childName) {
		Iterator it = contextNode.getChildElements(childName);
		if (it.hasNext()) {
			return (SOAPElement) it.next();
		} else {
			return null;
		}
	}
		
}
