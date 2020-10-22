/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.SwitchStatement;

public final class ASTSwitchStatement extends AbstractEcmascriptNode<SwitchStatement> {
    ASTSwitchStatement(SwitchStatement switchStatement) {
        super(switchStatement);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
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
