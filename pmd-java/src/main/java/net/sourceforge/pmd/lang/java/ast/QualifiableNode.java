/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Arrays;

/**
 * @author Clément Fournier
 */
public interface QualifiableNode {

    // non canonical symbols but probably better for regex parsing if need be
    // anyway can be changed
    char LEFT_CLASS_SEP = '$';
    char METHOD_SEP = '#';
    char NESTED_CLASS_SEP = ':';
    char LEFT_PARAM_SEP = '(';
    char RIGHT_PARAM_SEP = ')';
    char PARAMLIST_SEP = ',';
    char PACKAGE_SEP = '.';
    char FIELD_SEP = '!';

    QualifiedName getQualifiedName();

    class QualifiedName {
        private String[] packages = null; // unnamed package
        private String[] classes = new String[1];
        /** Either method name or field name */
        private String suffix = null;
        private boolean isField = false;

        public QualifiedName() {
        }

        public static QualifiedName makeOperationOf(QualifiedName parentClass, String operationName,
                                                    String[] paramTypes) {
            QualifiedName qname = new QualifiedName();
            qname.packages = parentClass.packages;
            qname.classes = parentClass.classes;

            qname.suffix = getMethodNameOf(operationName, paramTypes);
            qname.isField = false;
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

        public static QualifiedName makeFieldOf(QualifiedName parentClass, String fieldName) {
            QualifiedName qname = new QualifiedName();
            qname.packages = parentClass.packages;
            qname.classes = parentClass.classes;
            qname.suffix = fieldName;
            qname.isField = true;
            return qname;
        }

        /**
         * Returns a normalized method name based on parameter types and method name.
         *
         * @param methodName Name of the method.
         * @param paramTypes Type of parameters.
         *
         * @return Normalized method name.
         */
        public static String getMethodNameOf(String methodName, String[] paramTypes) {
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

        public boolean isClass() {
            return classes[0] != null && suffix == null;
        }

        public void setClass(String className) {
            classes[0] = className;
        }

        public boolean isNestedClass() {
            return classes.length > 1 && suffix == null;
        }

        public boolean isOperation() {
            return suffix != null && !isField;
        }

        public boolean isField() {
            return suffix != null && isField;
        }

        public void setField(String fieldName) {
            suffix = fieldName;
            isField = true;
        }

        public String[] getPackages() {
            return packages;
        }

        public void setPackages(String[] packages) {
            this.packages = packages;
        }

        public String[] getClasses() {
            return classes;
        }

        public String getSuffix() {
            return suffix;
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

            if (suffix != null) {
                sb.append(isField ? FIELD_SEP : METHOD_SEP);
                sb.append(suffix);
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
            if (suffix != null)
                hash += suffix.hashCode();
            return hash;
        }
    }
}
