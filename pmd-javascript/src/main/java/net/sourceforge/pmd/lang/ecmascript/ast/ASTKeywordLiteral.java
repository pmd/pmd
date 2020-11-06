/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.Locale;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.KeywordLiteral;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTKeywordLiteral extends AbstractEcmascriptNode<KeywordLiteral> {
    @Deprecated
    @InternalApi
    public ASTKeywordLiteral(KeywordLiteral keywordLiteral) {
        super(keywordLiteral);
        super.setImage(Token.typeToName(keywordLiteral.getType()).toLowerCase(Locale.ROOT));
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean isBoolean() {
        return node.isBooleanLiteral();
    }

    public boolean isThis() {
        return node.getType() == Token.THIS;
    }

    public boolean isNull() {
        return node.getType() == Token.NULL;
    }

    public boolean isDebugger() {
        return node.getType() == Token.DEBUGGER;
    }
}
