/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChain;
import net.sourceforge.pmd.lang.java.types.JMethodSig;

/**
 * A symbol table for a particular region of a Java program. Keeps track of the types,
 * values, and methods accessible from their simple name in their extent.
 *
 * <p>Instances of this interface just tie together a few {@link ShadowChain}
 * instances for each interesting namespace in the program.
 *
 * @since 7.0.0
 */
@Experimental
public interface JSymbolTable {

    /**
     * The chain of tables tracking variable names that are in scope here
     * (fields, locals, formals, etc).
     */
    ShadowChain<JVariableSymbol, ScopeInfo> variables();


    /**
     * The chain of tables tracking type names that are in scope here
     * (classes, type params, but not eg primitive types).
     */
    ShadowChain<JTypeDeclSymbol, ScopeInfo> types();


    /**
     * The chain of tables tracking method names that are in scope here.
     */
    ShadowChain<JMethodSig, ScopeInfo> methods();


}
