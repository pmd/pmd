/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class VarargsZeroArity {

    public void tester() {
        int var = aMethod();
        String var2 = aMethod("");
    }

    public int aMethod() {
        return 0;
    }

    public String aMethod(String... args) {
        return null;
    }
}
