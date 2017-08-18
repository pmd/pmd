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

/* default */ class JavaTypeDefinitionSimple extends JavaTypeDefinition {
    private final Class<?> clazz;
    private final List<JavaTypeDefinition> genericArgs;
    // cached because calling clazz.getTypeParameters().length create a new array every time
    private final int typeParameterCount;
    private final boolean isGeneric;
    private final JavaTypeDefinition enclosingClass;

    // only used when definitionType == LOWER_BOUND
    private final JavaTypeDefinition lowerBound;

    protected JavaTypeDefinitionSimple(JavaTypeDefinition lowerBound) {
        this(TypeDefinitionType.LOWER_BOUND, lowerBound, Object.class);
    }

    protected JavaTypeDefinitionSimple(TypeDefinitionType definitionType, Class<?> clazz,
                                       JavaTypeDefinition... boundGenerics) {
        this(definitionType, null, clazz, boundGenerics);
    }

    protected JavaTypeDefinitionSimple(TypeDefinitionType definitionType, JavaTypeDefinition lowerBound,
                                       final Class<?> clazz, JavaTypeDefinition... boundGenerics) {
        super(definitionType);

        if (definitionType == TypeDefinitionType.LOWER_BOUND && lowerBound == null) {
            throw new IllegalStateException("Constructing a lower bound type with invalid lower bound argument");
        } else if (definitionType != TypeDefinitionType.LOWER_BOUND && lowerBound != null) {
            throw new IllegalStateException("Not a lower bound type while providing a valid lower bound");
        }

        this.clazz = clazz;
        this.lowerBound = lowerBound;

        final TypeVariable<?>[] typeParameters;
        // the anonymous class can't have generics, but we may be binding generics from super classes
        if (clazz.isAnonymousClass()) {
            // is this an anonymous class based on an interface or a class?
            if (clazz.getInterfaces().length != 0) {
                typeParameters = clazz.getInterfaces()[0].getTypeParameters();
            } else {
                typeParameters = clazz.getSuperclass().getTypeParameters();
            }
        } else {
            typeParameters = clazz.getTypeParameters();
        }

        typeParameterCount = typeParameters.length;
        isGeneric = typeParameters.length != 0;

        if (isGeneric) {
            // Generics will be lazily loaded
            this.genericArgs = new ArrayList<>(typeParameters.length);
            // boundGenerics would be empty if this is a raw type, hence the lazy loading
            Collections.addAll(this.genericArgs, boundGenerics);
        } else {
            this.genericArgs = Collections.emptyList();
        }

        enclosingClass = forClass(clazz.getEnclosingClass());
    }

    @Override
    public Class<?> getType() {
        return clazz;
    }

    @Override
    public JavaTypeDefinition getEnclosingClass() {
        return enclosingClass;
    }

    @Override
    public boolean isGeneric() {
        return !genericArgs.isEmpty();
    }

    private JavaTypeDefinition getGenericType(final String parameterName, Method method,
                                                    List<JavaTypeDefinition> methodTypeArgumens) {
        if (method != null && methodTypeArgumens != null) {
            int paramIndex = getGenericTypeIndex(method.getTypeParameters(), parameterName);
            if (paramIndex != -1) {
                return methodTypeArgumens.get(paramIndex);
            }
        }

        return getGenericType(parameterName);
    }

    @Override
    public JavaTypeDefinition getGenericType(final String parameterName) {
        for (JavaTypeDefinition currTypeDef = this; currTypeDef != null; currTypeDef = currTypeDef.getEnclosingClass()) {
            int paramIndex = getGenericTypeIndex(currTypeDef.getType().getTypeParameters(), parameterName);
            if (paramIndex != -1) {
                return currTypeDef.getGenericType(paramIndex);
            }
        }

        // throw because we could not find parameterName
        StringBuilder builder = new StringBuilder("No generic parameter by name ").append(parameterName);
        for (JavaTypeDefinition currTypeDef = this; currTypeDef != null; currTypeDef = currTypeDef.getEnclosingClass()) {
            builder.append("\n on class ");
            builder.append(clazz.getSimpleName());
        }

        throw new IllegalArgumentException(builder.toString());
    }

    @Override
    public JavaTypeDefinition getGenericType(final int index) {
        // Check if it has been lazily initialized first
        if (genericArgs.size() > index) {
            final JavaTypeDefinition cachedDefinition = genericArgs.get(index);
            if (cachedDefinition != null) {
                return cachedDefinition;
            }
        }

        // Force the list to have enough elements
        for (int i = genericArgs.size(); i <= index; i++) {
            genericArgs.add(null);
        }
        
        /*
         * Set a default to circuit-brake any recursions (ie: raw types with no generic info)
         * Object.class is a right answer in those scenarios
         */
        genericArgs.set(index, forClass(Object.class));

        final TypeVariable<?> typeVariable = clazz.getTypeParameters()[index];
        final JavaTypeDefinition typeDefinition = resolveTypeDefinition(typeVariable.getBounds()[0]);

        // cache result
        genericArgs.set(index, typeDefinition);
        return typeDefinition;
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(final Type type) {
        return resolveTypeDefinition(type, null, null);
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(final Type type, Method method,
                                                          List<JavaTypeDefinition> methodTypeArgs) {
        if (type == null) {
            // Without more info, this is all we can tell...
            return forClass(Object.class);
        }

        if (type instanceof Class) { // Raw types take this branch as well
            return forClass((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;

            // recursively determine each type argument's type def.
            final Type[] typeArguments = parameterizedType.getActualTypeArguments();
            final JavaTypeDefinition[] genericBounds = new JavaTypeDefinition[typeArguments.length];
            for (int i = 0; i < typeArguments.length; i++) {
                genericBounds[i] = resolveTypeDefinition(typeArguments[i], method, methodTypeArgs);
            }

            // TODO : is this cast safe?
            return forClass((Class<?>) parameterizedType.getRawType(), genericBounds);
        } else if (type instanceof TypeVariable) {
            return getGenericType(((TypeVariable<?>) type).getName(), method, methodTypeArgs);
        } else if (type instanceof WildcardType) {
            final Type[] wildcardUpperBounds = ((WildcardType) type).getUpperBounds();
            if (wildcardUpperBounds.length != 0) { // upper bound wildcard
                return resolveTypeDefinition(wildcardUpperBounds[0], method, methodTypeArgs);
            } else { // lower bound wildcard
                return forClass(Object.class);
            }
        }

        // TODO : Shall we throw here?
        return forClass(Object.class);
    }

    // TODO: are generics okay like this?
    public JavaTypeDefinition getComponentType() {
        Class<?> componentType = getType().getComponentType();

        if (componentType == null) {
            throw new IllegalStateException(getType().getSimpleName() + " is not an array type!");
        }

        return forClass(componentType);
    }

    public boolean isClassOrInterface() {
        return !clazz.isEnum() && !clazz.isPrimitive() && !clazz.isAnnotation() && !clazz.isArray();
    }

    public boolean isNullType() {
        return false;
    }

    public boolean isPrimitive() {
        return clazz.isPrimitive();
    }

    public boolean equivalent(JavaTypeDefinition def) {
        // TODO: JavaTypeDefinition generic equality
        return clazz.equals(def.getType()) && getTypeParameterCount() == def.getTypeParameterCount();
    }

    public boolean hasSameErasureAs(JavaTypeDefinition def) {
        return clazz == def.getType();
    }

    public int getTypeParameterCount() {
        return typeParameterCount;
    }

    public boolean isArrayType() {
        return clazz.isArray();
    }

    @Override
    public String toString() {
        return new StringBuilder("JavaTypeDefinition [clazz=").append(clazz)
                .append(", definitionType=").append(getDefinitionType())
                .append(", genericArgs=").append(genericArgs)
                .append(", isGeneric=").append(isGeneric)
                .append(", lowerBound=").append(lowerBound)
                .append("]\n").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JavaTypeDefinitionSimple)) {
            return false;
        }

        // raw vs raw
        // we assume that this covers raw types, because they are cached
        if (this == obj) {
            return true;
        }

        JavaTypeDefinitionSimple otherTypeDef = (JavaTypeDefinitionSimple) obj;

        if (getDefinitionType() != otherTypeDef.getDefinitionType()) {
            return false;
        }

        if (isLowerBound() && !lowerBound.equals(otherTypeDef.lowerBound)) {
            return false;
        } else {
            // This should cover
            // raw vs proper
            // proper vs raw
            // proper vs proper

            if (clazz != otherTypeDef.clazz) {
                return false;
            }

            for (int i = 0; i < getTypeParameterCount(); ++i) {
                // Note: we assume that cycles can only exist because of raw types
                if (!getGenericType(i).equals(otherTypeDef.getGenericType(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    @Override
    public Set<JavaTypeDefinition> getSuperTypeSet() {
        return getSuperTypeSet(new HashSet<JavaTypeDefinition>());
    }

    @Override
    protected Set<JavaTypeDefinition> getSuperTypeSet(Set<JavaTypeDefinition> destinationSet) {
        destinationSet.add(this);

        if (this.clazz != Object.class) {

            resolveTypeDefinition(clazz.getGenericSuperclass()).getSuperTypeSet(destinationSet);

            for (Type type : clazz.getGenericInterfaces()) {
                resolveTypeDefinition(type).getSuperTypeSet(destinationSet);
            }
        }

        return destinationSet;
    }

    public Set<Class<?>> getErasedSuperTypeSet() {
        Set<Class<?>> result = new HashSet<>();
        result.add(Object.class);
        return getErasedSuperTypeSet(this.clazz, result);
    }

    private static Set<Class<?>> getErasedSuperTypeSet(Class<?> clazz, Set<Class<?>> destinationSet) {
        if (clazz != null) {
            destinationSet.add(clazz);
            getErasedSuperTypeSet(clazz.getSuperclass(), destinationSet);

            for (Class<?> interfaceType : clazz.getInterfaces()) {
                getErasedSuperTypeSet(interfaceType, destinationSet);
            }
        }

        return destinationSet;
    }

    public JavaTypeDefinition getAsSuper(Class<?> superClazz) {
        if (clazz == superClazz) { // optimize for same class calls
            return this;
        }

        for (JavaTypeDefinition superTypeDef : getSuperTypeSet()) {
            if (superTypeDef.getType() == superClazz) {
                return superTypeDef;
            }
        }

        return null;
    }

    public boolean isExactType() {
        return getDefinitionType() == TypeDefinitionType.EXACT;
    }

    public boolean isUpperBound() {
        return getDefinitionType() == TypeDefinitionType.UPPER_BOUND
                // intersection types can only be upper bounds in java
                || getDefinitionType() == TypeDefinitionType.INTERSECTION;
    }

    public boolean isLowerBound() {
        return getDefinitionType() == TypeDefinitionType.LOWER_BOUND;
    }

    public boolean isIntersectionType() {
        return getDefinitionType() == TypeDefinitionType.INTERSECTION;
    }

    public JavaTypeDefinition getIntersectionType(int index) {
        throw new UnsupportedOperationException("Not an intersection type");
    }

    public int getIntersectionTypeCount() {
        throw new UnsupportedOperationException("Not an intersection type");
    }

    public JavaTypeDefinition getLowerBound() {
        if (!isLowerBound()) {
            throw new UnsupportedOperationException("Not a lower bound type");
        }

        return lowerBound;
    }
}
