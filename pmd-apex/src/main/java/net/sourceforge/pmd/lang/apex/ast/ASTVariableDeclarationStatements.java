/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.VariableDeclaration;

public class ASTVariableDeclarationStatements extends AbstractApexNode.Many<VariableDeclaration> {

    @Deprecated
    @InternalApi
    public ASTVariableDeclarationStatements(List<VariableDeclaration> variableDeclarations) {
        super(variableDeclarations);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }
}
