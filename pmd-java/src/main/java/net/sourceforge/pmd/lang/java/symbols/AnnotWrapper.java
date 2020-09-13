/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 *
 */
class AnnotWrapperImpl implements SymAnnot {

    private final Annotation annotation;
    private final Class<? extends Annotation> annotationClass;
    private final JClassSymbol annotationClassSymbol;


    private AnnotWrapperImpl(JClassSymbol annotationClassSymbol, @NonNull Annotation annotation) {
        this.annotationClassSymbol = annotationClassSymbol;
        this.annotation = annotation;
        this.annotationClass = annotation.annotationType();
    }

    static SymAnnot wrap(TypeSystem ts, @NonNull Annotation annotation) {
        JClassSymbol sym = ts.getClassSymbol(annotation.annotationType());
        if (sym == null) {
            return null;
        }
        return new AnnotWrapperImpl(sym, annotation);
    }

    @Override
    public RetentionPolicy getRetention() {
        Retention annot = annotationClass.getAnnotation(Retention.class);
        return annot != null ? annot.value()
                             : RetentionPolicy.CLASS;
    }

    @Override
    public boolean isOfType(String binaryName) {
        return annotationClassSymbol.getBinaryName().equals(binaryName);
    }

    @Override
    public Set<String> getAttributeNames() {
        return annotationClassSymbol.getAnnotationAttributeNames();
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
                     }).findAny().orElse(null);
    }

    @Override
    public boolean valueEquals(Object o) {
        return annotation.equals(o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SymAnnot)) {
            return false;
        }
        SymAnnot that = (SymAnnot) o;
        if (!that.isOfType(annotationClassSymbol.getBinaryName())) {
            return false;
        }

        for (String attr : getAttributeNames()) {
            if (!Objects.equals(getAttribute(attr), ((SymAnnot) o).getAttribute(attr))) {
                return false;
            }
        }
        return true;
    }

}
