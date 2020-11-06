/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class ArrayAccess {

    @SuppressWarnings("unused")
    public void test() {
        int[] a = new int[1];
        int aElement = a[0];

        Object[][] b = new Object[1][0];
        Object bElement = b[0][0];

        ArrayAccess[][][] c = new ArrayAccess[][][] { new ArrayAccess[1][2] };
        ArrayAccess cElement = c[0][0][0];
    }

}
