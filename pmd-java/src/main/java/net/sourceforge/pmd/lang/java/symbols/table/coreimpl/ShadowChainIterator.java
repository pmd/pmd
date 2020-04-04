/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.Iterator;
import java.util.List;

/**
 * Iterates over a {@link ShadowGroup} chain to find a given name. This
 * can be used to find all shadowed declarations for a given name, or to
 * find the reason why a declaration is in scope {@link #getScopeTag()}.
 */
public interface ShadowChainIterator<S, I> extends Iterator<ShadowGroup<S, I>> {

    @Override
    boolean hasNext();


    /**
     * Returns the next shadow group that contains a declaration for
     * the name this iterator searches. If that group exists ({@link #hasNext()})
     * then the symbols yielded by {@link #getResults()} are shadowed
     * in the previous groups that were yielded (unless they are the same
     * symbols, in which case there are eg duplicate imports).
     */
    @Override
    ShadowGroup<S, I> next();


    /**
     * Returns the scope tag of the shadow group that was last yielded.
     *
     * @throws IllegalStateException If {@link #next()} has not been called
     */
    I getScopeTag();


    /**
     * Returns the results of the search in the current shadow group.
     * This list is nonempty.
     *
     * @throws IllegalStateException If {@link #next()} has not been called
     */
    List<S> getResults();

}
