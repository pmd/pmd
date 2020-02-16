/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;

import apex.jorje.semantic.ast.statement.VariableDeclaration;

public class ASTVariableDeclaration extends AbstractApexNode<VariableDeclaration> implements CanSuppressWarnings {

    public ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
        super(variableDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        if (node.getLocalInfo() != null) {
            return node.getLocalInfo().getName();
        }
        return null;
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        ASTVariableDeclarationStatements parent = (ASTVariableDeclarationStatements) getParent();

        for (ASTModifierNode modifier : parent.findChildrenOfType(ASTModifierNode.class)) {
            for (ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getType() {
        if (node.getLocalInfo() != null) {
            return node.getLocalInfo().getType().getApexName();
        }
        return null;
    }
}
