/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

/**
 * This is really just a POJO.
 */
public class MethodType {
    private final JavaTypeDefinition returnType;
    private final List<JavaTypeDefinition> argTypes;
    private final Method method;

    public MethodType(JavaTypeDefinition returnType, List<JavaTypeDefinition> argTypes, Method method) {
        this.returnType = returnType;
        this.argTypes = Collections.unmodifiableList(argTypes);
        this.method = method;
    }

    public JavaTypeDefinition getReturnType() {
        return returnType;
    }

    public List<JavaTypeDefinition> getParameterTypes() {
        return argTypes;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isVararg() {
        return method.isVarArgs();
    }

    public JavaTypeDefinition getVarargComponentType() {
        if (!isVararg()) {
            throw new IllegalStateException("Method is not vararg: " + method.toString() + "!");
        }

        return argTypes.get(argTypes.size() - 1).getComponentType();
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(method.getModifiers());
    }

    public JavaTypeDefinition getArgTypeIncludingVararg(int index) {
        if (index < argTypes.size() - 1) {
            return argTypes.get(index);
        } else {
            return getVarargComponentType();
        }
    }

    @Override
    public String toString() {
        return new StringBuilder("MethodType [method=").append(method)
                .append(", returnType=").append(returnType)
                .append(", argTypes=").append(argTypes)
                .append(']')
                .toString();
    }
}
