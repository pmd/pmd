/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedimports;

final class PackagePrivateUtils {
    private PackagePrivateUtils() {
    }

    static int f1(int x) {
        return x + 1;
    }

    static int f2(int x) {
        return x + 1;
    }

    static int f3(int x) {
        return x + 1;
    }
}
