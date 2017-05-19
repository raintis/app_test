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

import java.util.List;

class QueryFilter {

    private QueryFilterOperand leftOp;
    private String operator;
    private QueryFilterOperand rightOp;

    QueryFilter() {
        this.leftOp = null;
        this.operator = null;
        this.rightOp = null;
    }

    String getOperator() {
        return operator;
    }

    void setOperator(final String operator) {
        this.operator = operator;
    }

    QueryFilterOperand getLeftOp() {
        return leftOp;
    }

    void setLeftOp(final QueryFilterOperand leftOp) {
        this.leftOp = leftOp;
    }

    void setLeftOp(final QueryColumn col) {
        this.leftOp = new QueryFilterOperand();
        this.leftOp.setCol(col);
    }

    void setLeftOp(final List<String> valList) {
        this.leftOp = new QueryFilterOperand();
        this.leftOp.setValList(valList);
    }

    QueryFilterOperand getRightOp() {
        return rightOp;
    }

    void setRightOp(final QueryFilterOperand rightOp) {
        this.rightOp = rightOp;
    }

    void setRightOp(final QueryColumn col) {
        this.rightOp = new QueryFilterOperand();
        this.rightOp.setCol(col);
    }

    void setRightOp(final List<String> valList) {
        this.rightOp = new QueryFilterOperand();
        this.rightOp.setValList(valList);
    }

}
