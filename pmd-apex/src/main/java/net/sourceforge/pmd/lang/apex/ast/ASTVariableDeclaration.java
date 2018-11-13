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
        return node.getLocalInfo().getName();
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        ASTVariableDeclarationStatements parent = (ASTVariableDeclarationStatements) jjtGetParent();

        for (ASTModifierNode modifier : parent.findChildrenOfType(ASTModifierNode.class)) {
            for (ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }
}
