/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.useequalstocomparestrings;

public class ClassWithStringFields {
    private String string1 = "a";
    private String string2 = "a";

    public void bar() { }
}
