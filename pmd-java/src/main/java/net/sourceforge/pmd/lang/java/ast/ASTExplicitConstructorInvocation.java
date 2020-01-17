/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTExplicitConstructorInvocation extends AbstractJavaNode {

    private String thisOrSuper;

    @InternalApi
    @Deprecated
    public ASTExplicitConstructorInvocation(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTExplicitConstructorInvocation(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public int getArgumentCount() {
        if (this.getNumChildren() == 1) {
            return ((ASTArguments) this.getChild(0)).getArgumentCount();
        } else {
            return ((ASTArguments) this.getChild(1)).getArgumentCount();
        }
    }

    @InternalApi
    @Deprecated
    public void setIsThis() {
        this.thisOrSuper = "this";
    }

    @InternalApi
    @Deprecated
    public void setIsSuper() {
        this.thisOrSuper = "super";
    }

    public boolean isThis() {
        return thisOrSuper != null && "this".equals(thisOrSuper);
    }

    public boolean isSuper() {
        return thisOrSuper != null && "super".equals(thisOrSuper);
    }
}
