/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.declaration.TypeDeclaration;

public final class ASTAnonymousClass extends AbstractApexNode.Single<TypeDeclaration> {

    ASTAnonymousClass(TypeDeclaration anonymousClass) {
        super(anonymousClass);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getClass().getName();
    }
}
