/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import apex.jorje.semantic.symbol.type.TypeInfo;


/**
 * Qualified name of an apex class or method.
 *
 * @author Clément Fournier
 */
public final class ApexQualifiedName {


    private final String nameSpace;
    private final String[] classes;
    private final String operation;


    private ApexQualifiedName(String nameSpace, String[] classes, String operation) {
        this.nameSpace = nameSpace;
        this.operation = operation;
        this.classes = classes;
    }


    public String getOperation() {
        return operation;
    }


    public String[] getClasses() {
        return Arrays.copyOf(classes, classes.length);
    }


    /**
     * Gets the namespace prefix of this resource.
     *
     * @return The namespace prefix
     */
    public String getNameSpace() {
        return nameSpace;
    }


    /**
     * Returns true if the resource addressed by this qualified name is a class.
     *
     * @return true if the resource addressed by this qualified name is a class.
     */
    public boolean isClass() {
        return operation == null;
    }


    /**
     * Returns true if the resource addressed by this qualified name is an operation.
     *
     * @return true if the resource addressed by this qualified name is an operation.
     */
    public boolean isOperation() {
        return operation != null;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nameSpace).append("__");
        sb.append(classes[0]);

        if (classes.length > 1) {
            sb.append('.').append(classes[1]);
        }

        if (isOperation()) {
            sb.append("#").append(operation);
        }

        return sb.toString();
    }


    /**
     * Returns the qualified name of the class the resource is located in. If this instance addresses a class, returns
     * this instance.
     *
     * @return The qualified name of the class
     */
    public ApexQualifiedName getClassName() {
        if (isClass()) {
            return this;
        }

        return new ApexQualifiedName(this.nameSpace, this.classes, null);
    }


    @Override
    public int hashCode() {
        int result = nameSpace.hashCode();
        result = 31 * result + Arrays.hashCode(classes);
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof ApexQualifiedName
               && Objects.deepEquals(classes, ((ApexQualifiedName) obj).classes)
               && Objects.equals(operation, ((ApexQualifiedName) obj).operation)
               && Objects.equals(nameSpace, ((ApexQualifiedName) obj).nameSpace);

    }


    /**
     * Parses a string conforming to the format defined below and returns an ApexQualifiedName.
     *
     * <p>Here are some examples of the format:
     * <ul>
     * <li> {@code namespace__OuterClass.InnerClass}: name of an inner class
     * <li> {@code namespace__Class#method(String, int)}: name of an operation
     * </ul>
     *
     * @param toParse The string to parse
     *
     * @return An ApexQualifiedName, or null if the string couldn't be parsed
     */
    // private static final Pattern FORMAT = Pattern.compile("(\\w+)__(\\w+)(.(\\w+))?(#(\\w+))?"); // TODO
    public static ApexQualifiedName ofString(String toParse) {
        throw new UnsupportedOperationException();
    }


    static ApexQualifiedName ofOuterClass(ASTUserClassOrInterface<?> astUserClass) {
        String ns = astUserClass.getNamespace();
        String[] classes = {astUserClass.getImage()};
        return new ApexQualifiedName(StringUtils.isEmpty(ns) ? "c" : ns, classes, null);
    }


    static ApexQualifiedName ofNestedClass(ApexQualifiedName parent, ASTUserClassOrInterface<?> astUserClass) {

        String[] classes = Arrays.copyOf(parent.classes, parent.classes.length + 1);
        classes[classes.length - 1] = astUserClass.getImage();
        return new ApexQualifiedName(parent.nameSpace, classes, null);
    }


    static ApexQualifiedName ofOuterEnum(ASTUserEnum astUserEnum) {
        String ns = astUserEnum.getNamespace();
        String[] classes = {astUserEnum.getImage()};
        return new ApexQualifiedName(StringUtils.isEmpty(ns) ? "c" : ns, classes, null);
    }


    static ApexQualifiedName ofNestedEnum(ApexQualifiedName parent, ASTUserEnum astUserEnum) {
        String[] classes = Arrays.copyOf(parent.classes, parent.classes.length + 1);
        classes[classes.length - 1] = astUserEnum.getImage();
        return new ApexQualifiedName(parent.nameSpace, classes, null);
    }


    private static String getOperationString(ASTMethod node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getImage()).append('(');


        List<TypeInfo> paramTypes = node.node.getMethodInfo().getParameterTypes();

        if (!paramTypes.isEmpty()) {
            sb.append(paramTypes.get(0).getApexName());

            for (int i = 1; i < paramTypes.size(); i++) {
                sb.append(", ").append(paramTypes.get(i).getApexName());
            }

        }

        sb.append(')');

        return sb.toString();
    }


    static ApexQualifiedName ofMethod(ASTMethod node) {
        // Check first, as enum must be innermost potential parent
        ASTUserEnum enumParent = node.ancestors(ASTUserEnum.class).first();
        if (enumParent != null) {
            ApexQualifiedName baseName = enumParent.getQualifiedName();

            return new ApexQualifiedName(baseName.nameSpace, baseName.classes, getOperationString(node));
        }

        ASTUserClassOrInterface<?> parent = node.ancestors(ASTUserClassOrInterface.class).firstOrThrow();
        if (parent instanceof ASTUserTrigger) {
            ASTUserTrigger trigger = (ASTUserTrigger) parent;
            String ns = trigger.getNamespace();
            String targetObj = trigger.getTargetName();

            return new ApexQualifiedName(StringUtils.isEmpty(ns) ? "c" : ns, new String[]{"trigger", targetObj}, trigger.getImage()); // uses a reserved word as a class name to prevent clashes

        } else {
            ApexQualifiedName baseName = parent.getQualifiedName();

            return new ApexQualifiedName(baseName.nameSpace, baseName.classes, getOperationString(node));
        }
    }
}
