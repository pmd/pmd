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

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

public class DummyNode extends AbstractNodeWithTextCoordinates<DummyNode, DummyNode> implements GenericNode<DummyNode> {
    private final boolean findBoundary;
    private final String xpathName;
    private final Map<String, String> userData = new HashMap<>();
    private String image;
    private final List<Attribute> attributes = new ArrayList<>();

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
        private String filename = "sample.dummy";
        private LanguageVersion languageVersion = DummyLanguageModule.getInstance().getDefaultVersion();
        private String sourceText = "dummy text";


        public DummyRootNode withLanguage(LanguageVersion languageVersion) {
            this.languageVersion = languageVersion;
            return this;
        }

        public DummyRootNode withSourceText(String sourceText) {
            this.sourceText = sourceText;
            return this;
        }

        public DummyRootNode withNoPmdComments(Map<Integer, String> suppressMap) {
            this.suppressMap = suppressMap;
            return this;
        }


        public DummyRootNode withFileName(String filename) {
            this.filename = filename;
            return this;
        }


        @Override
        public AstInfo<DummyRootNode> getAstInfo() {
            return new AstInfo<>(
                    filename,
                    languageVersion,
                    sourceText,
                    this,
                    suppressMap
            );
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
