/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;

abstract class AbstractVfNode extends AbstractJjtreeNode<AbstractVfNode, VfNode> implements VfNode {

    protected AbstractVfNode(int id) {
        super(id);
    }

    @Override // override to make protected member accessible to parser
    protected void setImage(String image) {
        super.setImage(image);
    }

    @Override
    public String getXPathNodeName() {
        return VfParserImplTreeConstants.jjtNodeName[id];
    }
    protected abstract <P, R> R acceptVfVisitor(VfVisitor<? super P, ? extends R> visitor, P data);

    @Override
    @SuppressWarnings("unchecked")
    public final <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof VfVisitor) {
            return acceptVfVisitor((VfVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

}
