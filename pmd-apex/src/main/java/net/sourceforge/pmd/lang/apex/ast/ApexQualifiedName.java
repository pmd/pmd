/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Qualified name of an apex class or method.
 *
 * @author Clément Fournier
 */
public final class ApexQualifiedName {
    private static final Pattern QUALIFIED_NAME_PATTERN = Pattern.compile("(?<class1>\\w+)(?:.(?<class2>\\w+))?(?:#(?<operation>\\w+\\(.*\\)))?");

    private final String[] classes;
    private final String operation;


    private ApexQualifiedName(String[] classes, String operation) {
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

        return new ApexQualifiedName(this.classes, null);
    }


    @Override
    public int hashCode() {
        int result = Arrays.hashCode(classes);
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        return obj instanceof ApexQualifiedName
               && Objects.deepEquals(classes, ((ApexQualifiedName) obj).classes)
               && Objects.equals(operation, ((ApexQualifiedName) obj).operation);

    }

    /**
     * Parses a string conforming to the format defined below and returns an ApexQualifiedName.
     *
     * <p>Here are some examples of the format:
     * <ul>
     * <li> {@code OuterClass.InnerClass}: name of an inner class
     * <li> {@code Class#method(String, int)}: name of an operation
     * </ul>
     *
     * @param toParse The string to parse
     *
     * @return An ApexQualifiedName, or null if the string couldn't be parsed
     */
    public static ApexQualifiedName ofString(String toParse) {
        Matcher matcher = QUALIFIED_NAME_PATTERN.matcher(toParse);
        if (matcher.matches()) {
            List<String> classNames = new ArrayList<>();
            classNames.add(matcher.group("class1"));
            if (matcher.group("class2") != null) {
                classNames.add(matcher.group("class2"));
            }
            return new ApexQualifiedName(classNames.toArray(new String[0]), matcher.group("operation"));
        }
        return null;
    }


    static ApexQualifiedName ofOuterClass(ASTUserClassOrInterface<?> astUserClass) {
        String[] classes = {astUserClass.getSimpleName()};
        return new ApexQualifiedName(classes, null);
    }


    static ApexQualifiedName ofNestedClass(ApexQualifiedName parent, ASTUserClassOrInterface<?> astUserClass) {

        String[] classes = Arrays.copyOf(parent.classes, parent.classes.length + 1);
        classes[classes.length - 1] = astUserClass.getSimpleName();
        return new ApexQualifiedName(classes, null);
    }


    static ApexQualifiedName ofOuterEnum(ASTUserEnum astUserEnum) {
        String[] classes = {astUserEnum.getSimpleName()};
        return new ApexQualifiedName(classes, null);
    }


    static ApexQualifiedName ofNestedEnum(ApexQualifiedName parent, ASTUserEnum astUserEnum) {
        String[] classes = Arrays.copyOf(parent.classes, parent.classes.length + 1);
        classes[classes.length - 1] = astUserEnum.getSimpleName();
        return new ApexQualifiedName(classes, null);
    }

    /**
     * Returns the method operation string.
     *
     * This includes type arguments for the parameter types.
     * If the parameters are primitive types, their case will be normalized.
     */
    private static String getOperationString(ASTMethod node) {
        StringBuilder sb = new StringBuilder();
        sb.append(node.getImage()).append('(');

        List<String> paramTypes = node.children(ASTParameter.class).toStream()
            .map(ASTParameter::getType)
            .collect(Collectors.toList());

        if (!paramTypes.isEmpty()) {
            for (int i = 0; i < paramTypes.size(); i++) {
                sb.append(i > 0 ? ", " : "");
                sb.append(paramTypes.get(i));
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

            return new ApexQualifiedName(baseName.classes, getOperationString(node));
        }

        ASTUserClassOrInterface<?> parent = node.ancestors(ASTUserClassOrInterface.class).firstOrThrow();
        if (parent instanceof ASTUserTrigger) {
            ASTUserTrigger trigger = (ASTUserTrigger) parent;
            String targetObj = trigger.getTargetName();

            return new ApexQualifiedName(new String[]{"trigger", targetObj}, trigger.getSimpleName()); // uses a reserved word as a class name to prevent clashes

        } else {
            ApexQualifiedName baseName = parent.getQualifiedName();

            return new ApexQualifiedName(baseName.classes, getOperationString(node));
        }
    }
}
