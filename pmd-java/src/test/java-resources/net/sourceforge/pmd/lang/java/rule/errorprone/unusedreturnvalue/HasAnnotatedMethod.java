/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue;

public class HasAnnotatedMethod {
    private HasAnnotatedMethod() {}

    @CheckReturnValue
    public static int annotatedMethod() {
        return 42;
    }

    public static int notAnnotatedMethod() {
        return 4;
    }
}
