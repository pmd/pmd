/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;

public class FieldAccessGenericParameter<T extends GenericClass<String, GenericClass<String, Integer>>,
        S extends Double> {
    T parameterGeneric;
    S classGeneric;

    <M extends Character> void foo() {
        M localGeneric = null;

        // access type dependant on class/method type arguments
        // Primary[Prefix[Name[classGeneric]]]
        classGeneric = null; // Double
        localGeneric = null; // Character


        // test type parameters extending generic types
        // Primary[Prefix[Name[parameterGeneric.first]]]
        parameterGeneric.second.second = new Integer(0);
    }

    <C extends Number> FieldAccessGenericParameter() {
        C constructorGeneric = null;

        // access type dependant on constructor type arugments
        // Primary[Prefix[Name[localGeneric]]]
        constructorGeneric = null; // Number
    }
}
