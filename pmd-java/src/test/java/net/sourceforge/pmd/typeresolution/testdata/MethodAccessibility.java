/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB;

public class MethodAccessibility extends SuperClassA {
    class Nested extends SuperClassB {
        void test() {
            SuperClassA a = inheritedA();
            SuperClassB b = inheritedB();
        }
    }
}
