/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dummy;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.CommentNode;
import net.sourceforge.pmd.lang.rule.xpath.TextNode;
import net.sourceforge.pmd.reporting.ViolationSuppressor.SuppressionCommentWrapper;

/**
 * A node of the {@link DummyLanguageModule dummy language}.
 */
public class DummyNode extends AbstractNode<DummyNode, DummyNode> {

    private String xpathName = "dummyNode";
    private String image;
    private TextRegion region = TextRegion.caretAt(0);

    @Override
    public void addChild(DummyNode child, int index) {
        super.addChild(child, index);
    }

    @Override
    public void setParent(DummyNode node) {
        super.setParent(node);
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

    /**
     * Phony attribute that repeats the image 3 times.
     */
    public List<String> getLines() {
        return Arrays.asList(getImage(), getImage(), getImage());
    }

    /**
     * The root node of a dummy AST.
     */
    public static class DummyRootNode extends DummyNode implements RootNode, GenericNode<DummyNode> {

        private AstInfo<DummyRootNode> astInfo;

        public DummyRootNode(ParserTask task) {
            this.astInfo = new AstInfo<>(task, this);
        }

        public DummyRootNode withNoPmdComments(SuppressionCommentWrapper... suppressMap) {
            this.astInfo = astInfo.withSuppressionComments(Arrays.asList(suppressMap));
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

    /**
     * A text node, created for {@code (#text ...)}.
     */
    public static class DummyTextNode extends DummyNode implements TextNode {
        @Override
        public String getText() {
            return getImage();
        }

        @Override
        public String getXPathNodeName() {
            return TextNode.super.getXPathNodeName();
        }
    }

    /**
     * A comment node, created for {@code (#comment ...)}.
     */
    public static class DummyCommentNode extends DummyNode implements CommentNode {
        @Override
        public String getData() {
            return getImage();
        }

        @Override
        public String getXPathNodeName() {
            return CommentNode.super.getXPathNodeName();
        }
    }
}
