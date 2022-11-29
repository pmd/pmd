/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * An intersection type. Intersections type act as the
 * {@linkplain TypeSystem#glb(Collection) greatest lower bound}
 * for a set of types.
 *
 * <p>https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.9
 */
@SuppressWarnings("PMD.CompareObjectsWithEquals")
public final class JIntersectionType implements JTypeMirror {


    private final TypeSystem ts;
    private final JTypeMirror primaryBound;
    private final List<JTypeMirror> components;
    private JClassType induced;

    /**
     * @param primaryBound may be Object if every bound is an interface
     * @param allBounds    including the superclass, unless Object
     */
    JIntersectionType(TypeSystem ts,
                      JTypeMirror primaryBound,
                      List<? extends JTypeMirror> allBounds) {
        this.primaryBound = primaryBound;
        this.components = Collections.unmodifiableList(allBounds);
        this.ts = ts;

        assert Lub.isExclusiveIntersectionBound(primaryBound)
            : "Wrong primary intersection bound: " + toString(primaryBound, allBounds);
        assert primaryBound != ts.OBJECT || allBounds.size() > 1
            : "Intersection of a single bound: " + toString(primaryBound, allBounds); // should be caught by GLB

        checkWellFormed(primaryBound, allBounds);

    }

    @Override
    public PSet<SymAnnot> getTypeAnnotations() {
        return HashTreePSet.empty();
    }

    @Override
    public JTypeMirror withAnnotations(PSet<SymAnnot> newTypeAnnots) {
        return new JIntersectionType(
            ts,
            primaryBound.withAnnotations(newTypeAnnots),
            CollectionUtil.map(components, c -> c.withAnnotations(newTypeAnnots))
        );
    }

    /**
     * Returns the list of components. Their erasure must be pairwise disjoint.
     * If the intersection's superclass is {@link TypeSystem#OBJECT},
     * then it is excluded from this set.
     */
    public List<JTypeMirror> getComponents() {
        return components;
    }


    /**
     * The primary bound of this intersection, which may be a type variable,
     * array type, or class type (not an interface). If all bounds are interfaces,
     * then this returns {@link TypeSystem#OBJECT}.
     */
    public @NonNull JTypeMirror getPrimaryBound() {
        return primaryBound;
    }


    /**
     * Returns all additional bounds on the primary bound, which are
     * necessarily interface types.
     */
    @SuppressWarnings({"unchecked", "rawtypes"}) // safe because of checkWellFormed
    public @NonNull List<JClassType> getInterfaces() {
        return (List) (primaryBound == ts.OBJECT ? components
                                                 : components.subList(1, components.size()));
    }

    /**
     * Every intersection type induces a notional class or interface
     * for the purpose of identifying its members. This may be a functional
     * interface. This returns null for the non-implemented cases.
     *
     * @experimental this is only relevant to check for functional
     *     interface parameterization, eg {@code Runnable & Serializable}. Do
     *     not use this to find out the members of this type, rather, use {@link #streamMethods(Predicate)}
     *     or so.
     */
    @Experimental
    public @Nullable JClassType getInducedClassType() {
        JTypeMirror primary = getPrimaryBound();
        if (primary instanceof JTypeVar || primary instanceof JArrayType) {
            // Normally, should generate an interface which has all the members of Ti
            // But as per the experimental notice, this case may be ignored until needed
            return null;
        }

        if (induced == null) {
            JClassSymbol sym = new FakeIntersectionSymbol("", (JClassType) primary, getInterfaces());
            this.induced = (JClassType) ts.declaration(sym);
        }
        return induced;
    }

    @Override
    public <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitIntersection(this, p);
    }


    @Override
    public Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return getComponents().stream().flatMap(it -> it.streamMethods(prefilter));
    }


    @Override
    public JIntersectionType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        JTypeMirror newPrimary = primaryBound.subst(subst);
        List<JClassType> myItfs = getInterfaces();
        List<JClassType> newBounds = TypeOps.substClasses(myItfs, subst);
        return newPrimary == getPrimaryBound() && newBounds == myItfs // NOPMD UseEqualsToCompareObjectReferences
               ? this
               : new JIntersectionType(ts, newPrimary, newBounds);
    }

    @Override
    public @Nullable JTypeDeclSymbol getSymbol() {
        return null; // the induced type may have a symbol though
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }


    @Override
    public JTypeMirror getErasure() {
        return getPrimaryBound().getErasure();
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JIntersectionType)) {
            return false;
        }
        JIntersectionType that = (JIntersectionType) o;
        return TypeOps.isSameType(this, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(components);
    }

    private static void checkWellFormed(JTypeMirror primary, List<? extends JTypeMirror> flattened) {
        assert flattened.get(0) == primary || primary == primary.getTypeSystem().OBJECT
            : "Not a well-formed intersection " + flattened;
        for (int i = 0; i < flattened.size(); i++) {
            JTypeMirror ci = flattened.get(i);
            Objects.requireNonNull(ci, "Null intersection component");
            if (Lub.isExclusiveIntersectionBound(ci)) {
                if (i != 0) {
                    throw malformedIntersection(primary, flattened);
                }
            } else if (ci instanceof JClassType) {
                // must be an interface, as per isExclusiveBlabla
                assert ci.isInterface() || TypeOps.hasUnresolvedSymbol(ci);
            } else {
                throw malformedIntersection(primary, flattened);
            }
        }
    }

    private static RuntimeException malformedIntersection(JTypeMirror primary, List<? extends JTypeMirror> flattened) {
        return new IllegalArgumentException(
            "Malformed intersection: " + toString(primary, flattened)
        );
    }

    private static String toString(JTypeMirror primary, List<? extends JTypeMirror> flattened) {
        return flattened.stream().map(JTypeMirror::toString).collect(Collectors.joining(" & ",
                                                                                        primary.toString() + " & ",
                                                                                        ""));
    }
}
