/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

/**
 * Thrown when an invalid offset or region is passed to methods like
 * {@link TextDocument#createRegion(int, int)} or {@link TextEditor#replace(TextRegion, String)}.
 */
public class InvalidRegionException extends IllegalArgumentException {

    private static final String NOT_IN_RANGE = "Region {start=%d, end=%d} is not in range of this document (length %d)";
    private static final String NEGATIVE = "%s is negative, got %d";

    private InvalidRegionException(int start, int end, int maxLen) {
        super(String.format(NOT_IN_RANGE, start, end, maxLen));
    }

    private InvalidRegionException(String offsetId, int actual) {
        super(String.format(NEGATIVE, offsetId, actual));
    }


    static InvalidRegionException negativeQuantity(String offsetId, int actual) {
        return new InvalidRegionException(offsetId, actual);
    }

    static InvalidRegionException regionOutOfBounds(int start, int end, int maxLen) {
        return new InvalidRegionException(start, end, maxLen);
    }
}
