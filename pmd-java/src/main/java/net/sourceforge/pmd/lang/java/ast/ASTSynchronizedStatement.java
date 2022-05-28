/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTSynchronizedStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTSynchronizedStatement(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTSynchronizedStatement(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the body of this statement.
     */
    public ASTBlock getBody() {
        return (ASTBlock) getChild(1);
    }
}
