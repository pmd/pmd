/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.QualifiedName;

/**
 * Represents Qualified Names for use within the java metrics framework.
 */
public final class JavaQualifiedName implements QualifiedName {

    /**
     * Pattern specifying the format.
     *
     * <p>{@code ((\w+\.)+|\.)((\w+)(\$\w+)*)(#(\w+)\(((\w+)(, \w+)*)?\))?}
     */
    public static final Pattern FORMAT = Pattern.compile("((\\w+\\.)+|\\.)((\\w+)(\\$\\w+)*)(#(\\w+)\\(((\\w+)(, \\w+)*)?\\))?");

    private String[] packages = null; // unnamed package
    private String[] classes = new String[1];
    private String operation = null;

    private JavaQualifiedName() {

    }

    /**
     * Builds the qualified name of a method declaration.
     *
     * @param node The method declaration node
     *
     * @return The qualified name of the node
     */
    /* default */ static JavaQualifiedName ofOperation(ASTMethodDeclaration node) {
        JavaQualifiedName parentQname = node.getFirstParentOfType(ASTAnyTypeDeclaration.class)
                                            .getQualifiedName();

        return ofOperation(parentQname,
                           node.getMethodName(),
                           node.getFirstDescendantOfType(ASTFormalParameters.class));
    }


    /**
     * Builds the qualified name of a constructor declaration.
     *
     * @param node The constructor declaration node
     *
     * @return The qualified name of the node
     */
    /* default */ static JavaQualifiedName ofOperation(ASTConstructorDeclaration node) {
        ASTAnyTypeDeclaration parent = node.getFirstParentOfType(ASTAnyTypeDeclaration.class);

        return ofOperation(parent.getQualifiedName(),
                           parent.getImage(),
                           node.getFirstDescendantOfType(ASTFormalParameters.class));
    }


    /** Factorises the functionality of makeOperationof() */
    private static JavaQualifiedName ofOperation(JavaQualifiedName parent, String opName, ASTFormalParameters params) {
        JavaQualifiedName qname = new JavaQualifiedName();

        qname.packages = parent.packages;
        qname.classes = parent.classes;
        qname.operation = getOperationName(opName, params);

        return qname;
    }


    /**
     * Builds the qualified name of a nested class using the qualified name of its immediate parent.
     *
     * @param parent    The qname of the immediate parent
     * @param className The name of the class
     *
     * @return The qualified name of the nested class
     */
    /* default */ static JavaQualifiedName ofNestedClass(JavaQualifiedName parent, String className) {
        JavaQualifiedName qname = new JavaQualifiedName();
        qname.packages = parent.packages;
        if (parent.classes[0] != null) {
            qname.classes = Arrays.copyOf(parent.classes, parent.classes.length + 1);
            qname.classes[parent.classes.length] = className;
        } else {
            qname.classes[0] = className;
        }

        return qname;
    }

    /**
     * Builds the qualified name of an outer (not nested) class.
     *
     * @param node The class node
     *
     * @return The qualified name of the node
     */
    /* default */ static JavaQualifiedName ofOuterClass(ASTAnyTypeDeclaration node) {
        ASTPackageDeclaration pkg = node.getFirstParentOfType(ASTCompilationUnit.class)
                                        .getFirstChildOfType(ASTPackageDeclaration.class);

        JavaQualifiedName qname = new JavaQualifiedName();
        qname.packages = pkg == null ? null : pkg.getPackageNameImage().split("\\.");
        qname.classes[0] = node.getImage();

        return qname;
    }


    // Might be useful with type resolution
    public static JavaQualifiedName ofClass(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses a qualified name given in the format defined for this implementation. The format
     * is specified by a regex pattern (see {@link JavaQualifiedName#FORMAT}). Examples:
     *
     * <p>{@code com.company.MyClass$Nested#myMethod(String, int)}
     * <ul>
     * <li> Packages are separated by full stops;
     * <li> Nested classes are separated by a dollar symbol;
     * <li> The optional method suffix is separated from the class with a hashtag;
     * <li> Method arguments are separated by a comma and a single space.
     * </ul>
     *
     * <p>{@code .MyClass$Nested}
     * <ul>
     * <li> A class in the unnamed package is preceded by a single full stop.
     * </ul>
     *
     * @param name The name to parse.
     *
     * @return A qualified name instance corresponding to the parsed string.
     */
    public static JavaQualifiedName ofString(String name) {
        JavaQualifiedName qname = new JavaQualifiedName();

        Matcher matcher = FORMAT.matcher(name);

        if (!matcher.matches()) {
            return null;
        }

        qname.packages = ".".equals(matcher.group(1)) ? null : matcher.group(1).split("\\.");
        qname.classes = matcher.group(3).split("\\$");
        qname.operation = matcher.group(6) == null ? null : matcher.group(6).substring(1);

        return qname;
    }

    /** Returns a normalized method name (not Java-canonical!). */
    private static String getOperationName(String methodName, ASTFormalParameters params) {

        StringBuilder sb = new StringBuilder();
        sb.append(methodName);
        sb.append('(');

        int last = params.getParameterCount() - 1;
        for (int i = 0; i < last; i++) {
            // append type image of param
            sb.append(params.jjtGetChild(i).getFirstDescendantOfType(ASTType.class).getTypeImage());
            sb.append(", ");
        }

        if (last > -1) {
            sb.append(params.jjtGetChild(last).getFirstDescendantOfType(ASTType.class).getTypeImage());
        }

        sb.append(')');

        return sb.toString();
    }


    @Override
    public boolean isClass() {
        return classes[0] != null && operation == null;
    }


    @Override
    public boolean isOperation() {
        return operation != null;
    }


    /**
     * Returns the packages. This is specific to Java's package structure.
     *
     * @return The packages.
     */
    public String[] getPackages() {
        return packages;
    }


    /**
     * Returns the class specific part of the name. It identifies a class in the namespace it's declared in. If the
     * class is nested inside another, then the array returned contains all enclosing classes in order.
     *
     * @return The class names array.
     */
    public String[] getClasses() {
        return classes;
    }


    /**
     * Returns the operation specific part of the name. It identifies an operation in its namespace.
     *
     * @return The operation string.
     */
    public String getOperation() {
        return operation;
    }


    @Override
    public JavaQualifiedName getClassName() {
        if (isClass()) {
            return this;
        }

        JavaQualifiedName qname = new JavaQualifiedName();
        qname.classes = this.classes;
        qname.packages = this.packages;
        return qname;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JavaQualifiedName that = (JavaQualifiedName) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(packages, that.packages)) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(classes, that.classes)) {
            return false;
        }
        return operation != null ? operation.equals(that.operation) : that.operation == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(packages);
        result = 31 * result + Arrays.hashCode(classes);
        result = 31 * result + (operation != null ? operation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (packages != null) {
            int last = packages.length - 1;
            for (int i = 0; i < last; i++) {
                sb.append(packages[i]);
                sb.append('.');
            }

            sb.append(packages[last]);
        }
        sb.append('.'); // this dot is there even if package is null

        int last = classes.length - 1;
        for (int i = 0; i < last; i++) {
            sb.append(classes[i]);
            sb.append('$');
        }

        sb.append(classes[last]);

        if (operation != null) {
            sb.append('#');
            sb.append(operation);
        }

        return sb.toString();
    }
}
