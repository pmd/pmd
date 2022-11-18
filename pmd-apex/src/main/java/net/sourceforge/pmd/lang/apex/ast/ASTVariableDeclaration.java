/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.VariableDeclaration;

public class ASTVariableDeclaration extends AbstractApexNode.Single<VariableDeclaration> implements CanSuppressWarnings {

    @Deprecated
    @InternalApi
    public ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
        super(variableDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getId().getString();
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
        return node.getType().asCodeString();
    }
}
