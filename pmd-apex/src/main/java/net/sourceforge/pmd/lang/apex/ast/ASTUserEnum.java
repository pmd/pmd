/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.EnumDeclaration;

public final class ASTUserEnum extends BaseApexClass<EnumDeclaration> {
    private ApexQualifiedName qname;

    ASTUserEnum(EnumDeclaration enumDeclaration) {
        super(enumDeclaration);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public ApexQualifiedName getQualifiedName() {
        if (qname == null) {

            ASTUserClass parent = ancestors(ASTUserClass.class).first();

            if (parent != null) {
                qname = ApexQualifiedName.ofNestedEnum(parent.getQualifiedName(), this);
            } else {
                qname = ApexQualifiedName.ofOuterEnum(this);
            }
        }

        return qname;
    }
}
