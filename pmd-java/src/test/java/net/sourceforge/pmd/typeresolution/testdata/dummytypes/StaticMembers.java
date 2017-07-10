/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class StaticMembers {
    public static StaticMembers instanceFields;
    public static int staticPrimitive;
    public static Character staticChar;
    public static GenericClass<Long, Integer> staticGeneric;

    public long primitive;
    public GenericClass<String, Number> generic;


    public static StaticMembers staticInstanceMethod() {
        return null;
    }

    public static int primitiveStaticMethod() {
        return 0;
    }

    public static Character staticCharMethod() {
        return null;
    }
}
