/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.asm;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import net.sourceforge.pmd.dcd.ClassLoaderUtil;

public class TypeSignatureVisitor extends SignatureVisitor {

    private static final boolean TRACE = false;

    private static final int NO_TYPE = 0;

    private static final int FIELD_TYPE = 1;

    private static final int RETURN_TYPE = 2;

    private static final int PARAMETER_TYPE = 3;

    // The type of the current Type
    private int typeType;

    // The current Type identified.
    private Class<?> type;

    // The number of dimensions on an array for the current Type.
    private int arrayDimensions = 0;

    // Completed Field Type is stored here
    private Class<?> fieldType;

    // Completed Return Type is stored here
    private Class<?> returnType;

    // Completed Parameter Types are stored here
    private List<Class<?>> parameterTypes = new ArrayList<>(0);

    private final PrintVisitor p;

    public TypeSignatureVisitor() {
        super(Opcodes.ASM5);
        p = new PrintVisitor();
        init();
    }

    public TypeSignatureVisitor(PrintVisitor parent) {
        super(Opcodes.ASM5);
        p = new PrintVisitor(parent);
        init();
    }

    protected void println(String s) {
        p.println(s);
    }

    protected void printlnIndent(String s) {
        p.printlnIndent(s);
    }

    public void init() {
        typeType = FIELD_TYPE;
        type = null;
        arrayDimensions = 0;
        parameterTypes.clear();
    }

    public Class<?> getFieldType() {
        popType();
        if (fieldType == null) {
            throw new RuntimeException();
        }
        return fieldType;
    }

    public Class<?> getMethodReturnType() {
        popType();
        if (returnType == null) {
            throw new RuntimeException();
        }
        return returnType;
    }

    public Class<?>[] getMethodParameterTypes() {
        popType();
        if (parameterTypes == null) {
            throw new RuntimeException();
        }
        if (parameterTypes != null) {
            return parameterTypes.toArray(new Class<?>[parameterTypes.size()]);
        } else {
            return null;
        }
    }

    private void pushType(int type) {
        this.typeType = type;
    }

    private void popType() {
        switch (typeType) {
            case NO_TYPE:
                break;
            case FIELD_TYPE:
                fieldType = getType();
                break;
            case RETURN_TYPE:
                returnType = getType();
                break;
            case PARAMETER_TYPE:
                parameterTypes.add(getType());
                break;
            default:
                throw new RuntimeException("Unknown type type: " + typeType);
        }

        typeType = NO_TYPE;
        type = null;
        arrayDimensions = 0;
    }

    private Class<?> getType() {
        Class<?> type = null;
        if (this.type != null) {
            type = this.type;
            for (int i = 0; i < arrayDimensions; i++) {
                // Is there another way to get Array Classes?
                Object array = Array.newInstance(type, 0);
                type = array.getClass();
            }
        }
        return type;
    }

    @Override
    public SignatureVisitor visitArrayType() {
        if (TRACE) {
            println("visitArrayType:");
        }
        arrayDimensions++;
        return this;
    }

    @Override
    public void visitBaseType(char descriptor) {
        if (TRACE) {
            println("visitBaseType:");
            printlnIndent("descriptor: " + descriptor);
        }
        switch (descriptor) {
            case 'B':
                type = Byte.TYPE;
                break;
            case 'C':
                type = Character.TYPE;
                break;
            case 'D':
                type = Double.TYPE;
                break;
            case 'F':
                type = Float.TYPE;
                break;
            case 'I':
                type = Integer.TYPE;
                break;
            case 'J':
                type = Long.TYPE;
                break;
            case 'S':
                type = Short.TYPE;
                break;
            case 'Z':
                type = Boolean.TYPE;
                break;
            case 'V':
                type = Void.TYPE;
                break;
            default:
                throw new RuntimeException("Unknown baseType descriptor: " + descriptor);
        }
    }

    @Override
    public SignatureVisitor visitClassBound() {
        if (TRACE) {
            println("visitClassBound:");
        }
        return this;
    }

    @Override
    public void visitClassType(String name) {
        if (TRACE) {
            println("visitClassType:");
            printlnIndent("name: " + name);
        }
        name = ClassLoaderUtil.fromInternalForm(name);
        this.type = ClassLoaderUtil.getClass(name);
    }

    @Override
    public void visitEnd() {
        if (TRACE) {
            println("visitEnd:");
        }
        popType();
    }

    @Override
    public SignatureVisitor visitExceptionType() {
        if (TRACE) {
            println("visitExceptionType:");
        }
        return this;
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        if (TRACE) {
            println("visitFormalTypeParameter:");
            printlnIndent("name: " + name);
        }
    }

    @Override
    public void visitInnerClassType(String name) {
        if (TRACE) {
            println("visitInnerClassType:");
            printlnIndent("name: " + name);
        }
    }

    @Override
    public SignatureVisitor visitInterface() {
        if (TRACE) {
            println("visitInterface:");
        }
        return this;
    }

    @Override
    public SignatureVisitor visitInterfaceBound() {
        if (TRACE) {
            println("visitInterfaceBound:");
        }
        return this;
    }

    @Override
    public SignatureVisitor visitParameterType() {
        if (TRACE) {
            println("visitParameterType:");
        }
        popType();
        pushType(PARAMETER_TYPE);
        return this;
    }

    @Override
    public SignatureVisitor visitReturnType() {
        if (TRACE) {
            println("visitReturnType:");
        }
        popType();
        pushType(RETURN_TYPE);
        return this;
    }

    @Override
    public SignatureVisitor visitSuperclass() {
        if (TRACE) {
            println("visitSuperclass:");
        }
        return this;
    }

    @Override
    public void visitTypeArgument() {
        if (TRACE) {
            println("visitTypeArgument:");
        }
    }

    @Override
    public SignatureVisitor visitTypeArgument(char wildcard) {
        if (TRACE) {
            println("visitTypeArgument:");
            printlnIndent("wildcard: " + wildcard);
        }
        return this;
    }

    @Override
    public void visitTypeVariable(String name) {
        if (TRACE) {
            println("visitTypeVariable:");
            printlnIndent("name: " + name);
        }
    }
}
