/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.JavaMethodCallExpression;

public class ASTJavaMethodCallExpression extends AbstractApexNode<JavaMethodCallExpression> {

    public ASTJavaMethodCallExpression(JavaMethodCallExpression javaMethodCallExpression) {
        super(javaMethodCallExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
