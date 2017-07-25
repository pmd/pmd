/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericSuperClassA;

public class FieldAccessGenericRaw<T extends GenericClass2> extends GenericSuperClassA<Long> {
    GenericClass2 rawGeneric;
    T parameterRawGeneric;

    void foo() {
        // test raw types
        // Primary[Prefix[Name[rawGeneric.first]]]
        rawGeneric.first = new Integer(0);
        rawGeneric.second = new Integer(0);
        rawGeneric.third = new Object();
        rawGeneric.fourth.second = "";
        rawGeneric.rawGeneric.second = new Integer(0);

        // Primary[Prefix[Name[inheritedGeneric.first]]]
        inheritedRawGeneric.first = new Integer(0);
        inheritedRawGeneric.second = new Integer(0);
        inheritedRawGeneric.third = new Object();
        inheritedRawGeneric.fourth.second = "";
        inheritedRawGeneric.rawGeneric.second = new Integer(0);

        // Primary[Prefix[Name[parameterRawGeneric.first]]]
        parameterRawGeneric.first = new Integer(0);
        parameterRawGeneric.second = new Integer(0);
        parameterRawGeneric.third = new Object();
        parameterRawGeneric.fourth.second = "";
        parameterRawGeneric.rawGeneric.second = new Integer(0);

        // Bug #471
        rawGeneric.fifth = new GenericClass();
        inheritedRawGeneric.fifth = new GenericClass();
        parameterRawGeneric.fifth = new GenericClass();
    }
}
