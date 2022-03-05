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
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
