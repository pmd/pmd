/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class JavaTypeDefinition implements TypeDefinition {
    // contains non-generic and raw EXACT types
    private static final Map<Class<?>, JavaTypeDefinition> CLASS_EXACT_TYPE_DEF_CACHE = new HashMap<>();

    // contains non-generic and raw UPPER_BOUND types, does not contain intersection types
    private static final Map<Class<?>, JavaTypeDefinition> CLASS_UPPER_BOUND_TYPE_DEF_CACHE = new HashMap<>();

    // contains non-generic and raw LOWER_BOUND types, does not contain intersection types
    private static final Map<Class<?>, JavaTypeDefinition> CLASS_LOWER_BOUND_TYPE_DEF_CACHE = new HashMap<>();

    private final TypeDefinitionType definitionType;

    public enum TypeDefinitionType {
        EXACT, UPPER_BOUND, LOWER_BOUND, INTERSECTION
    }

    protected JavaTypeDefinition(TypeDefinitionType definitionType) {
        this.definitionType = definitionType;
    }

    public static JavaTypeDefinition forClassIntersetion(List<JavaTypeDefinition> intersectionTypes) {
        return new JavaTypeDefinitionIntersection(intersectionTypes);
    }

    public static JavaTypeDefinition forClassLower(JavaTypeDefinition lowerBound) {
        if(lowerBound == null) {
            return null;
        }

        if (!lowerBound.isGeneric() || lowerBound.isRawType()) {
            return new JavaTypeDefinitionSimple(lowerBound);
        }

        final JavaTypeDefinition typeDef = CLASS_LOWER_BOUND_TYPE_DEF_CACHE.get(lowerBound.getType());

        if (typeDef != null) {
            return typeDef;
        }

        final JavaTypeDefinition newDef = new JavaTypeDefinitionSimple(lowerBound);

        CLASS_LOWER_BOUND_TYPE_DEF_CACHE.put(lowerBound.getType(), newDef);

        return newDef;
    }

    public static JavaTypeDefinition forClassUpper(Class<?> clazz, JavaTypeDefinition... boundGenerics) {
        if (clazz == null) {
            return null;
        }

        if (boundGenerics.length != 0) {
            // no caching with generics
            return new JavaTypeDefinitionSimple(TypeDefinitionType.UPPER_BOUND, clazz, boundGenerics);
        }

        final JavaTypeDefinition typeDef = CLASS_UPPER_BOUND_TYPE_DEF_CACHE.get(clazz);

        if (typeDef != null) {
            return typeDef;
        }

        final JavaTypeDefinition newDef = new JavaTypeDefinitionSimple(TypeDefinitionType.UPPER_BOUND, clazz);

        CLASS_UPPER_BOUND_TYPE_DEF_CACHE.put(clazz, newDef);

        return newDef;
    }

    public static JavaTypeDefinition forClass(final Class<?> clazz, JavaTypeDefinition... boundGenerics) {
        if (clazz == null) {
            return null;
        }

        // deal with generic types
        if (boundGenerics.length != 0) {
            // With generics there is no cache
            return new JavaTypeDefinitionSimple(TypeDefinitionType.EXACT, clazz, boundGenerics);
        }

        final JavaTypeDefinition typeDef = CLASS_EXACT_TYPE_DEF_CACHE.get(clazz);

        if (typeDef != null) {
            return typeDef;
        }

        final JavaTypeDefinition newDef = new JavaTypeDefinitionSimple(TypeDefinitionType.EXACT, clazz);

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

    public abstract JavaTypeDefinition getGenericType(final String parameterName);

    public abstract JavaTypeDefinition getGenericType(final int index);

    public abstract JavaTypeDefinition resolveTypeDefinition(final Type type);

    public abstract JavaTypeDefinition resolveTypeDefinition(final Type type, Method method,
                                                             List<JavaTypeDefinition> methodTypeArgs);

    public abstract JavaTypeDefinition getComponentType();

    public abstract boolean isClassOrInterface();

    public abstract boolean isNullType();

    public abstract boolean isPrimitive();

    public abstract boolean hasSameErasureAs(JavaTypeDefinition def);

    public abstract int getTypeParameterCount();

    public abstract boolean isArrayType();

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
     * @return true if clazz is generic and had not been parameterized
     */
    public boolean isRawType() {
        return isGeneric() && CLASS_EXACT_TYPE_DEF_CACHE.containsKey(getType());
    }

    public abstract JavaTypeDefinition getAsSuper(Class<?> superClazz);

    public boolean isExactType() {
        return definitionType == TypeDefinitionType.EXACT;
    }

    public boolean isUpperBound() {
        return definitionType == TypeDefinitionType.UPPER_BOUND
                // intersection types can only be upper bounds in java
                || definitionType == TypeDefinitionType.INTERSECTION;
    }

    public boolean isLowerBound() {
        return definitionType == TypeDefinitionType.LOWER_BOUND;
    }

    public boolean isIntersectionType() {
        return definitionType == TypeDefinitionType.INTERSECTION;
    }

    public TypeDefinitionType getDefinitionType() {
        return definitionType;
    }

    public abstract JavaTypeDefinition getIntersectionType(int index);

    public abstract int getIntersectionTypeCount();

    public abstract JavaTypeDefinition getLowerBound();
}
