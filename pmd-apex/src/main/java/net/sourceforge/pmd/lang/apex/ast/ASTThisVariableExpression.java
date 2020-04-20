/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.ThisVariableExpression;

public class ASTThisVariableExpression extends AbstractApexNode<ThisVariableExpression> {

    @Deprecated
    @InternalApi
    public ASTThisVariableExpression(ThisVariableExpression thisVariableExpression) {
        super(thisVariableExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
