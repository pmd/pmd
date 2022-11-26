/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserEnum;

public final class ASTUserEnum extends BaseApexClass<UserEnum> {
    private ApexQualifiedName qname;

    ASTUserEnum(UserEnum userEnum) {
        super(userEnum);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
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
