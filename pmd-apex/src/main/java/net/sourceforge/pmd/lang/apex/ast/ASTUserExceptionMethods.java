/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserExceptionMethods;

public final class ASTUserExceptionMethods extends AbstractApexNode<UserExceptionMethods> {

    ASTUserExceptionMethods(UserExceptionMethods userExceptionMethods) {
        super(userExceptionMethods);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
