/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class ArrayTypes {

    @SuppressWarnings("unused")
    public void test() {
        int[] a = new int[1];
        Object[][] b = new Object[1][0];
        ArrayTypes[][][] c = new ArrayTypes[][][] { new ArrayTypes[1][2] };
    }
}
