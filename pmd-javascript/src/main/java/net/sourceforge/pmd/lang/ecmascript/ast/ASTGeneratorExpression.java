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
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
