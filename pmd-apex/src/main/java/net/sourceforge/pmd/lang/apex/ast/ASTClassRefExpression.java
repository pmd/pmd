/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.TypeRefExpression;

public final class ASTClassRefExpression extends AbstractApexNode.Single<TypeRefExpression> {

    ASTClassRefExpression(TypeRefExpression typeRefExpression) {
        super(typeRefExpression);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the name of the type referenced by this class literal, as it is
     * written in the source code.
     *
     * <p>Qualified and nested type names are retained as well as any type
     * arguments and array nesting. This is the type of the literal itself and
     * does not depend on how the expression is used, so it is available even
     * when the class literal is passed directly as a method argument.
     *
     * @since 7.29.0
     */
    public String getType() {
        return caseNormalizedTypeIfPrimitive(node.getTypeRef().asCodeString());
    }
}
