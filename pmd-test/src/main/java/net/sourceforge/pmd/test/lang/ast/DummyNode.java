/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang.ast;

import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;

public class DummyNode extends AbstractNodeWithTextCoordinates<DummyNode, DummyNode> {

    private String image;

    @Override
    public void setCoords(int bline, int bcol, int eline, int ecol) {
        super.setCoords(bline, bcol, eline, ecol);
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
