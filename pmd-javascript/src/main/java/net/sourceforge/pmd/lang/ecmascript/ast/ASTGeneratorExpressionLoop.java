/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.GeneratorExpressionLoop;

public final class ASTGeneratorExpressionLoop extends AbstractEcmascriptNode<GeneratorExpressionLoop> {

    ASTGeneratorExpressionLoop(GeneratorExpressionLoop generatorExpressionLoop) {
        super(generatorExpressionLoop);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
