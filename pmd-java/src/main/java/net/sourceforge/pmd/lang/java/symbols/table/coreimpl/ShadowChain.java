/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A shadow chain is a linked list of {@link NameResolver}s, which handles
 * shadowing relations between declarations. Chains track the scope of
 * declarations of a single kind, corresponding to a namespace (eg types
 * or methods).
 *
 * <p>Basic usage:
 * <pre>{@code
 *   List<JVariableSymbol> foo = chain.resolve("foo");
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
 * ShadowChain instances can be viewed as both a node in the
 * chain or as the entire chain. The former interpretation is rendered
 * by the lower-level API of {@link ShadowChainNode}.
 *
 * @param <S> Type of symbols this chain tracks
 * @param <I> Type of the "scope tag", some data used to help identify
 *            the reason why a declaration is in scope. This can be retrieved
 *            with {@link ShadowChainIterator#getScopeTag()}.
 */
public interface ShadowChain<S, I> {


    /**
     * Returns the list of symbols accessible by simple name in the scope
     * of this group. No name in this list shadows another. An empty list
     * means no such symbol exist. A list with more than one element may
     * mean there is ambiguity. For methods, ambiguity may be resolved through
     * overload resolution, for other kinds of symbols, it causes an error.
     *
     * <p>The ordering in the list is defined to be innermost first.
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
        return new ShadowChainIteratorImpl<>(asNode(), name);
    }


    /**
     * Returns the API of this instance that views the chain as individual nodes.
     */
    ShadowChainNode<S, I> asNode();

}
