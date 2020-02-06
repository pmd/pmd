/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.Objects;

/**
 * Immutable implementation of the {@link TextRegion} interface.
 */
final class TextRegionImpl implements TextRegion {

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

}
