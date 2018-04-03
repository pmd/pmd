/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

/**
 * Immutable implementation of the {@link RegionByOffset} interface.
 */
public class RegionByOffsetImp implements RegionByOffset {
    private final int offset;
    private final int length;
    private final int offsetAfterEnding;

    public RegionByOffsetImp(final int offset, final int length) {
        this.offset = requireNonNegative(offset);
        this.length = requireNonNegative(length);
        offsetAfterEnding = offset + length;
    }

    private static int requireNonNegative(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
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

    @Override
    public int getOffsetAfterEnding() {
        return offsetAfterEnding;
    }
}
