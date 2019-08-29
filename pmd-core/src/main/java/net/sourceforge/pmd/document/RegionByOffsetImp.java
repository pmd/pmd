/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Immutable implementation of the {@link TextRegion.RegionByOffset} interface.
 */
class RegionByOffsetImp implements TextRegion.RegionByOffset {
    private final int offset;
    private final int length;

    RegionByOffsetImp(final int offset, final int length) {
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
    public int getOffset() {
        return offset;
    }

    @Override
    public int getLength() {
        return length;
    }

}
