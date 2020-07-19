/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;

class ShadowChainNodeBase<S, I> implements ShadowChain<S, I>, ShadowChainNode<S, I> {

    protected final NameResolver<S> resolver;
    protected final @NonNull ShadowChainNode<S, I> parent;
    private final boolean shadowBarrier;
    private final I scopeTag;

    ShadowChainNodeBase(@NonNull ShadowChainNode<S, I> parent,
                        boolean shadowBarrier,
                        I scopeTag,
                        NameResolver<S> resolver) {
        this.parent = parent;
        this.scopeTag = scopeTag;
        this.shadowBarrier = shadowBarrier;
        this.resolver = resolver;
    }

    @Override
    public ShadowChainNode<S, I> asNode() {
        return this;
    }

    @Override
    public ShadowChain<S, I> asChain() {
        return this;
    }

    @Override
    public NameResolver<S> getResolver() {
        return resolver;
    }

    @Override
    public final boolean isShadowBarrier() {
        return shadowBarrier;
    }

    @Override
    public @NonNull ShadowChainNode<S, I> getParent() {
        return parent;
    }

    /**
     * This is package protected, because it would be impossible to find
     * a value for this on the root node. Instead, the scope tag
     * is only accessible from a {@link ShadowChainIterator}, if we found
     * results (which naturally excludes the root group, being empty)
     */
    I getScopeTag() {
        return scopeTag;
    }

    /** Doesn't ask the parents. */
    @Override
    public OptionalBool knowsSymbol(String simpleName) {
        return resolver.knows(simpleName);
    }


    @Override
    public @NonNull List<S> resolve(String name) {
        List<S> res = resolver.resolveHere(name);
        handleResolverKnows(name, !res.isEmpty());
        if (res.isEmpty()) {
            return getParent().asChain().resolve(name);
        } else if (!isShadowBarrier()) {
            // A successful search ends on the first node that is a
            // shadow barrier, inclusive
            // A failed search continues regardless
            return CollectionUtil.concatView(res, getParent().asChain().resolve(name));
        }
        return res;
    }

    protected void handleResolverKnows(String name, boolean resolverKnows) {
        // to be overridden
    }

    @Override
    public S resolveFirst(String name) {
        S sym = resolver.resolveFirst(name);
        handleResolverKnows(name, sym != null);
        return sym != null ? sym : getParent().asChain().resolveFirst(name);
    }

    @Override
    public String toString() {
        return scopeTag + "  " + resolver.toString();
    }

}
