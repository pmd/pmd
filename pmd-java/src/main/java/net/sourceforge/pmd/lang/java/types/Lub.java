/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.lang.java.types.TypeOps.typeArgContains;
import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.ConsPStack;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;
import org.pcollections.PStack;

import net.sourceforge.pmd.lang.java.types.internal.infer.JInferenceVar;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Helper class for {@link TypeSystem#lub(Collection)} and {@link TypeSystem#glb(Collection)}.
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
     * <pre>
     * Relevant(G) = { V | 1 ≤ i ≤ k: V in ST(Ui) and V = G<...> }
     *             = { V ∈ stunion | V = G<...> }
     * </pre>
     *
     * <p>G must be erased (raw).
     *
     * @return null if G is not a generic type, otherwise Relevant(G)
     */
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
        return list;
    }

    private static Set<JTypeMirror> erasedSuperTypes(Set<JTypeMirror> stui) {
        LinkedHashSet<JTypeMirror> erased = new LinkedHashSet<>();
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

            List<JTypeMirror> best = mec.stream().map(g -> {
                if (g instanceof JClassType) {
                    List<JClassType> relevant = relevant((JClassType) g, stunion);
                    return relevant != null ? lcp(relevant) : g;
                } else {
                    return g;
                }
            }).collect(Collectors.toList());

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
                throw new IllegalArgumentException();
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

            if (typeArgContains(t, s)) {
                return t;
            } else if (typeArgContains(s, t)) {
                return s;
            }

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

        @NonNull
        private JTypeMirror computeLcta(JTypeMirror t, JTypeMirror s) {

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
        }


        ArrayList<JTypeMirror> list = new ArrayList<>(types.size());

        for (JTypeMirror type : types) {
            // flatten intersections: (A & (B & C)) => (A & B & C)
            if (type instanceof JIntersectionType) {
                list.addAll(((JIntersectionType) type).getComponents());
            } else {
                list.add(type);
            }
        }


        JTypeMirror ck = ts.OBJECT; // Ck is a class type

        OptionalBool retryWithCaptureBounds = NO;
        PSet<JTypeMirror> cvarLowers = HashTreePSet.empty();
        PStack<JTypeMirror> cvarsToRemove = ConsPStack.empty();
        JTypeMirror lastBadClass = null;
        for (ListIterator<JTypeMirror> iterator = list.listIterator(); iterator.hasNext(); ) {
            JTypeMirror ci = iterator.next();

            if (ci.isPrimitive() || ci instanceof JWildcardType || ci instanceof JIntersectionType) {
                throw new IllegalArgumentException("Bad intersection type component: " + ci + " in " + types);
            }

            if (!isPossiblyAnInterface(ci)) {
                // either Ci is an array, or Ci is a class, or Ci is a type var (possibly captured)
                // Ci is not unresolved

                if (ci.isSubtypeOf(ck)) {
                    ck = ci; // Ci is more specific than Ck
                    iterator.remove(); // remove bound
                } else if (ck.isSubtypeOf(ci)) {
                    // then our Ck is already more specific than Ci
                    iterator.remove();
                } else {
                    JTypeMirror lower = cvarLowerBound(ci);
                    if (lower != ci && lower != ts.NULL_TYPE) {
                        cvarLowers = cvarLowers.plus(lower);
                        cvarsToRemove = cvarsToRemove.plus(ci);
                        retryWithCaptureBounds = YES;
                    } else {
                        retryWithCaptureBounds = retryWithCaptureBounds == YES ? YES
                                                                               : UNKNOWN;
                    }
                    lastBadClass = ci;
                }
            } else if (!(ci instanceof JInferenceVar) && ck.isSubtypeOf(ci)) {
                // then our Ck is already more specific than Ci
                iterator.remove();
            }
        }

        switch (retryWithCaptureBounds) {
        case YES:
            list.removeAll(cvarsToRemove);
            list.addAll(cvarLowers);
            return glb(ts, list);
        case NO:
            break;
        case UNKNOWN:
            throw new IllegalArgumentException("Bad intersection, unrelated class types " + lastBadClass + " and " + ck + " in " + types);
        }

        if (list.isEmpty()) {
            return ck;
        }

        if (ck != ts.OBJECT) {
            // readd ck as first component
            list.add(0, ck);
        }

        if (list.size() == 1) {
            return list.get(0);
        }

        if (ck instanceof JTypeVar) {
            return new IntersectionTypeImpl(ts, list);
        }

        // We assume there cannot be an array type here. Why?
        // In well-formed java programs an array type in a GLB can only occur in the following situation
        //
        // class C<T extends B1 & .. & Bn>      // nota: the Bi cannot be array types
        //
        // Somewhere: C<? extends Arr[]>

        // And capture would merge the bounds of the wildcard and of the tvar
        // into Arr[] & B1 & .. & Bn
        // Now the C<? ...> would only typecheck if Arr[] <: Bi forall i
        // (Note that this means, that Bi in { Serializable, Cloneable, Object })

        // This means, that the loop above would find Ck = Arr[], and delete all Bi, since Ck <: Bi
        // So in the end, we would return Arr[] alone, not create an intersection
        // TODO this is order dependent: Arr[] & Serializable is ok, but Serializable & Arr[] is not
        //   Possibly use TypeOps::mostSpecific to merge them
        assert ck instanceof JClassType : "Weird intersection involving multiple array types? " + list;

        return new IntersectionTypeImpl.MinimalIntersection(ts, (JClassType) ck, list);
    }


    private static boolean isPossiblyAnInterface(JTypeMirror ci) {
        return ci.isInterface()
            || ci instanceof JInferenceVar
            || ci.getSymbol() != null && ci.getSymbol().isUnresolved();
    }

    private static JTypeMirror cvarLowerBound(JTypeMirror t) {
        if (t instanceof JTypeVar && ((JTypeVar) t).isCaptured()) {
            return ((JTypeVar) t).getLowerBound();
        }
        return t;
    }


}
