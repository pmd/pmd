/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Helper class for {@link TypeSystem#lub(Collection)} and {@link TypeSystem#glb(Collection)}.
 *
 * <p>Lub: Least Upper Bound, Glb: Greatest Lower Bound.
 */
final class Lub {

    private Lub() {
    }

    static JTypeMirror lub(TypeSystem ts, Collection<? extends JTypeMirror> us) {
        return new LubJudge(ts).lub(us);
    }

    private static JTypeMirror upperBound(JTypeMirror type) {
        if (type instanceof JWildcardType) {
            return ((JWildcardType) type).asUpperBound();
        }
        return type;
    }

    private static JTypeMirror lowerBound(JTypeMirror type) {
        if (type instanceof JWildcardType) {
            return ((JWildcardType) type).asLowerBound();
        }
        return type;
    }

    private static boolean isUpperBound(JTypeMirror type) {
        return type instanceof JWildcardType && ((JWildcardType) type).isUpperBound();
    }

    private static boolean isLowerBound(JTypeMirror type) {
        return type instanceof JWildcardType && ((JWildcardType) type).isLowerBound();
    }

    /**
     * The "relevant" parameterizations of G, Relevant(G), is:
     *
     * <pre>{@code
     * Relevant(G) = { V | 1 ≤ i ≤ k: V in ST(Ui) and V = G<...> }
     *             = { V ∈ stunion | V = G<...> }
     * }</pre>
     *
     * <p>G must be erased (raw).
     *
     * @return null if G is not a generic type, otherwise Relevant(G)
     */
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull") // null is explicit mentioned as a possible return value
    static @Nullable List<JClassType> relevant(JClassType g, Set<JTypeMirror> stunion) {
        if (!g.isRaw()) {
            return null;
        }

        // else generic type:

        List<JClassType> list = new ArrayList<>();
        for (JTypeMirror it : stunion) {
            if (it instanceof JClassType
                && it.getErasure().equals(g) && !it.isRaw()) {
                list.add((JClassType) it);
            }
        }
        if (list.isEmpty()) {
            // there might be only the raw g type.
            return null;
        }
        return list;
    }

    private static Set<JTypeMirror> erasedSuperTypes(Set<JTypeMirror> stui) {
        Set<JTypeMirror> erased = new LinkedHashSet<>();
        for (JTypeMirror it : stui) {
            JTypeMirror t = it instanceof JTypeVar ? it : it.getErasure();
            erased.add(t);
        }
        return erased;
    }

    private static class LubJudge {

        // this is what this class is about: caching lubs, so that we
        // don't get an infinitely recursive type.
        private final Set<TypePair> lubCache = new HashSet<>();
        private final TypeSystem ts;

        LubJudge(TypeSystem ts) {
            this.ts = ts;
        }

        JTypeMirror lub(JTypeMirror... in) {
            return lub(Arrays.asList(in));
        }

        JTypeMirror glb(JTypeMirror... in) {
            return ts.glb(Arrays.asList(in));
        }

        JTypeMirror lub(Collection<? extends JTypeMirror> in) {

            if (in.isEmpty()) {
                throw new IllegalArgumentException("Empty set for lub?");
            }

            Set<JTypeMirror> us = new LinkedHashSet<>(in);

            us.remove(ts.NULL_TYPE);

            Iterator<JTypeMirror> uIterator = us.iterator();

            if (us.size() == 1) {
                return uIterator.next();
            } else if (us.isEmpty()) {
                // we removed the null type previously
                return ts.NULL_TYPE;
            }

            // This is the union of all generic supertypes of the Uis
            Set<JTypeMirror> stunion = new LinkedHashSet<>(uIterator.next().box().getSuperTypeSet());
            // Let EC, the erased candidate set for U1 ... Uk, be the intersection of all the sets EST(Ui)
            Set<JTypeMirror> ec = erasedSuperTypes(stunion);

            while (uIterator.hasNext()) {
                // Let ST(Ui), the set of supertypes of Ui
                Set<JTypeMirror> stui = uIterator.next().box().getSuperTypeSet();
                // Let EST(Ui), the set of erased supertypes of Ui
                Set<JTypeMirror> estui = erasedSuperTypes(stui);
                ec.retainAll(estui);
                stunion.addAll(stui);
            }

            // Let MEC, the minimal erased candidate set for U1 ... Uk, be:
            // MEC = { V | V in EC, and for all W ≠ V in EC, it is not the case that W <: V }
            Set<JTypeMirror> mec = TypeOps.mostSpecific(ec);

            List<JTypeMirror> best = CollectionUtil.map(mec, g -> {
                if (g instanceof JClassType) {
                    List<JClassType> relevant = relevant((JClassType) g, stunion);
                    return relevant != null ? lcp(relevant) : g;
                } else {
                    return g;
                }
            });

            return best.isEmpty() ? ts.OBJECT : ts.glb(best);
        }

        /**
         * LCP is the "least containing parameterization" of a set of
         * parameterizations of the same generic type G.
         *
         * <p>It is a parameterization of G such that all elements of
         * the set are subtypes of that parameterization.
         */
        private JClassType lcp(List<JClassType> relevant) {
            if (relevant.isEmpty()) {
                throw new IllegalArgumentException("Empty set");
            }

            if (relevant.size() == 1) {
                // single parameter
                return relevant.get(0);
            }

            JClassType acc = lcp(relevant.get(0), relevant.get(1));

            for (int i = 2; i < relevant.size(); i++) {
                acc = lcp(acc, relevant.get(i));
            }

            return acc;
        }

        private JClassType lcp(JClassType t, JClassType s) {

            int n = t.getFormalTypeParams().size();
            List<JTypeMirror> leastArgs = new ArrayList<>(n);

            List<JTypeMirror> targs = t.getTypeArgs();
            List<JTypeMirror> sargs = s.getTypeArgs();

            for (int i = 0; i < n; i++) {
                leastArgs.add(lcta(targs.get(i), sargs.get(i)));
            }

            return t.withTypeArguments(leastArgs);
        }

        /**
         * lcta(T, S), the least containing type argument, finds the most specific
         * type argument that contains both T and S, in the sense of
         * {@link TypeOps#typeArgContains(JTypeMirror, JTypeMirror)}.
         */
        private JTypeMirror lcta(JTypeMirror t, JTypeMirror s) {

            TypePair pair = new TypePair(t, s);

            if (lubCache.add(pair)) {
                JTypeMirror res = computeLcta(t, s);
                lubCache.remove(pair);
                return res;
            } else {
                // We're recursing on lcta(T,S) with the same arguments.
                // We have to break the recursion.
                return ts.UNBOUNDED_WILD;
            }
        }

        private @NonNull JTypeMirror computeLcta(JTypeMirror t, JTypeMirror s) {

            // lcta(U, V) = U if U = V
            if (t.equals(s)) {
                return t;
            }

            if (isUpperBound(t) && isLowerBound(s)) {
                // lcta(? extends U, ? super V) = ?
                return ts.UNBOUNDED_WILD;
            }

            if (isUpperBound(s)) {

                // lcta(U,           ? extends V)   = ? extends lub(U, V)
                // lcta(? extends U, ? extends V)   = ? extends lub(U, V)

                return ts.wildcard(true, this.lub(upperBound(t), upperBound(s)));
            }

            if (isLowerBound(s)) {
                // lcta(U,         ? super V) = ? super glb(U, V)
                // lcta(? super U, ? super V) = ? super glb(U, V)

                return ts.wildcard(false, this.glb(lowerBound(t), lowerBound(s)));
            }


            // otherwise ? extends lub(U, V)

            return ts.wildcard(true, this.lub(t, s));
        }
    }

    /** Simple record type for a pair of types. */
    private static final class TypePair {

        public final JTypeMirror left;
        public final JTypeMirror right;

        TypePair(JTypeMirror left, JTypeMirror right) {
            this.left = left;
            this.right = right;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TypePair pair = (TypePair) o;
            return Objects.equals(left, pair.left)
                && Objects.equals(right, pair.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public String toString() {
            return "Pair(" + left + " - " + right + ")";
        }
    }


    static JTypeMirror glb(TypeSystem ts, Collection<? extends JTypeMirror> types) {
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Cannot compute GLB of empty set");
        } else if (types.size() == 1) {
            return types.iterator().next();
        }


        List<JTypeMirror> flat = flattenRemoveTrivialBound(types);

        if (flat.size() == 1) {
            return flat.get(0);
        } else if (flat.isEmpty()) {
            return ts.OBJECT;
        }

        Set<JTypeMirror> mostSpecific = TypeOps.mostSpecific(flat);
        assert !mostSpecific.isEmpty() : "Empty most specific for bounds " + flat;

        if (mostSpecific.size() == 1) {
            return mostSpecific.iterator().next();
        }

        List<JTypeMirror> bounds = new ArrayList<>(mostSpecific.size());
        bounds.add(null); // first element will be replaced with primary bound

        JTypeMirror primaryBound = null;

        for (JTypeMirror ci : mostSpecific) {
            if (isExclusiveIntersectionBound(ci)) {
                // either Ci is an array, or Ci is a class, or Ci is a type var (possibly captured)
                // Ci is not unresolved
                if (primaryBound == null) {
                    primaryBound = ci;
                } else if (ci.isArray() && primaryBound.isArray()) {
                    // A[] & B[] = (A & B)[]
                    // Note that since we're after mostSpecific, we already know
                    // that A is unrelated to B. Therefore if both B and A are classes,
                    // then A & B cannot exist and so (A & B)[] similarly does not exist.

                    JTypeMirror componentGlb = glb(ts, setOf(((JArrayType) ci).getComponentType(),
                                                             ((JArrayType) primaryBound).getComponentType()));
                    primaryBound = ts.arrayType(componentGlb);

                } else {
                    // We have two primary bounds. This may happen during capture
                    // of recursive F-bounded types. Here we do a last resort check
                    // to see if one of the bounds is a subtype (unchecked) of the other.
                    // In that case we pick this as primary bound as it is more specific.

                    int cmp = compareRelatedness(ci.getErasure(), primaryBound.getErasure());
                    if (cmp == 0) {
                        throw new IllegalArgumentException(
                            "Bad intersection, unrelated class types " + ci + " and " + primaryBound + " in " + types
                        );
                    } else if (cmp < 0) {
                        primaryBound = ci;
                    }
                }
            } else {
                bounds.add(ci);
            }
        }


        if (primaryBound == null) {
            primaryBound = ts.OBJECT;
        }
        bounds.set(0, primaryBound); // set the primary bound
        if (primaryBound == ts.OBJECT) {
            // if primary bound is object, it does not appear in the bounds
            bounds = bounds.subList(1, bounds.size());
        }
        if (bounds.size() == 1) {
            return bounds.get(0); // not an intersection
        }

        return new JIntersectionType(ts, primaryBound, bounds);
    }

    private static int compareRelatedness(JTypeMirror t, JTypeMirror s) {
        if (TypeOps.isConvertiblePure(t, s.getErasure()).withoutWarnings()) {
            return -1;
        } else if (TypeOps.isConvertiblePure(s, t.getErasure()).withoutWarnings()) {
            return 1;
        }
        return 0;
    }

    private static void checkGlbComponent(Collection<? extends JTypeMirror> types, JTypeMirror ci) {
        if (ci.isPrimitive() || ci instanceof JWildcardType || ci instanceof JIntersectionType) {
            throw new IllegalArgumentException("Bad intersection type component: " + ci + " in " + types);
        }
    }

    private static @NonNull List<JTypeMirror> flattenRemoveTrivialBound(Collection<? extends JTypeMirror> types) {
        List<JTypeMirror> bounds = new ArrayList<>(types.size());

        for (JTypeMirror type : types) {
            // flatten intersections: (A & (B & C)) => (A & B & C)
            if (type instanceof JIntersectionType) {
                bounds.addAll(((JIntersectionType) type).getComponents());
            } else {
                checkGlbComponent(types, type);

                if (!type.isTop()) {
                    bounds.add(type);
                }
            }
        }
        return bounds;
    }


    static boolean isExclusiveIntersectionBound(JTypeMirror ci) {
        return !ci.isInterface()
            && !(ci instanceof InferenceVar)
            && (ci.getSymbol() == null || !ci.getSymbol().isUnresolved());
    }


}
