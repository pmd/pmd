package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.lang.java.rule.strings.InefficientEmptyStringCheckRule;

public class MethodFirstPhase {
    void test() {
        //  primitive, char, null, arrays
        int a = subtype(10, 'a', null, new Integer[0]);

        Exception b = vararg((Object) null);
        String c = vararg((Object[]) null);
    }

    String vararg(Object... a) {return null;}
    Exception vararg(Object a) {return null;}


    Exception subtype(short a, int b, String c, Number[] d) { return null; }

    int subtype(long a, int b, String c, Number[] d) {return 0;}

    Exception subtype(long a, byte b, String c, Number[] d) { return null; }

    Exception subtype(long a, int b, String c, Double[] d) {return null; }
}
