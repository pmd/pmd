/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.BreakStatement;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBreakStatement extends AbstractEcmascriptNode<BreakStatement> {
    @Deprecated
    @InternalApi
    public ASTBreakStatement(BreakStatement breakStatement) {
        super(breakStatement);
        super.setImage(breakStatement.getBreakLabel() != null ? breakStatement.getBreakLabel().getIdentifier() : null);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasLabel() {
        return node.getBreakLabel() != null;
    }

    public ASTName getLabel() {
        return (ASTName) getChild(0);
    }
}
