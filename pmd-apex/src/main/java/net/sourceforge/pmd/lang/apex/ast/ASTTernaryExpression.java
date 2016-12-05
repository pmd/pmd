/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.TernaryExpression;

public class ASTTernaryExpression extends AbstractApexNode<TernaryExpression> {

    public ASTTernaryExpression(TernaryExpression ternaryExpression) {
        super(ternaryExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
