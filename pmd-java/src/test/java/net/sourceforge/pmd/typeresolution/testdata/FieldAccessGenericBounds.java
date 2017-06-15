/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericSuperClassA;


public class FieldAccessGenericBounds extends GenericSuperClassA<Long> {
    GenericClass<? super String, ?> superGeneric;
    GenericClass<? extends Number, Object> upperBound;

    public void astPrimaryNameCases() {
        // test ?, ? super Something, ? extends Something
        // Primary[Prefix[Name[superGeneric.first]]]
        superGeneric.first = ""; // Object
        superGeneric.second = null; // Object
        inheritedSuperGeneric.first = ""; // Object
        inheritedSuperGeneric.second = null; // Object

        upperBound.first = null; // Number
        inheritedUpperBound.first = null; // String

        // test static imports
        // Primary[Prefix[Name[instanceFields.generic.first]]]
        //instanceFields.generic.first = "";
        //staticGeneric.first = new Long(0);
    }
}

