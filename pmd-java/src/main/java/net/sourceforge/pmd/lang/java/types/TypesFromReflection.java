/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader;

final class TypesFromReflection {

    // note: in the new type resolution for 7.0, this class has other
    // utilities to parse a JTypeMirror from a j.l.reflect.Type, and
    // is public.

    private TypesFromReflection() {

    }


    /** Maps names of primitives to their corresponding primitive {@code Class}es. */
    private static final Map<String, Class<?>> PRIMITIVES_BY_NAME = new HashMap<>();


    static {
        PRIMITIVES_BY_NAME.put("boolean", Boolean.TYPE);
        PRIMITIVES_BY_NAME.put("byte", Byte.TYPE);
        PRIMITIVES_BY_NAME.put("char", Character.TYPE);
        PRIMITIVES_BY_NAME.put("short", Short.TYPE);
        PRIMITIVES_BY_NAME.put("int", Integer.TYPE);
        PRIMITIVES_BY_NAME.put("long", Long.TYPE);
        PRIMITIVES_BY_NAME.put("double", Double.TYPE);
        PRIMITIVES_BY_NAME.put("float", Float.TYPE);
        PRIMITIVES_BY_NAME.put("void", Void.TYPE);
    }


    /**
     * Load a class. Supports loading array types like 'java.lang.String[]' and
     * converting a canonical name to a binary name (eg 'java.util.Map.Entry' ->
     * 'java.util.Map$Entry').
     */
    public static Class<?> loadClass(NullableClassLoader ctr, String className) {
        return loadClassMaybeArray(ctr, StringUtils.deleteWhitespace(className));
    }

    private static Class<?> loadClassFromCanonicalName(NullableClassLoader ctr, String className) {
        Class<?> clazz = PRIMITIVES_BY_NAME.get(className);
        if (clazz == null) {
            clazz = ctr.loadClassOrNull(className);
        }
        if (clazz != null) {
            return clazz;
        }
        // allow path separators (.) as inner class name separators
        final int lastDotIndex = className.lastIndexOf('.');

        if (lastDotIndex >= 0) {
            String asInner = className.substring(0, lastDotIndex)
                + '$' + className.substring(lastDotIndex + 1);
            return loadClassFromCanonicalName(ctr, asInner);
        }
        return null;
    }


    private static Class<?> loadClassMaybeArray(NullableClassLoader classLoader,
                                                String className) {
        Validate.notNull(className, "className must not be null.");
        if (className.endsWith("[]")) {
            int dimension = 0;
            int i = className.length();
            while (i >= 2 && className.startsWith("[]", i - 2)) {
                dimension++;
                i -= 2;
            }

            checkJavaIdent(className, i);
            String elementName = className.substring(0, i);

            Class<?> elementType = loadClassFromCanonicalName(classLoader, elementName);
            if (elementType == null) {
                return null;
            }

            return Array.newInstance(elementType, (int[]) Array.newInstance(int.class, dimension)).getClass();
        } else {
            checkJavaIdent(className, className.length());
            return loadClassFromCanonicalName(classLoader, className);
        }
    }

    private static IllegalArgumentException invalidClassName(String className) {
        return new IllegalArgumentException("Not a valid class name \"" + className + "\"");
    }

    private static void checkJavaIdent(String className, int endOffsetExclusive) {
        if (endOffsetExclusive <= 0 || !Character.isJavaIdentifierStart(className.charAt(0))) {
            throw invalidClassName(className);
        }

        for (int i = 1; i < endOffsetExclusive; i++) {
            char c = className.charAt(i);
            if (!(Character.isJavaIdentifierPart(c) || c == '.')) {
                throw invalidClassName(className);
            }
        }
    }


}
