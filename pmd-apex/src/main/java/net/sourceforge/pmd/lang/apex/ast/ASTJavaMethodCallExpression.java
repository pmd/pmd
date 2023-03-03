/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.JavaMethodCallExpression;

public final class ASTJavaMethodCallExpression extends AbstractApexNode<JavaMethodCallExpression> {

    ASTJavaMethodCallExpression(JavaMethodCallExpression javaMethodCallExpression) {
        super(javaMethodCallExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
