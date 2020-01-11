/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * The result of resolution by a symbol table. This includes scope-specific
 * information that symbols do not provide directly.
 *
 * <p>For example, when looking in the supertypes of the
 * current class for a field:
 * <pre>{@code
 *
 * abstract class Sup<K> {
 *      K field;
 * }
 *
 *
 * class Foo extends Sup<Integer> {
 *
 *      {
 *         super.field = 2;
 *      }
 *
 * }
 *
 * }</pre>
 *
 * <p>A lookup for the field in the initializer will give the symbol
 * {@code Sup#K}, the substitution {@code K -> Integer}, and as contributor
 * the node {@code Sup<Integer>} (part of the extends clause).
 */
public interface ResolveResult<T> {


    /**
     * Returns the result of the search. Is null if the search has failed.
     * Note that the
     */
    @Nullable T getResult();


    /**
     * Returns the node in the compilation unit that brings the
     * {@linkplain #getResult() result} in scope. Nullable if the
     * info is not available.
     */
    @Nullable JavaNode getContributor();


    /**
     * Returns the symbol table that found this declaration. This is
     * null for a failed result, and also
     */
    @Nullable JSymbolTable getSymbolTable();


}
