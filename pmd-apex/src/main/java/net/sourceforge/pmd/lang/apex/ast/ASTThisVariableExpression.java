/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.ThisVariableExpression;

public final class ASTThisVariableExpression extends AbstractApexNode<ThisVariableExpression> {

    ASTThisVariableExpression(ThisVariableExpression thisVariableExpression) {
        super(thisVariableExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
