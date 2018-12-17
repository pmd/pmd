/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedimports;

public class PublicUtils {
    private PublicUtils() {
    }

    public static int g1(int x) {
        return x + 1;
    }

    public static int g2(int x) {
        return x + 1;
    }

    public static int g3(int x) {
        return x + 1;
    }
}
