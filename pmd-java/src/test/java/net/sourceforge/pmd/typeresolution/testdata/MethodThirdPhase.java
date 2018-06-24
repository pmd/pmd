/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class MethodThirdPhase {
    void test() {
        // more args than parameters
        Exception a = vararg(10, (Number) null, (Number) null);

        // less args than parameters
        Exception b = vararg(10);

        // component type determined properly
        int c = vararg(10, "", "", "");

        // most specific vararg method
        String d = mostSpecific(10, 10, 10);
    }

    Exception mostSpecific(Number... b) {
        return null;
    }

    String mostSpecific(Integer... b) {
        return null;
    }

    Exception vararg(int a, Number... b) {
        return null;
    }

    int vararg(int a, String c, String... b) {
        return 0;
    }
}
