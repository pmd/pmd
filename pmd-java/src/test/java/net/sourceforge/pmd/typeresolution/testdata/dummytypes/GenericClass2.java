/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class GenericClass2<A extends Integer, B extends A, C,
        S extends String,
        D extends GenericClass<A, S>,
        E extends GenericClass<E, E>,
        F extends GenericClass2> {
    public A first;
    public B second;
    public C third;
    public D fourth;
    public E fifth; // recursion
    public F sixth; // recursion
    public GenericClass2 rawGeneric;
}
