/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;

abstract class AbstractVfNode extends AbstractJjtreeNode<VfNode> implements VfNode {

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
}
