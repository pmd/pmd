/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table;

import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JValueSymbol;

// @formatter:off
/**
 * A symbol table for a particular region of a Java program. Keeps track of the types,
 * values, and methods accessible from their simple name in their extent.
 *
 * <p>Each symbol table is linked to a parent table, and keeps track of a particular set
 * of declarations having the same relative precedence. When a symbol table is asked for
 * the meaning of a name in a particular syntactic context (type name, method name, value name),
 * it first determines if it tracks a declaration with a matching name.
 * <ul>
 *      <li>If there is one, it returns the {@link JElementSymbol} representing the entity
 *          the name stands for in the given context;
 *      <li>If there is none, it asks the same question to its parent table recursively
 *          and returns that result.
 * </ul>
 * This allows directly encoding shadowing and hiding mechanisms in the parent-child
 * relationships.
 *
 * <h2>Terminology</h3>
 *
 * <p> If a symbol table S directly "knows about" a declaration D, then D is said
 * to be <i>tracked</i> by S. A symbol table doesn't track the declarations tracked
 * by its parents.
 *
 * <p>Each symbol table is only relevant to a set of program points, which it is said
 * to <i>dominate</i>. The set of program points a table dominates is referred-to
 * as the <i>scope</i> of that symbol table. The scope of a symbol table is a subset of
 * the scope of its parent. The declarations tracked by a symbol table S are said to be
 * <i>in-scope</i> throughout the scope of S, which means they are accessible from their
 * simple name.
 *
 * <h3>Correspondence with JLS terminology</h4>
 *
 * <p>The JLS doesn't care about symbol table implementations so the above terminology is
 * not standard spec. The JLS only defines the <i>scope of a declaration</i>:
 *
 * <blockquote cite="https://docs.oracle.com/javase/specs/jls/se9/html/jls-6.html#jls-6.3">
 *     The scope of a declaration is the region of the program within which
 *     the entity declared by the declaration can be referred to using a
 *     simple name, provided it is not shadowed.
 * </blockquote>
 *
 * <p>For our purposes, a symbol table tracks a set of declarations having exactly the same
 * jls:scope, so that the pmd:scope of a symbol table is the jls:scope of any of its tracked
 * declarations.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
// @formatter:on
@Experimental
public interface JSymbolTable {

    /**
     * Returns the parent of this table, that is, the symbol table that will be
     * delegated to if this table doesn't find a declaration.
     *
     * @return a symbol table, or null if this is the top-level symbol table
     */
    JSymbolTable getParent();

    // note that types and value names can be obscured, but that depends on the syntactic
    // context of the *usage* and is not relevant to the symbol table stack.


    /**
     * Resolves the type referred to by the given name. This must be a simple name,
     * ie, parameterized types and array types are not available. Primitive types are
     * also not considered because it's probably not useful.
     *
     * @param simpleName Simple name of the type to look for
     *
     * @return The type reference if it can be found, otherwise {@code null}
     */
    @Nullable
    JTypeDeclSymbol resolveTypeName(String simpleName);


    /**
     * Finds the variable or field to which the given simple name refers in the scope of
     * this symbol table.
     *
     * @param simpleName simple name of the value to find
     *
     * @return The reference to the variable if it can be found, otherwise {@code null}
     */
    @Nullable
    JValueSymbol resolveValueName(String simpleName);


    /**
     * Finds all accessible methods that can be called with the given simple name
     * on an implicit receiver in the scope of this symbol table. The returned methods may
     * have different arity and parameter types.
     *
     * <p>Possibly, looking up a method may involve exploring all the
     * supertypes of the implicit receiver (and the enclosing classes
     * and their supertypes, for inner/anonymous classes). Since this
     * might be costly, the method returns a lazy stream that should be
     * filtered down to exactly what you want.
     *
     * @param simpleName Simple name of the method
     *
     * @return A stream yielding all methods with the given name accessible and applicable to the
     * implicit receiver in the scope of this symbol table.
     */
    Stream<JMethodSymbol> resolveMethodName(String simpleName);

}
