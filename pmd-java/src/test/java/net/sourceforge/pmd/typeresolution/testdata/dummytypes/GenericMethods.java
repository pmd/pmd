package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class GenericMethods<T> {
    public <A, B extends Number & Runnable, C extends D, D extends T> void foo() {}
}
