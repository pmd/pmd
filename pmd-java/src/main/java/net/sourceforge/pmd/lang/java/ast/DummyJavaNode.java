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

    @InternalApi
    @Deprecated
    public DummyJavaNode(JavaParser parser, int id) {
        super(parser, id);
    }
}
