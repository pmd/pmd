/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.annotation.Annotation;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.AnnotationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.ClassNamesUtil;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Structure to represent constant values of annotations symbolically.
 * Annotations may contain:
 * <ul>
 * <li>Primitive or string values: {@link SymValue}
 * <li>Enum constants: {@link SymEnum}
 * <li>Class instances: {@link SymClass}
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
 *
 * <p>Note: the point of this api is to enable comparisons between values,
 * not deep introspection into values. This is why there are very few getter
 * methods, except in {@link SymAnnot}, which is the API point used by
 * {@link AnnotableSymbol}.
 */
public interface SymbolicValue {


    /**
     * Returns true if this symbolic value represents the same value as
     * the given object. If the parameter is null, returns false.
     */
    boolean valueEquals(Object o);

    /**
     * Returns true if this value is equal to the other one. The parameter
     * must be a {@link SymbolicValue} of the same type. Use {@link #valueEquals(Object)}
     * to compare to a java object.
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns a symbolic value for the given java object
     * Returns an annotation element for the given java value. Returns
     * null if the value cannot be an annotation element or cannot be
     * constructed.
     */
    static @Nullable SymbolicValue of(TypeSystem ts, Object value) {
        Objects.requireNonNull(ts);
        if (value == null) {
            return null;
        }

        if (SymValue.isOkValue(value)) {
            return new SymValue(value);
        }

        if (value instanceof Enum<?>) {
            return SymEnum.fromEnum(ts, (Enum<?>) value);
        }

        if (value instanceof Annotation) {
            return AnnotWrapper.wrap(ts, (Annotation) value);
        }

        if (value instanceof Class<?>) {
            return SymClass.ofBinaryName(ts, ((Class<?>) value).getName());
        }

        if (value.getClass().isArray()) {
            if (!SymArray.isOkComponentType(value.getClass().getComponentType())) {
                return null;
            }
            return SymArray.forArray(ts, value);
        }

        return null;
    }

    /**
     * Symbolic representation of an annotation.
     */
    interface SymAnnot extends SymbolicValue {

        /**
         * Returns the value of the attribute, which may fall back to
         * the default value of the annotation element. Returns null if
         * the attribute does not exist, is unresolved, or has no default.
         * TODO do we need separate sentinels for that?
         */
        @Nullable SymbolicValue getAttribute(String attrName);

        /**
         * Return the symbol for the declaring class of the annotation.
         */
        @NonNull JClassSymbol getAnnotationSymbol();

        /**
         * Return the simple names of all attributes, including those
         * defined in the annotation type but not explicitly set in this annotation.
         * Note that if the annotation is reflected from a class file,
         * we can't know which annotations used their default value, so it
         * returns a set of all attribute names.
         */
        default PSet<String> getAttributeNames() {
            return getAnnotationSymbol().getAnnotationAttributeNames();
        }

        /** Return the binary name of the annotation type. */
        default String getBinaryName() {
            return getAnnotationSymbol().getBinaryName();
        }

        /** Return the simple name of the annotation type. */
        default String getSimpleName() {
            return getAnnotationSymbol().getSimpleName();
        }

