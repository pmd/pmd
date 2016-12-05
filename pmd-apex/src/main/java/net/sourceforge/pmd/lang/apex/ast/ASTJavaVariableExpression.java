/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.JavaVariableExpression;

public class ASTJavaVariableExpression extends AbstractApexNode<JavaVariableExpression> {

    public ASTJavaVariableExpression(JavaVariableExpression javaVariableExpression) {
        super(javaVariableExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
