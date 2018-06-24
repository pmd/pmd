/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther2;

public class GenericMethodsImplicit<T> {
    public <A, B extends Number & Runnable, C extends D, D extends T> void foo() {
    }

    public <A, B> A bar(A a, A b, Integer c, B d) {
        return null;
    }

    void test() {
        SuperClassA2 a = bar((SuperClassA) null, (SuperClassAOther) null, (Integer) null, (SuperClassAOther2) null);
    }
}
