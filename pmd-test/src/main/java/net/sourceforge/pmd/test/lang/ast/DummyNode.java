/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.lang.ast;

import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;
import net.sourceforge.pmd.util.document.FileLocation;

public class DummyNode extends AbstractNodeWithTextCoordinates<DummyNode, DummyNode> {

    private String image;
    protected int beginLine = -1;
    protected int endLine;
    protected int beginColumn = -1;
    protected int endColumn;

    public void setCoords(int bline, int bcol, int eline, int ecol) {
        beginLine = bline;
        beginColumn = bcol;
        endLine = eline;
        endColumn = ecol;
    }

    @Override
    public FileLocation getReportLocation() {
        return FileLocation.location("todo", beginLine, beginColumn, endLine, endColumn);
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
