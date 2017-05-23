/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;

/**
 * @author Clément Fournier
 */
public interface QualifiableNode {

    char CLASS_DELIMITER = '$';
    char METHOD_DELIMITER = '#';
    char NESTED_CLASS_DELIMITER = ':';
    char LEFT_PARAM_DELIMITER = '(';
    char RIGHT_PARAM_DELIMITER = ')';
    char PARAMLIST_DELIMITER = ',';
    char PACKAGE_DELIMITER = '.';

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
            return operation == null;
        }

        public void setClass(String className) {
            classes[0] = className;
        }

        public boolean isOperation() {
            return !isClass();
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
            sb.append(LEFT_PARAM_DELIMITER);
            int last = paramTypes.length - 1;
            for (int i = 0; i < last; i++) {
                sb.append(paramTypes[i]);
                sb.append(PARAMLIST_DELIMITER);
            }

            if (last > -1) {
                sb.append(paramTypes[last]);
            }

            sb.append(RIGHT_PARAM_DELIMITER);

            this.operation = sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            if (packages != null) {
                int last = packages.length - 1;
                for (int i = 0; i < last; i++) {
                    sb.append(packages[i]);
                    sb.append(PACKAGE_DELIMITER);
                }

                sb.append(packages[last]);
            }
            sb.append(CLASS_DELIMITER); // class delimiter is there anyway

            int last = classes.length - 1;
            for (int i = 0; i < last; i++) {
                sb.append(classes[i]);
                sb.append(NESTED_CLASS_DELIMITER);
            }

            sb.append(classes[last]);

            if (operation != null) {
                sb.append(METHOD_DELIMITER);
                sb.append(operation);
            }

            return sb.toString();
        }

    }
}
