/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * The result of resolution by a symbol table. This is provided as a way
 * to extend the usefulness of symbol tables at minimal cost later on. For
 * example, {@link #getSymbolTable()} can be used to check for hidden or
 * shadowed declarations.
 */
public interface ResolveResult<T> {


    /**
     * Returns the result of the search. Is null if the search has failed.
     * Note that the
     */
    @Nullable T getResult();


    /**
     * Returns the node in the compilation unit that brings the
     * {@linkplain #getResult() result} in scope. Null if the
     * info is not available.
     */
    @Nullable JavaNode getContributor();


    /**
     * Returns the symbol table that found this declaration. This is
     * null for a failed result.
     */
    @Nullable JSymbolTable getSymbolTable();


}
