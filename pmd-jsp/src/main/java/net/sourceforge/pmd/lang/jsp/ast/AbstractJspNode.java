/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;

public class AbstractJspNode extends AbstractJjtreeNode<JspNode> implements JspNode {

    public AbstractJspNode(int id) {
        super(id);
    }


    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public String getXPathNodeName() {
        return JspParserImplTreeConstants.jjtNodeName[id];
    }
}
