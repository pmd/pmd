/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Shadow groups structure {@link NameResolver}s into a linked list to
 * handle shadowing relations between declarations. Each group tracks
 * a single set of declarations of a single kind, resolved by a
 * {@link NameResolver}, and delegates resolve failures to the next
 * group in the chain. Groups can be seen as a node in the chain,
 * or as an entire chain. The latter view is more useful  the names
 * "group" They are meant to
 * be a building block for a full symbol table.
 *
 * <h3>API usage</h3>
 *
 * <p>Basic usage:
 * <pre>{@code
 *   List<JVariableSymbol> foo = group.resolve("foo");
 *   if (foo.isEmpty()) {
 *      // failed
 *   } else if (foo.size() > 1) {
 *      // ambiguity between all the members of the list
 *   } else {
 *      JVariableSymbol varFoo = foo.get(0); // it's this symbol
 *   }
 * }</pre>
 *
 * <p>More advanced functionality is provided by {@link ShadowChainIterator}.
 *
 * @param <S> Type of symbols this group tracks
 * @param <I> Type of the "scope tag", some data used to help identify
 *            the reason why a declaration is in scope. This can be retrieved
 *            with {@link ShadowChainIterator#getScopeTag()}
 *
 * @implNote Each shadow group is linked to the next one, and owns a {@link NameResolver}.
 *     When resolving a name, the search starts from the innermost shadow
 *     group around the name reference, then proceeds to the next one in the
 *     chain until it succeeds. When it succeeds, the next shadow groups in
 *     the chain are not queried: their declarations for the searched name
 *     are <i>shadowed</i> at the point of the name reference.
 *
 *     <p>Independent shadow group chains may track independent namespaces.
 *     For example in java, types, variables and methods occupy different
 *     namespaces (in fact, package names and statement labels do the same,
 *     in a useless sense). Shadow groups do not handle namespace collisions
 *     (in Java, <i>obscuring</i>), only name collisions within the same namespace.
 *     Shadow groups make the general assumption that names may be colliding
 *     anywhere, which is why {@link #resolve(String)} returns a list of symbols.
 *
 *     <p>Implementing this framework means implementing {@link NameResolver}s for
 *     each relevant way that a declaration may be brought in scope, then figuring
 *     out the correct way these resolvers should be linked into a ShadowGroup chain.
 */
public interface ShadowChain<S, I> {


    /**
     * Returns the list of symbols accessible by simple name in the scope
     * of this group. No name in this list shadows another. An empty list
     * means no such symbol exist. A list with more than one element may
     * mean there is ambiguity. For methods, ambiguity may be resolved through
     * overload resolution, for other kinds of symbols, it causes an error.
     *
     * @param name Simple name of the symbols to find
     *
     * @return A list of symbols
     */
    @NonNull List<S> resolve(String name);


    /**
     * Returns the first symbol that would be yielded by {@link #resolve(String)},
     * if it would return a non-empty list. Otherwise returns null.
     *
     * @param name Simple name of the symbol to find
     *
     * @return An optional symbol
     */
    S resolveFirst(String name);


    /**
     * Returns an iterator that iterates over sets of shadowed declarations
     * with the given name.
     *
     * @param name Simple name of the symbols to find
     */
    default ShadowChainIterator<S, I> iterateResults(String name) {
        return new ShadowChainIteratorImpl<>(this, name);
    }


    /**
     * Returns true if this group shadows the next groups in the chain.
     * This means, that if this group knows about a name, it won't delegate
     * resolve to the next group in the chain. If it doesn't know about it
     * then resolve proceeds anyway.
     */
    boolean isShadowBarrier();


    @Nullable ShadowChain<S, I> getParent();
}
