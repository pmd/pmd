/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SubType;

public class SubTypeUsage {

    public void foo() {
        SubType var = new SubType();
        var.myMethod();
    }
}
