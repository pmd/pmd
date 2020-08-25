/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.ClassNamesUtil;

/**
 * Structure to represent symbolic constant values for annotations.
 * Annotations may contain:
 * <ul>
 * <li>Primitive or string values
 * <li>Other annotations (currently unsupported)
 * <li>Enum constants
 * <li>Arrays of annotation elements values, of dimension 1 (currently, only arrays of strings or enum constants are supported)
 * </ul>
 */
public class AnnotationElement {

    AnnotationElement() {
        // package-private
    }

    public static AnnotationElement ofArray(AnnotationElement... values) {
        return new Array(Arrays.asList(values.clone()));
    }

    public static <T extends Enum<T>> AnnotationElement ofEnum(T value) {
        return new EnumConstant(ClassNamesUtil.getTypeDescriptor(value.getDeclaringClass()),
                                value.name());
    }

    // returns null for unsupported value
    public static @Nullable AnnotationElement ofSimple(Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            Class<?> comp = value.getClass().getComponentType();
            if (comp.isPrimitive()) {
                return null;
            }
            Object[] arr = (Object[]) value;
            List<AnnotationElement> lst = new ArrayList<>(arr.length);
            for (Object o : arr) {
                AnnotationElement elt = ofSimple(o);
                if (elt == null) {
                    return null;
                }
                lst.add(elt);
            }
            return new Array(lst);
        } else if (value.getClass() == String.class
            || ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
            return new Atom(value);
        }

        return null;
    }

    public static final class Array extends AnnotationElement {

        private final List<AnnotationElement> elements;

        public Array(List<AnnotationElement> elements) {
            this.elements = Collections.unmodifiableList(elements);
        }

        public int length() {
            return elements.size();
        }

        public List<AnnotationElement> elements() {
            return elements;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Array array = (Array) o;
            return Objects.equals(elements, array.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(elements);
        }
    }


    public static final class EnumConstant extends AnnotationElement {

        private final String enumTypeInternalName;
        private final String enumName;

        /**
         * @param enumTypeDescriptor The type descriptor, eg {@code Lcom/MyEnum;}
         * @param enumConstName      Name of the enum constant
         */
        public EnumConstant(String enumTypeDescriptor, String enumConstName) {
            this.enumTypeInternalName = enumTypeDescriptor;
            this.enumName = enumConstName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            EnumConstant that = (EnumConstant) o;
            return Objects.equals(enumTypeInternalName, that.enumTypeInternalName)
                && Objects.equals(enumName, that.enumName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enumTypeInternalName, enumName);
        }
    }

    /**
     * Represents a primitive or string value. Note that this does
     * not represent arrays of those.
     */
    public static final class Atom extends AnnotationElement {

        private final Object value;

        Atom(Object value) {
            if (value.getClass() != String.class
                && !ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
                throw new IllegalArgumentException("Expected a string or primitive value " + value);
            }
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public boolean valueEquals(Object o) {
            return this.value.equals(o);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Atom atom = (Atom) o;
            return Objects.equals(value, atom.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
