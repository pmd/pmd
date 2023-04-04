/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

public class DummyNode extends AbstractNode<DummyNode, DummyNode> {

    private final boolean findBoundary;
    private String xpathName;
    private String image;
    private final List<Attribute> attributes = new ArrayList<>();

    private TextRegion region = TextRegion.caretAt(0);

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
    public void addChild(DummyNode child, int index) {
        super.addChild(child, index);
    }

    @Override
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
    public TextRegion getTextRegion() {
        return region;
    }

    public void setRegion(TextRegion region) {
        this.region = region;
    }


    /**
     * Nodes with an image that starts with `#` also set the xpath name.
     */
    public void setImage(String image) {
        this.image = image;
        if (image.startsWith("#")) {
            xpathName = image;
        }
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

    public static class DummyRootNode extends DummyNode implements RootNode, GenericNode<DummyNode> {

        // FIXME remove this
        private static final LanguageProcessor STATIC_PROCESSOR =
            DummyLanguageModule.getInstance().createProcessor(DummyLanguageModule.getInstance().newPropertyBundle());
        private AstInfo<DummyRootNode> astInfo;

        public DummyRootNode() {
            TextDocument document = TextDocument.readOnlyString(
                "dummy text",
                TextFile.UNKNOWN_FILENAME,
                DummyLanguageModule.getInstance().getDefaultVersion()
            );
            astInfo = new AstInfo<>(
                new ParserTask(
                    document,
                    SemanticErrorReporter.noop(),
                    LanguageProcessorRegistry.singleton(STATIC_PROCESSOR)),
                this);
        }

        public DummyRootNode withTaskInfo(ParserTask task) {
            this.astInfo = new AstInfo<>(task, this);
            return this;
        }

        public DummyRootNode withNoPmdComments(Map<Integer, String> suppressMap) {
            this.astInfo = astInfo.withSuppressMap(suppressMap);
            return this;
        }

        @Override
        public AstInfo<DummyRootNode> getAstInfo() {
            return Objects.requireNonNull(astInfo, "no ast info");
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
