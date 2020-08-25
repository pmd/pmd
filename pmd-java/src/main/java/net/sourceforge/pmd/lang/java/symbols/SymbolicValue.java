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

/**
 * Structure to represent symbolic constant values for annotations.
 * Annotations may contain:
 * <ul>
 * <li>Primitive or string values
 * <li>Other annotations (currently unsupported)
 * <li>Enum constants
 * <li>Arrays of symbolic values (currently, only arrays of strings or enum constants are supported)
 * </ul>
 */
public class SymbolicValue {

    SymbolicValue() {
        // package-private
    }

    public static SymbolicValue arrayOf(SymbolicValue... values) {
        return new Array(Arrays.asList(values.clone()));
    }

    // returns null for unsupported value
    public static @Nullable SymbolicValue of(Object value) {
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            Class<?> comp = value.getClass().getComponentType();
            if (comp.isPrimitive()) {
                return null;
            }
            Object[] arr = (Object[]) value;
            List<SymbolicValue> lst = new ArrayList<>(arr.length);
            for (Object o : arr) {
                SymbolicValue elt = of(o);
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

    public static final class Array extends SymbolicValue {

        private final List<SymbolicValue> elements;

        public Array(List<SymbolicValue> elements) {
            this.elements = Collections.unmodifiableList(elements);
        }

        public int length() {
            return elements.size();
        }

        public List<SymbolicValue> elements() {
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


    public static final class EnumConstant extends SymbolicValue {

        private final String enumTypeInternalName;
        private final String enumName;

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
    public static final class Atom extends SymbolicValue {

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
