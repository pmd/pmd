/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class MethodPotentialApplicability {
    void test() {
        MethodPotentialApplicability field;
        field = this; // initialize like this to simplify XPath expr. in tests

        // Suffix/ASTName cases

        int a = vararg("");
        int b = vararg("", 10);

        String c = notVararg(0, 0);

        Number d = noArguments();

        Number e = field.noArguments();

        // PrimaryPrefix cases

        int f = this.vararg("");

        // TODO: add test for: if there are type parameters then method is either non-generic or type arg arity matches
    }

    // test if variable arity with arity n -> then call arity >= n-1
    int vararg(String b, int... a) {
        return 0;
    }

    Exception vararg(String a, String b, String c, int... d) {
        return null;
    }

    // test no arguments
    Number noArguments() {
        return null;
    }

    // test not vararg mathching arity
    String notVararg(int a, int b) {
        return null;
    }

    Exception notVararg(int a) {
        return null;
    }
}
