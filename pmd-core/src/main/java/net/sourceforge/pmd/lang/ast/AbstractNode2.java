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
public abstract class AbstractNode2<T extends AbstractNode2<T>> extends AbstractNode<T> {

    protected int beginLine = -1;
    protected int endLine = -1;
    protected int beginColumn = -1;
    protected int endColumn = -1;

    protected AbstractNode2() {}


    protected AbstractNode2(final int theBeginLine,
                            final int theEndLine,
                            final int theBeginColumn,
                            final int theEndColumn) {

        beginLine = theBeginLine;
        endLine = theEndLine;
        beginColumn = theBeginColumn;
        endColumn = theEndColumn;
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
