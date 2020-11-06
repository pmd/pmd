/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class VarargsAsFixedArity {

    public void tester() {
        int var = aMethod("");
        String var2 = aMethod();
        String var3 = aMethod("", "");
        String var4 = aMethod(new Object[] { null });
    }

    public int aMethod(Object s) {
        return 0;
    }

    public String aMethod(Object... s) {
        return null;
    }
}
