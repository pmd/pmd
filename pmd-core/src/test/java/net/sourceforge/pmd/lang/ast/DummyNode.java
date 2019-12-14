/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.HashMap;
import java.util.Map;

public class DummyNode extends AbstractNode {
    private final boolean findBoundary;
    private final Map<String, String> userData = new HashMap<>();

    public DummyNode(int id) {
        this(id, false);
    }
    
    public DummyNode(int id, boolean findBoundary) {
        super(id);
        this.findBoundary = findBoundary;
    }

    public void setBeginColumn(int i) {
        beginColumn = i;
    }

    public void setBeginLine(int i) {
        beginLine = i;
    }

    public void setCoords(int bline, int bcol, int eline, int ecol) {
        beginLine = bline;
        beginColumn = bcol;
        endLine = eline;
        endColumn = ecol;
    }

    @Override
    public String toString() {
        return "dummyNode";
    }

    @Override
    public String getXPathNodeName() {
        return "dummyNode";
    }
    
    @Override
    public boolean isFindBoundary() {
        return findBoundary;
    }

    @Override
    public Map<String, String> getUserData() {
        return userData;
    }
}
