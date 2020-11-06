/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.expression.JavaVariableExpression;

public class ASTJavaVariableExpression extends AbstractApexNode<JavaVariableExpression> {

    @Deprecated
    @InternalApi
    public ASTJavaVariableExpression(JavaVariableExpression javaVariableExpression) {
        super(javaVariableExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
