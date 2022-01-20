/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.TemplateLiteral;

public final class ASTTemplateLiteral extends AbstractEcmascriptNode<TemplateLiteral> {

    ASTTemplateLiteral(TemplateLiteral templateLiteral) {
        super(templateLiteral);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
