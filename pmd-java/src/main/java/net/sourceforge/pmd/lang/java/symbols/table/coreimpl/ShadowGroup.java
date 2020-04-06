/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Shadow groups structure {@link NameResolver}s into a linked list to
 * handle shadowing relations between declarations. They are meant to
 * be a building block for a full symbol table. Each shadow group is
 * linked to the next one, and owns a {@link NameResolver}. When resolving
 * a name, the search starts from the innermost shadow group around the
 * name reference, then proceeds to the next one in the chain until it
 * succeeds. When it succeeds, the next shadow groups in the chain are
 * not queried: their declarations for the searched name are <i>shadowed</i>
 * at the point of the name reference.
 *
 * <p>Independent shadow group chains may track independent namespaces.
 * For example in java, types, variables and methods occupy different
 * namespaces (in fact, package names and statement labels do the same).
 * When namespaces collide
 *
 * FIXME this is a mess
 *
 * It's a subset
 * of a symbol table, caring about symbols of a certain kind. For example
 * in java, there are three kinds of shadow groups, which correspond to
 * independent namespaces: for types, methods, and variables. At a particular
 * program point in the source code, many different declarations may be <i>in
 * scope</i>, meaning they can be referred to by simple name. When multiple
 * declarations which have the same simple name are in scope at the same
 * time, the language semantics decide which one is preferred (eg prefer
 * a local variable to a field). This is called <i>shadowing</i> (in a
 * loose sense). When the choice cannot be arbitrated, a compiler might
 * raise an ambiguity error (eg several types inherited from unrelated
 * interfaces), some other kind of error (eg declaring several local
 * variables with the same name in the same block is illegal), or just
 * ignore it. This again depends on the semantics of the language (the
 * examples are given for Java).
 *
 * <p>Inside a code analysis tool, like PMD, or a compiler, declarations
 * are tracked by a <i>symbol table</i>, which indexes the symbols that
 * are in scope at any program point. ShadowGroups are meant to be a
 * building block for
 *
 * <p>In languages like Java, the scope of declarations follow closely
 * the structure of the AST (eg they mostly correspond to blocks), which
 * means it's practicable to just have a data structure track the names
 * that
 *
 * Inside each scope, some declarations may be in scope,
 * and some may be They have independent namespaces, and though those may
 * collide (obscuring), shadow groups . The term "shadowing" is meant in a general
 * sense here, since it depends on the semantics associated with the members
 * of the namespace.
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
 */
public interface ShadowGroup<S, I> {




    /**
     * Returns the list of symbols accessible by simple name in the scope
     * of this group. No name in this list shadows another. An empty list
     * means no such symbol exist. A list with more than one element means
     * there is ambiguity. For methods, ambiguity may be resolved through
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


    @Nullable ShadowGroup<S, I> getParent();
}
