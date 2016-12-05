/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.SuperVariableExpression;

public class ASTSuperVariableExpression extends AbstractApexNode<SuperVariableExpression> {

    public ASTSuperVariableExpression(SuperVariableExpression superVariableExpression) {
        super(superVariableExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
