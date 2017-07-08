/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class MethodFirstPhase {
    void test() {
        //  primitive, char, simple
        int a = subtype(10, 'a', "");
        // TODO: add null, array types

        Exception b = vararg((Number) null);
    }

    String vararg(Number... a) {
        return null;
    }

    Exception vararg(Number a) {
        return null;
    }


    Exception subtype(short a, int b, String c) {
        return null;
    }

    int subtype(long a, int b, String c) {
        return 0;
    }

    Exception subtype(long a, byte b, String c) {
        return null;
    }
}
