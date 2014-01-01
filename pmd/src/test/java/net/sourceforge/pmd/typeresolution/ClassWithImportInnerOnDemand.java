/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;

import java.util.Map;

public class ClassWithImportInnerOnDemand {

    public void foo(Map m) {
        Map.Entry e = (Map.Entry) m.entrySet().iterator().next();
    }
}
