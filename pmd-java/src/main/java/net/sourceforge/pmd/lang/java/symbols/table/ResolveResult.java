/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The result of resolution by a symbol table. This is provided as a way
 * to extend the usefulness of symbol tables at minimal cost later on. For
 * example, {@link #getSymbolTable()} can be used to check for hidden or
 * shadowed declarations.
 */
public interface ResolveResult<T> {


    /**
     * Returns the result of the search.
     */
    @NonNull T getResult();


}
