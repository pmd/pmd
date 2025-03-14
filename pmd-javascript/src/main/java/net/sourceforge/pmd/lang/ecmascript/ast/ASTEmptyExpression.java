/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.EmptyExpression;

public final class ASTEmptyExpression extends AbstractEcmascriptNode<EmptyExpression> {
    ASTEmptyExpression(EmptyExpression emptyExpression) {
        super(emptyExpression);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
