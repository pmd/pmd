/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A shadow group indexes symbols by their simple name. It's a subset
 * of a symbol table, caring about symbols of a certain kind.
 *
 * <p>Usage example:
 * <pre>{@code
 *
 *   String name = "foo";
 *
 *   List<JVariableSymbol> foo = group.resolve(name);
 *   if (foo.isEmpty()) {
 *      // failed
 *   } else if (foo.size() > 1) {
 *      // ambiguity between all the members of the list
 *   } else {
 *      JVariableSymbol varFoo = foo.get(0); // it's this symbol
 *
 *      // does the symbol shadow another?
 *      ShadowGroup<JVariableSymbol> next = group.nextShadowGroup(name);
 *      if (next == null) {
 *          // then in the scope of "group", the name 'foo' is not shadowed
 *      } else {
 *          // otherwise this non-empty list contains the shadowed
 *          // symbols one level up.
 *          List<JVariableSymbol> fooNext = next.resolve(name);
 *          // next.nextShadowGroup(name) == null ? ... etc
 *      }
 *   }
 * }</pre>
 */
public interface ShadowGroup<S> {

    /**
     * Returns the list of symbols accessible by simple name in the scope
     * of this group. No name in this list shadows another. An empty list
     * means no such symbol exist. A list with more than one element means
     * there is ambiguity. For methods, ambiguity may be resolved through
     * overload resolution, for other kinds of symbols, it causes an error.
     *
     * @param name Simple name
     *
     * @return A list of symbols
     */
    @NonNull List<S> resolve(String name);


    /**
     * Returns the first symbol that would be yielded by {@link #resolve(String)},
     * if it would return a non-empty list. Otherwise returns null.
     *
     * @param name Simple name
     *
     * @return An optional symbol
     */
    S resolveFirst(String name);


    /**
     * Returns the next shadow group that contains a declaration for
     * the given name. If it exists (and this group has a declaration
     * for the given name), then those declarations are shadowed
     * by a declaration in this group.
     *
     * @param name Simple name
     *
     * @return A group, or null
     */
    @Nullable ShadowGroup<S> nextShadowGroup(String name);


    /**
     * Returns true if this group shadows the next groups in the chain.
     * This means, that if this group knows about a name, it won't delegate
     * resolve to the next group in the chain. If it doesn't know about it
     * then resolve proceeds anyway.
     */
    boolean isShadowBarrier();

}
