/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;

import apex.jorje.semantic.ast.statement.FieldDeclarationStatements;

public class ASTFieldDeclarationStatements extends AbstractApexNode<FieldDeclarationStatements>
        implements CanSuppressWarnings {

    public ASTFieldDeclarationStatements(FieldDeclarationStatements fieldDeclarationStatements) {
        super(fieldDeclarationStatements);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (ASTModifierNode modifier : findChildrenOfType(ASTModifierNode.class)) {
            for (ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }
}
