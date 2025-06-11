/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

public class LocalClasses {
    Object foo = new Object() {
                // LocalClasses$1
            };

    static {
        class Local1 {
            // LocalClasses$Local1
        }
    }

    void foo() {
        class Local2 {
            // LocalClasses$Local1
        }
    }
}
