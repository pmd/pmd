/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewObjectExpression;

public class ASTNewObjectExpression extends AbstractApexNode<NewObjectExpression> {

    public ASTNewObjectExpression(NewObjectExpression newObjectExpression) {
        super(newObjectExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
