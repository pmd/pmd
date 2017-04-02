/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import java.util.Map;

/**
 * This class is to verify that the inner class "Entry" of java.util.Map is
 * imported correctly.
 */
public class ClassWithImportInnerOnDemand {

    public void foo(Map<String, String> m) {
        Map.Entry<String, String> e = m.entrySet().iterator().next();
        assert e != null;
    }

    // StringTokenizer will only be considered if inner classes are visited
    interface Inner {
        java.util.StringTokenizer createTokenizer();
    }
}
