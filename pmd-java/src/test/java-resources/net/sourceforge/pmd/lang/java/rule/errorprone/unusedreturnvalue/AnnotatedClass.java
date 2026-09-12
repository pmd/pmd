/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue;

@CheckReturnValue
public class AnnotatedClass {
    private AnnotatedClass() {}

    public static int shouldBeChecked() {
        return 42;
    }

    @CanIgnoreReturnValue
    public static int doesNotNeedToBeChecked() {
        return 4;
    }

    public static void returnsVoid() {}
}
