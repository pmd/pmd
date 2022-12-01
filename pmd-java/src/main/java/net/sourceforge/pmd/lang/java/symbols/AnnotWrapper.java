/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.SymbolToStrings;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Wraps an instance of a JVM {@link Annotation} and provide the same API as {@link SymAnnot}.
 */
final class AnnotWrapper implements SymAnnot {

    private final Annotation annotation;
    private final Class<? extends Annotation> annotationClass;
    private final JClassSymbol annotationClassSymbol;


    private AnnotWrapper(JClassSymbol annotationClassSymbol, @NonNull Annotation annotation) {
        this.annotationClassSymbol = annotationClassSymbol;
        this.annotation = annotation;
        this.annotationClass = annotation.annotationType();
    }

    static SymAnnot wrap(TypeSystem ts, @NonNull Annotation annotation) {
        JClassSymbol sym = ts.getClassSymbol(annotation.annotationType());
        if (sym == null) {
            return null;
        }
        return new AnnotWrapper(sym, annotation);
    }

    @Override
    public @NonNull JClassSymbol getAnnotationSymbol() {
        return annotationClassSymbol;
    }

    @Override
    public @Nullable SymbolicValue getAttribute(String attrName) {
        return Arrays.stream(annotationClass.getDeclaredMethods())
                     .filter(it -> it.getName().equals(attrName) && it.getParameterCount() == 0)
                     .map(it -> {
                         try {
                             Object result = it.invoke(annotation);
                             return SymbolicValue.of(annotationClassSymbol.getTypeSystem(), result);
                         } catch (Exception ignored) {
                             return null;
                         }
                     })
                     .filter(Objects::nonNull)
                     .findAny().orElse(null);
    }

    @Override
    public boolean valueEquals(Object o) {
        return annotation.equals(o);
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.ANNOTATION.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.ANNOTATION.hash(this);
    }

    @Override
    public String toString() {
        return SymbolToStrings.FAKE.toString(this);
    }
}
