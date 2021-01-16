/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

/**
 * Utilities shared between rules (note: in pmd 7 this is used much more extensively).
 */
public final class JavaRuleUtil {

    private JavaRuleUtil() {
        // utility class
    }


    /**
     * Whether the name may be ignored by unused rules like UnusedAssignment.
     */
    public static boolean isExplicitUnusedVarName(String name) {
        return name.startsWith("ignored")
            || name.startsWith("unused")
            || "_".equals(name); // before java 9 it's ok
    }
}
