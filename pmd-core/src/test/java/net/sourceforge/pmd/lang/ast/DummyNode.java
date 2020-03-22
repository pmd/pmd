/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;

public class DummyNode extends AbstractNodeWithTextCoordinates<DummyNode> {

    private final boolean findBoundary;
    private final String xpathName;
    private final Map<String, String> userData = new HashMap<>();

    public DummyNode() {
        this(false);
    }

    public DummyNode(boolean findBoundary) {
        this(findBoundary, "dummyNode");
    }

    public DummyNode(boolean findBoundary, String xpathName) {
        super();
        this.findBoundary = findBoundary;
        this.xpathName = xpathName;
    }

    @Override
    public void setCoords(int bline, int bcol, int eline, int ecol) {
        super.setCoords(bline, bcol, eline, ecol);
    }

    @Override
    public String toString() {
        return getImage();
    }

    @Override
    public String getXPathNodeName() {
        return xpathName;
    }

    @Override
    public boolean isFindBoundary() {
        return findBoundary;
    }

    public Map<String, String> getUserData() {
        return userData;
    }

    @Override
    public void addChild(DummyNode child, int index) {
        super.addChild(child, index);
    }

    @Override
    public DummyNode getChild(int index) {
        return super.getChild(index);
    }

    public static class DummyNodeTypeB extends DummyNode {

    }

}
