/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.VariableDeclarationGroup;

public class ASTVariableDeclarationStatements extends AbstractApexNode.Single<VariableDeclarationGroup> {

    ASTVariableDeclarationStatements(VariableDeclarationGroup variableDeclarations) {
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
