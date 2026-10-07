/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.annotatedpackage;

import net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.CanIgnoreReturnValue;

public class ClassInAnnotatedPackage {
    private ClassInAnnotatedPackage() {}

    public static int shouldBeChecked() {
        return 42;
    }

    @CanIgnoreReturnValue
    public static int doesNotNeedToBeChecked() {
        return 4;
    }
}
