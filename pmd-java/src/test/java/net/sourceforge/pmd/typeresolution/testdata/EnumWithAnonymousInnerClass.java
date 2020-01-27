/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public enum EnumWithAnonymousInnerClass {
    A {
        @Override
        public void foo() {
            super.foo();
        }
    },
    B;

    public void foo() {
    }

    interface Inner {
        int get();
    }

    public static final Inner VAL = new Inner() {
        @Override
        public int get() {
            return 1;
        }
    };
}
