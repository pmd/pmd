/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.initializer.ConstructorInitializer;

public final class ASTNewKeyValueObjectExpression extends AbstractApexNode.Single<ConstructorInitializer> {

    ASTNewKeyValueObjectExpression(ConstructorInitializer constructorInitializer) {
        super(constructorInitializer);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the type name.
     *
     * This includes any type arguments.
     * If the type is a primitive, its case will be normalized.
     */
    public String getType() {
        return caseNormalizedTypeIfPrimitive(node.getType().asCodeString());
    }

    public int getParameterCount() {
        return node.getArgs().size();
    }
}
