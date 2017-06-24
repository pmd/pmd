/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericSuperClassA;


/*
 * TODO: add anonymous class this (Allocation expression)
 * TODO: add primitives, parameterized arrays
 * TODO: diamond, type parmeter declarations can shadow Class declarations
 */

public class FieldAccessGenericSimple extends GenericSuperClassA<Long> {
    GenericClass<String, Double> genericField;
    GenericClass<String, GenericClass<Number, Double>> genericTypeArg;
    FieldAccessGenericSimple fieldAcc;

    void foo(GenericClass<Integer, Character> param) {
        GenericClass<Float, Long> local = null;

        // access a generic field through member field
        // Primary[Prefix[Name[genericField.first]]]
        genericField.first = "";
        genericField.second = new Double(0);

        // access a generic field whose type depends on a generic type argument
        // Primary[Prefix[Name[genericTypeArg.second.second]]]
        genericTypeArg.second.second = new Double(0);

        // access a generic field through a local or a parameter
        // Primary[Prefix[Name[param.first]]]
        param.first = new Integer(0);
        local.second = new Long(0);

        // access a generic field whose type depends on indirect type arguments
        // Primary[Prefix[Name[generic.generic.first]]]
        param.generic.first = new Character('c');
        local.generic.second = new Float(0);
        genericField.generic.generic.generic.first = new Double(0);

        // test inherited generic
        // Primary[Prefix[Name[generic.first]]]
        fieldA = new Long(0);
        fieldB.generic.second = "";

        // test inherited generic
        // Primary[Prefix[Name[fieldAcc.fieldA]]]
        fieldAcc.fieldA = new Long(0);
    }

    public class Nested extends GenericSuperClassA<Long> {
        void foo() {
            fieldA = new Long(0);
        }
    }
}
