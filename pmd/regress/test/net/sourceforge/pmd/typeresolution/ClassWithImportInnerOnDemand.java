package test.net.sourceforge.pmd.typeresolution;

import java.util.*;

public class ClassWithImportInnerOnDemand {

    public void foo(Map m) {
        Map.Entry e = (Map.Entry) m.entrySet().iterator().next();
    }
}
