/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.VariableDeclarationGroup;

public final class ASTVariableDeclarationStatements extends AbstractApexNode.Single<VariableDeclarationGroup> {

    ASTVariableDeclarationStatements(VariableDeclarationGroup variableDeclarations) {
        super(variableDeclarations);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTModifierNode getModifiers() {
        return firstChild(ASTModifierNode.class);
    }
}
