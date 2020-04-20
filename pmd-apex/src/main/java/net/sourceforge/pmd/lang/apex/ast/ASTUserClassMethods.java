/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.compilation.UserClassMethods;

public class ASTUserClassMethods extends AbstractApexNode<UserClassMethods> {

    @Deprecated
    @InternalApi
    public ASTUserClassMethods(UserClassMethods userClassMethods) {
        super(userClassMethods);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
