/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.List;
import java.util.function.BinaryOperator;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;

class ShadowChainNodeBase<S, I> implements ShadowChain<S, I>, ShadowChainNode<S, I> {

    protected final NameResolver<S> resolver;
    private final BinaryOperator<List<S>> merger;
    protected final @NonNull ShadowChainNode<S, I> parent;
    private final boolean shadowBarrier;
    private final I scopeTag;

    @SuppressWarnings("unchecked") // NameResolver is covariant in S
    ShadowChainNodeBase(@NonNull ShadowChainNode<S, I> parent,
                        boolean shadowBarrier,
                        I scopeTag,
                        NameResolver<? extends S> resolver,
                        BinaryOperator<List<S>> merger) {
        this.parent = parent;
        this.scopeTag = scopeTag;
        this.shadowBarrier = shadowBarrier;
        this.resolver = (NameResolver<S>) resolver;
        this.merger = merger;
    }

    ShadowChainNodeBase(ShadowChainNode<S, I> parent, boolean shadowBarrier, I scopeTag, NameResolver<? extends S> resolver) {
        this(parent, shadowBarrier, scopeTag, resolver, defaultMerger());
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
        List<S> res = this.resolveHere(name);
        if (res.isEmpty()) {
            // failed, continue
            return getParent().asChain().resolve(name);
        } else {
            // successful search: fetch all non-shadowed names
            // note: we can't call ShadowChain::resolve on the parent
            // as it would ignore the shadow barriers if the parent
            // does not know the name.
            ShadowChainNode<S, I> node = this;
            while (!node.isShadowBarrier() && node.getParent() != null) {
                // The search ends on the first node that is a
                // shadow barrier, inclusive.
                node = node.getParent();
                res = merger.apply(res, node.resolveHere(name));
            }
        }
        return res;
    }

    @Override
    public List<S> resolveHere(String simpleName) {
        List<S> res = resolver.resolveHere(simpleName);
        handleResolverKnows(simpleName, !res.isEmpty());
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

    static <S> BinaryOperator<List<S>> defaultMerger() {
        return CollectionUtil::concatView;
    }
}
