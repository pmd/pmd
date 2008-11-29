/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.BreakStatement;

public class ASTBreakStatement extends AbstractEcmascriptNode<BreakStatement> {
    public ASTBreakStatement(BreakStatement breakStatement) {
	super(breakStatement);
	super.setImage(breakStatement.getBreakLabel() != null ? breakStatement.getBreakLabel().getIdentifier() : null);
    }

    public boolean hasLabel() {
	return node.getBreakLabel() != null;
    }

    public ASTName getLabel() {
	return (ASTName) jjtGetChild(0);
    }
}
