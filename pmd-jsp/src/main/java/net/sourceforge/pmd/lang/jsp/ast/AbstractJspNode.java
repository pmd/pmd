/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;

abstract class AbstractJspNode extends AbstractJjtreeNode<AbstractJspNode, JspNode> implements JspNode {

    protected AbstractJspNode(int id) {
        super(id);
    }

    @Override
    public final <R, P> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof JspVisitor) {
            return this.acceptVisitor((JspVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.visitNode(this, data);
    }

    protected abstract <P, R> R acceptVisitor(JspVisitor<? super P, ? extends R> visitor, P data);


    @Override // override to make protected member accessible to parser
    protected void setImage(String image) {
        super.setImage(image);
    }

    @Override
    public String getXPathNodeName() {
        return JspParserImplTreeConstants.jjtNodeName[id];
    }
}
