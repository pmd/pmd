/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;


/**
 * An intersection type. An intersection type act as the
 * {@linkplain TypeSystem#glb(Collection) greatest lower bound}
 * for a set of types.
 *
 * <p>https://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.9
 */
public interface JIntersectionType extends JTypeMirror {

    /**
     * Returns the list of components. Their erasure must be pairwise disjoint.
     * If the intersection's superclass is {@link TypeSystem#OBJECT},
     * then it is excluded from this set.
     */
    List<JTypeMirror> getComponents();


    /**
     * The primary bound of this intersection, which may be a type variable,
     * array type, or class type (not an interface). If all bounds are interfaces,
     * then this returns {@link TypeSystem#OBJECT}.
     */
    @NonNull JTypeMirror getPrimaryBound();


    /**
     * Returns all additional bounds on the primary bound, which are 
     * necessarily interface types.
     */
    @NonNull List<JClassType> getInterfaces();


    /**
     * Every intersection type induces a notional class or interface 
     * for the purpose of identifying its members. This may be a functional
     * interface.
     */
    JClassType getInducedClassType();


    @Override
    default <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitIntersection(this, p);
    }

    @Override
    default Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return getComponents().stream().flatMap(it -> it.streamMethods(prefilter));
    }


    @Override
    default JIntersectionType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        List<JTypeMirror> comps = TypeOps.subst(getComponents(), subst);
        return comps == getComponents() ? this : (JIntersectionType) getTypeSystem().intersect(comps);
    }
}
