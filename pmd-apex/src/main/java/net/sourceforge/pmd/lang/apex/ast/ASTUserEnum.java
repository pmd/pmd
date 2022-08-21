/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.EnumDeclaration;

public class ASTUserEnum extends ApexRootNode<TypeDeclaration> implements ApexQualifiableNode {

    private ApexQualifiedName qname;

    ASTUserEnum(EnumDeclaration enumDeclaration) {
        super(enumDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getId().getString();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    @Override
    public ApexQualifiedName getQualifiedName() {
        if (qname == null) {

            ASTUserClass parent = this.getFirstParentOfType(ASTUserClass.class);

            if (parent != null) {
                qname = ApexQualifiedName.ofNestedEnum(parent.getQualifiedName(), this);
            } else {
                qname = ApexQualifiedName.ofOuterEnum(this);
            }
        }

        return qname;
    }
}
