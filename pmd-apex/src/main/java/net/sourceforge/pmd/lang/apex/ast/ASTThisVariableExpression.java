/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.ThisVariableExpression;

public class ASTThisVariableExpression extends AbstractApexNode<ThisVariableExpression> {

    public ASTThisVariableExpression(ThisVariableExpression thisVariableExpression) {
        super(thisVariableExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
