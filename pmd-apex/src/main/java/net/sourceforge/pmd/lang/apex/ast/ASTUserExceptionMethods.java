/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserExceptionMethods;

public class ASTUserExceptionMethods extends AbstractApexNode<UserExceptionMethods> {

    public ASTUserExceptionMethods(UserExceptionMethods userExceptionMethods) {
        super(userExceptionMethods);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
