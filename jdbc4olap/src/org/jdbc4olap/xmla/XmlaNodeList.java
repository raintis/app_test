package org.jdbc4olap.xmla;

import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlaNodeList implements NodeList {

    private final ArrayList<Node> list;

    public int getLength() {
        return list.size();
    }

    public Node item(int index) {
        return list.get(index);
    }

    public XmlaNodeList() {
        list = new ArrayList<Node>();
    }

    public boolean add(Node node) {
        return list.add(node);
    }

    public boolean addAll(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            list.add(nodeList.item(i));
        }
        return nodeList.getLength() > 0;
    }

}
