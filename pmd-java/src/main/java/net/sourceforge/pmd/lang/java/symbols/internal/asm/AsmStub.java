/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


/**
 * Common interface for symbols wrapping a class file "stub".
 * The class is parsed with ASM, only the signature information
 * is retained, hence the name stub.
 */
interface AsmStub {


    /** Resolver that produced this instance. The resolver is global. */
    AsmSymbolResolver getResolver();


    /** Object that can parse type signatures (necessary for eg field types). */
    default SignatureParser sigParser() {
        return getResolver().getSigParser();
    }

}
