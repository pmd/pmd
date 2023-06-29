/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class Foo {
    public void unusedMethod() {
        // ääää // at least 4 umlauts are required in order to
        // reproduce the ArrayIndexOutOfBoundsException
        Integer a;
        // note: there should be no additional characters
        // after this method, only the two closing brackets
    }
}
