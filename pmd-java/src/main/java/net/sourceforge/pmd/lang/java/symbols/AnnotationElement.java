/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.ClassNamesUtil;

/**
 * Structure to represent constant values of annotations symbolically.
 * Annotations may contain:
 * <ul>
 * <li>Primitive or string values: {@link Atom}
 * <li>Arrays of primitives or strings: {@link Atom}
 * <li>Enum constants: {@link EnumConstant}
 * <li>Other annotations (currently unsupported)
 * <li>Arrays of annotations, or enum constants, of dimension 1: {@link Array}
 * </ul>
 *
 * <p>Currently the public API allows comparing the values to an actual
 * java value that you compiled against ({@link #valueEquals(Object)}).
 * This may be improved later to allow comparing values without needing
 * them in the compile classpath.
 */
public abstract class AnnotationElement {

    AnnotationElement() {
        // package-private
    }


    /**
     * Returns an annotation element for the given java value. Returns
     * null if the value cannot be an annotation element.
     */
    public static @Nullable AnnotationElement of(Object value) {
        if (value == null) {
            return null;
        }

        if (value.getClass() == String.class
            || ClassUtils.isPrimitiveOrWrapper(value.getClass())) {
            return new Atom(value);
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
                return new Atom(value);
            } else if (comp.isArray()) {
                return null; // arrays of arrays are not possible in annotations
            }

            Object[] arr = (Object[]) value;
            List<AnnotationElement> lst = new ArrayList<>(arr.length);
            for (Object o : arr) {
                // this must be an annotation, or an enum constant
                if (o == null) {
                    return null;
                }
                AnnotationElement elt = of(o);
                assert !(elt instanceof Atom || elt instanceof Array);
                if (elt == null) {
                    return null;
                }
                lst.add(elt);
            }
            return new Array(lst);
        }

        return null;
    }


    // test only
    static AnnotationElement ofArray(AnnotationElement... values) {
        return new Array(Arrays.asList(values.clone()));
    }

    // test only
    static <T extends Enum<T>> AnnotationElement ofEnum(Enum<T> value) {
        return new EnumConstant(ClassNamesUtil.getTypeDescriptor(value.getDeclaringClass()),
                                value.name());
    }

    /**
     * Returns true if this symbolic value represents the same value as
     * the given object. If the parameter is null, returns false.
     */
    public abstract boolean valueEquals(Object o);

    /**
     * Returns an array of enum constants, or of annotations.
     * Note that arrays of strings, and arrays of primitives,
     * are represented by an {@link Atom}.
     */
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

        public boolean valueEquals(Object o) {
            if (!o.getClass().isArray()) {
                return false;
            }

            if (o instanceof Object[]) {// not a primitive array
                Object[] arr = (Object[]) o;
                if (arr.length != elements.size()) {
                    return false;
                }
                for (int i = 0; i < elements.size(); i++) {
                    if (!elements.get(i).valueEquals(arr[i])) {
                        return false;
                    }
                }
                return true;
            }

            return false; // not implemented
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


    /**
     * Symbolically represents an enum constant.
     */
    public static final class EnumConstant extends AnnotationElement {

        private final String enumTypeInternalName;
        private final String enumName;

        /**
         * @param enumTypeDescriptor The type descriptor, eg {@code Lcom/MyEnum;}
         * @param enumConstName      Simple name of the enum constant
         */
        public EnumConstant(String enumTypeDescriptor, String enumConstName) {
            this.enumTypeInternalName = enumTypeDescriptor;
            this.enumName = enumConstName;
        }


        public boolean valueEquals(Object o) {
            if (!(o instanceof Enum)) {
                return false;
            }
            Enum<?> value = (Enum<?>) o;
            if (!this.enumName.equals(value.name())) {
                return false;
            }
            Class<?> k = value.getDeclaringClass();
            if (!this.enumTypeInternalName.endsWith(k.getSimpleName())) {
                // optimisation, fails early without having to compute the internal name
                return false;
            }

            return ClassNamesUtil.getTypeDescriptor(k).equals(enumTypeInternalName);
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
     * Represents a primitive or string value, or an array of those.
     * Arrays of enum constants, and of annotations, are represented
     * by {@link Array}.
     */
    public static final class Atom extends AnnotationElement {

        private final Object value;

        Atom(Object value) {
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public boolean isArray() {
            return value.getClass().isArray();
        }

        public boolean valueEquals(Object o) {
            return Objects.deepEquals(value, o);
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
            return Objects.deepEquals(value, atom.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
}
