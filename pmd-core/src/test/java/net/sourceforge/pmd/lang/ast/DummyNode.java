/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.internal.FileNameXPathFunction;

public class DummyNode extends AbstractNode {

    private final boolean findBoundary;
    private final String xpathName;
    private final List<Attribute> attributes = new ArrayList<>();

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

        Iterator<Attribute> iter = super.getXPathAttributesIterator();
        while (iter.hasNext()) {
            attributes.add(iter.next());
        }
    }

    @Override
    public DummyNode getParent() {
        return (DummyNode) super.getParent();
    }

    @Override
    public DummyNode getChild(int index) {
        return (DummyNode) super.getChild(index);
    }

    public void setParent(DummyNode node) {
        jjtSetParent(node);
    }

    public void setBeginColumn(int i) {
        beginColumn = i;
    }

    public void setBeginLine(int i) {
        beginLine = i;
    }

    public void setEndLine(int i) {
        super.testingOnlySetEndColumn(i);
    }

    public void setEndColumn(int i) {
        super.testingOnlySetEndColumn(i);
    }

    public void setCoords(int bline, int bcol, int eline, int ecol) {
        beginLine = bline;
        beginColumn = bcol;
        endLine = eline;
        endColumn = ecol;
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

    public void setXPathAttribute(String name, String value) {
        attributes.add(new Attribute(this, name, value));
    }


    public void clearXPathAttributes() {
        attributes.clear();
    }

    @Override
    public Iterator<Attribute> getXPathAttributesIterator() {
        return attributes.iterator();
    }

    public static class DummyRootNode extends DummyNode implements RootNode {

        public DummyRootNode() {
            this("afile.txt");
        }

        public DummyRootNode(String fileName) {
            super(0);
            // remove prefixed path segments.
            String simpleFileName = Paths.get(fileName).getFileName().toString();
            getUserMap().set(FileNameXPathFunction.FILE_NAME_KEY, simpleFileName);
        }
    }
}
