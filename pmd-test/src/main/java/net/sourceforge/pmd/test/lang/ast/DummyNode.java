/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang.ast;

import java.util.Objects;

import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.TextRegion;

public class DummyNode extends AbstractNode<DummyNode, DummyNode> {

    private String image;
    private TextRegion region = TextRegion.caretAt(0);

    public DummyNode withCoords(TextRegion region) {
        this.region = Objects.requireNonNull(region);
        return this;
    }

    public DummyNode newChild() {
        DummyNode child = new DummyNode();
        addChild(child, getNumChildren());
        return child;
    }

    @Override
    public TextRegion getTextRegion() {
        return region;
    }

    @Deprecated
    @Override
    public String toString() {
        return "dummyNode";
    }

    @Override
    public String getXPathNodeName() {
        return "dummyNode";
    }

    @Override
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
