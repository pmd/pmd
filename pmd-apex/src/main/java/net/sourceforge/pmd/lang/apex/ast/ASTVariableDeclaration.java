/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.VariableDeclaration;

public final class ASTVariableDeclaration extends AbstractApexNode.Single<VariableDeclaration> {

    ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
        super(variableDeclaration);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getId().getString();
    }


    /**
     * Returns the variable's type name.
     *
     * <p>This includes any type arguments.
     * If the type is a primitive, its case will be normalized.
     */
    public String getType() {
        return caseNormalizedTypeIfPrimitive(node.getType().asCodeString());
    }
}
