/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;


public abstract class JavaTypeDefinition implements TypeDefinition {
    // contains non-generic and raw EXACT types
    private static final Map<Class<?>, JavaTypeDefinition> CLASS_EXACT_TYPE_DEF_CACHE = new ConcurrentHashMap<>();

    private final TypeDefinitionType definitionType;

    protected JavaTypeDefinition(TypeDefinitionType definitionType) {
        this.definitionType = definitionType;
    }

    public static JavaTypeDefinition forClass(TypeDefinitionType type, Class<?> clazz,
                                              JavaTypeDefinition... boundGenerics) {
        return forClass(type, forClass(clazz, boundGenerics));
    }

    public static JavaTypeDefinition forClass(TypeDefinitionType type, JavaTypeDefinition... intersectionTypes) {
        switch (type) {
        case EXACT:
            if (intersectionTypes.length == 1) {
                return intersectionTypes[0];
            } else {
                throw new IllegalArgumentException("Exact intersection types do not exist!");
            }
        case UPPER_BOUND:
        case UPPER_WILDCARD:
            // In theory, if one of the bounds can't be resolved, then the type is useless.
            // Looking at the implementation of JavaTypeDefinitionUpper, it looks like only the
            // first bound is used, so we could only check for the first array component.
            // But isn't that behaviour weird ? Where are the other bounds useful then ?
            return ArrayUtils.contains(intersectionTypes, null) ? null : new JavaTypeDefinitionUpper(type, intersectionTypes);
        case LOWER_WILDCARD:
            return ArrayUtils.contains(intersectionTypes, null) ? null : new JavaTypeDefinitionLower(intersectionTypes);
        default:
            throw new IllegalStateException("Unknow type");
        }
    }

    public static JavaTypeDefinition forClass(final Class<?> clazz, JavaTypeDefinition... boundGenerics) {
        if (clazz == null) {
            return null;
        }

        // deal with generic types
        if (boundGenerics.length != 0) {
            // With generics there is no cache
            return new JavaTypeDefinitionSimple(clazz, boundGenerics);
        }

        final JavaTypeDefinition typeDef = CLASS_EXACT_TYPE_DEF_CACHE.get(clazz);

        if (typeDef != null) {
            return typeDef;
        }

        final JavaTypeDefinition newDef;
        try {
            newDef = new JavaTypeDefinitionSimple(clazz);
        } catch (final NoClassDefFoundError e) {
            return null; // Can happen if a parent class references a class not in classpath
        }

        CLASS_EXACT_TYPE_DEF_CACHE.put(clazz, newDef);

        return newDef;
    }

    @Override
    public abstract Class<?> getType();

    public abstract JavaTypeDefinition getEnclosingClass();

    public abstract boolean isGeneric();

    public static int getGenericTypeIndex(TypeVariable<?>[] typeParameters, final String parameterName) {
        for (int i = 0; i < typeParameters.length; i++) {
            if (typeParameters[i].getName().equals(parameterName)) {
                return i;
            }
        }

        return -1;
    }

    public abstract JavaTypeDefinition getGenericType(String parameterName);

    public abstract JavaTypeDefinition getGenericType(int index);

    public abstract JavaTypeDefinition resolveTypeDefinition(Type type);

    public abstract JavaTypeDefinition resolveTypeDefinition(Type type, Method method,
                                                             List<JavaTypeDefinition> methodTypeArgs);


    public abstract boolean isClassOrInterface();

    public abstract boolean isNullType();

    public abstract boolean isPrimitive();

    public abstract boolean hasSameErasureAs(JavaTypeDefinition def);

    public abstract int getTypeParameterCount();

    public abstract boolean isArrayType();


    /**
     * Gets the component type of this type definition if it
     * is an array type. The component type of an array is
     * the type is the same type as the array, with one less
     * dimension, e.g. the component type of {@code int[][][]}
     * is {@code int[][]}.
     *
     * @return The component type of this array type
     *
     * @throws IllegalStateException if this definition doesn't identify an array type
     * @see #getElementType()
     */
    public abstract JavaTypeDefinition getComponentType();


    /**
     * Gets the element type of this type definition if it
     * is an array type. The component type of an array is
     * the type is the same type as the array, stripped of
     * all array dimensions, e.g. the element type of
     * {@code int[][][]} is {@code int}.
     *
     * @return The element type of this array type, or this
     * type definition if {@link #isArrayType()} returns false
     *
     * @see #getComponentType()
     */
    public abstract JavaTypeDefinition getElementType();


    // @formatter:off
    /**
     * Returns the type definition of the array type which
     * has the given number of array dimensions, plus the dimensions
     * of this type definition. Examples, assuming JavaTypeDefinition
     * and Class are interchangeable (== is equality, not identity):
     * <ul>
     *     <li>{@code int.class.withDimensions(3) == int[][][].class}
     *     <li>{@code int[].class.withDimensions(1) == int[][].class}
     *     <li>{@code c.withDimensions(0) == c}
     *     <li>{@code n > 0 => c.withDimensions(n).getComponentType() == c.withDimensions(n - 1)}
     * </ul>
     *
     * @param numDimensions Number of dimensions added to this type in
     *                      the resulting array type
     *
     * @return A new type definition, or this if numDimensions == 0
     * @throws IllegalArgumentException if numDimensions &lt; 0
     */
    // @formatter:on
    public abstract JavaTypeDefinition withDimensions(int numDimensions);


    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public abstract Set<JavaTypeDefinition> getSuperTypeSet();

    protected abstract Set<JavaTypeDefinition> getSuperTypeSet(Set<JavaTypeDefinition> destinationSet);

    public abstract Set<Class<?>> getErasedSuperTypeSet();


    /**
     * Returns true if this type has type parameters and has not been parameterized,
     * e.g. {@code List} instead of {@code List<T>}.
     */
    public abstract boolean isRawType();

    public abstract JavaTypeDefinition getAsSuper(Class<?> superClazz);

    public final boolean isExactType() {
        return definitionType == TypeDefinitionType.EXACT;
    }

    public final boolean isUpperBound() {
        return definitionType == TypeDefinitionType.UPPER_BOUND
                || definitionType == TypeDefinitionType.UPPER_WILDCARD;
    }

    public final boolean isLowerBound() {
        return definitionType == TypeDefinitionType.LOWER_WILDCARD;
    }

    public abstract boolean isIntersectionType();

    public final boolean isWildcard() {
        return definitionType == TypeDefinitionType.LOWER_WILDCARD
                || definitionType == TypeDefinitionType.UPPER_WILDCARD;
    }

    public final TypeDefinitionType getDefinitionType() {
        return definitionType;
    }

    public abstract JavaTypeDefinition getJavaType(int index);

    public abstract int getJavaTypeCount();

    protected abstract String shallowString();
}
