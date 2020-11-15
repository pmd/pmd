/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;
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

    protected abstract <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data);

    @Override
    @SuppressWarnings("unchecked")
    public final <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof PlsqlVisitor) {
            return acceptPlsqlVisitor((PlsqlVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

    @Override
    public String getXPathNodeName() {
        return PLSQLParserImplTreeConstants.jjtNodeName[id];
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
