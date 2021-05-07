
package my.pack;


/*
    For this test we compile this file manually, save the classes in the
    resource tree, but purposefully exclude SuperItf.class to mimic an
    incomplete classpath.
 */


class SuperKlass<A, B> { }
interface SuperItf<A, B> { }


public class BrokenGeneric<C, D> extends SuperKlass<C, C> implements SuperItf<D, D> {


}
