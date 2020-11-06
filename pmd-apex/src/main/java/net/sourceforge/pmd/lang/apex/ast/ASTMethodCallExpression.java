/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.data.Identifier;
import apex.jorje.semantic.ast.expression.MethodCallExpression;


public class ASTMethodCallExpression extends AbstractApexNode<MethodCallExpression> {
    @Deprecated
    @InternalApi
    public ASTMethodCallExpression(MethodCallExpression methodCallExpression) {
        super(methodCallExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getMethodName() {
        return node.getMethodName();
    }

    public String getFullMethodName() {
        final String methodName = getMethodName();
        StringBuilder typeName = new StringBuilder();
        for (Iterator<Identifier> it = node.getReferenceContext().getNames().iterator(); it.hasNext();) {
            typeName.append(it.next().getValue()).append('.');
        }
        return typeName.toString() + methodName;
    }

    public int getInputParametersSize() {
        return node.getInputParameters().size();
    }
}
