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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/* default */ class JavaTypeDefinitionSimple extends JavaTypeDefinition {
    private final Class<?> clazz;
    private final JavaTypeDefinition[] genericArgs;
    // cached because calling clazz.getTypeParameters().length create a new array every time
    private final int typeParameterCount;
    private final boolean isGeneric;
    private final boolean isRawType;
    private final JavaTypeDefinition enclosingClass;

    private static final Logger LOG = Logger.getLogger(JavaTypeDefinitionSimple.class.getName());
    private static final JavaTypeDefinition[] NO_GENERICS = {};

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
            // Generics will be lazily loaded if not already known
            this.genericArgs = Arrays.copyOf(boundGenerics, typeParameterCount);
        } else {
            this.genericArgs = NO_GENERICS;
        }

        Class<?> enclosing = null;
        try {
            enclosing = clazz.getEnclosingClass();
        } catch (LinkageError e) {
            if (LOG.isLoggable(Level.WARNING)) {
                LOG.log(Level.WARNING, "Could not load enclosing class of " + clazz.getName() + ", due to: " + e);
            }
        }
        enclosingClass = forClass(enclosing);
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
        return isGeneric;
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
            builder.append(currTypeDef.getType().getSimpleName());
        }

        LOG.log(Level.FINE, builder.toString());
        // TODO: throw eventually
        //throw new IllegalArgumentException(builder.toString());
        return forClass(Object.class);
    }

    @Override
    public JavaTypeDefinition getGenericType(final int index) {
        // Check if it has been lazily initialized first
        final JavaTypeDefinition cachedDefinition = genericArgs[index];
        if (cachedDefinition != null) {
            return cachedDefinition;
        }

        /*
         * Set a default to circuit-break any recursions (ie: raw types with no generic info)
         * Object.class is a right answer in those scenarios
         */
        genericArgs[index] = forClass(Object.class);

        final TypeVariable<?> typeVariable = clazz.getTypeParameters()[index];
        final JavaTypeDefinition typeDefinition = resolveTypeDefinition(typeVariable.getBounds()[0]);

        // cache result
        genericArgs[index] = typeDefinition;
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


    @Override
    public boolean isArrayType() {
        return clazz.isArray();
    }


    // TODO: are generics okay like this?
    @Override
    public JavaTypeDefinition getComponentType() {
        Class<?> componentType = getType().getComponentType();

        if (componentType == null) {
            throw new IllegalStateException(getType().getSimpleName() + " is not an array type!");
        }

        return forClass(componentType);
    }


    private Class<?> getElementTypeRec(Class<?> arrayType) {
        return arrayType.isArray() ? getElementTypeRec(arrayType.getComponentType()) : arrayType;
    }


    @Override
    public JavaTypeDefinition getElementType() {
        return isArrayType() ? forClass(getElementTypeRec(getType())) : this;
    }


    @Override
    public JavaTypeDefinition withDimensions(int numDimensions) {
        if (numDimensions < 0) {
            throw new IllegalArgumentException("Negative array dimension");
        }
        return numDimensions == 0
                ? this
                : forClass(Array.newInstance(getType(), (int[]) Array.newInstance(int.class, numDimensions)).getClass());
    }

    @Override
    public boolean isClassOrInterface() {
        return !clazz.isEnum() && !clazz.isPrimitive() && !clazz.isAnnotation() && !clazz.isArray();
    }

    @Override
    public boolean isNullType() {
        return false;
    }

    @Override
    public boolean isPrimitive() {
        return clazz.isPrimitive();
    }

    public boolean equivalent(JavaTypeDefinition def) {
        // TODO: JavaTypeDefinition generic equality
        return clazz.equals(def.getType()) && getTypeParameterCount() == def.getTypeParameterCount();
    }

    @Override
    public boolean hasSameErasureAs(JavaTypeDefinition def) {
        return clazz == def.getType();
    }

    @Override
    public int getTypeParameterCount() {
        return typeParameterCount;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JavaTypeDefinition [clazz=").append(clazz)
                .append(", definitionType=").append(getDefinitionType())
                .append(", genericArgs=[");

        // Forcefully resolve all generic types
        for (int i = 0; i < genericArgs.length; i++) {
            getGenericType(i);
        }

        for (final JavaTypeDefinition jtd : genericArgs) {
            sb.append(jtd.shallowString()).append(", ");
        }

        if (genericArgs.length != 0) {
            sb.replace(sb.length() - 3, sb.length() - 1, "");   // remove last comma
        }

        return sb.append("], isGeneric=").append(isGeneric)
            .append("]\n").toString();
    }

    @Override
    public String shallowString() {
        return new StringBuilder("JavaTypeDefinition [clazz=").append(clazz)
                .append(", definitionType=").append(getDefinitionType())
                .append(", isGeneric=").append(isGeneric)
                .append("]\n").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JavaTypeDefinitionSimple)) {
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

    @Override
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


    @Override
    public JavaTypeDefinition getAsSuper(Class<?> superClazz) {
        if (Objects.equals(clazz, superClazz)) { // optimize for same class calls
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
