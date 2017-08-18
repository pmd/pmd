package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition.TypeDefinitionType
        .INTERSECTION;

public class JavaTypeDefinitionIntersection extends JavaTypeDefinition {
    private List<JavaTypeDefinition> intersectionTypes;

    protected JavaTypeDefinitionIntersection(List<JavaTypeDefinition> intersectionTypes) {
        super(INTERSECTION);

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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isGeneric() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaTypeDefinition getGenericType(String parameterName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaTypeDefinition getGenericType(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(Type type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaTypeDefinition resolveTypeDefinition(Type type, Method method, List<JavaTypeDefinition> methodTypeArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaTypeDefinition getComponentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isClassOrInterface() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNullType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPrimitive() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSameErasureAs(JavaTypeDefinition def) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getTypeParameterCount() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isArrayType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Intersection type [");
        builder.append(intersectionTypes.get(0));
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

        if(otherTypeDef.getIntersectionTypeCount() != getIntersectionTypeCount()) {
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
        throw new UnsupportedOperationException("");
    }

    @Override
    protected Set<JavaTypeDefinition> getSuperTypeSet(Set<JavaTypeDefinition> destinationSet) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public Set<Class<?>> getErasedSuperTypeSet() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public boolean isRawType() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public JavaTypeDefinition getAsSuper(Class<?> superClazz) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public JavaTypeDefinition getIntersectionType(int index) {
        return intersectionTypes.get(index);
    }

    @Override
    public int getIntersectionTypeCount() {
        return intersectionTypes.size();
    }

    @Override
    public JavaTypeDefinition getLowerBound() {
        throw new UnsupportedOperationException("Not a lower bound");
    }
}
