/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

/**
 * Base class for implementations that need fields to store text
 * coordinates.
 */
public abstract class AbstractNodeWithTextCoordinates<B extends AbstractNodeWithTextCoordinates<B, T>, T extends GenericNode<T>> extends AbstractNode<B, T> {

    protected int beginLine = -1;
    protected int endLine = -1;
    protected int beginColumn = -1;
    protected int endColumn = -1;

    protected AbstractNodeWithTextCoordinates() {
        // only for subclassing
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    protected void setCoords(int bline, int bcol, int eline, int ecol) {
        assert bline >= 1 && bcol >= 1 && eline >= 1 && ecol >= 1 : "coordinates are 1-based";
        assert bline <= eline && (bline != eline || bcol <= ecol) : "coordinates must be ordered";
        beginLine = bline;
        beginColumn = bcol;
        endLine = eline;
        endColumn = ecol;
    }

}
