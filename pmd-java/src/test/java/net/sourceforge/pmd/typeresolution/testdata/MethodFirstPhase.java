/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MethodFirstPhase {
    void test() {
        //  primitive, char, simple
        int a = subtype(10, 'a', "");
        // TODO: add null, array types

        Exception b = vararg((Number) null);

        Set<String> set = new HashSet<>();
        set.addAll(Arrays.asList("a", "b")); // TODO: return type of method call Arrays.asList is missing
    }

    String vararg(Number... a) {
        return null;
    }

    Exception vararg(Number a) {
        return null;
    }

    void stringVarargs(String... s) {
        
    }

    void classVarargs(Class<?>... c) {
        
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
