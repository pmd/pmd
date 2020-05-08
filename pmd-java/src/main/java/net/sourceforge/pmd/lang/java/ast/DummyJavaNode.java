/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * This is a basic JavaNode implementation, useful when needing to create a
 * dummy node.
 */
@Deprecated
@InternalApi
public class DummyJavaNode extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public DummyJavaNode(int id) {
        super(id);
    }

    @Override
    public void setImage(String image) {
        super.setImage(image);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return data;
    }

    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        // do nothing
    }
}
