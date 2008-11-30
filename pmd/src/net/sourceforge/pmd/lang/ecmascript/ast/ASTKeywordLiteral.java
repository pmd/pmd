/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.KeywordLiteral;

public class ASTKeywordLiteral extends AbstractEcmascriptNode<KeywordLiteral> {
    public ASTKeywordLiteral(KeywordLiteral keywordLiteral) {
	super(keywordLiteral);
	super.setImage(Token.typeToName(keywordLiteral.getType()).toLowerCase());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isBoolean() {
	return node.isBooleanLiteral();
    }
}
