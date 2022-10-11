/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.declaration.TypeDeclaration;

public class ASTUserEnum extends ApexRootNode<TypeDeclaration> {

    @Deprecated
    @InternalApi
    public ASTUserEnum(TypeDeclaration userEnum) {
        super(userEnum);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
//        String apexName = getDefiningType();
//        return apexName.substring(apexName.lastIndexOf('.') + 1);
        // TODO(b/239648780)
        return "";
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
