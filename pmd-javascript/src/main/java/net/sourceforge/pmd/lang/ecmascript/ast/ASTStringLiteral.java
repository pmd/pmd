/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.StringLiteral;

public final class ASTStringLiteral extends AbstractEcmascriptNode<StringLiteral> {
    ASTStringLiteral(StringLiteral stringLiteral) {
        super(stringLiteral);
        super.setImage(stringLiteral.getValue());
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public char getQuoteCharacter() {
        return node.getQuoteCharacter();
    }

    public boolean isSingleQuoted() {
        return '\'' == getQuoteCharacter();
    }

    public boolean isDoubleQuoted() {
        return '"' == getQuoteCharacter();
    }
}
