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
