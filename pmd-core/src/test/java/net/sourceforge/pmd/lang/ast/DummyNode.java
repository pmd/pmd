/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.util.document.FileLocation;

public class DummyNode extends AbstractNode<DummyNode, DummyNode> implements GenericNode<DummyNode> {
    private final boolean findBoundary;
    private final String xpathName;
    private final Map<String, String> userData = new HashMap<>();
    private String image;

    // note: since line & columns are 1-based, getReportLocation
    // will throw unless these are all set
    private FileLocation location;

    public DummyNode(String xpathName) {
        super();
        this.findBoundary = false;
        this.xpathName = xpathName;
    }

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

    public void setCoords(int bline, int bcol, int eline, int ecol) {
        this.location = FileLocation.location(":dummyFile:", bline, bcol, eline, ecol);
    }

    @Override
    public FileLocation getReportLocation() {
        assert location != null : "Should have called setCoords";
        return location;
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

        public DummyNodeTypeB() {
            super("dummyNodeB");
        }
    }

}
