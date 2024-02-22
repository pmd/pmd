/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A place in a text document, represented as line/column information.
 * <p>Line and column are 1-based.</p>
 */
public final class TextPos2d implements Comparable<TextPos2d> {

    private final int line;
    private final int column;

    private TextPos2d(int line, int column) {
        this.line = line;
        this.column = column;

        assert line > 0 && column > 0 : "Invalid position " + toTupleString();
    }

    /**
     * Returns the (1-based) line number.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the (1-based) column number.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Builds a new region from offset and length.
     *
     * @throws AssertionError If either parameter is negative
     */
    public static TextPos2d pos2d(int line, int column) {
        return new TextPos2d(line, column);
    }


    /** Compares the start offset, then the length of a region. */
    @Override
    public int compareTo(@NonNull TextPos2d that) {
        int cmp = Integer.compare(this.getLine(), that.getLine());
        if (cmp != 0) {
            return cmp;
        }
        return Integer.compare(this.getColumn(), that.getColumn());
    }

    /**
     * Returns a string looking like {@code "(line=2, column=4)"}.
     */
    public String toTupleString() {
        return "(line=" + line + ", column=" + column + ")";
    }

    /**
     * Returns a string looking like {@code "line 2, column 4")}.
     */
    public String toDisplayStringInEnglish() {
        return "line " + line + ", column " + column;
    }

    /**
     * Returns a string looking like {@code "2:4")}.
     */
    public String toDisplayStringWithColon() {
        return line + ":" + column;
    }

    @Override
    public String toString() {
        return "!debug only! Pos2d(line=" + line + ", column=" + column + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextPos2d)) {
            return false;
        }
        TextPos2d that = (TextPos2d) o;
        return line == that.getLine()
            && column == that.getColumn();
    }

    @Override
    public int hashCode() {
        return line * 31 + column;
    }
}
