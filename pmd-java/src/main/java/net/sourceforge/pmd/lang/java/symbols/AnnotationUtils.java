/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymArray;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymEnum;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymValue;

/**
 *
 */
public final class AnnotationUtils {

    private AnnotationUtils() {
        // utility class
    }

    /**
     * Returns an annotation element for the given java value. Returns
     * null if the value cannot be an annotation element.
     *
     * <p>Note: annotations are currently unsupported.
     */
    public static @Nullable SymbolicValue symValueFor(Object value) {
        if (value == null) {
            return null;
        }

        if (value.getClass() == String.class
            || ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
            return new SymValue(value);
        }

        if (value instanceof Enum<?>) {
            return ofEnum((Enum<?>) value);
        }

        if (value instanceof Annotation) {
            return null;// unsupported for now
        }

        if (value.getClass().isArray()) {
            Class<?> comp = value.getClass().getComponentType();
            if (comp.isPrimitive() || comp == String.class) {
                return new SymValue(value);
            } else if (comp.isArray()) {
                return null; // arrays of arrays are not possible in annotations
            }

            Object[] arr = (Object[]) value;
            List<SymbolicValue> lst = new ArrayList<>(arr.length);
            for (Object o : arr) {
                // this must be an annotation, or an enum constant
                if (o == null) {
                    return null;
                }
                SymbolicValue elt = symValueFor(o);
                assert !(elt instanceof SymValue || elt instanceof SymArray);
                if (elt == null) {
                    return null;
                }
                lst.add(elt);
            }
            return new SymArray(lst);
        }

        return null;
    }

    // test only
    static SymbolicValue ofArray(SymbolicValue... values) {
        return new SymArray(Arrays.asList(values.clone()));
    }

    // test only
    static <T extends Enum<T>> SymbolicValue ofEnum(Enum<T> value) {
        return new SymEnum(value.getDeclaringClass().getName(), value.name(), true);
    }

}
