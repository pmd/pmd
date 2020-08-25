/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.apache.commons.lang3.NotImplementedException;

/**
 *
 */
public final class ClassNamesUtil {

    private ClassNamesUtil() {
        // utility class
    }


    public static String getTypeDescriptor(Class<?> klass) {
        if (klass.isPrimitive()) {
            throw new NotImplementedException("Doesn't handle primitive types");
        } else if (klass.isArray()) {
            return "[" + getTypeDescriptor(klass.getComponentType());
        } else {
            return "L" + getInternalName(klass) + ";";
        }
    }

    public static String getInternalName(Class<?> klass) {
        return klass.getName().replace('.', '/');
    }

}
