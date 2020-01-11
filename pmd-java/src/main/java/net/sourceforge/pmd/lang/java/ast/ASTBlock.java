/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBlock extends AbstractJavaNode {

    private boolean containsComment;

    @InternalApi
    @Deprecated
    public ASTBlock(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean containsComment() {
        return this.containsComment;
    }

    @InternalApi
    @Deprecated
    public void setContainsComment() {
        this.containsComment = true;
    }

}
