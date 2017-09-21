/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes;

import java.util.Objects;

public class RegionImp implements Region {

    private int offset;
    private int length;

    public RegionImp(final int offset, final int length) {
        setOffset(offset);
        setLength(length);
    }

    public RegionImp(final Region region) {
        Objects.requireNonNull(region);

        setOffset(region.getOffset());
        setLength(region.getLength());
    }

    @Override
    public boolean contains(final Region otherRegion) {
        Objects.requireNonNull(otherRegion, "otherRegion cannot be null");

        return isOffsetLowerThan(otherRegion) && hasLargerOffsetEndingThan(otherRegion);
    }

    private boolean isOffsetLowerThan(final Region otherRegion) {
        return getOffset() <= otherRegion.getOffset();
    }

    private boolean hasLargerOffsetEndingThan(final Region otherRegion) {
        return getOffsetAfterEnding() >= otherRegion.getOffsetAfterEnding();
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOffset(final int newOffset) {
        requireNonNegative(newOffset);
        offset = newOffset;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void setLength(final int newLength) {
        requireNonNegative(newLength);
        length = newLength;
    }

    @Override
    public int getOffsetAfterEnding() {
        return getOffset() + getLength();
    }

    @Override
    public void sumOffset(final int offsetToSum) {
        final int checkedSum = getOffset() + offsetToSum;

        if (checkedSum < 0) {
            throw new IllegalArgumentException("offsetToSum should not make the offset negative");
        }
        setOffset(checkedSum);
    }

    @Override
    public void sumLength(final int lengthToSum) {
        final int checkedSum = getLength() + lengthToSum;

        if (checkedSum < 0) {
            throw new IllegalArgumentException("lengthToSum should not make the offset negative");
        }

        setLength(checkedSum);
    }

    private void requireNonNegative(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non negative");
        }
    }
}
