/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.Comment;

public class ASTComment extends AbstractApexNode<Comment> {
    public ASTComment(Comment comment) {
	super(comment);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public String getValue() {
	return node.getValue();
    }
}
