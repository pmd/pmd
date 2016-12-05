/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable.testdata;

public class InnerClass {
    public static class TheInnerClass {
        public enum EnumTest {
            THREE, FOUR;
        }
    }

    enum EnumTest {
        ONE, TWO;
    }

    public void foo(TheInnerClass arg) {
    }

    public void bar(InnerClass.TheInnerClass arg) {
    }

    public void baz(EnumTest e) {
    }
}
