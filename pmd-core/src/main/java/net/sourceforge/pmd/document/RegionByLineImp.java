/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Immutable implementation of the {@link RegionByLine} interface.
 */
public class RegionByLineImp implements RegionByLine {

    private final int beginLine;
    private final int endLine;
    private final int beginColumn;
    private final int endColumn;

    public RegionByLineImp(final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
        this.beginLine = requireNonNegative(beginLine);
        this.endLine = requireNonNegative(endLine);
        this.beginColumn = requireNonNegative(beginColumn);
        this.endColumn = requireNonNegative(endColumn);

        requireLinesCorrectlyOrdered();
    }

    private void requireLinesCorrectlyOrdered() {
        if (beginLine > endLine) {
            throw new IllegalArgumentException("endLine must be equal or greater than beginLine");
        }
    }

    private static int requireNonNegative(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException("parameter must be non-negative");
        }
        return value;
    }

    @Override
    public int getBeginLine() {
        return beginLine;
    }

    @Override
    public int getEndLine() {
        return endLine;
    }

    @Override
    public int getBeginColumn() {
        return beginColumn;
    }

    @Override
    public int getEndColumn() {
        return endColumn;
    }

    @Override
    public String toString() {
        return "RegionByLineImp{"
                + "beginLine=" + beginLine
                + ", endLine=" + endLine
                + ", beginColumn=" + beginColumn
                + ", endColumn=" + endColumn
                + '}';
    }
}
