/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Keeps track of unresolved classes, can update some information about
 * unresolved classes if needed. For example, creating a {@link JClassType}
 * with a wrong number of type arguments is not allowed. If the symbol
 * is unresolved, we don't have access to the type vars or their count.
 * For that reason, the first time we encounter a parameterized class
 * type, we set its arity, ie the number of type params it expects.
 * Inconsistent numbers of type arguments are reported as errors in the
 * disambiguation pass (but zero type arguments is always allowed, that
 * could be a raw type) to not throw off errors later during type resolution.
 *
 * <p>Not thread-safe. One instance is created by file (in JavaAstProcessor).
 */
public final class UnresolvedClassStore {

    private final Map<String, UnresolvedClassImpl> unresolved = new HashMap<>();
    private final TypeSystem ts;

    public UnresolvedClassStore(TypeSystem ts) {
        this.ts = ts;
    }


    /**
     * Produces an unresolved class symbol from the given canonical name.
     *
     * @param canonicalName Canonical name of the returned symbol
     * @param typeArity     Number of type arguments parameterizing the reference.
     *                      Type parameter symbols will be created to represent them.
     *                      This may also mutate an existing unresolved reference.
     *
     * @throws NullPointerException If the name is null
     */
    public @NonNull JClassSymbol makeUnresolvedReference(@Nullable String canonicalName, int typeArity) {

        UnresolvedClassImpl unresolved = this.unresolved.computeIfAbsent(canonicalName,
                                                                         n -> new FlexibleUnresolvedClassImpl(this.ts, null, n));
        unresolved.setTypeParameterCount(typeArity);
        return unresolved;
    }

    public @NonNull JClassSymbol makeUnresolvedReference(JClassSymbol qualifier, String simpleName, int typeArity) {

        if (qualifier instanceof UnresolvedClassImpl) {
            UnresolvedClassImpl child = ((UnresolvedClassImpl) qualifier).getOrCreateUnresolvedChildClass(simpleName);
            child.setTypeParameterCount(typeArity);
            this.unresolved.putIfAbsent(child.getCanonicalName(), child);
            return child;
        }

        return makeUnresolvedReference(qualifier.getCanonicalName() + '.' + simpleName, typeArity);
    }
}
