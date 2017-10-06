/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class OverloadedMethods {

    public static boolean equals(byte[] a, byte[] b) {
        return false;
    }

    public static boolean equals(Object[] a, Object[] b) {
        return false;
    }
}
