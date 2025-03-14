/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryimport.package1;

import net.sourceforge.pmd.lang.java.rule.codestyle.unnecessaryimport.package2.*; // SUPPRESS CHECKSTYLE needed for test case

public class U {
    private void g() {
        String k = C.V;
    }
}
