/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;

import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * A symbol that can have annotations.
 */
public interface AnnotableSymbol extends JElementSymbol {

    /**
     * Return the valid symbolic annotations defined on this symbol.
     * Annotations that could not be converted, eg because
     * they are written with invalid code, are discarded, so
     * this might not match the annotations on a node one to one.
     */
    default PSet<SymAnnot> getDeclaredAnnotations() {
        return HashTreePSet.empty();
    }


    /**
     * Return an annotation of the given type, if it is present on this declaration.
     * This does not consider inherited annotations.
     */
    default SymbolicValue.SymAnnot getDeclaredAnnotation(Class<? extends Annotation> type) {
        for (SymAnnot a : getDeclaredAnnotations()) {
            if (a.isOfType(type)) {
                return a;
            }
        }
        return null;
    }


    /**
     * Return true if an annotation of the given type is present on this declaration.
     */
    default boolean isAnnotationPresent(Class<? extends Annotation> type) {
        return getDeclaredAnnotation(type) != null;
    }
}
