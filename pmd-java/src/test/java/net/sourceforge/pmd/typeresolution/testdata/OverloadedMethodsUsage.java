/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.OverloadedMethods;

public class OverloadedMethodsUsage {

    private String[] arg1 = null;
    private String[] arg2 = new String[1];

    public void foo() {
        OverloadedMethods.equals(arg1, arg2);
    }
}
