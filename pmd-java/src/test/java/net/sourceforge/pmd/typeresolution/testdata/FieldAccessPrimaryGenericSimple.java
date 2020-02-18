/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericSuperClassA;

public class FieldAccessPrimaryGenericSimple extends GenericSuperClassA<Long> {
    GenericClass<String, Double> genericField;
    GenericClass<String, GenericClass<Number, Double>> genericTypeArg;

    void foo(GenericClass<Integer, Character> param) {
        GenericClass<Float, Long> local = null;

        // access a generic field through member field
        // Primary[Prefix[this], Suffix[genericField], Suffix[first]]
        this.genericField.first = "";
        (this).genericField.second = new Double(0);

        // access a generic field whose type depends on a generic type argument
        // Primary[Prefix[this], Suffix[genericTypeArg], Suffix[second], Suffix[second]]
        this.genericTypeArg.second.second = new Double(0);

        // access a generic field whose type depends on indirect type arguments
        // Primary[Prefix[this], Suffix[genericField], Suffix[generic], Suffix[generic]...]
        (this).genericField.generic.generic.generic.first = new Double(0);

        // test inherited generic
        // Primary[Primary[Prefix[(this)]], Suffix[fieldA]]
        (this).fieldA = new Long(0);
        this.fieldB.generic.second = "";

        // test inherited generic
        // Primary[Prefix[super], Suffix[fieldA]]
        super.fieldA = new Long(0);
        super.fieldB.generic.second = "";
    }

    class Nested<T extends GenericClass<String, Number>> {
        T field;

        void foo() {
            // Primary[Prefix[this], Suffix[field], Suffix[first]]
            this.field.first = "";
        }
    }
}
