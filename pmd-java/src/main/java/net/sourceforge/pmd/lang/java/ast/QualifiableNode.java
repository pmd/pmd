/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;

/**
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

    QualifiedName getQualifiedName();

    class QualifiedName {
        private String[] packages = null; // unnamed package
        private String[] classes = new String[1];
        private String operation = null;

        public QualifiedName() {
        }

        public static QualifiedName makeOperationOf(QualifiedName parentClass, String operationName, String[] paramTypes) {
            QualifiedName qname = new QualifiedName();
            qname.packages = parentClass.packages;
            qname.classes = parentClass.classes;

            qname.setOperation(operationName, paramTypes);
            return qname;
        }

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

        public boolean isClass() {
            return classes[0] != null && operation == null;
        }

        public void setClass(String className) {
            classes[0] = className;
        }

        public boolean isNestedClass() {
            return classes.length > 1 && operation == null;
        }

        public boolean isOperation() {
            return operation != null;
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

        public void setClasses(String[] classes) {
            this.classes = classes;
        }

        public String getOperation() {
            return operation;
        }


        public void setOperation(String methodName, String[] paramTypes) {
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

            this.operation = sb.toString();
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
            sb.append(LEFT_CLASS_SEP); // class delimiter is there anyway

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
