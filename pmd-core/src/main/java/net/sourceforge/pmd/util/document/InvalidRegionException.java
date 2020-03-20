/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

/**
 * Thrown when an invalid offset or region is passed to methods like
 * {@link TextDocument#createRegion(int, int)} or {@link TextEditor#replace(TextRegion, String)}.
 */
public final class InvalidRegionException extends IllegalArgumentException {

    private static final String NOT_IN_RANGE = "Region {start=%d, end=%d} is not in range of this document (length %d)";
    private static final String NEGATIVE = "%s is negative, got %d";
    private static final String INVALID_LINE_RANGE = "Line range %d..%d is not in range of this document (%d lines) (line numbers are 1-based)";

    private InvalidRegionException(String message) {
        super(message);
    }

    static InvalidRegionException negativeQuantity(String offsetId, int actual) {
        return new InvalidRegionException(String.format(NEGATIVE, offsetId, actual));
    }

    static InvalidRegionException invalidLineRange(int start, int end, int numLines) {
        return new InvalidRegionException(String.format(INVALID_LINE_RANGE, start, end, numLines));
    }

    static InvalidRegionException regionOutOfBounds(int start, int end, int maxLen) {
        return new InvalidRegionException(String.format(NOT_IN_RANGE, start, end, maxLen));
    }
}
