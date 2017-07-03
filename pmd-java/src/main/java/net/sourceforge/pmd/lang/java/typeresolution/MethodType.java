package net.sourceforge.pmd.lang.java.typeresolution;


import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This is really just a POJO.
 */

public class MethodType {
    private JavaTypeDefinition returnType;
    private List<JavaTypeDefinition> argTypes;
    private Method method;

    public MethodType(JavaTypeDefinition returnType, List<JavaTypeDefinition> argTypes, Method method) {
        this.returnType = returnType;
        this.argTypes = argTypes;
        this.method = method;
    }

    public JavaTypeDefinition getReturnType() {
        return returnType;
    }

    public List<JavaTypeDefinition> getArgTypes() {
        return argTypes;
    }

    public Method getMethod() {
        return method;
    }
}
