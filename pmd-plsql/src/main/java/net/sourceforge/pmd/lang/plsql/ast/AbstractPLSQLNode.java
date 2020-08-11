/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;

abstract class AbstractPLSQLNode extends AbstractJjtreeNode<AbstractPLSQLNode, PLSQLNode> implements PLSQLNode {

    protected Object value;
    protected PLSQLParser parser;
    protected Scope scope;

    AbstractPLSQLNode(int i) {
        super(i);
    }

    @Override // override to make protected member accessible to parser
    protected void setImage(String image) {
        super.setImage(image);
    }

    protected void jjtSetValue(Object value) {
        this.value = value;
    }

    public Object jjtGetValue() {
        return value;
    }


    @Override
    public String getXPathNodeName() {
        return PLSQLParserImplTreeConstants.jjtNodeName[id];
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to
     * customize the way the node appears when the tree is dumped. If your
     * output uses more than one line you should override toString(String),
     * otherwise overriding toString() is probably all you need to do.
     */
    public String toString(String prefix) {
        return prefix + toString();
    }

    @Override
    public String toString() {
        return getXPathNodeName();
    }

    @Override
    public Scope getScope() {
        if (scope == null) {
            return getParent().getScope();
        }
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
    }
}
