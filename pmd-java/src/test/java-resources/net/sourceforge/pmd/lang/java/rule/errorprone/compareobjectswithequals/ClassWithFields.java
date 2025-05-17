/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.compareobjectswithequals;

public class ClassWithFields {
    private Object a;
    private Object b;

    boolean test1() {
        return false;
    }
}
