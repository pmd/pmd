/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Base class for all implementations of the Node interface.
 *
 * <p>Please use the {@link Node} interface wherever possible and
 * not this class, unless you're compelled to do so.
 *
 * <p>Note that nearly all methods of the {@link Node} interface
 * will have default implementations with PMD 7.0.0, so that it
 * will not be necessary to extend this class directly.
 */
public abstract class AbstractNodeWithTextCoordinates<T extends Node> extends AbstractNode<T> {

    protected int beginLine = -1;
    protected int endLine = -1;
    protected int beginColumn = -1;
    protected int endColumn = -1;

    protected AbstractNodeWithTextCoordinates() {
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
        beginLine = bline;
        beginColumn = bcol;
        endLine = eline;
        endColumn = ecol;
    }

}
