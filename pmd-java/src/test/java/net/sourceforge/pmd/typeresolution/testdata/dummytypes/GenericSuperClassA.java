/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public class GenericSuperClassA<T> extends GenericSuperClassB<T, GenericClass<String, T>> {
    public T fieldA;
    public GenericClass2 inheritedRawGeneric;
    public GenericClass<? super String, ?> inheritedSuperGeneric;
    public GenericClass<? extends String, Object> inheritedUpperBound;
}
