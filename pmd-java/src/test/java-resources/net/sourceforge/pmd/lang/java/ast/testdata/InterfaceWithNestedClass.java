/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.testdata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface InterfaceWithNestedClass {
    Map<String, String> MAPPING = Collections.unmodifiableMap(new HashMap<String, String>() {
        private static final long serialVersionUID = 3855526803226948630L;
        {
            put("X", "10");
            put("L", "50");
            put("C", "100");
            put("M", "1000");
        }
    });
}
