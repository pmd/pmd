/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class MethodSecondPhase {
    void test() {
        // boxing and widening conversion
        String a = boxing(10, "");
        // boxing
        Exception b = boxing('a', "");
        // boxing and most specific
        int c = boxing(10L, "");

        // unboxing and widening conversion
        String d = unboxing("", (Integer) null);

        Exception e = unboxing("", (Character) null);
        int f = unboxing("", (Byte) null);
    }

    String boxing(Number a, String b) {
        return null;
    }

    Exception boxing(Character a, String b) {
        return null;
    }

    int boxing(Long a, String b) {
        return 0;
    }

    String unboxing(String b, long a) {
        return null;
    }

    Exception unboxing(String b, char a) {
        return null;
    }

    int unboxing(String b, short a) {
        return 0;
    }
}
