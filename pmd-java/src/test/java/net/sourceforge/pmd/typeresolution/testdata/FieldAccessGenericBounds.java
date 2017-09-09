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
        // Primary[Prefix[Name[superGeneric.first]]]
        superGeneric.first = ""; // ? super String
        superGeneric.second = null; // ?
        inheritedSuperGeneric.first = ""; // ? super String
        inheritedSuperGeneric.second = null; // ?

        upperBound.first = null; // ? extends Number
        inheritedUpperBound.first = null; // ? extends String
    }
}

