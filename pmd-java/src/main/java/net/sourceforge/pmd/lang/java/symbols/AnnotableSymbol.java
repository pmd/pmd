/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 *
 */
public interface AnnotableSymbol extends JElementSymbol {

    default List<SymAnnot> getDeclaredAnnotations() {
        return Collections.emptyList();
    }

    default SymbolicValue.SymAnnot getDeclaredAnnotation(Class<? extends Annotation> type) {
        for (SymAnnot a : getDeclaredAnnotations()) {
            if (a.isOfType(type)) {
                return a;
            }
        }
        return null;
    }

    default boolean isAnnotationPresent(Class<? extends Annotation> type) {
        for (SymAnnot a : getDeclaredAnnotations()) {
            if (a.isOfType(type)) {
                return true;
            }
        }
        return false;
    }
}
