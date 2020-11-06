/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.IfStatement;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTIfStatement extends AbstractEcmascriptNode<IfStatement> {
    @Deprecated
    @InternalApi
    public ASTIfStatement(IfStatement ifStatement) {
        super(ifStatement);
    }

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
