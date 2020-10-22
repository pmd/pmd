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
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
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
