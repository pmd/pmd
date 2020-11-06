/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTFinallyStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTFinallyStatement(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTFinallyStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the body of this finally clause.
     */
    public ASTBlock getBody() {
        return (ASTBlock) getChild(0);
    }
}
