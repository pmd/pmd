/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.TypeDeclaration;

public class ASTUserClass extends ApexRootNode<TypeDeclaration> implements ASTUserClassOrInterface<TypeDeclaration>,
       CanSuppressWarnings {

    private ApexQualifiedName qname;

    @Deprecated
    @InternalApi
    public ASTUserClass(TypeDeclaration userClass) {
        super(userClass);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public String getImage() {
        String apexName = getDefiningType();
        return apexName.substring(apexName.lastIndexOf('.') + 1);
    }

    @Override
    public ApexQualifiedName getQualifiedName() {
        if (qname == null) {

            ASTUserClass parent = this.getFirstParentOfType(ASTUserClass.class);

            if (parent != null) {
                qname = ApexQualifiedName.ofNestedClass(parent.getQualifiedName(), this);
            } else {
                qname = ApexQualifiedName.ofOuterClass(this);
            }
        }

        return qname;
    }


    @Override
    public TypeKind getTypeKind() {
        return TypeKind.CLASS;
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


    public String getSuperClassName() {
        /*
        return node.getDefiningType().getCodeUnitDetails().getSuperTypeRef().map(TypeRef::getNames)
            .map(it -> it.stream().map(Identifier::getValue).collect(Collectors.joining(".")))
            .orElse("");
         */
        // TODO(b/239648780)
        return null;
    }

    public List<String> getInterfaceNames() {
        /*
        return node.getDefiningType().getCodeUnitDetails().getInterfaceTypeRefs().stream()
                .map(TypeRef::getNames).map(it -> it.stream().map(Identifier::getValue).collect(Collectors.joining(".")))
                .collect(Collectors.toList());
         */
        // TODO(b/239648780)
        return null;
    }
}
