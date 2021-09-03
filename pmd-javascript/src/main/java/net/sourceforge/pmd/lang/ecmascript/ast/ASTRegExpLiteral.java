/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.RegExpLiteral;

public final class ASTRegExpLiteral extends AbstractEcmascriptNode<RegExpLiteral> {
    ASTRegExpLiteral(RegExpLiteral regExpLiteral) {
        super(regExpLiteral);
        super.setImage(regExpLiteral.getValue());
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getFlags() {
        return node.getFlags();
    }
}