        @Override
        default boolean valueEquals(Object o) {
            if (!(o instanceof Annotation)) {
                return false;
            }
            Annotation annot = (Annotation) o;
            if (!this.isOfType(annot.annotationType())) {
                return false;
            }

            for (String attrName : getAttributeNames()) { // todo this is not symmetric...

                Object attr = null;
                try {
                    attr = MethodUtils.invokeExactMethod(annot, attrName);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                }
                if (attr == null || !AnnotationUtils.isValidAnnotationMemberType(attr.getClass())) {
                    continue;
                }

                SymbolicValue myAttr = getAttribute(attrName);
                if (myAttr == null || !myAttr.valueEquals(attr)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * The retention policy. Note that naturally, members accessed
         * from class files cannot reflect annotations with {@link RetentionPolicy#SOURCE}.
         */
        default RetentionPolicy getRetention() {
            return getAnnotationSymbol().getAnnotationRetention();
        }

        /**
         * Return true if this annotation's binary name matches the given
         * binary name.
         */
        default boolean isOfType(String binaryName) {
            return getBinaryName().equals(binaryName);
        }

        /**
         * Whether the annotation has the given type. Note that only
         * the name of the class is taken into account, because its
         * {@code Class} instance may be missing from the type system classpath.
         */
        default boolean isOfType(Class<? extends Annotation> klass) {
            return isOfType(klass.getName());
        }

        /**
         * Returns YES if the annotation has the attribute set to the
         * given value. Returns NO if it is set to another value.
         * Returns UNKNOWN if the attribute does not exist or is
         * unresolved.
         */
        default OptionalBool attributeMatches(String attrName, Object attrValue) {
            SymbolicValue attr = getAttribute(attrName);
            if (attr == null) {
                return OptionalBool.UNKNOWN;
            }

            return OptionalBool.definitely(SymbolicValueHelper.equalsModuloWrapper(attr, attrValue));
        }

        /**
         * Returns YES if the annotation has the attribute set to the
         * given value, or to an array containing the given value. Returns
         * NO if that's not the case. Returns UNKNOWN if the attribute
         * does not exist or is unresolved.
         */
        default OptionalBool attributeContains(String attrName, Object attrValue) {
            SymbolicValue attr = getAttribute(attrName);
            if (attr == null) {
                return OptionalBool.UNKNOWN;
            }
            if (attr instanceof SymArray) {
                // todo what if the value is an array itself
                return OptionalBool.definitely(((SymArray) attr).containsValue(attrValue));
            }

            return OptionalBool.definitely(SymbolicValueHelper.equalsModuloWrapper(attr, attrValue));
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
            assert elements == null ^ primArray == null : "Either elements or array must be mentioned";
            assert primArray == null || primArray.getClass().isArray();
        }

        /**
         * Returns a SymArray for a list of symbolic values.
         *
         * @param values The elements
         *
         * @throws NullPointerException if the parameter is null
         */
        public static SymArray forElements(List<SymbolicValue> values) {
            return new SymArray(Collections.unmodifiableList(new ArrayList<>(values)), null, values.size());
        }

        /**
         * Returns a SymArray for the parameter.
         *
         * @throws NullPointerException     if the parameter is null
         * @throws IllegalArgumentException If the parameter is not an array,
         *                                  or has an unsupported component type
         */
        // package-private, people should use SymbolicValue#of
        static SymArray forArray(TypeSystem ts, @NonNull Object array) {
            if (!array.getClass().isArray()) {
                throw new IllegalArgumentException("Needs an array, got " + array);
            }

            if (array.getClass().getComponentType().isPrimitive()) {
                int len = Array.getLength(array);
                return new SymArray(null, array, len);
            } else {
                Object[] arr = (Object[]) array;
                if (!isOkComponentType(arr.getClass().getComponentType())) {
                    throw new IllegalArgumentException(
                        "Unsupported component type" + arr.getClass().getComponentType());
                }

                List<SymbolicValue> lst = new ArrayList<>(arr.length);
                for (Object o : arr) {
                    SymbolicValue elt = SymbolicValue.of(ts, o);
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
                || compType == Class.class
                || compType.isEnum()
                || compType.isAnnotation();
        }

        public int length() {
            return length;
        }


        /**
         * Return true if this array contains the given object. If the
         * object is a {@link SymbolicValue}, it uses {@link #equals(Object)},
         * otherwise it uses {@link #valueEquals(Object)} to compare elements.
         */
        public boolean containsValue(Object value) {
            if (primArray != null) {
                // todo I don't know how to code that without switching on the type
                throw new NotImplementedException("not implemented: containsValue with a primitive array");
            } else if (elements != null) {
                return elements.stream().anyMatch(it -> SymbolicValueHelper.equalsModuloWrapper(it, value));
            }
            return false;
        }

        @Override
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
            if (elements != null) {
                return Objects.equals(elements, array.elements);
            } else {
                return Objects.deepEquals(primArray, array.primArray);
            }
        }

        @Override
        public int hashCode() {
            if (elements != null) {
                return elements.hashCode();
            } else {
                assert primArray != null;
                return primArray.hashCode();
            }
        }

        @Override
        public String toString() {
            if (elements != null) {
                return "[list " + elements + ']';
            } else {
                return "[array " + ArrayUtils.toString(primArray) + ']';
            }
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
         * If this enum constant is declared in the given enum class,
         * returns its value. Otherwise returns null.
         *
         * @param enumClass Class of an enum
         * @param <E>       Return type
         */
        public <E extends Enum<E>> @Nullable E toEnum(Class<E> enumClass) {
            return enumClass.getName().equals(enumBinaryName) ? EnumUtils.getEnum(enumClass, enumName)
                                                              : null;
        }

        /**
         * Returns the symbolic value for the given enum constant.
         *
         * @param ts    Type system
         * @param value An enum constant
         *
         * @throws NullPointerException if the parameter is null
         */
        public static SymbolicValue fromEnum(TypeSystem ts, Enum<?> value) {
            return fromBinaryName(ts, value.getDeclaringClass().getName(), value.name());
        }

        /**
         * @param ts             Type system
         * @param enumBinaryName A binary name, eg {@code com.MyEnum}
         * @param enumConstName  Simple name of the enum constant
         *
         * @throws NullPointerException if any parameter is null
         */
        public static SymEnum fromBinaryName(TypeSystem ts, String enumBinaryName, String enumConstName) {
            return new SymEnum(enumBinaryName, enumConstName);
        }

        /**
         * @param ts                 Type system
         * @param enumTypeDescriptor The type descriptor, eg {@code Lcom/MyEnum;}
         * @param enumConstName      Simple name of the enum constant
         */
        public static SymEnum fromTypeDescriptor(TypeSystem ts, String enumTypeDescriptor, String enumConstName) {
            String enumBinaryName = ClassNamesUtil.classDescriptorToBinaryName(enumTypeDescriptor);
            return fromBinaryName(ts, enumBinaryName, enumConstName);
        }

        @Override
        public boolean valueEquals(Object o) {
            if (!(o instanceof Enum)) {
                return false;
            }
            Enum<?> value = (Enum<?>) o;
            return this.enumName.equals(value.name())
                && enumBinaryName.equals(value.getDeclaringClass().getName());
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

        private SymValue(Object value) { // note that the value is always boxed
            assert value != null && isOkValue(value) : "Invalid value " + value;
            this.value = value;
        }

        private static boolean isOkValue(@NonNull Object value) {
            return ClassUtils.isPrimitiveWrapper(value.getClass())
                || value instanceof String;
        }

        @Override
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
            return value.hashCode();
        }

        @Override
        public String toString() {
            return value.toString();
        }
    }


    /**
     * Represents a class constant.
     */
    final class SymClass implements SymbolicValue {

        private final String binaryName;

        private SymClass(String binaryName) {
            this.binaryName = binaryName;
        }


        public static SymClass ofBinaryName(TypeSystem ts, String binaryName) {
            return new SymClass(binaryName);
        }

        @Override
        public boolean valueEquals(Object o) {
            return o instanceof Class<?> && ((Class<?>) o).getName().equals(binaryName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SymClass symClass = (SymClass) o;
            return Objects.equals(binaryName, symClass.binaryName);
        }

        @Override
        public int hashCode() {
            return binaryName.hashCode();
        }
    }
}
