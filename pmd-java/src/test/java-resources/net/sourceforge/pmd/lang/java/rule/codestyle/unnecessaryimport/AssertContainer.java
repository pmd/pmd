/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryimport;

public class AssertContainer {

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
