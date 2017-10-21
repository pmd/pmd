/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Default immutable implementation for the RegionByLine interface which is used by {@link GenericToken}
 */
public class RegionByLineImpl implements RegionByLine {
    private int beginLine;
    private int endLine;
    private int beginColumn;
    private int endColumn;

    /**
     * Create an immutable instance with all the corresponding fields. Every field requires to be non-negative
     * @param beginLine the line where the region begins
     * @param endLine the line where the region ends
     * @param beginColumn the column offset from the start of the begin line where the region begins
     * @param endColumn the column offset from the start of the end line where the region ends
     */
    public RegionByLineImpl(final int beginLine, final int endLine, final int beginColumn, final int endColumn) {
        setBeginLine(beginLine);
        setEndLine(endLine);
        setBeginColumn(beginColumn);
        setEndColumn(endColumn);
    }

    private void setBeginLine(final int beginLine) {
        this.beginLine = requireNonNegative(beginLine);
    }

    private void setEndLine(final int endLine) {
        this.endLine = requireNonNegative(endLine);
    }

    private void setBeginColumn(final int beginColumn) {
        this.beginColumn = requireNonNegative(beginColumn);
    }

    private void setEndColumn(final int endColumn) {
        this.endColumn = requireNonNegative(endColumn);
    }

    private int requireNonNegative(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException("value = " + value + " must be non-negative");
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
}
