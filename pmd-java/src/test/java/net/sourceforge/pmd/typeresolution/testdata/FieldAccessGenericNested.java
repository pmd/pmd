/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;

public class FieldAccessGenericNested<T extends String> {

    public class Nested {
        Nested n;
        T field;
        GenericClass<T, Number> generic;

        void foo() {
            n.field = null;
            n.generic.first = null;
        }
    }
}
