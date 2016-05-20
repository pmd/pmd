/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Various class-related utility methods intended for mapping common java.lang types to their short 
 * short forms allowing end users to enter these names in UIs without the package prefixes.
 *
 * @author Brian Remedios
 */
public final class ClassUtil {

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    @SuppressWarnings("PMD.AvoidUsingShortType")
    private static final TypeMap PRIMITIVE_TYPE_NAMES = new TypeMap(new Class[] { int.class, byte.class, long.class,
            short.class, float.class, double.class, char.class, boolean.class, });

    private static final TypeMap TYPES_BY_NAME = new TypeMap(new Class[] { Integer.class, Byte.class, Long.class,
            Short.class, Float.class, Double.class, Character.class, Boolean.class, BigDecimal.class, String.class,
            Object.class, Class.class});

    private static final Map<Class<?>, String> SHORT_NAMES_BY_TYPE = computeClassShortNames();
    
    private ClassUtil() {
    }

    /**
     * Returns the type(class) for the name specified or null if not found.
     *
     * @param name String
     * @return Class
     */
    public static Class<?> getPrimitiveTypeFor(String name) {
        return PRIMITIVE_TYPE_NAMES.typeFor(name);
    }

    /**
     * Return a map of all the short names of classes we maintain mappings for.
     * The names are keyed by the classes themselves.
     *
     * @return Map<Class, String>
     */
    private static Map<Class<?>, String> computeClassShortNames() {
        
        Map<Class<?>, String> map = new HashMap<>();
        map.putAll(PRIMITIVE_TYPE_NAMES.asInverseWithShortName());
        map.putAll(TYPES_BY_NAME.asInverseWithShortName());
        return map;
    }

    public static Map<Class<?>, String> getClassShortNames() {
        return SHORT_NAMES_BY_TYPE;
    }
    
    /**
     * Attempt to determine the actual class given the short name.
     *
     * @param shortName String
     * @return Class
     */
    public static Class<?> getTypeFor(String shortName) {
        Class<?> type = TYPES_BY_NAME.typeFor(shortName);
        if (type != null) {
            return type;
        }

        type = PRIMITIVE_TYPE_NAMES.typeFor(shortName);
        if (type != null) {
            return type;
        }

        return CollectionUtil.getCollectionTypeFor(shortName);
    }

    /**
     * Return the name of the type in its short form if its known to us
     * otherwise return its name fully packaged.
     * 
     * @param type
     * @return String
     */
    public static String asShortestName(Class<?> type) {
        
        String name = SHORT_NAMES_BY_TYPE.get(type);
        return name == null ? type.getName() : name;
    }
    
    /**
     * Returns the abbreviated name of the type, without the package name
     *
     * @param fullTypeName
     * @return String
     */

    public static String withoutPackageName(String fullTypeName) {
        int dotPos = fullTypeName.lastIndexOf('.');
        return dotPos > 0 ? fullTypeName.substring(dotPos + 1) : fullTypeName;
    }

    /**
     * Attempts to return the specified method from the class provided but will
     * walk up its superclasses until it finds a match. Returns null if it
     * doesn't.
     *
     * @param clasz		 Class
     * @param methodName String
     * @param paramTypes Class[]
     * @return Method
     */
    public static Method methodFor(Class<?> clasz, String methodName, Class<?>[] paramTypes) {
        Method method = null;
        Class<?> current = clasz;
        while (current != Object.class) {
            try {
                method = current.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException ex) {
                current = current.getSuperclass();
            }
            if (method != null) {
                return method;
            }
        }
        return null;
    }
    
    /**
     * Return the methods as a map keyed by their common declaration types.
     * 
     * @param methods
     * @return Map<String, List<Method>>
     */
    public static Map<String, List<Method>> asMethodGroupsByTypeName(Method[] methods) {
        
        Map<String, List<Method>> methodGroups = new HashMap<>(methods.length);
        
        for (int i=0; i<methods.length; i++) {
            String clsName = asShortestName(methods[i].getDeclaringClass());
            if (!methodGroups.containsKey(clsName)) {
                methodGroups.put(clsName, new ArrayList<Method>());
            }
            methodGroups.get(clsName).add(methods[i]);
        }
        return methodGroups;
    }
}
