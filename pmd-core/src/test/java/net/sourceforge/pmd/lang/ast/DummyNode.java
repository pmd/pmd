/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

public class DummyNode extends AbstractNode<DummyNode, DummyNode> implements GenericNode<DummyNode> {

    private final boolean findBoundary;
    private final String xpathName;
    private final Map<String, String> userData = new HashMap<>();
    private String image;
    private final List<Attribute> attributes = new ArrayList<>();

    private int bline = 1;
    private int bcol = 1;
    private int eline = 1;
    private int ecol = 1;

    public DummyNode(String xpathName) {
        this(false, xpathName);
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

        Iterator<Attribute> iter = super.getXPathAttributesIterator();
        while (iter.hasNext()) {
            attributes.add(iter.next());
        }
    }

    @Override
    public DummyNode getParent() {
        return super.getParent();
    }

    @Override
    public void addChild(DummyNode child, int index) {
        super.addChild(child, index);
    }

    @Override
    public DummyNode getChild(int index) {
        return super.getChild(index);
    }

    public void setParent(DummyNode node) {
        super.setParent(node);
    }

    public void publicSetChildren(DummyNode... children) {
        assert getNumChildren() == 0;
        for (int i = children.length - 1; i >= 0; i--) {
            addChild(children[i], i);
        }
    }

    public DummyNode setCoords(int bline, int bcol, int eline, int ecol) {
        this.bline = bline;
        this.bcol = bcol;
        this.eline = eline;
        this.ecol = ecol;
        return this;
    }

    public DummyNode setCoords(TextDocument document, TextRegion region) {
        FileLocation loc = document.toLocation(region);
        return setCoords(loc.getStartLine(), loc.getStartColumn(), loc.getEndLine(), loc.getEndColumn());
    }

    public DummyNode setCoords(TextRange2d region) {
        return setCoords(region.getStartLine(), region.getStartColumn(), region.getEndLine(), region.getEndColumn());
    }

    public TextRange2d getCoords() {
        return TextRange2d.range2d(bline, bcol, eline, ecol);
    }

    @Override
    public FileLocation getReportLocation() {
        return getTextDocument().toLocation(TextRange2d.range2d(bline, bcol, eline, ecol));
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
        return getXPathNodeName() + "[@Image=" + getImage() + "]";
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

        private Map<Integer, String> suppressMap = Collections.emptyMap();
        private TextDocument sourceText = TextDocument.readOnlyString(
            "dummy text",
            TextFile.UNKNOWN_FILENAME,
            DummyLanguageModule.getInstance().getDefaultVersion()
        );

        private AstInfo<DummyRootNode> astInfo;

        public DummyRootNode withTaskInfo(ParserTask task) {
            this.astInfo = new AstInfo<>(task, this);
            return this;
        }

        public DummyRootNode withNoPmdComments(Map<Integer, String> suppressMap) {
            this.suppressMap = suppressMap;
            return this;
        }

        @Override
        public AstInfo<DummyRootNode> getAstInfo() {
            return Objects.requireNonNull(astInfo, "no ast info, don't use DummyRootNode's ctor directly");
        }

        @Override
        public String getXPathNodeName() {
            return "dummyRootNode";
        }
    }

    public static class DummyNodeTypeB extends DummyNode {

        public DummyNodeTypeB() {
            super("dummyNodeB");
        }
    }
}
