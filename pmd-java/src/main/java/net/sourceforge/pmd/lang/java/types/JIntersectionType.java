/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
     * From the JLS: Every intersection type induces a notional class
     * or interface for the purpose of identifying its members.
     */
    JClassType getInducedClassType();


    List<JTypeMirror> getComponents();


    @Nullable
    default JTypeMirror getSuperClass() {
        for (JTypeMirror ci : getComponents()) {
            // there can't be more than one normally
            if (!ci.isInterface()) {
                return ci;
            }
        }
        return getTypeSystem().OBJECT;
    }


    @Nullable
    default Set<JTypeMirror> getInterfaces() {
        Set<JTypeMirror> interfaces = new LinkedHashSet<>(getComponents());
        interfaces.remove(getSuperClass());
        return interfaces;
    }


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
