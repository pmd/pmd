package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.GenericClass2;


/*
 * TODO: static field access not tested
 * TODO: upper bounds: add extends, super, wild card and what not cases
 * TODO: sources: nested
 * TODO: diamond
 *
 * Add fields dependent on type arguments from class, method, have super, extends, wild card and what not
 *
 * TODO: cover (first) one being local or param or field
 * TODO: generic from class, method, consturctor, type argument
 *
 */

class GenericSuperClassB<T, S> {
    public S fieldB;
}

class GenericSuperClassA<T> extends GenericSuperClassB<T, GenericClass<String, T>> {
    public T fieldA;
}

public class GenericFieldAccess<T, S extends Double> extends GenericSuperClassA<Long> {
    public GenericClass<String, Double> genericField;
    public GenericClass<String, GenericClass<Number, Double>> genericTypeArg;
    public GenericClass2 rawGeneric;
    public GenericFieldAccess field;
    public S classGeneric;

    public <M extends Character> void foo(GenericClass<Integer, Character> param) {
        GenericClass<Float, Long> local = null;
        M localGeneric = null;


        // test inherited generic
        // Primary[Prefix[Name[generic.first]]]
        fieldA = new Long(0);
        fieldB.generic.second = "";


        // test raw types
        // Primary[Prefix[Name[rawGeneric.first]]]
        //rawGeneric.first = new Integer(0);
        //rawGeneric.second = "";
        //rawGeneric.rawGeneric.first = new Integer(0);


        // access a generic field whose type depends on a generic type argument
        // Primary[Prefix[Name[genericTypeArg.second.second]]]
        genericTypeArg.second.second = new Double(0);

        // access a generic field through member field
        // Primary[Prefix[Name[genericField.first]]]
        genericField.first = "";
        genericField.second = new Double(0);

        // access a generic field whose type depends on indirect type arguments
        // Primary[Prefix[Name[generic.generic.first]]]
        param.generic.first = new Character('c');
        local.generic.second = new Float(0);
        genericField.generic.generic.generic.first = new Double(0);


        // access a generic field through a local or a parameter
        // Primary[Prefix[Name[param.first]]]
        param.first = new Integer(0);
        local.second = new Long(0);

        // access type dependant on class/method type arguments
        // Primary[Prefix[Name[classGeneric]]]
        classGeneric = null; // Double
        localGeneric = null; // Character

    }

    public <C extends Number> GenericFieldAccess() {
        C localGeneric = null;

        // access type dependant on constructor type arguments
        // Primary[Prefix[Name[localGeneric]]]
        localGeneric = null; // Number
    }
}
