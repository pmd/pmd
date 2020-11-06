/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedimports;

import java.util.Objects;

/**
 * Note: In order for this test case to work, the class "Issue2016" must also be compiled and available
 * on the auxclasspath.
 */
public class Issue2016 {
    public void testFunction() {
        Objects.toString(null);
    }
}
