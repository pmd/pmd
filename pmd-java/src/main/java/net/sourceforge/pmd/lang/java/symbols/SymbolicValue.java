/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.ClassNamesUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Structure to represent constant values of annotations symbolically.
 * Annotations may contain:
 * <ul>
 * <li>Primitive or string values: {@link SymValue}
 * <li>Arrays of primitives or strings: {@link SymValue}
 * <li>Enum constants: {@link SymEnum}
 * <li>Other annotations: {@link SymAnnot}
 * <li>Arrays of annotations, or enum constants, of dimension 1: {@link SymArray}
 * </ul>
 *
 * <p>Any other values, including the null reference, are unsupported and
 * cannot be represented by this API.
 *
 * <p>Currently the public API allows comparing the values to an actual
 * java value that you compiled against ({@link #valueEquals(Object)}).
 * This may be improved later to allow comparing values without needing
 * them in the compile classpath.
 */
public interface SymbolicValue {


    /**
     * Returns true if this symbolic value represents the same value as
     * the given object. If the parameter is null, returns false.
     *
     * <p>Note that this is partially implemented and will always return
     * false if the tested value is an annotation, or an array of annotation
     * values.
     */
    boolean valueEquals(Object o);

    /**
     * Returns true if this value is equal to the other one. The parameter
     * must be a {@link SymbolicValue} of the same type. Use {@link #valueEquals(Object)}
     * to compare to a java object.
     */
    boolean equals(Object o);

    /**
     * Symbolic representation of an annotation.
     */
    interface SymAnnot extends SymbolicValue {

        /** Returns the value of the attribute of this annotation named so. */
        default @Nullable SymbolicValue getAttribute(String name) {
            return getExplicitAttributes().get(name);
        }

        /**
         * The explicit attributes, mentioned in the annotation.
         * Attributes that take default values are not in this map.
         * The map is indexed by attribute name.
         */
        Map<String, SymbolicValue> getExplicitAttributes();

        RetentionPolicy getRetention();

        boolean isOfType(String binaryName);

        default boolean isOfType(Class<? extends Annotation> klass) {
            return isOfType(klass.getName());
        }

        /**
         * Returns YES if the annotation has the attribute explicitly
         * set to the given value. Returns NO if it is explicitly set
         * to another value. Returns UNKNOWN if the attribute was not
         * explicitly set (it could have a default value though).
         *
         * @param attrValue An object value, or a {@link SymbolicValue}
         */
        default OptionalBool attributeMatches(String name, Object attrValue) {
            SymbolicValue attr = getAttribute(name);
            if (attr == null) {
                return OptionalBool.UNKNOWN;
            }

            if (attrValue instanceof SymbolicValue) {
                return OptionalBool.definitely(attr.equals(attrValue));
            } else {
                return OptionalBool.definitely(attr.valueEquals(attrValue));
            }
        }
    }

    /**
     * An array of enum constants, or of annotations.
     * Note that arrays of strings, and arrays of primitives,
     * are represented by an {@link SymValue}.
     */
    final class SymArray implements SymbolicValue {

        private final List<SymbolicValue> elements;

        /**
         * @param elements A list of symbolic values, note that these
         *                 should not be {@link SymValue}s, instead,
         *                 arrays of such things should be a {@link SymValue}
         *                 itself.
         */
        public SymArray(List<SymbolicValue> elements) {
            this.elements = Collections.unmodifiableList(elements);
        }

        public int length() {
            return elements.size();
        }

        public List<SymbolicValue> elements() {
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
            SymArray array = (SymArray) o;
            return Objects.equals(elements, array.elements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(elements);
        }

        @Override
        public String toString() {
            return "[" + elements + ']';
        }
    }


    /**
     * Symbolic representation of an enum constant.
     */
    final class SymEnum implements SymbolicValue {

        private final String enumBinaryName;
        private final String enumName;

        private SymEnum(String enumBinaryName, String enumConstName) {
            this.enumBinaryName = enumBinaryName;
            this.enumName = enumConstName;
        }

        /**
         * @param enumBinaryName A binary name, eg {@code com.MyEnum}
         * @param enumConstName  Simple name of the enum constant
         */
        public static SymEnum fromBinaryName(String enumBinaryName, String enumConstName) {
            return new SymEnum(enumBinaryName, enumConstName);
        }

        /**
         * @param enumTypeDescriptor The type descriptor, eg {@code Lcom/MyEnum;}
         * @param enumConstName      Simple name of the enum constant
         */
        public static SymEnum fromTypeDescriptor(String enumTypeDescriptor, String enumConstName) {
            String enumBinaryName = ClassNamesUtil.classDescriptorToBinaryName(enumTypeDescriptor);
            return fromBinaryName(enumBinaryName, enumConstName);
        }


        public boolean valueEquals(Object o) {
            if (!(o instanceof Enum)) {
                return false;
            }
            Enum<?> value = (Enum<?>) o;
            if (!this.enumName.equals(value.name())) {
                return false;
            }
            return enumBinaryName.equals(value.getDeclaringClass().getName());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SymEnum that = (SymEnum) o;
            return Objects.equals(enumBinaryName, that.enumBinaryName)
                && Objects.equals(enumName, that.enumName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(enumBinaryName, enumName);
        }

        @Override
        public String toString() {
            return enumBinaryName + "#" + enumName;
        }
    }

    /**
     * Represents a primitive or string value, or an array of those.
     * Arrays of enum constants, and of annotations, are represented
     * by {@link SymArray}.
     */
    final class SymValue implements SymbolicValue {

        private final Object value;

        SymValue(Object value) {
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
            SymValue symValue = (SymValue) o;
            return Objects.deepEquals(value, symValue.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            if (value.getClass().isArray()) {
                return ArrayUtils.toString(value);
            }
            return value.toString();
        }
    }
}
