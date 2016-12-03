/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public enum EnumWithAnonymousInnerClass {
    A;
    interface Inner {
        int get();
    }

    private static final Inner VAL = new Inner() {
        @Override
        public int get() {
            return 1;
        }
    };
}
