/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTResultType extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTResultType(int id) {
        super(id);
    }

    public boolean returnsArray() {
        return !isVoid() && ((ASTType) getChild(0)).isArray();
    }

    public boolean isVoid() {
        return getNumChildren() == 0;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
