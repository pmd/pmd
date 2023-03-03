/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.testframework.PmdRuleTst;

class UnnecessaryImportTest extends PmdRuleTst {
    // these 2 methods are used for a test case, do not delete

    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            System.out.println(message);
        }
    }

    public static void assertSomething(String message, boolean condition) {
        if (!condition) {
            System.out.println(message);
        }
    }
}
