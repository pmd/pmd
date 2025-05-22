/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryfullyqualifiedname;

/**
 * Test case for #4133
 */
public class ClassA {
    public static class Foo implements net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryfullyqualifiedname.Foo {

    }
}
