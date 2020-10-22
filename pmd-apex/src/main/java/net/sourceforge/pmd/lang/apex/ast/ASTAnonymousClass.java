/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.AnonymousClass;

public final class ASTAnonymousClass extends AbstractApexNode<AnonymousClass> {

    ASTAnonymousClass(AnonymousClass anonymousClass) {
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
