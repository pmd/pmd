/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.ParametrizedSubType;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.WildcardMethod;

public class MethodGenericParam {

    public void foo() {
        ParametrizedSubType type = new ParametrizedSubType();

        WildcardMethod m = new WildcardMethod();
        m.useWildcard(type);
    }
}
