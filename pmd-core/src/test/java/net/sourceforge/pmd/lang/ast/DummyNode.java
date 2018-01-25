/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

public class DummyNode extends AbstractNode {
    public DummyNode(int id) {
        super(id);
    }

    @Deprecated
    @Override
    public String toString() {
        return "dummyNode";
    }

    @Override
    public String getXPathNodeName() {
        return "dummyNode";
    }
}
