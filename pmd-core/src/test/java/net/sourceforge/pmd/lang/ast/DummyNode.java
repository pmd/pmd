/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.HashMap;
import java.util.Map;

public class DummyNode extends AbstractNode {

    private final boolean findBoundary;
    private final String xpathName;
    private final Map<String, String> userData = new HashMap<>();

    public DummyNode(int id) {
        this(id, false);
    }

    public DummyNode(String xpathName) {
        super(0);
        this.findBoundary = false;
        this.xpathName = xpathName;
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

    public void setBeginColumn(int i) {
        beginColumn = i;
    }

    public void setBeginLine(int i) {
        beginLine = i;
    }

    public DummyNode setCoords(int bline, int bcol, int eline, int ecol) {
        beginLine = bline;
        beginColumn = bcol;
        endLine = eline;
        endColumn = ecol;
        return this;
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

    @Override
    public Map<String, String> getUserData() {
        return userData;
    }


    public static class DummyNodeTypeB extends DummyNode {

        @Override
        public String toString() {
            return getImage();
        }
    }

}
