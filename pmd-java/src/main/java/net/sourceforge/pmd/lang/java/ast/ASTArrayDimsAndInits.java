/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTArrayDimsAndInits extends AbstractJavaNode implements Dimensionable {

    private int arrayDepth;

    @InternalApi
    @Deprecated
    public ASTArrayDimsAndInits(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTArrayDimsAndInits(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Deprecated
    public void bumpArrayDepth() {
        arrayDepth++;
    }

    @Override
    @Deprecated
    public int getArrayDepth() {
        return arrayDepth;
    }

    @Override
    @Deprecated
    public boolean isArray() {
        return arrayDepth > 0; // should always be true...
    }
}
