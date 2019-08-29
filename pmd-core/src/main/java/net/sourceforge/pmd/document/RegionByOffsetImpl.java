/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Immutable implementation of the {@link TextRegion.RegionByOffset} interface.
 */
class RegionByOffsetImpl implements TextRegion.RegionByOffset {
    private final int offset;
    private final int length;

    RegionByOffsetImpl(final int offset, final int length) {
        this.offset = requireNonNegative(offset);
        this.length = requireNonNegative(length);
    }


    private static int requireNonNegative(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Expected a non-negative value, got " + value);
        }
        return value;
    }

    @Override
    public int getStartOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "Region(start=" + offset + ", len=" + length + ")";
    }
}
