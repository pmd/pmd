package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class StaticFields {
    public static StaticFields instanceFields;
    public static int staticPrimitive;
    public static GenericClass<Long, Integer> staticGeneric;

    public long primitive;
    public GenericClass<String, Number> generic;
}
