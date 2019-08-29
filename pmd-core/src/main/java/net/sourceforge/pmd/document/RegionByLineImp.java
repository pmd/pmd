/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Immutable implementation of the {@link TextRegion.RegionByLine} interface.
 */
class RegionByLineImp implements TextRegion.RegionByLine {

    private final int beginLine;
    private final int endLine;
    private final int beginColumn;
    private final int endColumn;

    RegionByLineImp(final int beginLine, final int beginColumn, final int endLine, final int endColumn) {
        this.beginLine = requireOver1(beginLine);
        this.endLine = requireOver1(endLine);
        this.beginColumn = requireOver1(beginColumn);
        this.endColumn = requireOver1(endColumn);

        requireLinesCorrectlyOrdered();
    }

    private void requireLinesCorrectlyOrdered() {
        if (beginLine > endLine) {
            throw new IllegalArgumentException("endLine must be equal or greater than beginLine");
        }
    }

    private static int requireOver1(final int value) {
        if (value < 1) {
            throw new IllegalArgumentException("parameter must be >= 1");
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
