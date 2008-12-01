package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Comment;

public class ASTComment extends AbstractEcmascriptNode<Comment> {
    public ASTComment(Comment comment) {
	super(comment);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    // TODO Implement something useful
}
