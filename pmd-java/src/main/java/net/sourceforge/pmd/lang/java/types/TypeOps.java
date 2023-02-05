/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.lang.java.types.Substitution.EMPTY;
import static net.sourceforge.pmd.lang.java.types.Substitution.mapping;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.capture;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.CoreResolvers;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaResolvers;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;
import net.sourceforge.pmd.lang.java.types.internal.infer.OverloadSet;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * Common operations on types.
 */
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public final class TypeOps {

    private TypeOps() {
        // utility class
    }


    // <editor-fold  defaultstate="collapsed" desc="Type equality">

    /**
     * Return true if t and s are the same method type. This compares
     * their declaring type, and then their signature.
     *
     * @see #haveSameSignature(JMethodSig, JMethodSig)
     */
    public static boolean isSameType(JMethodSig t, JMethodSig s) {
        return t.getDeclaringType().equals(s.getDeclaringType()) && haveSameSignature(t, s);
    }

    /*
     * Note that type mirror implementations use this method as their
     * Object#equals, which means it can't be used here unless it's on
     * the smaller parts of a type.
     */


    /**
     * Return true if t and s are the same type, ignoring any type annotations
     * appearing within them. This is the implementation of the equals method
     * of {@link JTypeMirror}.
     */
    public static boolean isSameType(JTypeMirror t, JTypeMirror s) {
        return isSameType(t, s, false, false);
    }

    /**
     * Return true if t and s are the same type, considering any type annotations
     * appearing within them.
     */
    public static boolean isSameTypeWithSameAnnotations(JTypeMirror t, JTypeMirror s) {
        return isSameType(t, s, false, true);
    }

    /**
     * Return true if t and s are the same type. This may perform side effects
     * on inference variables. Annotations are ignored.
     */
    @InternalApi
    public static boolean isSameTypeInInference(JTypeMirror t, JTypeMirror s) {
        return isSameType(t, s, true, false);
    }

    /**
     * Returns true if t and s are the same type. If 'inInference' is
     * true, then encountering inference variables produces side effects
     * on them, adding bounds.
     */
    private static boolean isSameType(JTypeMirror t, JTypeMirror s, boolean inInference, boolean considerAnnotations) {
        if (t == s) {
            // also returns true if both t and s are null
            return true;
        }

        if (t == null || s == null) {
            return false;
        }

        if (!inInference) {
            if (considerAnnotations) {
                if (t instanceof CaptureMatcher || s instanceof CaptureMatcher) {
                    return t.equals(s); // skip check for type annotations
                }
                return t.getTypeAnnotations().equals(s.getTypeAnnotations())
                    && t.acceptVisitor(SameTypeVisitor.PURE_WITH_ANNOTATIONS, s);
            } else {
                return t.acceptVisitor(SameTypeVisitor.PURE, s);
            }
        }

        // reorder
        if (t instanceof InferenceVar) {
            return t.acceptVisitor(SameTypeVisitor.INFERENCE, s);
        } else {
            return s.acceptVisitor(SameTypeVisitor.INFERENCE, t);
        }
    }

    public static boolean areSameTypes(List<JTypeMirror> ts, List<JTypeMirror> ss) {
        return areSameTypes(ts, ss, EMPTY, false, false);
    }

    public static boolean areSameTypesInInference(List<JTypeMirror> ts, List<JTypeMirror> ss) {
        return areSameTypes(ts, ss, EMPTY, true, false);
    }

    private static boolean areSameTypes(List<JTypeMirror> ts, List<JTypeMirror> ss, boolean inInference, boolean considerAnnotations) {
        return areSameTypes(ts, ss, EMPTY, inInference, considerAnnotations);
    }

    private static boolean areSameTypes(List<JTypeMirror> ts, List<JTypeMirror> ss, Substitution subst) {
        return areSameTypes(ts, ss, subst, false, false);
    }

    private static boolean areSameTypes(List<JTypeMirror> ts, List<JTypeMirror> ss, Substitution subst, boolean inInference, boolean considerAnnotations) {
        if (ts.size() != ss.size()) {
            return false;
        }
        for (int i = 0; i < ts.size(); i++) {
            if (!isSameType(ts.get(i), ss.get(i).subst(subst), inInference, considerAnnotations)) {
                return false;
            }
        }
        return true;
    }

    // note that this does not take type annotations into account
    private static final class SameTypeVisitor implements JTypeVisitor<Boolean, JTypeMirror> {

        static final SameTypeVisitor INFERENCE = new SameTypeVisitor(true, false);
        static final SameTypeVisitor PURE = new SameTypeVisitor(false, false);
        static final SameTypeVisitor PURE_WITH_ANNOTATIONS = new SameTypeVisitor(false, true);

        private final boolean inInference;
        private final boolean considerAnnotations;

        private SameTypeVisitor(boolean inInference, boolean considerAnnotations) {
            this.inInference = inInference;
            this.considerAnnotations = considerAnnotations;
        }

        @Override
        public Boolean visit(JTypeMirror t, JTypeMirror s) {
            // for sentinel types
            return t == s;
        }

        @Override
        public Boolean visitPrimitive(JPrimitiveType t, JTypeMirror s) {
            return s.isPrimitive(t.getKind());
        }

        @Override
        public Boolean visitClass(JClassType t, JTypeMirror s) {
            if (s instanceof JClassType) {
                JClassType s2 = (JClassType) s;
                return t.getSymbol().equals(s2.getSymbol()) // maybe compare the type system as well.
                    && t.hasErasedSuperTypes() == s2.hasErasedSuperTypes()
                    && isSameType(t.getEnclosingType(), s2.getEnclosingType(), inInference, considerAnnotations)
                    && areSameTypes(t.getTypeArgs(), s2.getTypeArgs(), inInference, considerAnnotations);
            }
            return false;
        }

        @Override
        public Boolean visitTypeVar(JTypeVar t, JTypeMirror s) {
            return t.equals(s);
        }

        @Override
        public Boolean visitWildcard(JWildcardType t, JTypeMirror s) {
            if (!(s instanceof JWildcardType)) {
                return false;
            }
            JWildcardType s2 = (JWildcardType) s;
            return s2.isUpperBound() == t.isUpperBound() && isSameType(t.getBound(), s2.getBound(), inInference, considerAnnotations);
        }

        @Override
        public Boolean visitInferenceVar(InferenceVar t, JTypeMirror s) {
            if (!inInference) {
                return t == s;
            }

            if (s instanceof JPrimitiveType) {
                return false;
            }

            if (s instanceof JWildcardType) {
                JWildcardType s2 = (JWildcardType) s;
                if (s2.isUpperBound()) {
                    t.addBound(BoundKind.UPPER, s2.asUpperBound());
                } else {
                    t.addBound(BoundKind.LOWER, s2.asLowerBound());
                }
                return true;
            }

            // add an equality bound
            t.addBound(BoundKind.EQ, s);
            return true;
        }

        @Override
        public Boolean visitIntersection(JIntersectionType t, JTypeMirror s) {
            if (!(s instanceof JIntersectionType)) {
                return false;
            }

            JIntersectionType s2 = (JIntersectionType) s;

            // order is irrelevant

            if (s2.getComponents().size() != t.getComponents().size()) {
                return false;
            }

            if (!isSameType(t.getPrimaryBound(), s2.getPrimaryBound(), inInference, considerAnnotations)) {
                return false;
            }

            List<JTypeMirror> sComps = ((JIntersectionType) s).getComponents();
            for (JTypeMirror ti : t.getComponents()) {
                boolean found = false;
                for (JTypeMirror si : sComps) {
                    // todo won't this behaves weirdly during inference? test it
                    if (isSameType(ti, si, inInference, considerAnnotations)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Boolean visitArray(JArrayType t, JTypeMirror s) {
            return s instanceof JArrayType
                && isSameType(t.getComponentType(), ((JArrayType) s).getComponentType(), inInference, considerAnnotations);
        }
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Supertype enumeration">


    /**
     * Returns the set of all supertypes of the given type.
     *
     * @see JTypeMirror#getSuperTypeSet()
     */
    public static Set<JTypeMirror> getSuperTypeSet(@NonNull JTypeMirror t) {
        Set<JTypeMirror> result = new LinkedHashSet<>();
        t.acceptVisitor(SuperTypesVisitor.INSTANCE, result);
        assert !result.isEmpty() : "Empty supertype set for " + t;
        return result;
    }

    private static final class SuperTypesVisitor implements JTypeVisitor<Void, Set<JTypeMirror>> {

        static final SuperTypesVisitor INSTANCE = new SuperTypesVisitor();

        @Override
        public Void visit(JTypeMirror t, Set<JTypeMirror> result) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public Void visitTypeVar(JTypeVar t, Set<JTypeMirror> result) {
            if (result.add(t)) {
                // prevent infinite loop
                t.getUpperBound().acceptVisitor(this, result);
            }
            return null;
        }

        @Override
        public Void visitNullType(JTypeMirror t, Set<JTypeMirror> result) {
            // too many types
            throw new UnsupportedOperationException("The null type has all reference types as supertype");
        }

        @Override
        public Void visitSentinel(JTypeMirror t, Set<JTypeMirror> result) {
            result.add(t);
            return null;
        }

        @Override
        public Void visitInferenceVar(InferenceVar t, Set<JTypeMirror> result) {
            result.add(t);
            return null;
        }

        @Override
        public Void visitWildcard(JWildcardType t, Set<JTypeMirror> result) {
            t.asUpperBound().acceptVisitor(this, result);
            // wildcards should be captured and so we should not end up here
            return null;
        }

        @Override
        public Void visitClass(JClassType t, Set<JTypeMirror> result) {
            result.add(t);


            // prefer digging up the superclass first
            JClassType sup = t.getSuperClass();
            if (sup != null) {
                sup.acceptVisitor(this, result);
            }
            for (JClassType i : t.getSuperInterfaces()) {
                visitClass(i, result);
            }
            if (t.isInterface() && t.getSuperInterfaces().isEmpty()) {
                result.add(t.getTypeSystem().OBJECT);
            }
            return null;
        }

        @Override
        public Void visitIntersection(JIntersectionType t, Set<JTypeMirror> result) {
            for (JTypeMirror it : t.getComponents()) {
                it.acceptVisitor(this, result);
            }
            return null;
        }

        @Override
        public Void visitArray(JArrayType t, Set<JTypeMirror> result) {
            result.add(t);

            TypeSystem ts = t.getTypeSystem();

            for (JTypeMirror componentSuper : t.getComponentType().getSuperTypeSet()) {
                result.add(ts.arrayType(componentSuper));
            }
            result.add(ts.CLONEABLE);
            result.add(ts.SERIALIZABLE);
            result.add(ts.OBJECT);

            return null;
        }

        @Override
        public Void visitPrimitive(JPrimitiveType t, Set<JTypeMirror> result) {
            result.addAll(t.getSuperTypeSet()); // special implementation in JPrimitiveType
            return null;
        }
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Subtyping">


    public static Convertibility isConvertible(@NonNull JTypeMirror t, @NonNull JTypeMirror s) {
        return isConvertible(t, s, true);
    }

    public static Convertibility isConvertibleNoCapture(@NonNull JTypeMirror t, @NonNull JTypeMirror s) {
        return isConvertible(t, s, false);
    }

    /**
     * Returns whether if {@code T <: S}, ie T is a subtype of S.
     *
     * <p>Note that {@link TypeSystem#ERROR} and {@link TypeSystem#UNKNOWN}
     * are considered subtypes of anything.
     *
     * @param t A type T
     * @param s A type S
     */
    public static Convertibility isConvertible(@NonNull JTypeMirror t, @NonNull JTypeMirror s, boolean capture) {
        // This is commented out as it makes JTypeMirror#isSubtypeOf partial,
        // which is not nice for the API... But this assert caught a bug and
        // should probably be enabled.
        // assert !(t instanceof JWildcardType || s instanceof JWildcardType) : "Wildcards do not support subtyping";

        if (t == s) {
            Objects.requireNonNull(t);
            return Convertibility.SUBTYPING;
        } else if (s.isTop()) {
            return Convertibility.subtypeIf(!t.isPrimitive());
        } else if (s.isVoid() || t.isVoid()) { // t != s
            return Convertibility.NEVER;
        } else if (s instanceof InferenceVar) {
            // it's possible to add a bound to UNKNOWN or ERROR
            ((InferenceVar) s).addBound(BoundKind.LOWER, t);
            return Convertibility.SUBTYPING;
        } else if (isTypeRange(s)) {
            // If s is a type range L..U,
            // then showing t <: s is the same thing as t <: L
            JTypeMirror lower = lowerBoundRec(s);
            if (!lower.isBottom()) {
                return isConvertible(t, lower, capture);
            }
            // otherwise fallthrough
        } else if (isSpecialUnresolved(t)) {
            // error type or unresolved type
            return Convertibility.SUBTYPING;
        } else if (hasUnresolvedSymbol(t) && t instanceof JClassType) {
            // This also considers types with an unresolved symbol
            // subtypes of (nearly) anything. This allows them to
            // pass bound checks on type variables.
            if (Objects.equals(t.getSymbol(), s.getSymbol())) {
                return typeArgsAreContained((JClassType) t, (JClassType) s);
            } else {
                return Convertibility.subtypeIf(s instanceof JClassType); // excludes array or so
            }
        } else if (s instanceof JIntersectionType) { // TODO test intersection with tvars & arrays
            // If S is an intersection, then T must conform to *all* bounds of S
            // Symmetrically, if T is an intersection, T <: S requires only that
            // at least one bound of T is a subtype of S.
            return Convertibility.subtypesAll(t, asList(s));
        }

        if (capture) {
            t = capture(t);
        }
        return t.acceptVisitor(SubtypeVisitor.INSTANCE, s);
    }

    // does not perform side effects on inference vars
    private static Convertibility isSubtypePure(JTypeMirror t, JTypeMirror s) {
        if (t instanceof InferenceVar) {
            return Convertibility.subtypeIf(((InferenceVar) t).isSubtypeNoSideEffect(s));
        } else if (s instanceof InferenceVar) {
            return Convertibility.subtypeIf(((InferenceVar) s).isSupertypeNoSideEffect(t));
        }

        return isConvertible(t, s);
    }

    public static boolean allArgsAreUnboundedWildcards(List<JTypeMirror> sargs) {
        for (JTypeMirror sarg : sargs) {
            if (!(sarg instanceof JWildcardType) || !((JWildcardType) sarg).isUnbounded()) {
                return false;
            }
        }
        return true;
    }

    /**
     * A result for a convertibility check. This is a tiny generalization of
     * a subtyping check.
     *
     * <p>Primitive types are implicitly convertible to each other by
     * widening primitive conversion. For reference types, subtyping
     * implies convertibility (the conversion is technically called
     * "widening reference conversion"). You can check those cases using:
     *
     * {@link #bySubtyping() t.isConvertibleTo(s).bySubtyping()}
     *
     * <p>Unchecked conversion may go backwards from subtyping. For example,
     * {@code List<String>} is a subtype of the raw type {@code List}, and
     * as such is convertible to it by reference widening. But {@code List}
     * may be "coerced" to {@code List<String>} with an unchecked warning:
     *
     * {@link #withUncheckedWarning() t.isConvertibleTo(s).withUncheckedWarning()}
     *
     * <p>If the parameterized type only has wildcard type arguments,
     * then the conversion produces no warning.
     *
     * {@link #UNCHECKED_NO_WARNING t.isConvertibleTo(s) == UNCHECKED_NO_WARNING}
     *
     * <p>Two types may be unconvertible:
     *
     * {@link #never() t.isConvertibleTo(s).never()}
     *
     * <p>the negation of which being
     *
     * {@link #somehow() t.isConvertibleTo(s).somehow()}
     *
     * <p>Note that this does not check for boxing or unboxing conversions,
     * nor for narrowing conversions, which may happen through casts.
     */
    public enum Convertibility {
        /** T is never implicitly convertible to S. */
        NEVER,

        /**
         * T is not a subtype of S, but every time T is used in a context
         * where an S is expected, unchecked conversion converts the T to
         * an S with a mandated warning. For example the raw type {@code Class}
         * is convertible to {@code Class<String>} with an unchecked warning.
         */
        UNCHECKED_WARNING,

        /**
         * {@code T <: |S|} and {@code T </: S}, but S is
         * parameterized with only unbounded wildcards. This is a special
         * case of unchecked conversion that produces no warning. We keep
         * it distinct from subtyping to help some algorithms that require
         * subtyping to be a partial order.
         *
         * <p>For example, {@code List<String>} is a subtype of the raw
         * {@code Collection}, not a subtype of {@code Collection<?>},
         * but it is still convertible without warning.
         */
        UNCHECKED_NO_WARNING,

        /**
         * T is a subtype of S ({@code T <: S}). In particular, any type
         * is a subtype of itself ({@code T <: T}).
         *
         * <p>For example, {@code int} can be widened to {@code long},
         * so we consider {@code int <: long}.
         */
        SUBTYPING;

        // public:

        /** Returns true if this is {@link #NEVER}. */
        public boolean never() {
            return this == NEVER;
        }

        /**
         * Returns true if this is anything but {@link #NEVER}.
         */
        public boolean somehow() {
            return this != NEVER;
        }

        /**
         * True if this is {@link #SUBTYPING}.
         */
        public boolean bySubtyping() {
            return this == SUBTYPING;
        }

        /**
         * True if this is {@link #UNCHECKED_WARNING}.
         */
        public boolean withUncheckedWarning() {
            return this == UNCHECKED_WARNING;
        }

        // package:


        /** Preserves an unchecked warning. */
        Convertibility and(Convertibility b) {
            return min(this, b);
        }

        static Convertibility min(Convertibility c1, Convertibility c2) {
            return c1.ordinal() < c2.ordinal() ? c1 : c2;
        }

        static Convertibility subtypeIf(boolean b) {
            return b ? SUBTYPING : NEVER;
        }

        static Convertibility subtypesAll(JTypeMirror t, Iterable<? extends JTypeMirror> supers) {
            Convertibility result = SUBTYPING;
            for (JTypeMirror ui : supers) {
                Convertibility sub = isConvertible(t, ui);
                if (sub == NEVER) {
                    return NEVER;
                }
                result = result.and(sub);
            }
            return result;
        }

        static Convertibility anySubTypesAny(Iterable<? extends JTypeMirror> us, Iterable<? extends JTypeMirror> vs) {
            for (JTypeMirror ui : us) {
                for (JTypeMirror vi : vs) {
                    Convertibility sub = isConvertible(ui, vi);
                    if (sub != NEVER) {
                        return sub.and(SUBTYPING); // never return identity here
                    }
                }
            }
            return NEVER;
        }
    }

    private static JTypeMirror wildUpperBound(JTypeMirror type) {
        if (type instanceof JWildcardType) {
            JWildcardType wild = (JWildcardType) type;
            if (wild.isUpperBound()) {
                return wildUpperBound(wild.asUpperBound());
            } else if (wild.asLowerBound() instanceof JTypeVar) {
                return ((JTypeVar) wild.asLowerBound()).getUpperBound();
            }
        } else if (type instanceof JTypeVar && ((JTypeVar) type).isCaptured()) {
            // note: tvar.getUpperBound() != tvar.getCapturedOrigin().asUpperBound()
            return wildUpperBound(((JTypeVar) type).getUpperBound());
        }
        return type;
    }

    private static JTypeMirror wildLowerBound(JTypeMirror type) {
        if (type instanceof JWildcardType) {
            return wildLowerBound(((JWildcardType) type).asLowerBound());
        }
        return type;
    }

    private static JTypeMirror lowerBoundRec(JTypeMirror type) {
        if (type instanceof JWildcardType) {
            return lowerBoundRec(((JWildcardType) type).asLowerBound());
        } else if (type instanceof JTypeVar && ((JTypeVar) type).isCaptured()) {
            return lowerBoundRec(((JTypeVar) type).getLowerBound());
        }
        return type;
    }

    private static boolean isTypeRange(JTypeMirror s) {
        return s instanceof JWildcardType || isCvar(s);
    }

    private static boolean isCvar(JTypeMirror s) {
        return s instanceof JTypeVar && ((JTypeVar) s).isCaptured();
    }


    /**
     * Returns true if {@code T <= S}, ie "S contains T".
     *
     * <p>S contains T if:
     *
     * <p>{@code L(S) <: L(T) && U(T) <: U(S)}
     *
     * <p>This only makes sense for type arguments, it's a component of
     * subtype checks for parameterized types:
     *
     * <p>{@code C<S> <: C<T> if S <= T}
     *
     * <p>Defined in JLS§4.5.1 (Type Arguments of Parameterized Types)
     */
    static Convertibility typeArgContains(JTypeMirror s, JTypeMirror t) {
        // the contains relation can be understood intuitively if we
        // represent types as ranges on a line:

        // ⊥ ---------L(S)---L(T)------U(T)-----U(S)---> Object
        // range of S   [-------------------------]
        // range of T          [---------]

        // here S contains T because its range is greater

        // since a wildcard is either "super" or "extends", in reality
        // either L(S) = ⊥, or U(S) = Object.

        // meaning when S != T, we only have two scenarios where T <= S:

        //      ⊥ -------U(T)-----U(S)------> Object   (L(T) = L(S) = ⊥)
        //      ⊥ -------L(S)-----L(T)------> Object   (U(T) = U(S) = Object)

        if (isSameTypeInInference(s, t)) {
            // S <= S
            return Convertibility.SUBTYPING;
        }

        if (s instanceof JWildcardType) {
            JWildcardType sw = (JWildcardType) s;

            // capt(? extends T) <= ? extends T
            // capt(? super T) <= ? super T
            if (t instanceof JTypeVar && ((JTypeVar) t).isCaptureOf(sw)) {
                return Convertibility.SUBTYPING;
            }

            if (sw.isUpperBound()) {
                // Test U(T) <: U(S),  we already know L(S) <: L(T), because L(S) is bottom
                return isConvertible(wildUpperBound(t), sw.asUpperBound());
            } else {
                // Test L(S) <: L(T), we already know U(T) <: U(S), because U(S) is top
                return isConvertible(sw.asLowerBound(), wildLowerBound(t));
            }
        }

        return Convertibility.NEVER;
    }


    /**
     * Generalises containment to check if for each i, {@code Ti <= Si}.
     */
    static Convertibility typeArgsAreContained(JClassType t, JClassType s) {
        List<JTypeMirror> targs = t.getTypeArgs();
        List<JTypeMirror> sargs = s.getTypeArgs();

        if (targs.isEmpty()) {
            if (sargs.isEmpty()) {
                // Some "erased" non-generic types may appear as the supertypes
                // of raw types, and they're different from the regular flavor
                // as their own supertypes are erased, yet they're not considered
                // raw. To fix the subtyping relation, we say that `C <: (erased) C`
                // but `(erased) C` converts to `C` by unchecked conversion, without
                // warning.
                boolean tRaw = t.hasErasedSuperTypes();
                boolean sRaw = s.hasErasedSuperTypes();
                if (tRaw && !sRaw) {
                    return Convertibility.UNCHECKED_NO_WARNING;
                } else {
                    return Convertibility.SUBTYPING;
                }
            }
            // for some C, S = C<...> and T = C, ie T is raw
            // T is convertible to S, by unchecked conversion.
            // If S = D<?, .., ?>, then the conversion produces
            // no unchecked warning.
            return allArgsAreUnboundedWildcards(sargs) ? Convertibility.UNCHECKED_NO_WARNING
                                                       : Convertibility.UNCHECKED_WARNING;
        }

        if (targs.size() != sargs.size()) {
            // types are not well-formed
            return Convertibility.NEVER;
        }

        Convertibility result = Convertibility.SUBTYPING;
        for (int i = 0; i < targs.size(); i++) {
            Convertibility sub = typeArgContains(sargs.get(i), targs.get(i));
            if (sub == Convertibility.NEVER) {
                return Convertibility.NEVER;
            }
            result = result.and(sub);
        }

        return result;
    }

    private static final class SubtypeVisitor implements JTypeVisitor<Convertibility, JTypeMirror> {

        static final SubtypeVisitor INSTANCE = new SubtypeVisitor();

        @Override
        public Convertibility visit(JTypeMirror t, JTypeMirror s) {
            throw new IllegalStateException("Should not be called");
        }

        @Override
        public Convertibility visitTypeVar(JTypeVar t, JTypeMirror s) {
            if (s instanceof JTypeVar && t.getSymbol() != null && Objects.equals(t.getSymbol(), s.getSymbol())) {
                return Convertibility.SUBTYPING;
            }
            if (isTypeRange(s)) {
                return isConvertible(t, lowerBoundRec(s));
            }
            return isConvertible(t.getUpperBound(), s);
        }

        @Override
        public Convertibility visitNullType(JTypeMirror t, JTypeMirror s) {
            return Convertibility.subtypeIf(!s.isPrimitive());
        }

        @Override
        public Convertibility visitSentinel(JTypeMirror t, JTypeMirror s) {
            // we know t != s
            return t.isVoid() ? Convertibility.NEVER
                              : Convertibility.SUBTYPING;
        }

        @Override
        public Convertibility visitInferenceVar(InferenceVar t, JTypeMirror s) {
            if (s == t.getTypeSystem().NULL_TYPE || s instanceof JPrimitiveType) {
                return Convertibility.NEVER;
            }
            // here we add a constraint on the variable
            t.addBound(BoundKind.UPPER, s);
            return Convertibility.SUBTYPING;
        }

        @Override
        public Convertibility visitWildcard(JWildcardType t, JTypeMirror s) {
            // wildcards should be captured and so we should not end up here
            return Convertibility.NEVER;
        }

        @Override
        public Convertibility visitClass(JClassType t, JTypeMirror s) {
            if (!(s instanceof JClassType)) {
                // note, that this ignores wildcard types,
                // because they're only compared through
                // type argument containment.
                return Convertibility.NEVER;
            }

            JClassType cs = (JClassType) s;

            JClassType superDecl = t.getAsSuper(cs.getSymbol());

            if (superDecl == null) {
                return Convertibility.NEVER;
            } else if (cs.isRaw()) {
                // a raw type C is a supertype for all the family of parameterized type generated by C<F1, .., Fn>
                return Convertibility.SUBTYPING;
            } else {
                return typeArgsAreContained(superDecl, cs);
            }
        }

        @Override
        public Convertibility visitIntersection(JIntersectionType t, JTypeMirror s) {
            // A & B <: A
            // A & B <: B

            // But for a class C, `C <: A & B` if `C <: A` and `C <: B`

            // So we can't just say, "any component of t must subtype s",
            // because if s is itself an intersection we have a problem:
            // Eg let T = S = A & B
            // T <: S -> A & B <: S
            //        -> A <: S OR B <: S
            //        -> A <: A & B OR B <: A & B
            //        -> A <: A AND A <: B OR B <: A AND B <: B
            //        -> true   AND false  OR false  AND true
            //        -> false

            // what we mean is, if S is an intersection, then
            // "any component of T subtypes any component of S"

            return Convertibility.anySubTypesAny(t.getComponents(), asList(s));
        }

        @Override
        public Convertibility visitArray(JArrayType t, JTypeMirror s) {
            TypeSystem ts = t.getTypeSystem();
            if (s == ts.OBJECT || s.equals(ts.CLONEABLE) || s.equals(ts.SERIALIZABLE)) {
                return Convertibility.SUBTYPING;
            }

            if (!(s instanceof JArrayType)) {
                // not comparable to any other type
                return Convertibility.NEVER;
            }

            JArrayType cs = (JArrayType) s;

            if (t.getComponentType().isPrimitive() || cs.getComponentType().isPrimitive()) {
                // arrays of primitive types have no sub-/ supertype
                return Convertibility.subtypeIf(cs.getComponentType() == t.getComponentType());
            } else {
                return isConvertible(t.getComponentType(), cs.getComponentType());
            }
        }

        @Override
        public Convertibility visitPrimitive(JPrimitiveType t, JTypeMirror s) {
            if (s instanceof JPrimitiveType) {
                return t.superTypes.contains(s) ? Convertibility.SUBTYPING
                                                : Convertibility.NEVER;
            }
            return Convertibility.NEVER;
        }
    }

    public static boolean isStrictSubtype(@NonNull JTypeMirror t, @NonNull JTypeMirror s) {
        return !t.equals(s) && t.isSubtypeOf(s);
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Substitution">

    /**
     * Replace the type variables occurring in the given type to their
     * image by the given function. Substitutions are not applied
     * recursively.
     *
     * @param type  Type to substitute
     * @param subst Substitution function, eg a {@link Substitution}
     */
    public static JTypeMirror subst(@Nullable JTypeMirror type, Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        if (type == null || Substitution.isEmptySubst(subst)) {
            return type;
        }
        return type.subst(subst);
    }


    /** Substitute on a list of types. */
    public static List<JTypeMirror> subst(List<? extends JTypeMirror> ts, Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        if (Substitution.isEmptySubst(subst)) {
            return CollectionUtil.makeUnmodifiableAndNonNull(ts);
        }
        return mapPreservingSelf(ts, t -> t.subst(subst));
    }

    public static List<JClassType> substClasses(List<JClassType> ts, Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        if (Substitution.isEmptySubst(subst)) {
            return ts;
        }
        return mapPreservingSelf(ts, t -> t.subst(subst));
    }

    public static List<JTypeVar> substInBoundsOnly(List<JTypeVar> ts, Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        if (Substitution.isEmptySubst(subst)) {
            return ts;
        }
        return mapPreservingSelf(ts, t -> t.substInBounds(subst));
    }

    // relies on the fact the original list is unmodifiable or won't be
    // modified
    @SuppressWarnings("unchecked")
    private static @NonNull <T> List<T> mapPreservingSelf(List<? extends T> ts, Function<? super T, ? extends @NonNull T> subst) {
        // Profiling shows, only 10% of calls to this method need to
        // create a new list. Substitution in general is a hot spot
        // of the framework, so optimizing this out is nice
        List<T> list = null;
        for (int i = 0, size = ts.size(); i < size; i++) {
            T it = ts.get(i);
            T substed = subst.apply(it);
            if (substed != it) {
                if (list == null) {
                    list = Arrays.asList((T[]) ts.toArray()); // NOPMD ClassCastExceptionWithToArray
                }
                list.set(i, substed);
            }
        }

        // subst relies on the fact that the original list is returned
        // to avoid new type creation. Thus one cannot use
        // Collections::unmodifiableList here
        return list != null ? list : (List<T>) ts;
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Projection">


    /**
     * Returns the upwards projection of the given type, with respect
     * to the set of capture variables that are found in it. This is
     * some supertype of T which does not mention those capture variables.
     * This is used for local variable type inference.
     *
     * https://docs.oracle.com/javase/specs/jls/se11/html/jls-4.html#jls-4.10.5
     */
    public static JTypeMirror projectUpwards(JTypeMirror t) {
        return t.acceptVisitor(UPWARDS_PROJECTOR, new RecursionStop());
    }

    private static final JTypeMirror NO_DOWN_PROJECTION = null;
    private static final ProjectionVisitor UPWARDS_PROJECTOR = new ProjectionVisitor(true) {

        @Override
        public JTypeMirror visitTypeVar(JTypeVar t, RecursionStop recursionStop) {
            if (t.isCaptured()) {
                return t.getUpperBound().acceptVisitor(UPWARDS_PROJECTOR, recursionStop);
            }
            return t;
        }


        @Override
        public JTypeMirror visitWildcard(JWildcardType t, RecursionStop recursionStop) {
            JTypeMirror u = t.getBound().acceptVisitor(UPWARDS_PROJECTOR, recursionStop);
            TypeSystem ts = t.getTypeSystem();
            if (u == t.getBound()) {
                return t;
            }

            if (t.isUpperBound()) {
                return ts.wildcard(true, u);
            } else {
                JTypeMirror down = t.getBound().acceptVisitor(DOWNWARDS_PROJECTOR, recursionStop);
                return down == NO_DOWN_PROJECTION ? ts.UNBOUNDED_WILD : ts.wildcard(false, down);
            }
        }


        @Override
        public JTypeMirror visitNullType(JTypeMirror t, RecursionStop recursionStop) {
            return t;
        }

    };


    private static final ProjectionVisitor DOWNWARDS_PROJECTOR = new ProjectionVisitor(false) {

        @Override
        public JTypeMirror visitWildcard(JWildcardType t, RecursionStop recursionStop) {
            JTypeMirror u = t.getBound().acceptVisitor(UPWARDS_PROJECTOR, recursionStop);
            if (u == t.getBound()) {
                return t;
            }
            TypeSystem ts = t.getTypeSystem();

            if (t.isUpperBound()) {
                JTypeMirror down = t.getBound().acceptVisitor(DOWNWARDS_PROJECTOR, recursionStop);
                return down == NO_DOWN_PROJECTION ? NO_DOWN_PROJECTION
                                                  : ts.wildcard(true, down);
            } else {
                return ts.wildcard(false, u);
            }
        }


        @Override
        public JTypeMirror visitTypeVar(JTypeVar t, RecursionStop recursionStop) {
            if (t.isCaptured()) {
                return t.getLowerBound().acceptVisitor(DOWNWARDS_PROJECTOR, recursionStop);
            }
            return t;
        }

        @Override
        public JTypeMirror visitNullType(JTypeMirror t, RecursionStop recursionStop) {
            return NO_DOWN_PROJECTION;
        }
    };

    static final class RecursionStop {

        private Set<JTypeVar> set;

        boolean isAbsent(JTypeVar tvar) {
            if (set == null) {
                set = new LinkedHashSet<>(1);
            }
            return set.add(tvar);
        }

        <T extends JTypeMirror> JTypeMirror recurseIfNotDone(T t, BiFunction<T, RecursionStop, JTypeMirror> body) {
            if (t instanceof JTypeVar) {
                JTypeVar var = (JTypeVar) t;
                try {
                    if (isAbsent(var)) {
                        return body.apply(t, this);
                    } else {
                        return t;
                    }
                } finally {
                    set.remove(var);
                }
            } else {
                return body.apply(t, this);
            }
        }
    }

    /**
     * Restricted type variables are:
     * - Inference vars
     * - Capture vars
     *
     * See
     *
     * https://docs.oracle.com/javase/specs/jls/se11/html/jls-4.html#jls-4.10.5
     *
     *
     * <p>Here we use {@link #NO_DOWN_PROJECTION} as a sentinel
     * (downwards projection is a partial function). If a type does not mention
     * restricted type variables, then the visitor should return the original
     * type (same reference). This allows testing predicates like
     * <blockquote>
     * "If Ai does not mention any restricted type variable, then Ai' = Ai."
     * </blockquote>
     */
    private abstract static class ProjectionVisitor implements JTypeVisitor<JTypeMirror, RecursionStop> {

        private final boolean upwards;

        private ProjectionVisitor(boolean upwards) {
            this.upwards = upwards;
        }


        @Override
        public abstract JTypeMirror visitNullType(JTypeMirror t, RecursionStop recursionStop);


        @Override
        public abstract JTypeMirror visitWildcard(JWildcardType t, RecursionStop recursionStop);


        @Override
        public abstract JTypeMirror visitTypeVar(JTypeVar t, RecursionStop recursionStop);


        @Override
        public JTypeMirror visit(JTypeMirror t, RecursionStop recursionStop) {
            return t;
        }

        @Override
        public JTypeMirror visitClass(JClassType t, RecursionStop recursionStop) {
            if (t.isParameterizedType()) {
                TypeSystem ts = t.getTypeSystem();

                List<JTypeMirror> targs = t.getTypeArgs();
                List<JTypeMirror> newTargs = new ArrayList<>(targs.size());
                List<JTypeVar> formals = t.getFormalTypeParams();
                boolean change = false;

                for (int i = 0; i < targs.size(); i++) {
                    JTypeMirror ai = targs.get(i);
                    JTypeMirror u = recursionStop.recurseIfNotDone(ai, (s, stop) -> s.acceptVisitor(this, stop));
                    if (u == ai) {
                        if (isCvar(ai)) { // cvar hit recursion stop
                            u = ts.UNBOUNDED_WILD;
                            change = true;
                        }
                        // no change, or handled by the visitWildcard
                        newTargs.add(u);
                        continue;
                    } else if (!upwards) {
                        // If Ai is a type that mentions a restricted type variable, then Ai' is undefined.
                        return NO_DOWN_PROJECTION;
                    }

                    change = true;

                    /*
                        If Ai is a type that mentions a restricted type variable...
                     */
                    JTypeMirror bi = formals.get(i).getUpperBound();

                    if (u != ts.OBJECT && (mentionsAny(bi, formals) || !bi.isSubtypeOf(u))) {
                        newTargs.add(ts.wildcard(true, u));
                    } else {
                        JTypeMirror down = ai.acceptVisitor(DOWNWARDS_PROJECTOR, recursionStop);
                        if (down == NO_DOWN_PROJECTION) {
                            newTargs.add(ts.UNBOUNDED_WILD);
                        } else {
                            newTargs.add(ts.wildcard(false, down));
                        }
                    }
                }

                return change ? t.withTypeArguments(newTargs) : t;
            } else {
                return t;
            }
        }

        @Override
        public JTypeMirror visitIntersection(JIntersectionType t, RecursionStop recursionStop) {
            List<JTypeMirror> comps = new ArrayList<>(t.getComponents());
            boolean change = false;
            for (int i = 0; i < comps.size(); i++) {
                JTypeMirror ci = comps.get(i);
                JTypeMirror proj = ci.acceptVisitor(this, recursionStop);
                if (proj == NO_DOWN_PROJECTION) {
                    return NO_DOWN_PROJECTION;
                } else {
                    comps.set(i, proj);
                    if (ci != proj) {
                        change = true;
                    }
                }
            }
            return change ? t.getTypeSystem().glb(comps) : t;
        }

        @Override
        public JTypeMirror visitArray(JArrayType t, RecursionStop recursionStop) {
            JTypeMirror comp2 = t.getComponentType().acceptVisitor(this, recursionStop);
            return comp2 == NO_DOWN_PROJECTION
                   ? NO_DOWN_PROJECTION
                   : comp2 == t.getComponentType()
                     ? t : t.getTypeSystem().arrayType(comp2);
        }

        @Override
        public JTypeMirror visitSentinel(JTypeMirror t, RecursionStop recursionStop) {
            return t;
        }
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Overriding">

    /**
     * Returns true if m1 is return-type substitutable with m2. The notion of return-type-substitutability
     * supports covariant returns, that is, the specialization of the return type to a subtype.
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.4.5
     */
    public static boolean isReturnTypeSubstitutable(JMethodSig m1, JMethodSig m2) {

        JTypeMirror r1 = m1.getReturnType();
        JTypeMirror r2 = m2.getReturnType();

        if (r1 == r1.getTypeSystem().NO_TYPE) {
            return r1 == r2;
        }

        if (r1.isPrimitive()) {
            return r1 == r2;
        }

        JMethodSig m1Prime = adaptForTypeParameters(m1, m2);
        return m1Prime != null && isConvertible(m1Prime.getReturnType(), r2) != Convertibility.NEVER
                || !haveSameSignature(m1, m2) && isSameType(r1, r2.getErasure());
    }

    /**
     * Adapt m1 to the type parameters of m2. Returns null if that's not possible.
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.4.4
     *
     * <p>Note that the type parameters of m1 are not replaced, only
     * their occurrences in the rest of the signature.
     */
    static @Nullable JMethodSig adaptForTypeParameters(JMethodSig m1, JMethodSig m2) {
        if (haveSameTypeParams(m1, m2)) {
            return m1.subst(mapping(m1.getTypeParameters(), m2.getTypeParameters()));
        }

        return null;
    }

    public static boolean haveSameTypeParams(JMethodSig m1, JMethodSig m2) {
        List<JTypeVar> tp1 = m1.getTypeParameters();
        List<JTypeVar> tp2 = m2.getTypeParameters();
        if (tp1.size() != tp2.size()) {
            return false;
        }

        if (tp1.isEmpty()) {
            return true;
        }

        Substitution mapping = mapping(tp2, tp1);
        for (int i = 0; i < tp1.size(); i++) {
            JTypeVar p1 = tp1.get(i);
            JTypeVar p2 = tp2.get(i);

            if (!isSameType(p1.getUpperBound(), subst(p2.getUpperBound(), mapping))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Two method signatures m1 and m2 are override-equivalent iff either
     * m1 is a subsignature of m2 or m2 is a subsignature of m1. This does
     * not look at the origin of the methods (their declaring class).
     *
     * <p>This is a prerequisite for one method to override the other,
     * but not the only condition. See {@link #overrides(JMethodSig, JMethodSig, JTypeMirror)}.
     *
     * See <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-8.html#jls-8.4.2">JLS§8</a>
     */
    public static boolean areOverrideEquivalent(JMethodSig m1, JMethodSig m2) {
        // This method is a very hot spot as it is used to prune shadowed/overridden/hidden
        // methods from overload candidates before overload resolution.
        // Any optimization makes a big impact.
        if (m1.getArity() != m2.getArity()) {
            return false; // easy case
        } else if (m1 == m2) {
            return true;
        } else if (!m1.getName().equals(m2.getName())) {
            // note: most call sites statically know this is true
            // profile to figure out whether this matters
            return false;
        }

        List<JTypeMirror> formals1 = m1.getFormalParameters();
        List<JTypeMirror> formals2 = m2.getFormalParameters();
        for (int i = 0; i < formals1.size(); i++) {
            JTypeMirror fi1 = formals1.get(i);
            JTypeMirror fi2 = formals2.get(i);

            if (!isSameType(fi1.getErasure(), fi2.getErasure())) {
                return false;
            }
        }

        // a non-generic method may override a generic one
        return !m1.isGeneric() || !m2.isGeneric()
            // if both are generic, they must have the same type params
            || haveSameTypeParams(m1, m2);
    }

    /**
     * The signature of a method m1 is a subsignature of the signature of a method m2 if either:
     * - m2 has the same signature as m1, or
     * - the signature of m1 is the same as the erasure (§4.6) of the signature of m2.
     */
    public static boolean isSubSignature(JMethodSig m1, JMethodSig m2) {
        // prune easy cases
        if (m1.getArity() != m2.getArity() || !m1.getName().equals(m2.getName())) {
            return false;
        }
        boolean m1Gen = m1.isGeneric();
        boolean m2Gen = m2.isGeneric();
        if (m1Gen ^ m2Gen) {
            if (m1Gen) {
                return false; // this test is assymetric
            } else {
                m2 = m2.getErasure();
            }
        }
        return haveSameSignature(m1, m2);
    }

    /**
     * Two methods or constructors, M and N, have the same signature if
     * they have the same name, the same type parameters (if any) (§8.4.4),
     * and, after adapting the formal parameter types of N to the the type
     * parameters of M, the same formal parameter types.
     *
     * Thrown exceptions are not part of the signature of a method.
     */
    private static boolean haveSameSignature(JMethodSig m1, JMethodSig m2) {
        return m1.getName().equals(m2.getName())
                && m1.getArity() == m2.getArity()
                && haveSameTypeParams(m1, m2)
                && areSameTypes(m1.getFormalParameters(),
                            m2.getFormalParameters(),
                            Substitution.mapping(m2.getTypeParameters(), m1.getTypeParameters()));
    }

    /**
     * Returns true if m1 overrides m2, when both are view as members of
     * class origin. m1 and m2 may be declared in supertypes of origin,
     * possibly unrelated (default methods), which is why we need that
     * third parameter. By convention a method overrides itself.
     *
     * <p>This method ignores the static modifier. If both methods are
     * static, then this method tests for <i>hiding</i>. Otherwise, this
     * method properly tests for overriding. Note that it is an error for
     * a static method to override an instance method, or the reverse.
     */
    public static boolean overrides(JMethodSig m1, JMethodSig m2, JTypeMirror origin) {

        if (m1.isConstructor() || m2.isConstructor()) {
            return m1.equals(m2); // "by convention a method overrides itself"
        }

        JTypeMirror m1Owner = m1.getDeclaringType();
        JClassType m2Owner = (JClassType) m2.getDeclaringType();

        if (isOverridableIn(m2, m1Owner.getSymbol())) {
            JClassType m2AsM1Supertype = (JClassType) m1Owner.getAsSuper(m2Owner.getSymbol());
            if (m2AsM1Supertype != null) {
                JMethodSig m2Prime = m2AsM1Supertype.getDeclaredMethod(m2.getSymbol());
                assert m2Prime != null;
                if (isSubSignature(m1, m2Prime)) {
                    return true;
                }
            }
        }

        // todo that is very weird
        if (m1.isAbstract()
            || !m2.isAbstract() && !m2.getSymbol().isDefaultMethod()
            || !isOverridableIn(m2, origin.getSymbol())
            || !(m1Owner instanceof JClassType)) {
            return false;
        }

        JTypeMirror m1AsSuper = origin.getAsSuper(((JClassType) m1Owner).getSymbol());
        JTypeMirror m2AsSuper = origin.getAsSuper(m2Owner.getSymbol());
        if (m1AsSuper instanceof JClassType && m2AsSuper instanceof JClassType) {
            m1 = ((JClassType) m1AsSuper).getDeclaredMethod(m1.getSymbol());
            m2 = ((JClassType) m2AsSuper).getDeclaredMethod(m2.getSymbol());
            assert m1 != null && m2 != null;
            return isSubSignature(m1, m2);
        }
        return false;
    }

    private static boolean isOverridableIn(JMethodSig m, JTypeDeclSymbol origin) {
        return isOverridableIn(m.getSymbol(), origin);
    }

    /**
     * Returns true if the given method can be overridden in the origin
     * class. This only checks access modifiers and not eg whether the
     * method is final or static. Regardless of whether the method is
     * final it is overridden - whether this is a compile error or not
     * is another matter.
     *
     * <p>Like {@link #overrides(JMethodSig, JMethodSig, JTypeMirror)},
     * this does not check the static modifier, and tests for hiding
     * if the method is static.
     *
     * @param m      Method to test
     * @param origin Site of the potential override
     */
    public static boolean isOverridableIn(JExecutableSymbol m, JTypeDeclSymbol origin) {
        if (m instanceof JConstructorSymbol) {
            return false;
        }

        final int accessFlags = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;

        // JLS 8.4.6.1
        switch (m.getModifiers() & accessFlags) {
        case Modifier.PUBLIC:
            return true;
        case Modifier.PROTECTED:
            return !origin.isInterface();
        case 0:
            // package private
            return
                m.getPackageName().equals(origin.getPackageName())
                    && !origin.isInterface();
        default:
            // private
            return false;
        }
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="SAM types">

    /*
     * Function types of SAM (single-abstract-method) types.
     *
     * See https://docs.oracle.com/javase/specs/jls/se11/html/jls-9.html#jls-9.9
     */


    /**
     * Returns the non-wildcard parameterization of the given functional
     * interface type. Returns null if such a parameterization does not
     * exist.
     *
     * <p>This is used to remove wildcards from the type of a functional
     * interface.
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-9.html#jls-9.9
     *
     * @param type A parameterized functional interface type
     */
    public static @Nullable JClassType nonWildcardParameterization(@NonNull JClassType type) {
        TypeSystem ts = type.getTypeSystem();

        List<JTypeMirror> targs = type.getTypeArgs();
        if (targs.stream().noneMatch(it -> it instanceof JWildcardType)) {
            return type;
        }

        List<JTypeVar> tparams = type.getFormalTypeParams();
        List<JTypeMirror> newArgs = new ArrayList<>();

        for (int i = 0; i < tparams.size(); i++) {
            JTypeMirror ai = targs.get(i);
            if (ai instanceof JWildcardType) {
                JTypeVar pi = tparams.get(i);
                JTypeMirror bi = pi.getUpperBound();
                if (mentionsAny(bi, new HashSet<>(tparams))) {
                    return null;
                }

                JWildcardType ai2 = (JWildcardType) ai;

                if (ai2.isUnbounded()) {
                    newArgs.add(bi);
                } else if (ai2.isUpperBound()) {
                    newArgs.add(ts.glb(Arrays.asList(ai2.asUpperBound(), bi)));
                } else { // lower bound
                    newArgs.add(ai2.asLowerBound());
                }

            } else {
                newArgs.add(ai);
            }

        }

        return type.withTypeArguments(newArgs);
    }

    /**
     * Finds the method of the given type that can be overridden as a lambda
     * expression. That is more complicated than "the unique abstract method",
     * it's actually a function type which can override all abstract methods
     * of the SAM at once.
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-9.html#jls-9.9
     *
     * <p>If the parameter is not mappable to a class type with {@link #asClassType(JTypeMirror)},
     * or if the functional method does not exist, returns null.
     */
    public static @Nullable JMethodSig findFunctionalInterfaceMethod(@Nullable JTypeMirror type) {
        JClassType candidateSam = asClassType(type);
        if (candidateSam == null) {
            return null;
        }

        if (candidateSam.isParameterizedType()) {
            return findFunctionTypeImpl(nonWildcardParameterization(candidateSam));
        } else if (candidateSam.isRaw()) {
            //  The function type of the raw type of a generic functional
            //  interface I<...> is the erasure of the function type of the generic functional interface I<...>.
            JMethodSig fun = findFunctionTypeImpl(candidateSam.getGenericTypeDeclaration());
            return fun == null ? null : fun.getErasure();
        } else {
            return findFunctionTypeImpl(candidateSam);
        }
    }

    /**
     * Returns t if it is a class or interface type. If it is an intersection type,
     * returns the induced class or interface type. Returns null otherwise, including
     * if the parameter is null.
     */
    public static @Nullable JClassType asClassType(@Nullable JTypeMirror t) {
        if (t instanceof JClassType) {
            return (JClassType) t;
        } else if (t instanceof JIntersectionType) {
            return ((JIntersectionType) t).getInducedClassType();
        }
        return null;
    }

    private static @Nullable JMethodSig findFunctionTypeImpl(@Nullable JClassType candidateSam) {

        if (candidateSam == null || !candidateSam.isInterface() || candidateSam.getSymbol().isAnnotation()) {
            return null;
        }

        Map<String, List<JMethodSig>> relevantMethods = candidateSam.streamMethods(it -> !Modifier.isStatic(it.getModifiers()))
                                                                    .filter(TypeOps::isNotDeclaredInClassObject)
                                                                    .collect(Collectors.groupingBy(JMethodSig::getName, OverloadSet.collectMostSpecific(candidateSam)));


        List<JMethodSig> candidates = new ArrayList<>();
        for (Entry<String, List<JMethodSig>> entry : relevantMethods.entrySet()) {
            for (JMethodSig sig : entry.getValue()) {
                if (sig.isAbstract()) {
                    candidates.add(sig);
                }
            }
        }

        if (candidates.isEmpty()) {
            return null;
        } else if (candidates.size() == 1) {
            return candidates.get(0);
        }

        JMethodSig currentBest = null;

        nextCandidate:
        for (int i = 0; i < candidates.size(); i++) {
            JMethodSig cand = candidates.get(i);

            for (JMethodSig other : candidates) {
                if (!isSubSignature(cand, other)
                    || !isReturnTypeSubstitutable(cand, other)) {
                    continue nextCandidate;
                }
            }

            if (currentBest == null) {
                currentBest = cand;
            } else if (cand.getReturnType().isSubtypeOf(currentBest.getReturnType())) {
                // select the most specific return type
                currentBest = cand;
            }
        }

        return currentBest;
    }

    private static boolean isNotDeclaredInClassObject(JMethodSig it) {
        TypeSystem ts = it.getDeclaringType().getTypeSystem();
        return ts.OBJECT.streamDeclaredMethods(om -> Modifier.isPublic(om.getModifiers())
            && om.nameEquals(it.getName()))
                        .noneMatch(om -> haveSameSignature(it, om));
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="As super">

    /**
     * @see JTypeMirror#getAsSuper(JClassSymbol)
     */
    public static @Nullable JTypeMirror asSuper(@NonNull JTypeMirror t, @NonNull JClassSymbol s) {

        if (!t.isPrimitive() && s.equals(t.getTypeSystem().OBJECT.getSymbol())) {
            // interface types need to have OBJECT somewhere up their hierarchy
            return t.getTypeSystem().OBJECT;
        }

        return t.acceptVisitor(AsSuperVisitor.INSTANCE, s);
    }

    /**
     * Return the base type of t or any of its outer types that starts
     * with the given type.  If none exists, return null.
     */
    public static JClassType asOuterSuper(JTypeMirror t, JClassSymbol sym) {
        if (t instanceof JClassType) {
            JClassType ct = (JClassType) t;
            do {
                JClassType sup = ct.getAsSuper(sym);
                if (sup != null) {
                    return sup;
                }
                ct = ct.getEnclosingType();
            } while (ct != null);
        } else if (t instanceof JTypeVar || t instanceof JArrayType) {
            return (JClassType) t.getAsSuper(sym);
        }
        return null;
    }

    private static final class AsSuperVisitor implements JTypeVisitor<@Nullable JTypeMirror, JClassSymbol> {

        static final AsSuperVisitor INSTANCE = new AsSuperVisitor();

        /** Parameter is the erasure of the target. */

        @Override
        public JTypeMirror visit(JTypeMirror t, JClassSymbol target) {
            return null;
        }

        @Override
        public JTypeMirror visitClass(JClassType t, JClassSymbol target) {
            if (target.equals(t.getSymbol())) {
                return t;
            }

            // prefer digging up the superclass first
            JClassType sup = t.getSuperClass();
            JClassType res = sup == null ? null : (JClassType) sup.acceptVisitor(this, target);
            if (res != null) {
                return res;
            } else {
                // then look in interfaces if possible
                if (target.isInterface() || target.isUnresolved()) {
                    return firstResult(target, t.getSuperInterfaces());
                }
            }

            return null;
        }

        @Override
        public JTypeMirror visitIntersection(JIntersectionType t, JClassSymbol target) {
            return firstResult(target, t.getComponents());
        }

        public @Nullable JTypeMirror firstResult(JClassSymbol target, Iterable<? extends JTypeMirror> components) {
            for (JTypeMirror ci : components) {
                @Nullable JTypeMirror sup = ci.acceptVisitor(this, target);
                if (sup != null) {
                    return sup;
                }
            }
            return null;
        }

        @Override
        public JTypeMirror visitTypeVar(JTypeVar t, JClassSymbol target) {
            // caution, infinite recursion
            return t.getUpperBound().acceptVisitor(this, target);
        }

        @Override
        public JTypeMirror visitArray(JArrayType t, JClassSymbol target) {
            // Cloneable, Serializable, Object
            JTypeMirror decl = t.getTypeSystem().declaration(target);
            return t.isSubtypeOf(decl) ? decl : null;
        }
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="LUB/GLB">

    /**
     * Returns a subset S of the parameter, whose components have no
     * strict supertype in S.
     *
     * <pre>{@code
     * S = { V | V in set, and for all W ≠ V in set, it is not the case that W <: V }
     * }</pre>
     */
    public static Set<JTypeMirror> mostSpecific(Collection<? extends JTypeMirror> set) {
        Set<JTypeMirror> result = new LinkedHashSet<>(set.size());

        // Notice that this loop needs a well-behaved subtyping relation,
        // i.e. antisymmetric: A <: B && A != B implies not(B <: A)
        // This is not the case if we include unchecked conversion in there,
        // or special provisions for unresolved types.
        vLoop:
        for (JTypeMirror v : set) {
            for (JTypeMirror w : set) {
                if (!w.equals(v) && !hasUnresolvedSymbol(w) && isSubtypePure(w, v).bySubtyping()) {
                    continue vLoop;
                }
            }
            result.add(v);
        }
        return result;
    }

    // </editor-fold>

    /**
     * Returns the components of t if it is an intersection type,
     * otherwise returns t.
     */
    public static List<JTypeMirror> asList(JTypeMirror t) {
        if (t instanceof JIntersectionType) {
            return ((JIntersectionType) t).getComponents();
        } else {
            return Collections.singletonList(t);
        }
    }

    /** Returns a list with the erasures of the given types, may be unmodifiable. */
    public static List<JTypeMirror> erase(Collection<? extends JTypeMirror> ts) {
        return CollectionUtil.map(ts, JTypeMirror::getErasure);
    }

    // <editor-fold  defaultstate="collapsed" desc="Mentions">


    public static boolean mentions(@NonNull JTypeVisitable type, @NonNull InferenceVar parent) {
        return type.acceptVisitor(MentionsVisitor.INSTANCE, Collections.singleton(parent));
    }

    public static boolean mentionsAny(JTypeVisitable t, Collection<? extends SubstVar> vars) {
        return !vars.isEmpty() && t.acceptVisitor(MentionsVisitor.INSTANCE, vars);
    }


    private static final class MentionsVisitor implements JTypeVisitor<Boolean, Collection<? extends JTypeMirror>> {

        static final MentionsVisitor INSTANCE = new MentionsVisitor();

        @Override
        public Boolean visit(JTypeMirror t, Collection<? extends JTypeMirror> targets) {
            return false;
        }

        @Override
        public Boolean visitTypeVar(JTypeVar t, Collection<? extends JTypeMirror> targets) {
            return targets.contains(t);
        }

        @Override
        public Boolean visitInferenceVar(InferenceVar t, Collection<? extends JTypeMirror> targets) {
            return targets.contains(t);
        }

        @Override
        public Boolean visitWildcard(JWildcardType t, Collection<? extends JTypeMirror> targets) {
            return t.getBound().acceptVisitor(this, targets);
        }

        @Override
        public Boolean visitMethodType(JMethodSig t, Collection<? extends JTypeMirror> targets) {
            if (t.getReturnType().acceptVisitor(this, targets)) {
                return true;
            }
            for (JTypeMirror fi : t.getFormalParameters()) {
                if (fi.acceptVisitor(this, targets)) {
                    return true;
                }
            }
            for (JTypeMirror ti : t.getThrownExceptions()) {
                if (ti.acceptVisitor(this, targets)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Boolean visitClass(JClassType t, Collection<? extends JTypeMirror> targets) {
            JClassType encl = t.getEnclosingType();
            if (encl != null && encl.acceptVisitor(this, targets)) {
                return true;
            }

            for (JTypeMirror typeArg : t.getTypeArgs()) {
                if (typeArg.acceptVisitor(this, targets)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public Boolean visitIntersection(JIntersectionType t, Collection<? extends JTypeMirror> targets) {
            for (JTypeMirror comp : t.getComponents()) {
                if (comp.acceptVisitor(this, targets)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Boolean visitArray(JArrayType t, Collection<? extends JTypeMirror> targets) {
            return t.getComponentType().acceptVisitor(this, targets);
        }
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Accessibility utils">


    public static Predicate<JMethodSymbol> accessibleMethodFilter(String name, @NonNull JClassSymbol symbol) {
        return it -> it.nameEquals(name) && isAccessible(it, symbol);
    }

    public static Iterable<JMethodSig> lazyFilterAccessible(List<JMethodSig> visible, @NonNull JClassSymbol accessSite) {
        return () -> IteratorUtil.filter(visible.iterator(), it -> isAccessible(it.getSymbol(), accessSite));
    }

    public static List<JMethodSig> filterAccessible(List<JMethodSig> visible, @NonNull JClassSymbol accessSite) {
        return CollectionUtil.mapNotNull(visible, m -> isAccessible(m.getSymbol(), accessSite) ? m : null);
    }


    public static List<JMethodSig> getMethodsOf(JTypeMirror type, String name, boolean staticOnly, @NonNull JClassSymbol enclosing) {
        if (staticOnly && type.isInterface()) {
            // static methods, start on interface
            // static interface methods are not inherited
            return type.streamDeclaredMethods(staticMethodFilter(name, true, enclosing)).collect(Collectors.toList());
        } else if (staticOnly) {
            // static methods, doesn't start on interface
            // -> ignore non-static, ignore any that are interfaces
            return type.streamMethods(staticMethodFilter(name, false, enclosing)).collect(OverloadSet.collectMostSpecific(type));
        } else {
            return type.streamMethods(methodFilter(name, enclosing))
                       .collect(OverloadSet.collectMostSpecific(type));
        }
    }

    private static @NonNull Predicate<JMethodSymbol> methodFilter(String name, @NonNull JClassSymbol enclosing) {
        return it -> isAccessibleWithName(name, enclosing, it);
    }

    private static @NonNull Predicate<JMethodSymbol> staticMethodFilter(String name, boolean acceptItfs, @NonNull JClassSymbol enclosing) {
        return it -> Modifier.isStatic(it.getModifiers())
            && (acceptItfs || !it.getEnclosingClass().isInterface())
            && isAccessibleWithName(name, enclosing, it);
    }

    private static boolean isAccessibleWithName(String name, @NonNull JClassSymbol enclosing, JMethodSymbol m) {
        return m.nameEquals(name) && isAccessible(m, enclosing);
    }


    private static boolean isAccessible(JExecutableSymbol method, JClassSymbol ctx) {
        Objects.requireNonNull(ctx, "Cannot check a null symbol");

        int mods = method.getModifiers();
        if (Modifier.isPublic(mods)) {
            return true;
        }

        JClassSymbol owner = method.getEnclosingClass();

        if (Modifier.isPrivate(mods)) {
            return ctx.getNestRoot().equals(owner.getNestRoot());
        }

        return ctx.getPackageName().equals(owner.getPackageName())
            // we can exclude interfaces because their members are all public
            || Modifier.isProtected(mods) && isSubClassOfNoInterface(ctx, owner);
    }

    private static boolean isSubClassOfNoInterface(JClassSymbol sub, JClassSymbol symbol) {
        if (symbol.equals(sub)) {
            return true;
        }

        JClassSymbol superclass = sub.getSuperclass();
        return superclass != null && isSubClassOfNoInterface(superclass, symbol);
    }

    public static NameResolver<FieldSig> getMemberFieldResolver(JTypeMirror c, @NonNull String accessPackageName, @Nullable JClassSymbol access, String name) {
        if (c instanceof JClassType) {
            // fast path
            return JavaResolvers.getMemberFieldResolver((JClassType) c, accessPackageName, access, name);
        }

        return c.acceptVisitor(GetFieldVisitor.INSTANCE, new FieldSearchParams(accessPackageName, access, name));
    }

    private static final class FieldSearchParams {

        private final @NonNull String accessPackageName;
        private final @Nullable JClassSymbol access;
        private final String name;

        FieldSearchParams(@NonNull String accessPackageName, @Nullable JClassSymbol access, String name) {
            this.accessPackageName = accessPackageName;
            this.access = access;
            this.name = name;
        }
    }

    private static final class GetFieldVisitor implements JTypeVisitor<NameResolver<FieldSig>, FieldSearchParams> {

        static final GetFieldVisitor INSTANCE = new GetFieldVisitor();

        @Override
        public NameResolver<FieldSig> visit(JTypeMirror t, FieldSearchParams fieldSearchParams) {
            return CoreResolvers.emptyResolver();
        }

        @Override
        public NameResolver<FieldSig> visitClass(JClassType t, FieldSearchParams fieldSearchParams) {
            return JavaResolvers.getMemberFieldResolver(t, fieldSearchParams.accessPackageName, fieldSearchParams.access, fieldSearchParams.name);
        }

        @Override
        public NameResolver<FieldSig> visitTypeVar(JTypeVar t, FieldSearchParams fieldSearchParams) {
            return t.getUpperBound().acceptVisitor(this, fieldSearchParams);
        }

        @Override
        public NameResolver<FieldSig> visitIntersection(JIntersectionType t, FieldSearchParams fieldSearchParams) {
            return NameResolver.composite(
                CollectionUtil.map(t.getComponents(), c -> c.acceptVisitor(this, fieldSearchParams))
            );
        }

        @Override
        public NameResolver<FieldSig> visitArray(JArrayType t, FieldSearchParams fieldSearchParams) {
            if ("length".equals(fieldSearchParams.name)) {
                return CoreResolvers.singleton("length", t.getTypeSystem().sigOf(t, t.getSymbol().getDeclaredField("length")));
            }
            return CoreResolvers.emptyResolver();
        }
    }

    // </editor-fold>

    // <editor-fold  defaultstate="collapsed" desc="Miscellaneous">

    /**
     * Returns true if both types have a common supertype that is not Object.
     * Primitive types are only related to themselves.
     *
     * @param t Non-null type
     * @param s Non-null type
     *
     * @throws NullPointerException if a parameter is null
     */
    public static boolean areRelated(@NonNull JTypeMirror t, JTypeMirror s) {
        if (t.isPrimitive() || s.isPrimitive()) {
            return s.equals(t);
        }
        if (t.equals(s)) {
            return true;
        }
        // maybe they have a common supertype
        Set<JTypeMirror> tSupertypes = new HashSet<>(t.getSuperTypeSet());
        tSupertypes.retainAll(s.getSuperTypeSet());
        return !tSupertypes.equals(Collections.singleton(t.getTypeSystem().OBJECT));
    }

    /**
     * Returns true if the type is {@link TypeSystem#UNKNOWN},
     * {@link TypeSystem#ERROR}, or its symbol is unresolved.
     *
     * @param t Non-null type
     *
     * @throws NullPointerException if the parameter is null
     */
    public static boolean isUnresolved(@NonNull JTypeMirror t) {
        return isSpecialUnresolved(t) || hasUnresolvedSymbol(t);
    }

    public static boolean isSpecialUnresolved(@NonNull JTypeMirror t) {
        TypeSystem ts = t.getTypeSystem();
        return t == ts.UNKNOWN || t == ts.ERROR;
    }

    /**
     * Return true if the argument is a {@link JClassType} with
     * {@linkplain JClassSymbol#isUnresolved() an unresolved symbol} or
     * a {@link JArrayType} whose element type matches the first criterion.
     */
    public static boolean hasUnresolvedSymbol(@Nullable JTypeMirror t) {
        if (!(t instanceof JClassType)) {
            return t instanceof JArrayType && hasUnresolvedSymbol(((JArrayType) t).getElementType());
        }
        return t.getSymbol() != null && t.getSymbol().isUnresolved();
    }

    public static boolean isUnresolvedOrNull(@Nullable JTypeMirror t) {
        return t == null || isUnresolved(t);
    }


    public static @Nullable JTypeMirror getArrayComponent(@Nullable JTypeMirror t) {
        return t instanceof JArrayType ? ((JArrayType) t).getComponentType() : null;
    }

    // </editor-fold>
}
