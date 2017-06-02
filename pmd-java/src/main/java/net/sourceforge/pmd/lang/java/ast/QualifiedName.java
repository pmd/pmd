/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;

/**
 * Represents Qualified Names for use within PackageStats
 * TODO make unit tests once the visitor is working to ensure new implementations won't break it
 */
public class QualifiedName {
    private String[] packages = null; // unnamed package
    private String[] classes = new String[1];
    private String operation = null;

    private QualifiedName() {
    }

    /**
     * Builds the qualified name of a method declaration
     *
     * @param node The method declaration node
     *
     * @return The qualified name of the node
     */
    public static QualifiedName makeOperationOf(ASTMethodDeclaration node) {
        QualifiedName parentQname = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class).getQualifiedName();
        QualifiedName qname = new QualifiedName();

        qname.packages = parentQname.packages;
        qname.classes = parentQname.classes;
        qname.operation = getOperationName(node.getMethodName(), node.getFirstDescendantOfType(ASTFormalParameters.class));

        return qname;
    }

    /**
     * Builds the qualified name of a constructor declaration
     *
     * @param node The constructor declaration node
     *
     * @return The qualified name of the node
     */
    public static QualifiedName makeOperationOf(ASTConstructorDeclaration node) {
        ASTClassOrInterfaceDeclaration parent = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        QualifiedName qname = new QualifiedName();
        QualifiedName parentQName = parent.getQualifiedName();

        qname.packages = parentQName.packages;
        qname.classes = parentQName.classes;
        qname.operation = getOperationName(parent.getImage(), node.getFirstDescendantOfType(ASTFormalParameters.class));

        return qname;
    }


    /**
     * Builds a nested class QName using the QName of its immediate parent
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
     * Builds the QName of an outer (not nested) class.
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
    public static QualifiedName parseCanonicalName(String canon) {
        throw new UnsupportedOperationException();
    }

    /** Returns a normalized method name (not Java-canonical!) */
    private static String getOperationName(String methodName, ASTFormalParameters params) {

        StringBuilder sb = new StringBuilder();
        sb.append(methodName);
        sb.append('(');

        int last = params.getParameterCount() - 1;
        for (int i = 0; i < last; i++) {
            // append type image of param
            sb.append(params.jjtGetChild(i).getFirstDescendantOfType(ASTType.class).getTypeImage());
            sb.append(',');
        }

        if (last > -1) {
            sb.append(params.jjtGetChild(last).getFirstDescendantOfType(ASTType.class).getTypeImage());
        }

        sb.append(')');

        return sb.toString();
    }


    public String[] getPackages() {
        return packages;
    }

    public String[] getClasses() {
        return classes;
    }

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
