package org.jdbc4olap.xmla;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlaHelper {

    public NodeList getElementsByTagName(final Node root, final String name) {
        final XmlaNodeList nodeList = new XmlaNodeList();
        final String nodeName = root.getNodeName();
        if (nodeName.equals(name)) {
            nodeList.add(root);
        }
        if (root.hasChildNodes()) {
            final NodeList rootChildren = root.getChildNodes();
            for (int i = 0; i < rootChildren.getLength(); i++) {
                nodeList.addAll(getElementsByTagName(rootChildren.item(i), name));
            }
        }
        return nodeList;
    }

    public String getTextContent(final Node node) {
        final Node child = node.getFirstChild();
        if (child != null && child.getNodeType() == Node.TEXT_NODE) {
            return child.getNodeValue();
        }
        return null;
    }
}
