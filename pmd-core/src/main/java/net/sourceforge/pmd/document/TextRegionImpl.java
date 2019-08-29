/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Immutable implementation of the {@link TextRegion} interface.
 */
class TextRegionImpl implements TextRegion {

    private final int startOffset;
    private final int length;

    TextRegionImpl(int offset, int length) {
        this.startOffset = requireNonNegative(offset);
        this.length = requireNonNegative(length);
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
    public String toString() {
        return "Region(start=" + startOffset + ", len=" + length + ")";
    }

    private static int requireNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Expected a non-negative value, got " + value);
        }
        return value;
    }

    final static class WithLineInfo extends TextRegionImpl implements RegionWithLines {

        private final int beginLine;
        private final int endLine;
        private final int beginColumn;
        private final int endColumn;

        WithLineInfo(int startOffset, int length, int beginLine, int beginColumn, int endLine, int endColumn) {
            super(startOffset, length);
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


        private int requireOver1(final int value) {
            if (value < 1) {
                throw new IllegalArgumentException("parameter must be >= 1");
            }
            return value;
        }

    }

}
