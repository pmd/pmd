/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ReturnStatement;

public class ASTReturnStatement extends AbstractEcmascriptNode<ReturnStatement> {
    public ASTReturnStatement(ReturnStatement returnStatement) {
	super(returnStatement);
    }

    public boolean hasResult() {
	return node.getReturnValue() != null;
    }
}
