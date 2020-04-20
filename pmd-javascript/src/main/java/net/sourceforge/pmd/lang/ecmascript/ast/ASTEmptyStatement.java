/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.EmptyStatement;

public final class ASTEmptyStatement extends AbstractEcmascriptNode<EmptyStatement> {
    ASTEmptyStatement(EmptyStatement emptyStatement) {
        super(emptyStatement);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
