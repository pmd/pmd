/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.ClassUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.ClassNamesUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Structure to represent constant values of annotations symbolically.
 * Annotations may contain:
 * <ul>
 * <li>Primitive or string values: {@link SymValue}
 * <li>Enum constants: {@link SymEnum}
 * <li>Other annotations: {@link SymAnnot}
 * <li>Arrays of the above, of dimension 1: {@link SymArray}
 * </ul>
 *
 * <p>Any other values, including the null reference, are unsupported and
 * cannot be represented by this API.
 *
 * <p>Currently the public API allows comparing the values to an actual
 * java value that you compiled against ({@link #valueEquals(Object)}).
 * This may be improved later to allow comparing values without needing
 * them in the compile classpath.
 *
 * <p>This is a sealed interface and should not be implemented by clients.
 */
public interface SymbolicValue {
    // TODO these should probably be bound to a TypeSystem
    //  (as the impl of SymAnnot is)


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
     * Returns an annotation element for the given java value. Returns
     * null if the value cannot be an annotation element.
     *
     * <p>Note: annotations are currently unsupported.
     */
    static @Nullable SymbolicValue of(Object value) {
        if (value == null) {
            return null;
        }

        if (value.getClass() == String.class
            || ClassUtils.isPrimitiveWrapper(value.getClass())) {
            return new SymValue(value);
        }

        if (value instanceof Enum<?>) {
            return SymEnum.fromEnum((Enum<?>) value);
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

    /**
     * Symbolic representation of an annotation.
     */
    interface SymAnnot extends SymbolicValue {

        /**
         * Returns the explicit value of the attribute. If the attribute
         * is not mentioned in the {@link #getExplicitAttributes()}, returns
         * null.
         *
         * @see #getAttributeOrDefault(String)
         */
        default @Nullable SymbolicValue getAttribute(String attrName) {
            return getExplicitAttributes().get(attrName);
        }

        /**
         * Returns the default value for the given attribute, as declared
         * on the annotation method. Returns null if the attribute does
         * not exist, is unresolved, or has no default. TODO do we need separate sentinels for that?
         */
        @Nullable SymbolicValue getDefaultValue(String attrName);

        /**
         * An alias for {@link #getAttribute(String, boolean)}, which
         * uses defaults values.
         */
        default @Nullable SymbolicValue getAttributeOrDefault(String attrName) {
            return getAttribute(attrName, true);
        }

        /**
         * Returns the value of the attribute. This asks for an explicit
         * attribute, and may fall back to the default value, if the {@code useDefaults}
         * parameter is true. Returns null if both {@link #getAttribute(String)}
         * and {@link #getDefaultValue(String)} return null, see their doc.
         *
         * @param attrName    Attribute name
         * @param useDefaults Whether to fallback to a default value
         */
        default @Nullable SymbolicValue getAttribute(String attrName, boolean useDefaults) {
            SymbolicValue value = getAttribute(attrName);
            if (value != null || !useDefaults) {
                return value;
            }

            return getDefaultValue(attrName);
        }

        /**
         * The explicit attributes, mentioned in the annotation.
         * Attributes that take default values are not in this map.
         * The map is indexed by attribute name.
         */
        Map<String, SymbolicValue> getExplicitAttributes();

        /**
         * The retention policy. Note that naturally, members accessed
         * from class files cannot reflect annotations with {@link RetentionPolicy#SOURCE}.
         */
        RetentionPolicy getRetention();

        boolean isOfType(String binaryName);

        /**
         * Whether the annotation has the given type. Note that only
         * the name of the class is taken into account, because its
         * {@code Class} instance may be missing from the type system classpath.
         */
        default boolean isOfType(Class<? extends Annotation> klass) {
            return isOfType(klass.getName());
        }

        /**
         * Like the other overloads, but does not use defaults.
         *
         * @see #attributeMatches(String, Object, boolean)
         */
        default OptionalBool attributeMatches(String name, Object attrValue) {
            return attributeMatches(name, attrValue, false);
        }

        /**
         * Returns YES if the annotation has the attribute set to the
         * given value. Returns NO if it is set to another value.
         * Returns UNKNOWN if the attribute does not exist or is unresolved.
         *
         * @param attrName   Attribute name
         * @param attrValue  An object value, or a {@link SymbolicValue}
         * @param useDefault If true, default values are considered. If false,
         *                   and the attribute is not explicitly set, this method
         *                   will return UNKNOWN even if the attribute has a default value
         */
        default OptionalBool attributeMatches(String attrName, Object attrValue, boolean useDefault) {
            SymbolicValue attr = getAttribute(attrName, useDefault);
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
     * An array of values.
     */
    final class SymArray implements SymbolicValue {

        // exactly one of those is non-null
        private final @Nullable List<SymbolicValue> elements;
        private final @Nullable Object primArray; // for primitive arrays we keep this around
        private final int length;

        private SymArray(@Nullable List<SymbolicValue> elements, @Nullable Object primArray, int length) {
            this.elements = elements;
            this.primArray = primArray;
            this.length = length;
        }

        public static SymArray forElements(List<SymbolicValue> values) {
            return new SymArray(Collections.unmodifiableList(new ArrayList<>(values)), null, values.size());
        }

        /**
         * Returns a SymArray for the parameter.
         *
         * @throws IllegalArgumentException If the parameter is not an array,
         *                                  or has an unsupported component type
         */
        public static SymArray forArray(Object array) {
            if (array == null || !array.getClass().isArray()) {
                throw new IllegalArgumentException("Needs an array, got " + array);
            }

            if (array.getClass().getComponentType().isPrimitive()) {
                int len = Array.getLength(array);
                return new SymArray(null, array, len);
            } else {
                Object[] arr = (Object[]) array;
                if (!isOkComponentType(arr.getClass().getComponentType())) {
                    throw new IllegalArgumentException("Unsupported component type" + arr.getClass().getComponentType());
                }

                List<SymbolicValue> lst = new ArrayList<>(arr.length);
                for (Object o : arr) {
                    SymbolicValue elt = SymbolicValue.of(o);
                    if (elt == null) {
                        throw new IllegalArgumentException("Unsupported array element" + o);
                    }
                    lst.add(elt);
                }
                return new SymArray(lst, null, arr.length);
            }
        }

        static boolean isOkComponentType(Class<?> compType) {
            return compType.isPrimitive()
                || compType == String.class
                || compType.isEnum();
            // for now we don't support this
            // || compType.isAnnotation();
        }

        public int length() {
            return length;
        }

        public boolean valueEquals(Object o) {
            if (!o.getClass().isArray() || !isOkComponentType(o.getClass().getComponentType())) {
                return false;
            }
            if (primArray != null) {
                return Objects.deepEquals(primArray, o);
            } else if (!(o instanceof Object[])) {
                return false;
            }
            assert elements != null;

            Object[] arr = (Object[]) o;
            if (arr.length != length) {
                return false;
            }
            for (int i = 0; i < elements.size(); i++) {
                if (!elements.get(i).valueEquals(arr[i])) {
                    return false;
                }
            }
            return true;

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
            this.enumBinaryName = Objects.requireNonNull(enumBinaryName);
            this.enumName = Objects.requireNonNull(enumConstName);
        }

        /**
         * Returns the symbolic value for the given enum constant.
         *
         * @param value An enum constant
         *
         * @throws NullPointerException if the parameter is null
         */
        public static SymbolicValue fromEnum(Enum<?> value) {
            return fromBinaryName(value.getDeclaringClass().getName(), value.name());
        }

        /**
         * @param enumBinaryName A binary name, eg {@code com.MyEnum}
         * @param enumConstName  Simple name of the enum constant
         *
         * @throws NullPointerException if any parameter is null
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
     * Represents a primitive or string value.
     */
    final class SymValue implements SymbolicValue {

        private final Object value;

        SymValue(Object value) { // note that the value is always boxed
            assert value != null && isOkValue(value) : "Invalid value " + value;
            this.value = value;
        }

        private static boolean isOkValue(Object value) {
            return ClassUtils.isPrimitiveWrapper(value.getClass())
                || value instanceof String;
        }

        public boolean valueEquals(Object o) {
            return Objects.equals(value, o);
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
            return valueEquals(symValue.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }
}
