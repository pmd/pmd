/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaTypeDefinition implements TypeDefinition {
    // contains TypeDefs where only the clazz field is used
    private static final Map<Class<?>, JavaTypeDefinition> CLASS_TYPE_DEF_CACHE = new HashMap<>();

    private final Class<?> clazz;
    private final List<JavaTypeDefinition> genericArgs;
    private final boolean isGeneric;
    
    private JavaTypeDefinition(final Class<?> clazz) {
        this.clazz = clazz;

        final TypeVariable<?>[] typeParameters;
        // the anonymous class can't have generics, but we may be binding generics from super classes
        if (clazz.isAnonymousClass()) {
            // is this an anonymous class based on an interface or a class?
            if (clazz.getSuperclass() == Object.class) {
                // is this based off an interface?
                if (clazz.getInterfaces().length != 0) {
                    typeParameters = clazz.getInterfaces()[0].getTypeParameters();
                } else {
                    // This guy is just doing new Object() { ... }
                    typeParameters = clazz.getTypeParameters();
                }
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

        // We can only cache types without generics, since their values are context-based
        if (!newDef.isGeneric) {
            CLASS_TYPE_DEF_CACHE.put(clazz, newDef);
        }
        
        return newDef;
    }
    
    public static JavaTypeDefinition forClass(final Class<?> clazz, final JavaTypeDefinition... boundGenerics) {
        if (clazz == null) {
            return null;
        }
        
        // With generics there is no cache
        final JavaTypeDefinition typeDef = new JavaTypeDefinition(clazz);

        for (final JavaTypeDefinition generic : boundGenerics) {
            typeDef.genericArgs.add(generic);
        }
        
        return typeDef;
    }
    
    @Override
    public Class<?> getType() {
        return clazz;
    }
    
    public boolean isGeneric() {
        return !genericArgs.isEmpty();
    }

    public JavaTypeDefinition getGenericType(final String parameterName) {
        final TypeVariable<?>[] typeParameters = clazz.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            if (typeParameters[i].getName().equals(parameterName)) {
                return getGenericType(i);
            }
        }
        
        throw new IllegalArgumentException("No generic parameter by name " + parameterName
                + " on class " + clazz.getSimpleName());
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
                genericBounds[i] = resolveTypeDefinition(typeArguments[i]);
            }
            
            // TODO : is this cast safe?
            return forClass((Class<?>) parameterizedType.getRawType(), genericBounds);
        } else if (type instanceof TypeVariable) {
            return getGenericType(((TypeVariable<?>) type).getName());
        } else if (type instanceof WildcardType) {
            final Type[] wildcardUpperBounds = ((WildcardType) type).getUpperBounds();
            if (wildcardUpperBounds.length != 0) { // upper bound wildcard
                return resolveTypeDefinition(wildcardUpperBounds[0]);
            } else { // lower bound wildcard
                return forClass(Object.class);
            }
        }

        // TODO : Shall we throw here?
        return forClass(Object.class);
    }
}
