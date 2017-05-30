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

    public QualifiedName() {
    }

    /** Builds a QName for an operation using the QName of the enclosing class */
    public static QualifiedName makeOperationOf(QualifiedName parentClass, String operationName, String[] paramTypes) {
        QualifiedName qname = new QualifiedName();
        qname.packages = parentClass.packages;
        qname.classes = parentClass.classes;

        qname.operation = getOperationName(operationName, paramTypes);
        return qname;
    }

    /** Builds a nested class QName using the QName of its immediate parent */
    public static QualifiedName makeClassOf(QualifiedName parent, String className) {
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

    // Might be useful with type resolution
    public static QualifiedName parseCanonicalName(String canon) {
        throw new UnsupportedOperationException();
    }

    /** Returns a normalized method name (not Java-canonical!) */
    private static String getOperationName(String methodName, String[] paramTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName);
        sb.append('(');
        int last = paramTypes.length - 1;
        for (int i = 0; i < last; i++) {
            sb.append(paramTypes[i]);
            sb.append(',');
        }

        if (last > -1) {
            sb.append(paramTypes[last]);
        }

        sb.append(')');

        return sb.toString();
    }

    /** Sets the class to the specified name, truncates the array to length of one */
    public void setClass(String className) {
        if (classes.length == 1) {
            classes[0] = className;
            return;
        }

        classes = new String[]{className};

    }

    public String[] getPackages() {
        return packages;
    }

    public void setPackages(String[] packs) {
        packages = packs;
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
