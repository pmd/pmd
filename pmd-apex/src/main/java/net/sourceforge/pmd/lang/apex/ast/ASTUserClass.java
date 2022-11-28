/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.Rule;

import com.google.summit.ast.TypeRef;
import com.google.summit.ast.declaration.ClassDeclaration;

public class ASTUserClass extends ApexRootNode<ClassDeclaration> implements ASTUserClassOrInterface<ClassDeclaration>,
        CanSuppressWarnings {

    private ApexQualifiedName qname;

    ASTUserClass(ClassDeclaration userClass) {
        super(userClass);
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

    /**
     * Returns the name of the superclass of this class, or an empty string if there is none.
     *
     * The type name does NOT include type arguments.
     */
    public String getSuperClassName() {
        TypeRef extendsType = node.getExtendsType();
        if (extendsType != null) {
            return extendsType.asTypeErasedString();
        }
        return "";
    }

    /**
     * Returns a list of the names of the interfaces implemented by this class.
     *
     * The type names do NOT include type arguments. (This is tested.)
     */
    public List<String> getInterfaceNames() {
        return node.getImplementsTypes().stream()
            .map(TypeRef::asTypeErasedString)
            .collect(Collectors.toList());
    }
}
