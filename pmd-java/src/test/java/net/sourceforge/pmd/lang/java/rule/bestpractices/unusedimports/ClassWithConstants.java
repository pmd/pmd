/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedimports;

import java.util.Arrays;
import java.util.List;

public class ClassWithConstants {

    private ClassWithConstants() {
        // Utility class
    }

    /*package*/ static final List<String> LIST1 = Arrays.asList("A");
    /*package*/ static final List<String> LIST2 = Arrays.asList("B");
}
