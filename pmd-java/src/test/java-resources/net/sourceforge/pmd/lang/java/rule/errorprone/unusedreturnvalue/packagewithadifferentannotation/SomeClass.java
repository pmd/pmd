/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.packagewithadifferentannotation;

public class SomeClass {
    private SomeClass() {}

    public static int doesNotNeedToBeChecked() {
        return 42;
    }
}
