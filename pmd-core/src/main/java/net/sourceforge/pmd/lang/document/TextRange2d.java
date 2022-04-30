/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.util.Comparator;
import java.util.Objects;

/**
 * A place in a text document, represented as line/column information.
 */
public final class TextRange2d implements Comparable<TextRange2d> {
    private static final Comparator<TextRange2d> COMPARATOR =
        Comparator.comparingInt(TextRange2d::getStartLine)
            .thenComparingInt(TextRange2d::getStartColumn)
            .thenComparingInt(TextRange2d::getEndLine)
            .thenComparingInt(TextRange2d::getEndColumn);

    private final int startLine;
    private final int startCol;
    private final int endLine;
    private final int endCol;

    public TextRange2d(int startLine, int startCol, int endLine, int endCol) {
        this.startLine = startLine;
        this.startCol = startCol;
        this.endLine = endLine;
        this.endCol = endCol;
        assert startCol >= 1 && startLine >= 1 && endLine >= 1 && endCol >= 1
            : "Not a valid range " + toDisplayStringWithColon();
    }


    public TextPos2d getStartPos() {
        return TextPos2d.pos2d(startLine, startCol);
    }

    public TextPos2d getEndPos() {
        return TextPos2d.pos2d(endLine, endCol);
    }

    public String toDisplayStringWithColon() {
        return getStartPos().toDisplayStringWithColon() + "-"
            + getEndPos().toDisplayStringWithColon();
    }

    public int getStartLine() {
        return startLine;
    }

    public int getStartColumn() {
        return startCol;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getEndColumn() {
        return endCol;
    }

    public static TextRange2d range2d(TextPos2d start, TextPos2d end) {
        return new TextRange2d(start.getLine(), start.getColumn(), end.getLine(), end.getColumn());
    }

    public static TextRange2d range2d(int bline, int bcol, int eline, int ecol) {
        return new TextRange2d(bline, bcol, eline, ecol);
    }

    public static TextRange2d fullLine(int line, int lineLength) {
        return new TextRange2d(line, 1, line, 1 + lineLength);
    }

    @Override
    public int compareTo(TextRange2d o) {
        return COMPARATOR.compare(this, o);
    }

    public boolean contains(TextRange2d range) {
        return getStartPos().compareTo(range.getStartPos()) <= 0 && getEndPos().compareTo(range.getEndPos()) >= 0;
    }

    public boolean contains(TextPos2d pos) {
        return getStartPos().compareTo(pos) <= 0 && getEndPos().compareTo(pos) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextRange2d that = (TextRange2d) o;
        return this.getStartPos().equals(that.getStartPos())
            && this.getEndPos().equals(that.getEndPos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStartPos(), getEndPos());
    }

    @Override
    public String toString() {
        return "!debug only! [" + getStartPos().toTupleString()
            + " - " + getEndPos().toTupleString() + ']';
    }

}
