/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

abstract class AbstractOperator extends AbstractModelicaNode {
    protected String operator;

    AbstractOperator(int id) {
        super(id);
    }

    void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}
