/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

public class DummyNode extends AbstractNodeWithTextCoordinates<DummyNode, DummyNode> implements GenericNode<DummyNode> {

    private final boolean findBoundary;
    private final String xpathName;
    private final Map<String, String> userData = new HashMap<>();
    private String image;

    public DummyNode() {
        this(false);
    }

    public DummyNode(boolean findBoundary) {
        this(findBoundary, "dummyNode");
    }

    public DummyNode(boolean findBoundary, String xpathName) {
        this.findBoundary = findBoundary;
        this.xpathName = xpathName;
    }

    public void publicSetChildren(DummyNode... children) {
        assert getNumChildren() == 0;
        for (int i = children.length - 1; i >= 0; i--) {
            addChild(children[i], i);
        }
    }

    @Override
    public void setCoords(int bline, int bcol, int eline, int ecol) {
        super.setCoords(bline, bcol, eline, ecol);
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String getImage() {
        return image;
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
