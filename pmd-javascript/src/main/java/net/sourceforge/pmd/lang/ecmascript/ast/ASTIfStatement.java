/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.IfStatement;

public class ASTIfStatement extends AbstractEcmascriptNode<IfStatement> {
    public ASTIfStatement(IfStatement ifStatement) {
        super(ifStatement);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasElse() {
        return node.getElsePart() != null;
    }

    public EcmascriptNode<?> getCondition() {
        return (EcmascriptNode<?>) getChild(0);
    }

    public EcmascriptNode<?> getThen() {
        return (EcmascriptNode<?>) getChild(1);
    }

    public EcmascriptNode<?> getElse() {
        return (EcmascriptNode<?>) getChild(2);
    }
}
