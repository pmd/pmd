/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;


import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.EXACT;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.LOWER_WILDCARD;
import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.UPPER_WILDCARD;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/* default */ class JavaTypeDefinitionSimple extends JavaTypeDefinition {
    private final Class<?> clazz;
    private final List<JavaTypeDefinition> genericArgs;
    // cached because calling clazz.getTypeParameters().length create a new array every time
    private final int typeParameterCount;
    private final boolean isGeneric;
    private final boolean isRawType;
    private final JavaTypeDefinition enclosingClass;

    private static final Logger LOG = Logger.getLogger(JavaTypeDefinitionSimple.class.getName());

    protected JavaTypeDefinitionSimple(Class<?> clazz, JavaTypeDefinition... boundGenerics) {
        super(EXACT);
        this.clazz = clazz;

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
        isRawType = isGeneric && boundGenerics.length == 0;

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
                                              List<JavaTypeDefinition> methodTypeArguments) {
        if (method != null && methodTypeArguments != null) {
            int paramIndex = getGenericTypeIndex(method.getTypeParameters(), parameterName);
            if (paramIndex != -1) {
                return methodTypeArguments.get(paramIndex);
            }
        }

        return getGenericType(parameterName);
    }

    @Override
    public JavaTypeDefinition getGenericType(final String parameterName) {
        for (JavaTypeDefinition currTypeDef = this; currTypeDef != null;
             currTypeDef = currTypeDef.getEnclosingClass()) {
            
            int paramIndex = getGenericTypeIndex(currTypeDef.getType().getTypeParameters(), parameterName);
            if (paramIndex != -1) {
                return currTypeDef.getGenericType(paramIndex);
            }
        }

        // throw because we could not find parameterName
        StringBuilder builder = new StringBuilder("No generic parameter by name ").append(parameterName);
        for (JavaTypeDefinition currTypeDef = this; currTypeDef != null;
             currTypeDef = currTypeDef.getEnclosingClass()) {
            
            builder.append("\n on class ");
            builder.append(clazz.getSimpleName());
        }

        LOG.log(Level.FINE, builder.toString());
        // TODO: throw eventually
        //throw new IllegalArgumentException(builder.toString());
        return null;
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
            final Type[] wildcardLowerBounds = ((WildcardType) type).getLowerBounds();

            if (wildcardLowerBounds.length != 0) { // lower bound wildcard
                return forClass(LOWER_WILDCARD, resolveTypeDefinition(wildcardLowerBounds[0], method, methodTypeArgs));
            } else { // upper bound wildcard
                final Type[] wildcardUpperBounds = ((WildcardType) type).getUpperBounds();
                return forClass(UPPER_WILDCARD, resolveTypeDefinition(wildcardUpperBounds[0], method, methodTypeArgs));
            }
        } else if (type instanceof GenericArrayType) {
            JavaTypeDefinition component = resolveTypeDefinition(((GenericArrayType) type).getGenericComponentType(), method, methodTypeArgs);
            // TODO: retain the generic types of the array component...
            return forClass(Array.newInstance(component.getType(), 0).getClass());
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

    @Override
    public JavaTypeDefinition getJavaType(int index) {
        if (index == 0) {
            return this;
        } else {
            throw new IllegalArgumentException("Not an intersection type!");
        }
    }

    @Override
    public int getJavaTypeCount() {
        return 1;
    }

    @Override
    public boolean isRawType() {
        return isRawType;
    }

    @Override
    public boolean isIntersectionType() {
        return false;
    }
}
