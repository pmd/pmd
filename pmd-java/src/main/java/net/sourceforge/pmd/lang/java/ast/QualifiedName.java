/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents Qualified Names for use within PackageStats.
 * TODO:cf make unit tests once the visitor is working to ensure new implementations won't break it
 */
public class QualifiedName {

    /** See {@link QualifiedName#parseName(String)}. */
    public static final Pattern FORMAT = Pattern.compile("((\\w+\\.)+|\\.)((\\w+)(\\$\\w+)*)(#(\\w+)\\(((\\w+)(, \\w+)*)?\\))?");

    private String[] packages = null; // unnamed package
    private String[] classes = new String[1];
    private String operation = null;

    private QualifiedName() {

    }

    /**
     * Builds the qualified name of a method declaration.
     *
     * @param node The method declaration node
     *
     * @return The qualified name of the node
     */
    public static QualifiedName makeOperationOf(ASTMethodDeclaration node) {
        QualifiedName parentQname = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class).getQualifiedName();

        return makeOperationOf(parentQname,
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
    public static QualifiedName makeOperationOf(ASTConstructorDeclaration node) {
        ASTClassOrInterfaceDeclaration parent = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);

        return makeOperationOf(parent.getQualifiedName(),
                               parent.getImage(),
                               node.getFirstDescendantOfType(ASTFormalParameters.class));
    }


    /** Factorises the functionality of makeOperationof() */
    private static QualifiedName makeOperationOf(QualifiedName parent, String opName, ASTFormalParameters params) {
        QualifiedName qname = new QualifiedName();

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
    public static QualifiedName makeNestedClassOf(QualifiedName parent, String className) {
        QualifiedName qname = new QualifiedName();
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
    public static QualifiedName makeOuterClassOf(ASTClassOrInterfaceDeclaration node) {
        ASTPackageDeclaration pkg = node.getFirstParentOfType(ASTCompilationUnit.class)
                                        .getFirstChildOfType(ASTPackageDeclaration.class);

        QualifiedName qname = new QualifiedName();
        qname.packages = pkg == null ? null : pkg.getPackageNameImage().split("\\.");
        qname.classes[0] = node.getImage();

        return qname;
    }


    // Might be useful with type resolution
    public static QualifiedName makeClassOf(Class<?> clazz) {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses a qualified name given in the format defined for this implementation. The format
     * is described as the following regex pattern :
     *
     * <p>{@code ((\w+\.)+|\.)((\w+)(\$\w+)*)(#(\w+)\(((\w+)(, \w+)*)?\))?}
     *
     * <p>Notes:
     * <ul>
     * <li> Group 1 : dot separated packages, or just dot if unnamed package;
     * <li> Group 5 : nested classes are separated by a dollar symbol;
     * <li> Group 6 : the optional method suffix is separated from the class with a hashtag;
     * <li> Group 8 : method arguments. Note the presence of a single space after commas.
     * </ul>
     *
     * <p>The pattern is available as a static class member.
     *
     * @param name The name to parse.
     *
     * @return A qualified name instance corresponding to the parsed string.
     */
    public static QualifiedName parseName(String name) {
        QualifiedName qname = new QualifiedName();

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

    /**
     * Returns true if the resource addressed by this qualified name is a class.
     *
     * @return true if the resource addressed by this qualified name is a class.
     */
    public boolean isClass() {
        return classes[0] != null && operation == null;
    }

    /**
     * Returns true if the resource addressed by this qualified name is an operation.
     *
     * @return true if the resource addressed by this qualified name is an operation.
     */
    public boolean isOperation() {
        return operation != null;
    }

    /** Returns the packages. @return The packages. */
    public String[] getPackages() {
        return packages;
    }

    /** Returns the classes. @return The classes. */
    public String[] getClasses() {
        return classes;
    }

    /** Returns the operation string. @return The operation string. */
    public String getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QualifiedName that = (QualifiedName) o;

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
