/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated Replaced by {@link ASTArrayDimensions}
 */
@Deprecated
public class ASTArrayDimsAndInits extends AbstractJavaNode {

    private int arrayDepth;

    ASTArrayDimsAndInits(int id) {
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


    @Deprecated
    public void bumpArrayDepth() {
        arrayDepth++;
    }

    @Deprecated
    public int getArrayDepth() {
        return arrayDepth;
    }

    @Deprecated
    public boolean isArray() {
        return arrayDepth > 0; // should always be true...
    }
}
