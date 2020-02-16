/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


/* default */ class JavaTypeDefinitionUpper extends JavaTypeDefinition {
    private final JavaTypeDefinition[] typeList;

    protected JavaTypeDefinitionUpper(TypeDefinitionType defType, JavaTypeDefinition... typeList) {
        super(defType);

        if (typeList.length == 0) {
            throw new IllegalArgumentException("Intersection type list can't be empty");
        }

        this.typeList = typeList;
    }


    /**
     * All the calls to this method are to delegate JavaTypeDefinition method calls to the first
     * JavaTypeDefinition in the 'typeList' list.
     */
    protected JavaTypeDefinition firstJavaType() {
        return typeList[0];
    }

    @Override
    public Class<?> getType() {
        if (firstJavaType() == null) {
            return null;
        }


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
    public JavaTypeDefinition getElementType() {
        return firstJavaType().getElementType();
    }


    @Override
    public JavaTypeDefinition withDimensions(int numDimensions) {
        return firstJavaType().withDimensions(numDimensions);
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
                .append(getDefinitionType())
                .append(" [")
                .append(typeList[0]);
        for (int index = 1; index < typeList.length; ++index) {
            builder.append(" && ")
                .append(typeList[index]);
        }
        return builder.append("]").toString();
    }

    @Override
    protected String shallowString() {
        return toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || !this.getClass().isInstance(obj)) {
            return false;
        }

        JavaTypeDefinitionUpper otherTypeDef = (JavaTypeDefinitionUpper) obj;

        if (otherTypeDef.getJavaTypeCount() != getJavaTypeCount()
                || getDefinitionType() != otherTypeDef.getDefinitionType()) {
            return false;
        }

        // we assume that the typeList list cannot contain duplicates, then indeed, this will prove equality
        return Arrays.deepEquals(typeList, otherTypeDef.typeList);
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
        return typeList.length;
    }

    @Override
    public boolean isRawType() {
        // with lower bounds the second part would always eval true,
        // because with lower bounds firstJavaType is Object.class
        return typeList.length == 1 && firstJavaType().isRawType();
    }

    @Override
    public boolean isIntersectionType() {
        return typeList.length > 1;
    }
}
