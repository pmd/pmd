/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.FieldDeclarationGroup;

public class ASTFieldDeclarationStatements extends AbstractApexNode.Single<FieldDeclarationGroup>
        implements CanSuppressWarnings {

    @Deprecated
    @InternalApi
    public ASTFieldDeclarationStatements(FieldDeclarationGroup fieldDeclarationStatements) {
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
        return node.getType().asCodeString();
    }

    /*
    private static String identifiersToString(List<Identifier> identifiers) {
        return identifiers.stream().map(Identifier::getValue).collect(Collectors.joining("."));
    }
     */
    // TODO(b/239648780)

    public List<String> getTypeArguments() {
        List<String> result = new ArrayList<>();

        /*
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
         */
        // TODO(b/239648780)

        return result;
    }
}
