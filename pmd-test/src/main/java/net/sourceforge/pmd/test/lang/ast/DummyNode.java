/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang.ast;

import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.util.document.FileLocation;

public class DummyNode extends AbstractNode<DummyNode, DummyNode> {

    private String image;
    private FileLocation location;

    public void setCoords(int bline, int bcol, int eline, int ecol) {
        this.location = FileLocation.location(":dummyFile:", bline, bcol, eline, ecol);
    }

    @Override
    public FileLocation getReportLocation() {
        assert location != null : "Should have called setCoords";
        return location;
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
