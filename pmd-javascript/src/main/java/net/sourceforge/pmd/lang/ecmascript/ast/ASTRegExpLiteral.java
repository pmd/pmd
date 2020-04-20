/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.RegExpLiteral;

public class ASTRegExpLiteral extends AbstractEcmascriptNode<RegExpLiteral> {
    ASTRegExpLiteral(RegExpLiteral regExpLiteral) {
        super(regExpLiteral);
        super.setImage(regExpLiteral.getValue());
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getFlags() {
        return node.getFlags();
    }
}
