/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.TextRegion;

import apex.jorje.data.Identifier;
import apex.jorje.semantic.ast.expression.MethodCallExpression;


public final class ASTMethodCallExpression extends AbstractApexNode<MethodCallExpression> {
    ASTMethodCallExpression(MethodCallExpression methodCallExpression) {
        super(methodCallExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getMethodName() {
        return node.getMethodName();
    }

    public String getFullMethodName() {
        final String methodName = getMethodName();
        StringBuilder typeName = new StringBuilder();
        for (Identifier identifier : node.getReferenceContext().getNames()) {
            typeName.append(identifier.getValue()).append('.');
        }
        return typeName.toString() + methodName;
    }

    public int getInputParametersSize() {
        return node.getInputParameters().size();
    }

    @Override
    public @NonNull TextRegion getTextRegion() {
        int fullLength = getFullMethodName().length();
        int nameLength = getMethodName().length();
        TextRegion base = super.getTextRegion();
        if (fullLength > nameLength) {
            base = base.growLeft(fullLength - nameLength);
        }
        return base;
    }
}
