/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;


public final class AssertionUtil {

    private AssertionUtil() {
        // utility class
    }

    public static void assertArgNonNegative(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Argument should be positive or null, got " + n);
        }
    }

}
