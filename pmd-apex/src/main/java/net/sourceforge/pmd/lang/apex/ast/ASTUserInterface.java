/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;

import com.google.summit.ast.TypeRef;
import com.google.summit.ast.declaration.InterfaceDeclaration;

public class ASTUserInterface extends ApexRootNode<InterfaceDeclaration> implements ASTUserClassOrInterface<InterfaceDeclaration>,
       CanSuppressWarnings {

    private ApexQualifiedName qname;

    ASTUserInterface(InterfaceDeclaration userInterface) {
        super(userInterface);
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
    public TypeKind getTypeKind() {
        return TypeKind.INTERFACE;
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

    public String getSuperInterfaceName() {
        return node.getExtendsTypes().stream().map(TypeRef::asCodeString).findFirst().orElse("");
    }
}
