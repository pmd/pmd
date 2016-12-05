/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.SuperMethodCallExpression;

public class ASTSuperMethodCallExpression extends AbstractApexNode<SuperMethodCallExpression> {

    public ASTSuperMethodCallExpression(SuperMethodCallExpression superMethodCallExpression) {
        super(superMethodCallExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
