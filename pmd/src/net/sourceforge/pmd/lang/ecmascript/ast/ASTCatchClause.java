/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.CatchClause;

public class ASTCatchClause extends AbstractEcmascriptNode<CatchClause> {
    public ASTCatchClause(CatchClause catchClause) {
	super(catchClause);
    }

    public ASTName getVariableName() {
	return (ASTName) jjtGetChild(0);
    }

    public boolean isIf() {
	return node.getCatchCondition() != null;
    }

    public EcmascriptNode getCatchCondition() {
	return (EcmascriptNode) jjtGetChild(1);
    }

    public ASTBlock getBlock() {
	return (ASTBlock) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
