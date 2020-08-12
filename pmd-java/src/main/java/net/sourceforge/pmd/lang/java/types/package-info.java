/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Support for compile-time type resolution on the AST.
 */
package net.sourceforge.pmd.lang.java.types;
/*
TODO:  inference of `throws` clause

import java.io.IOException;

@FunctionalInterface
interface ThrowingRunnable<E extends Throwable> {

    void run() throws E;
}

class Scratch {

    static <E extends Throwable> void wrap(ThrowingRunnable<? extends E> runnable) throws E {
        runnable.run();
    }

    static void runThrowing() throws IOException {
        throw new IOException();
    }

    {
        try {
            wrap(Scratch::runThrowing);
        } catch (IOException e) {

        }
    }

}
 */


/*
TODO: qualified super ctor call


class Outer {
    class Inner<T> {
        public Inner(T value) { }
    }
}


class Scratch extends Outer.Inner<String> {

    public Scratch(Outer o) {
        o.super("value");
    }
}

 */


/* TODO: inference with unchecked conversion

    public static <T> T min(Collection<? extends T> coll, Comparator<? super T> comp) {
        if (comp==null)
            return (T) min((Collection) coll);
        return null;
    }

    public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
        return null;
    }


    [WARNING] CTDecl resolution failed. Summary of failures:
    STRICT:
        Incompatible bounds: ξ349 = java.lang.Object and ξ349 <: java.lang.Comparable<? super ξ349>		min(java.util.Collection<? extends T>) -> T

    LOOSE:
        Incompatible bounds: ο349 = java.lang.Object and ο349 <: java.lang.Comparable<? super ο349>		min(java.util.Collection<? extends T>) -> T


 */

/* TODO: an array initializer is an assignment context
    -> see PolyResolution

    class Scratch {

        final Runnable r[] = {
            () -> { } // is a Runnable
        }

    }
 */
