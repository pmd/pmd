/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.compilation.UserExceptionMethods;

public class ASTUserExceptionMethods extends AbstractApexNode<UserExceptionMethods> {

    @Deprecated
    @InternalApi
    public ASTUserExceptionMethods(UserExceptionMethods userExceptionMethods) {
        super(userExceptionMethods);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
