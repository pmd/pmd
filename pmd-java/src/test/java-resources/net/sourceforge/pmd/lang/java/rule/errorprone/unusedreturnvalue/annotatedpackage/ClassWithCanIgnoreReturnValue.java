/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.annotatedpackage;

import net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.CanIgnoreReturnValue;
import net.sourceforge.pmd.lang.java.rule.errorprone.unusedreturnvalue.CheckReturnValue;

@CanIgnoreReturnValue
public class ClassWithCanIgnoreReturnValue {
    private ClassWithCanIgnoreReturnValue() {}

    @CheckReturnValue
    public static int shouldBeChecked() {
        return 42;
    }

    public static int doesNotNeedToBeChecked() {
        return 4;
    }
}
