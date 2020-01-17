/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

public class DummyNode extends AbstractNode {
    private final boolean findBoundary;
    private final String xpathName;

    public DummyNode(int id) {
        this(id, false);
    }

    public DummyNode() {
        this(0, false);
    }

    public DummyNode(int id, boolean findBoundary) {
        this(id, findBoundary, "dummyNode");
    }

    public DummyNode(int id, boolean findBoundary, String xpathName) {
        super(id);
        this.findBoundary = findBoundary;
        this.xpathName = xpathName;
    }

    @Override
    public String toString() {
        return xpathName;
    }

    @Override
    public String getXPathNodeName() {
        return xpathName;
    }

    @Override
    public boolean isFindBoundary() {
        return findBoundary;
    }
}
