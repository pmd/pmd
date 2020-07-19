/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTInitializer extends AbstractJavaNode {

    private boolean isStatic;

    @InternalApi
    @Deprecated
    public ASTInitializer(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public boolean isStatic() {
        return isStatic;
    }

    @InternalApi
    @Deprecated
    public void setStatic() {
        isStatic = true;
    }
}
