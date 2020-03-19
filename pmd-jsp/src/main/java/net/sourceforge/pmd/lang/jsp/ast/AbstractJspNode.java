/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;

abstract class AbstractJspNode extends AbstractJjtreeNode<JspNode> implements JspNode {

    protected AbstractJspNode(int id) {
        super(id);
    }

    @Override
    public String getXPathNodeName() {
        return JspParserImplTreeConstants.jjtNodeName[id];
    }
}
