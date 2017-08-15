package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassAOther2;

public class GenericMethods<T> {
    public <A, B extends Number & Runnable, C extends D, D extends T> void foo() {
    }

    public <A, B> void bar(A a, A b, Integer c, B d) {
        bar((SuperClassA) null, (SuperClassAOther) null, null, (SuperClassAOther2) null);
    }
}
