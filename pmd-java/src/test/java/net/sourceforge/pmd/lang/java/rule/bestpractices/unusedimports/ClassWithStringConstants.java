/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedimports;

public class ClassWithStringConstants {

    private ClassWithStringConstants() {
        // Utility class
    }

    public static final String CONST1 = "a";
    public static final String CONST2 = "b";
}
