/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * Support for compile-time type resolution on the AST.
 */
package net.sourceforge.pmd.lang.java.types;
/*
TODO:  inference of `throws` clause
    -> pretty hard, see throws constraint formulas in JLS 18

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
            wrap(Scratch::runThrowing); // throws IOException
        } catch (IOException e) {

        }
    }

}
 */




/* TODO possibly, the type node for a diamond should have the parameterized
    type, for now it's the generic type declaration, which has out-of-scope
    type params
    See TypesFromAst

import java.util.ArrayList;

class O {
    {
        List<String> l = new ArrayList<>();
        //                   -----------
        //                   maybe this node should have type ArrayList<String>

        // Note that the whole expression already has type ArrayList<String> after inference
    }
}

 */

/* TODO test bridge method execution filtering
    In AsmLoaderTest


 */


/* TODO finish NamedReferenceExpr by patching LazyTypeResolver
     this needs tests, will be done in fwd branch

 */


/* TODO test explicitly typed lambda (in ExplicitTypesTest)
     wildcard parameterization inference is not implemented yet.
 */


