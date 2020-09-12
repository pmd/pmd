/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.util.Arrays;

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
            if (!SymArray.isOkComponentType(value.getClass().getComponentType())) {
                return null;
            }
            return SymArray.forArray(value);
        }

        return null;
    }

    // test only
    static SymbolicValue ofArray(SymbolicValue... values) {
        return SymArray.forElements(Arrays.asList(values.clone()));
    }

    // test only
    static <T extends Enum<T>> SymbolicValue ofEnum(Enum<T> value) {
        return SymEnum.fromBinaryName(value.getDeclaringClass().getName(), value.name());
    }

}
