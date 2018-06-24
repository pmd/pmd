/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class GenericClass<T, S> {
    public T first;
    public S second;
    public GenericClass<S, T> generic;
}
