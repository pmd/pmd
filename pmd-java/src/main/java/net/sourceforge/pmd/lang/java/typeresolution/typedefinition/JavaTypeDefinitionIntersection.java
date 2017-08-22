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

public class JavaTypeDefinitionIntersection extends JavaTypeDefinition {
    private List<JavaTypeDefinition> intersectionTypes;

    protected JavaTypeDefinitionIntersection(TypeDefinitionType defType, List<JavaTypeDefinition> intersectionTypes) {
        super(defType);

        if (intersectionTypes.isEmpty()) {
            throw new IllegalArgumentException("Intersection type list can't be empty");
        }

        this.intersectionTypes = Collections.unmodifiableList(new ArrayList<>(intersectionTypes));
    }

    @Override
    public Class<?> getType() {
        return intersectionTypes.get(0).getType();
    }

    @Override
    public JavaTypeDefinition getEnclosingClass() {
        return intersectionTypes.get(0).getEnclosingClass();
    }

    @Override
    public boolean isGeneric() {
        return intersectionTypes.get(0).isGeneric();
    }

    @Override
    public JavaTypeDefinition getGenericType(String parameterName) {
        return intersectionTypes.get(0).getGenericType(parameterName);
    }

    @Override
    public JavaTypeDefinition getGenericType(int index) {
        return intersectionTypes.get(0).getGenericType(index);
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(Type type) {
        return intersectionTypes.get(0).resolveTypeDefinition(type);
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(Type type, Method method, List<JavaTypeDefinition> methodTypeArgs) {
        return intersectionTypes.get(0).resolveTypeDefinition(type, method, methodTypeArgs);
    }

    @Override
    public JavaTypeDefinition getComponentType() {
        return intersectionTypes.get(0).getComponentType();
    }

    @Override
    public boolean isClassOrInterface() {
        return intersectionTypes.get(0).isClassOrInterface();
    }

    @Override
    public boolean isNullType() {
        return intersectionTypes.get(0).isNullType();
    }

    @Override
    public boolean isPrimitive() {
        return intersectionTypes.get(0).isPrimitive();
    }

    @Override
    public boolean hasSameErasureAs(JavaTypeDefinition def) {
        return intersectionTypes.get(0).hasSameErasureAs(def);
    }

    @Override
    public int getTypeParameterCount() {
        return intersectionTypes.get(0).getTypeParameterCount();
    }

    @Override
    public boolean isArrayType() {
        return intersectionTypes.get(0).isArrayType();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append("JavaTypeDefinition ")
                .append(getDefinitionType().toString())
                .append(" [")
                .append(intersectionTypes.get(0));
        for (int index = 1; index < intersectionTypes.size(); ++index) {
            builder.append(" && ");
            builder.append(intersectionTypes.get(index));
        }
        return builder.append("]").toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JavaTypeDefinitionIntersection)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        JavaTypeDefinitionIntersection otherTypeDef = (JavaTypeDefinitionIntersection) obj;

        if (otherTypeDef.getJavaTypeCount() != getJavaTypeCount()
                || getDefinitionType() != otherTypeDef.getDefinitionType()) {
            return false;
        }

        // we assume that the intersectionTypes list cannot contain duplicates, then indeed, this will prove equality
        outer:
        for (JavaTypeDefinition intersectionTypeDef : intersectionTypes) {
            for (JavaTypeDefinition otherIntersectionTypeDef : otherTypeDef.intersectionTypes) {
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

        for (JavaTypeDefinition typeDef : intersectionTypes) {
            result *= typeDef.hashCode();
        }

        return result;
    }

    @Override
    public Set<JavaTypeDefinition> getSuperTypeSet() {
        return intersectionTypes.get(0).getSuperTypeSet();
    }

    @Override
    protected Set<JavaTypeDefinition> getSuperTypeSet(Set<JavaTypeDefinition> destinationSet) {
        return intersectionTypes.get(0).getSuperTypeSet(destinationSet);
    }

    @Override
    public Set<Class<?>> getErasedSuperTypeSet() {
        return intersectionTypes.get(0).getErasedSuperTypeSet();
    }

    @Override
    public JavaTypeDefinition getAsSuper(Class<?> superClazz) {
        return intersectionTypes.get(0).getAsSuper(superClazz);
    }

    @Override
    public JavaTypeDefinition getJavaType(int index) {
        return intersectionTypes.get(0);
    }

    @Override
    public int getJavaTypeCount() {
        return intersectionTypes.size();
    }
}
