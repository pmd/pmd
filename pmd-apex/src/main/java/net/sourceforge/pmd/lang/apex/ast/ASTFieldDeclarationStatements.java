/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.ArrayList;
import java.util.List;

import com.google.summit.ast.TypeRef;
import com.google.summit.ast.declaration.FieldDeclarationGroup;


public final class ASTFieldDeclarationStatements extends AbstractApexNode.Single<FieldDeclarationGroup> {

    ASTFieldDeclarationStatements(FieldDeclarationGroup fieldDeclarationStatements) {
        super(fieldDeclarationStatements);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public ASTModifierNode getModifiers() {
        return firstChild(ASTModifierNode.class);
    }

    /**
     * Returns the type name.
     *
     * <p>This includes any type arguments.
     * If the type is a primitive, its case will be normalized.
     */
    public String getTypeName() {
        return caseNormalizedTypeIfPrimitive(node.getType().asCodeString());
    }

    /**
     * This returns the first level of the type arguments. If there are nested
     * types (e.g. {@code List<List<String>>}), then these returned types
     * contain themselves type arguments.
     *
     * <p>Note: This method only exists for this AST type and in no other type,
     * even though type arguments are possible e.g. for {@link ASTVariableDeclaration#getType()}.
     */
    public List<String> getTypeArguments() {
        List<String> result = new ArrayList<>();
        // note: for void types, there are no components anyway
        for (TypeRef.Component component : node.getType().getComponents()) {
            for (TypeRef typeRef : component.getArgs()) {
                result.add(caseNormalizedTypeIfPrimitive(typeRef.asCodeString()));
            }
        }
        return result;
    }
}
