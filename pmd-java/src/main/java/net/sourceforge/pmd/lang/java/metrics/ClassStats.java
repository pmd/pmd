/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class ClassStats {

    public class MethodHeader {
        public static final byte PACKAGE   = 1;
        public static final byte PUBLIC    = 2;
        public static final byte PRIVATE   = 3;
        public static final byte PROTECTED = 4;

        public final byte        visibility;
        public final boolean     isGetterOrSetter;
        public final boolean     isConstructor;
        public final boolean     isAbstract;

        public MethodHeader(byte visibility, boolean isGetterOrSetter, boolean isConstructor, boolean isAbstract) {
            this.visibility = visibility;
            this.isGetterOrSetter = isGetterOrSetter;
            this.isConstructor = isConstructor;
            this.isAbstract = isAbstract;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MethodHeader) {
                // TODO
                return true;
            }
            return false;
        }
    }
    
    private Map<MethodHeader, List<String>> m_methods = new HashMap<>();
    
}
