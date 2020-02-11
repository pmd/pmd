/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

/**
 * An array type (1 dimension). Multi-level arrays have an array type
 * as component themselves.
 */
public interface JArrayType extends JTypeMirror {

    @Override
    @NonNull JClassSymbol getSymbol();


    @Override
    JArrayType getErasure();


    /** Returns the component type of the array, as described on {@link JavaTypeDefinition#getComponentType()}. */
    JTypeMirror getComponentType();


    /** Returns the element type of the array, as described on {@link JavaTypeDefinition#getElementType()}. */
    default JTypeMirror getElementType() {
        JTypeMirror c = this;
        while (c instanceof JArrayType) {
            c = ((JArrayType) c).getComponentType();
        }

        return c;
    }


    @Override
    Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter);


    @Override
    List<JMethodSig> getConstructors();


    @Override
    default <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitArray(this, p);
    }


    @Override
    default JArrayType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        JTypeMirror newComp = getComponentType().subst(subst);
        return newComp == getComponentType() ? this : (JArrayType) getTypeSystem().arrayType(newComp, 1);
    }
}
