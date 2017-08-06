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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaTypeDefinition implements TypeDefinition {
    // contains TypeDefs where only the clazz field is used
    private static final Map<Class<?>, JavaTypeDefinition> CLASS_TYPE_DEF_CACHE = new HashMap<>();

    private final Class<?> clazz;
    private final List<JavaTypeDefinition> genericArgs;
    // cached because calling clazz.getTypeParameters().length create a new array every time
    private final int typeParameterCount;
    private final boolean isGeneric;
    private final JavaTypeDefinition enclosingClass;

    private JavaTypeDefinition(final Class<?> clazz) {
        this.clazz = clazz;
        this.typeParameterCount = clazz.getTypeParameters().length;

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

        isGeneric = typeParameters.length != 0;
        if (isGeneric) {
            // Generics will be lazily loaded
            this.genericArgs = new ArrayList<JavaTypeDefinition>(typeParameters.length);
        } else {
            this.genericArgs = Collections.emptyList();
        }

        enclosingClass = forClass(clazz.getEnclosingClass());
    }

    public static JavaTypeDefinition forClass(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        final JavaTypeDefinition typeDef = CLASS_TYPE_DEF_CACHE.get(clazz);

        if (typeDef != null) {
            return typeDef;
        }

        final JavaTypeDefinition newDef = new JavaTypeDefinition(clazz);

        CLASS_TYPE_DEF_CACHE.put(clazz, newDef);

        return newDef;
    }

    public static JavaTypeDefinition forClass(final Class<?> clazz, final JavaTypeDefinition... boundGenerics) {
        if (clazz == null) {
            return null;
        }

        // With generics there is no cache
        final JavaTypeDefinition typeDef = new JavaTypeDefinition(clazz);

        Collections.addAll(typeDef.genericArgs, boundGenerics);

        return typeDef;
    }

    @Override
    public Class<?> getType() {
        return clazz;
    }

    public boolean isGeneric() {
        return !genericArgs.isEmpty();
    }

    private int getGenericTypeIndex(TypeVariable<?>[] typeParameters, final String parameterName) {
        for (int i = 0; i < typeParameters.length; i++) {
            if (typeParameters[i].getName().equals(parameterName)) {
                return i;
            }
        }

        return -1;
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

    public JavaTypeDefinition getGenericType(final String parameterName) {
        for (JavaTypeDefinition currTypeDef = this; currTypeDef != null; currTypeDef = currTypeDef.enclosingClass) {
            int paramIndex = getGenericTypeIndex(currTypeDef.clazz.getTypeParameters(), parameterName);
            if (paramIndex != -1) {
                return currTypeDef.getGenericType(paramIndex);
            }
        }

        // throw because we could not find parameterName
        StringBuilder builder = new StringBuilder("No generic parameter by name ").append(parameterName);
        for (JavaTypeDefinition currTypeDef = this; currTypeDef != null; currTypeDef = currTypeDef.enclosingClass) {
            builder.append("\n on class ");
            builder.append(clazz.getSimpleName());
        }

        throw new IllegalArgumentException(builder.toString());
    }

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

    public JavaTypeDefinition resolveTypeDefinition(final Type type) {
        return resolveTypeDefinition(type, null, null);
    }

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
        return clazz.equals(def.clazz) && getTypeParameterCount() == def.getTypeParameterCount();
    }

    public boolean hasSameErasureAs(JavaTypeDefinition def) {
        return clazz == def.clazz;
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
                .append(", genericArgs=").append(genericArgs)
                .append(", isGeneric=").append(isGeneric)
                .append(']').toString();

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JavaTypeDefinition)) {
            return false;
        }

        // raw vs raw
        // we assume that this covers raw types, because they are cached
        if (this == obj) {
            return true;
        }

        JavaTypeDefinition otherTypeDef = (JavaTypeDefinition) obj;

        if (clazz != otherTypeDef.clazz) {
            return false;
        }


        // This should cover
        // raw vs proper
        // proper vs raw
        // proper vs proper

        // Note: we have to force raw types to compute their generic args, class Stuff<? extends List<Stuff>>
        // Stuff a;
        // Stuff<? extends List<Stuff>> b;
        // Stuff<List<Stuff>> c;
        // all of the above should be equal

        for (int i = 0; i < getTypeParameterCount(); ++i) {
            // Note: we assume that cycles can only exist because of raw types
            if (!getGenericType(i).equals(otherTypeDef.getGenericType(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return clazz.hashCode();
    }

    public Set<JavaTypeDefinition> getSuperTypeSet() {
        return getSuperTypeSet(new HashSet<JavaTypeDefinition>());
    }

    private Set<JavaTypeDefinition> getSuperTypeSet(Set<JavaTypeDefinition> destinationSet) {
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

            for(Class<?> interfaceType : clazz.getInterfaces()) {
                getErasedSuperTypeSet(interfaceType, destinationSet);
            }
        }

        return destinationSet;
    }
}
