/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.data.Identifier;
import apex.jorje.data.ast.TypeRef;
import apex.jorje.data.ast.TypeRefs.ArrayTypeRef;
import apex.jorje.data.ast.TypeRefs.ClassTypeRef;
import apex.jorje.semantic.ast.statement.FieldDeclarationStatements;

public class ASTFieldDeclarationStatements extends AbstractApexNode<FieldDeclarationStatements>
        implements CanSuppressWarnings {

    @Deprecated
    @InternalApi
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

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getTypeName() {
        if (node.getTypeName() != null) {
            List<Identifier> names = node.getTypeName().getNames();
            return names.stream().map(Identifier::getValue).collect(Collectors.joining("."));
        }
        return null;
    }

    private static String identifiersToString(List<Identifier> identifiers) {
        return identifiers.stream().map(Identifier::getValue).collect(Collectors.joining("."));
    }

    public List<String> getTypeArguments() {
        List<String> result = new ArrayList<>();

        if (node.getTypeName() != null) {
            List<TypeRef> typeArgs = node.getTypeName().getTypeArguments();
            for (TypeRef arg : typeArgs) {
                if (arg instanceof ClassTypeRef) {
                    result.add(identifiersToString(arg.getNames()));
                } else if (arg instanceof ArrayTypeRef) {
                    ArrayTypeRef atr = (ArrayTypeRef) arg;
                    if (atr.getHeldType() instanceof ClassTypeRef) {
                        result.add(identifiersToString(atr.getHeldType().getNames()));
                    }
                }
            }
        }

        return result;
    }
}
