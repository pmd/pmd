/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class JavaTypeDefinition implements TypeDefinition {
    // contains non-generic and raw EXACT types
    private static final Map<Class<?>, JavaTypeDefinition> CLASS_EXACT_TYPE_DEF_CACHE = new HashMap<>();

    private final TypeDefinitionType definitionType;

    public enum TypeDefinitionType {
        EXACT, UPPER_BOUND, UPPER_WILDCARD, LOWER_WILDCARD
    }

    protected JavaTypeDefinition(TypeDefinitionType definitionType) {
        this.definitionType = definitionType;
    }

    public static JavaTypeDefinition forClass(TypeDefinitionType type, List<JavaTypeDefinition> intersectionTypes) {
        switch (type) {
        case EXACT:
            if (intersectionTypes.size() == 1) {
                return intersectionTypes.get(0);
            } else {
                throw new IllegalArgumentException("Exact intersection types do not exist!");
            }
        case UPPER_BOUND:
        case UPPER_WILDCARD:
        case LOWER_WILDCARD:
            return new JavaTypeDefinitionIntersection(type, intersectionTypes);
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

        final JavaTypeDefinition newDef = new JavaTypeDefinitionSimple(clazz);

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
    public final boolean isRawType() {
        return !isIntersectionType() && isGeneric() && CLASS_EXACT_TYPE_DEF_CACHE.containsKey(getType());
    }

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

    public final boolean isIntersectionType() {
        return getJavaTypeCount() != 1;
    }

    public final boolean isWildcard() {
        return definitionType == TypeDefinitionType.LOWER_WILDCARD
                || definitionType == TypeDefinitionType.UPPER_WILDCARD;
    }

    public final TypeDefinitionType getDefinitionType() {
        return definitionType;
    }

    public abstract JavaTypeDefinition getJavaType(int index);

    public abstract int getJavaTypeCount();
}
