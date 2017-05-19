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

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

/**
 * @author <a href="mailto:fsiberchicot@jdbc4olap.org">Florian SIBERCHICOT</a>
 */
class XmlaRestrictions {

    private String providerType;
    private String catalog;
    private String cubeName;
    private String dimensionUniqueName;
    private String hierarchyUniqueName;
    private String levelUniqueName;
    private String memberUniqueName;
    private String treeOp;

    XmlaRestrictions() {
    }

    XmlaRestrictions(final String catalogName) {
        this();
        catalog = catalogName;
    }

    String getCatalog() {
        return catalog;
    }

    void setCatalog(final String s) {
        catalog = s;
    }

    String getCubeName() {
        return cubeName;
    }

    void setCubeName(final String s) {
        cubeName = s;
    }

    String getDimensionUniqueName() {
        return dimensionUniqueName;
    }

    void setDimensionUniqueName(final String s) {
        dimensionUniqueName = s;
    }

    String getHierarchyUniqueName() {
        return hierarchyUniqueName;
    }

    void setHierarchyUniqueName(final String s) {
        hierarchyUniqueName = s;
    }

    void setLevelUniqueName(final String s) {
        levelUniqueName = s;
    }

    String getLevelUniqueName() {
        return levelUniqueName;
    }

    void setMemberUniqueName(final String s) {
        memberUniqueName = s;
    }

    String getMemberUniqueName() {
        return memberUniqueName;
    }

    void setTreeOp(final String s) {
        treeOp = s;
    }

    String getTreeOp() {
        return treeOp;
    }


    SOAPElement getXMLA() throws SOAPException {
        final SOAPFactory factory = XmlaConn.SOAP_FACTORY;
        final SOAPElement restrictions = factory.createElement("Restrictions", "", "urn:schemas-microsoft-com:xml-analysis");
        final SOAPElement restrictionList = restrictions.addChildElement(factory.createName("RestrictionList", "", "urn:schemas-microsoft-com:xml-analysis"));
        if (catalog != null && !catalog.equals("")) {
            final SOAPElement soapProperty = restrictionList.addChildElement(factory.createName("CATALOG_NAME"));
            soapProperty.addTextNode(catalog);
        }
        if (cubeName != null && !cubeName.equals("")) {
            final SOAPElement soapProperty = restrictionList.addChildElement(factory.createName("CUBE_NAME"));
            soapProperty.addTextNode(cubeName);
        }
        if (dimensionUniqueName != null && !dimensionUniqueName.equals("")) {
            final SOAPElement soapProperty = restrictionList.addChildElement(factory.createName("DIMENSION_UNIQUE_NAME"));
            soapProperty.addTextNode(dimensionUniqueName);
        }
        if (hierarchyUniqueName != null && !hierarchyUniqueName.equals("")) {
            final SOAPElement soapProperty = restrictionList.addChildElement(factory.createName("HIERARCHY_UNIQUE_NAME"));
            soapProperty.addTextNode(hierarchyUniqueName);
        }
        if (levelUniqueName != null && !levelUniqueName.equals("")) {
            final SOAPElement soapProperty = restrictionList.addChildElement(factory.createName("LEVEL_UNIQUE_NAME"));
            soapProperty.addTextNode(levelUniqueName);
        }
        if (memberUniqueName != null && !memberUniqueName.equals("")) {
            final SOAPElement soapProperty = restrictionList.addChildElement(factory.createName("MEMBER_UNIQUE_NAME"));
            soapProperty.addTextNode(memberUniqueName);
        }
        if (treeOp != null && !treeOp.equals("")) {
            final SOAPElement soapProperty = restrictionList.addChildElement(factory.createName("TREE_OP"));
            soapProperty.addTextNode(treeOp);
        }

        return restrictions;
    }

    String getProviderType() {
        return providerType;
    }

    // @todo Actual value of parameter 'providerType' is always '"MDP"',
    // Inline value '"MDP"' for parameter 'providerType'?
    void setProviderType(final String providerType) {
        this.providerType = providerType;
    }

}
