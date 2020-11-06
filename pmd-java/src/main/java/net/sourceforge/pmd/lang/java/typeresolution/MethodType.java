/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

/**
 * This is really just a POJO.
 */
@Deprecated
@InternalApi
public final class MethodType {
    private final JavaTypeDefinition returnType;
    private final List<JavaTypeDefinition> argTypes;
    private final Method method;

    private MethodType(JavaTypeDefinition returnType, List<JavaTypeDefinition> argTypes, Method method) {
        this.returnType = returnType;
        this.argTypes = argTypes;
        this.method = method;
    }

    /**
     * @return An unparameterized MethodType
     */
    public static MethodType build(Method method) {
        return new MethodType(null, null, method);
    }

    public static MethodType build(JavaTypeDefinition returnType, List<JavaTypeDefinition> argTypes, Method method) {
        return new MethodType(returnType, Collections.unmodifiableList(argTypes), method);
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

    public boolean isParameterized() {
        return returnType != null && argTypes != null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((argTypes == null) ? 0 : argTypes.hashCode());
        // note: only taking the method's name
        result = prime * result + ((method == null) ? 0 : method.getName().hashCode());
        result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MethodType other = (MethodType) obj;
        if (argTypes == null) {
            if (other.argTypes != null) {
                return false;
            }
        } else if (!argTypes.equals(other.argTypes)) {
            return false;
        }
        if (method == null) {
            if (other.method != null) {
                return false;
            }
        } else if (!method.getName().equals(other.method.getName())) {
            // note: only comparing the method's name
            return false;
        }
        if (returnType == null) {
            if (other.returnType != null) {
                return false;
            }
        } else if (!returnType.equals(other.returnType)) {
            return false;
        }
        return true;
    }
}
