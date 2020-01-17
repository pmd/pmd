/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.SwitchStatement;

public class ASTSwitchStatement extends AbstractEcmascriptNode<SwitchStatement> {
    public ASTSwitchStatement(SwitchStatement switchStatement) {
        super(switchStatement);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public EcmascriptNode<?> getExpression() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public int getNumCases() {
        return node.getCases().size();
    }

    public ASTSwitchCase getSwitchCase(int index) {
        return (ASTSwitchCase) getChild(index + 1);
    }
}
