package net.sourceforge.pmd.typeresolution.testdata;

public class MethodPotentialApplicability {
    void test() {
        int a = vararg("", "");
        int b = vararg("", "", 10);

        String c = notVararg(0, 0);

        Number d = noArguments();

        // TODO: add test for: if there are type parameters then method is either non-generic or type arg airty matches
    }

    // test no arguments
    Number noArguments() {return null;}

    // test not vararg mathching arity
    String notVararg(int a, int b) {return null;}

    Exception notVararg(int a) {return null;}

    // test if variable arity with arity n -> then call arity >= n-1
    int vararg(String b, String c, int... a) { return 0;}

    Exception vararg(int... b) {return null;}
}
