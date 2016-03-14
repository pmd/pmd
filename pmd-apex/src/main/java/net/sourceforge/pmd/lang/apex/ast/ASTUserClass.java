/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserClass;

public class ASTUserClass extends AbstractApexNode<UserClass> {
    public ASTUserClass(UserClass userClass) {
        super(userClass);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}
