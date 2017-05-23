/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;

/**
 * Nodes that can be described with a qualified name.
 *
 * @author Clément Fournier
 */
public interface QualifiableNode {

    char LEFT_CLASS_SEP = '$';
    char METHOD_SEP = '#';
    char NESTED_CLASS_SEP = ':';
    char LEFT_PARAM_SEP = '(';
    char RIGHT_PARAM_SEP = ')';
    char PARAMLIST_SEP = ',';
    char PACKAGE_SEP = '.';

    /**
     * Returns a qualified name for this node.
     *
     * @return A qualified name.
     */
    QualifiedName getQualifiedName();

    /**
     * Represents Qualified Names for use within PackageStats
     * TODO make unit tests once the visitor is working to ensure new implementations won't break it
     */
    class QualifiedName {
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
        public static String getOperationName(String methodName, String[] paramTypes) {
            StringBuilder sb = new StringBuilder();
            sb.append(methodName);
            sb.append(LEFT_PARAM_SEP);
            int last = paramTypes.length - 1;
            for (int i = 0; i < last; i++) {
                sb.append(paramTypes[i]);
                sb.append(PARAMLIST_SEP);
            }

            if (last > -1) {
                sb.append(paramTypes[last]);
            }

            sb.append(RIGHT_PARAM_SEP);

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
        public String toString() {
            StringBuilder sb = new StringBuilder();

            if (packages != null) {
                int last = packages.length - 1;
                for (int i = 0; i < last; i++) {
                    sb.append(packages[i]);
                    sb.append(PACKAGE_SEP);
                }

                sb.append(packages[last]);
            }
            sb.append(LEFT_CLASS_SEP); // class delimiter is there even if package null

            int last = classes.length - 1;
            for (int i = 0; i < last; i++) {
                sb.append(classes[i]);
                sb.append(NESTED_CLASS_SEP);
            }

            sb.append(classes[last]);

            if (operation != null) {
                sb.append(METHOD_SEP);
                sb.append(operation);
            }

            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof QualifiedName) {
                return this.toString().equals(o.toString());
            } else if (o instanceof String) {
                return this.toString().equals(o);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 0;
            if (packages != null) {
                for (String p : packages) {
                    hash += p.hashCode();
                }
            }
            for (String p : classes) {
                if (p != null) {
                    hash += p.hashCode();
                }
            }
            if (operation != null)
                hash += operation.hashCode();
            return hash;
        }
    }
}
