/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.GeneratorExpression;

public final class ASTGeneratorExpression extends AbstractEcmascriptNode<GeneratorExpression> {

    ASTGeneratorExpression(GeneratorExpression generatorExpression) {
        super(generatorExpression);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
