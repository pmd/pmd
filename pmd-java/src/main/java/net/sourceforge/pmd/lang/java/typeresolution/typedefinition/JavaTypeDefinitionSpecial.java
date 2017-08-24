/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/* default */ class JavaTypeDefinitionSpecial extends JavaTypeDefinition {
    private List<JavaTypeDefinition> typeList;

    protected JavaTypeDefinitionSpecial(TypeDefinitionType defType, List<JavaTypeDefinition> typeList) {
        super(defType);

        if (typeList.isEmpty()) {
            throw new IllegalArgumentException("Intersection type list can't be empty");
        }

        this.typeList = Collections.unmodifiableList(new ArrayList<>(typeList));
    }

    /**
     * All the calls to this method are to delegate JavaTypeDefinition method calls to the first
     * JavaTypeDefinition in the 'typeList' list.
     */
    private JavaTypeDefinition firstJavaType() {
        return typeList.get(0);
    }

    @Override
    public Class<?> getType() {
        return firstJavaType().getType();
    }

    @Override
    public JavaTypeDefinition getEnclosingClass() {
        return firstJavaType().getEnclosingClass();
    }

    @Override
    public boolean isGeneric() {
        return firstJavaType().isGeneric();
    }

    @Override
    public JavaTypeDefinition getGenericType(String parameterName) {
        return firstJavaType().getGenericType(parameterName);
    }

    @Override
    public JavaTypeDefinition getGenericType(int index) {
        return firstJavaType().getGenericType(index);
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(Type type) {
        return firstJavaType().resolveTypeDefinition(type);
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(Type type, Method method, List<JavaTypeDefinition> methodTypeArgs) {
        return firstJavaType().resolveTypeDefinition(type, method, methodTypeArgs);
    }

    @Override
    public JavaTypeDefinition getComponentType() {
        return firstJavaType().getComponentType();
    }

    @Override
    public boolean isClassOrInterface() {
        return firstJavaType().isClassOrInterface();
    }

    @Override
    public boolean isNullType() {
        return firstJavaType().isNullType();
    }

    @Override
    public boolean isPrimitive() {
        return firstJavaType().isPrimitive();
    }

    @Override
    public boolean hasSameErasureAs(JavaTypeDefinition def) {
        return firstJavaType().hasSameErasureAs(def);
    }

    @Override
    public int getTypeParameterCount() {
        return firstJavaType().getTypeParameterCount();
    }

    @Override
    public boolean isArrayType() {
        return firstJavaType().isArrayType();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("JavaTypeDefinition ")
                .append(getDefinitionType().toString())
                .append(" [")
                .append(firstJavaType());
        for (int index = 1; index < typeList.size(); ++index) {
            builder.append(" && ");
            builder.append(typeList.get(index));
        }
        return builder.append("]").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JavaTypeDefinitionSpecial)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        JavaTypeDefinitionSpecial otherTypeDef = (JavaTypeDefinitionSpecial) obj;

        if (otherTypeDef.getJavaTypeCount() != getJavaTypeCount()
                || getDefinitionType() != otherTypeDef.getDefinitionType()) {
            return false;
        }

        // we assume that the typeList list cannot contain duplicates, then indeed, this will prove equality
        outer:
        for (JavaTypeDefinition intersectionTypeDef : typeList) {
            for (JavaTypeDefinition otherIntersectionTypeDef : otherTypeDef.typeList) {
                if (intersectionTypeDef.equals(otherIntersectionTypeDef)) {
                    continue outer;
                }
            }

            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 31;

        for (JavaTypeDefinition typeDef : typeList) {
            result *= typeDef.hashCode();
        }

        return result;
    }

    @Override
    public Set<JavaTypeDefinition> getSuperTypeSet() {
        return firstJavaType().getSuperTypeSet();
    }

    @Override
    protected Set<JavaTypeDefinition> getSuperTypeSet(Set<JavaTypeDefinition> destinationSet) {
        return firstJavaType().getSuperTypeSet(destinationSet);
    }

    @Override
    public Set<Class<?>> getErasedSuperTypeSet() {
        return firstJavaType().getErasedSuperTypeSet();
    }

    @Override
    public JavaTypeDefinition getAsSuper(Class<?> superClazz) {
        return firstJavaType().getAsSuper(superClazz);
    }

    @Override
    public JavaTypeDefinition getJavaType(int index) {
        return firstJavaType();
    }

    @Override
    public int getJavaTypeCount() {
        return typeList.size();
    }

    @Override
    public boolean isRawType() {
        return typeList.size() == 1 && firstJavaType().isRawType();
    }

    @Override
    public boolean isIntersectionType() {
        return typeList.size() == 1;
    }
}
