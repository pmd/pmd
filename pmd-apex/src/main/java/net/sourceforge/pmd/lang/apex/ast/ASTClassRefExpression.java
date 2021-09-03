/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.ClassRefExpression;

public final class ASTClassRefExpression extends AbstractApexNode<ClassRefExpression> {

    ASTClassRefExpression(ClassRefExpression classRefExpression) {
        super(classRefExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
