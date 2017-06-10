package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class GenericClass2<A extends Integer, B extends A, C,
        D extends GenericClass<A, B>> {
    public A first;
    public B second;
    public C third;
    public D fourth;
    public GenericClass rawGeneric;
}
