/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution.testdata;

public class UsesJavaStreams {
    interface WithStaticAndDefaultMethod {
        static void performOn() { }
        default void myToString() {}
    }

    class ImplWithStaticAndDefaultMethod implements WithStaticAndDefaultMethod {}

    public void performStuff() {
        WithStaticAndDefaultMethod.performOn();
        new ImplWithStaticAndDefaultMethod().myToString();
    }
}
