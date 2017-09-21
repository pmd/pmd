/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes;

/**
 * Represents a region at {@link net.sourceforge.pmd.autofixes.Document }, by using an offset/index and the length
 * of that region, covering the region from [offset; offset + length - 1]
 */
public interface Region {

    /**
     * Checks if another region is contained in this region's instance by checking if this instance's
     * offset is lower than the other region's offset and if this instance's offset ending is larger
     * than the other region's offset ending.
     *
     * @param otherRegion the other region to check if it's contained in this instance's region
     * @return true if the other region is contained; false if it's not
     */
    boolean contains(Region otherRegion);

    /**
     * Get the offset of the region. It is a non-negative number.
     *
     * @return the offset
     */
    int getOffset();

    /**
     * Sets the offset of the region. It must not be a negative number
     *
     * @param newOffset the new offset of the region
     */
    void setOffset(int newOffset);

    /**
     * Gets the length of the region. It is a non-negative number.
     *
     * @return the length
     */
    int getLength();

    /**
     * Sets the length of the region. It must not be a negative number
     *
     * @param newLength the new length of the region
     */
    void setLength(int newLength);

    /**
     * Gets the first index after this region ends.
     *
     * @return the sum of the offset and the length
     */
    int getOffsetAfterEnding();

    /**
     * Add a number to the current offset. The result must be a non-negative number
     *
     * @param offsetToSum the number to add to the offset.
     */
    void sumOffset(int offsetToSum);

    /**
     * Add a number to the current length. The result must be a non-negative number
     *
     * @param lengthToSum the number to add to the length.
     */
    void sumLength(int lengthToSum);
}
