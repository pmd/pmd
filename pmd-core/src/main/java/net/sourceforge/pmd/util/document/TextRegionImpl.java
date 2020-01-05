/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.Objects;

import net.sourceforge.pmd.internal.util.AssertionUtil;

/**
 * Immutable implementation of the {@link TextRegion} interface.
 */
class TextRegionImpl implements TextRegion {

    private final int startOffset;
    private final int length;

    /**
     * @throws IllegalArgumentException If the start offset or length are negative
     */
    private TextRegionImpl(int startOffset, int length) {
        this.startOffset = startOffset;
        this.length = length;
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public int getEndOffset() {
        return startOffset + length;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public boolean isEmpty() {
        return length == 0;
    }

    @Override
    public String toString() {
        return "Region(start=" + startOffset + ", len=" + length + ")";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextRegion)) {
            return false;
        }
        TextRegion that = (TextRegion) o;
        return startOffset == that.getStartOffset()
            && length == that.getLength();
    }

    @Override
    public int hashCode() {
        return Objects.hash(startOffset, length);
    }

    /**
     * Builds a new region from offset and length.
     */
    static TextRegion fromOffsetLength(int startOffset, int length) {
        return new TextRegionImpl(startOffset, length);
    }

    /**
     * Builds a new region from start and end offset.
     */
    static TextRegion fromBothOffsets(int startOffset, int endOffset) {
        return new TextRegionImpl(startOffset, endOffset - startOffset);
    }

    static final class WithLineInfo extends TextRegionImpl implements RegionWithLines {

        private final int beginLine;
        private final int endLine;
        private final int beginColumn;
        private final int endColumn;

        /**
         * @throws IllegalArgumentException If any of the line/col parameters are strictly less than 1
         * @throws IllegalArgumentException If the line and column are not correctly ordered
         * @throws IllegalArgumentException If the start offset or length are negative
         */
        WithLineInfo(int startOffset, int length, int beginLine, int beginColumn, int endLine, int endColumn) {
            super(startOffset, length);
            this.beginLine = AssertionUtil.requireOver1("Begin line", beginLine);
            this.endLine = AssertionUtil.requireOver1("End line", endLine);
            this.beginColumn = AssertionUtil.requireOver1("Begin column", beginColumn);
            this.endColumn = AssertionUtil.requireOver1("End column", endColumn);

            requireLinesCorrectlyOrdered();
        }

        private void requireLinesCorrectlyOrdered() {
            if (beginLine > endLine) {
                throw AssertionUtil.mustBe("endLine", endLine, ">= beginLine (= " + beginLine + ")");
            } else if (beginLine == endLine && beginColumn > endColumn) {
                throw AssertionUtil.mustBe("endColumn", endColumn, ">= beginColumn (= " + beginColumn + ")");
            }
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

}
