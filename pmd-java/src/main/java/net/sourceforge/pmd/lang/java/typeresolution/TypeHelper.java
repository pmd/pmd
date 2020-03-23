/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader.ClassLoaderWrapper;

public final class TypeHelper {


    /** Maps a primitive class name to its corresponding abbreviation used in array class names. */
    private static final Map<String, String> abbreviationMap;
    /** Maps names of primitives to their corresponding primitive {@code Class}es. */
    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();


    static {
        namePrimitiveMap.put("boolean", Boolean.TYPE);
        namePrimitiveMap.put("byte", Byte.TYPE);
        namePrimitiveMap.put("char", Character.TYPE);
        namePrimitiveMap.put("short", Short.TYPE);
        namePrimitiveMap.put("int", Integer.TYPE);
        namePrimitiveMap.put("long", Long.TYPE);
        namePrimitiveMap.put("double", Double.TYPE);
        namePrimitiveMap.put("float", Float.TYPE);
        namePrimitiveMap.put("void", Void.TYPE);
    }

    static {
        final Map<String, String> m = new HashMap<>();
        m.put("int", "I");
        m.put("boolean", "Z");
        m.put("float", "F");
        m.put("long", "J");
        m.put("short", "S");
        m.put("byte", "B");
        m.put("double", "D");
        m.put("char", "C");
        final Map<String, String> r = new HashMap<>();
        for (final Map.Entry<String, String> e : m.entrySet()) {
            r.put(e.getValue(), e.getKey());
        }
        abbreviationMap = Collections.unmodifiableMap(m);
    }


    private TypeHelper() {
        // utility class
    }

    /**
     * Checks whether the resolved type of the given {@link TypeNode} n is of the type
     * given by the clazzName. If the clazzName is on the auxclasspath, then also subclasses
     * are considered.
     *
     * <p>If clazzName is not on the auxclasspath (so it can't be resolved), then a string
     * comparison of the class names are performed. This might result in comparing only
     * the simple name of the classes.
     *
     * @param n the type node to check
     * @param clazzName the class name to compare to
     * @return <code>true</code> if type node n is of type clazzName or a subtype of clazzName
     */
    public static boolean isA(final TypeNode n, final String clazzName) {
        if (n.getType() != null && n.getType().isAnnotation()) {
            return isAnnotationSubtype(n, clazzName);
        }

        final Class<?> clazz = loadClassWithNodeClassloader(n, clazzName);

        if (clazz != null || n.getType() != null) {
            return isA(n, clazz);
        }

        return fallbackIsA(n, clazzName);
    }

    private static boolean isAnnotationSubtype(TypeNode n, String clazzName) {
        // then, the supertype may only be Object, j.l.Annotation, or the class name
        // this avoids classloading altogether
        // this is used e.g. by the typeIs function in XPath
        return clazzName.equals("java.lang.annotation.Annotation")
            || clazzName.equals("java.lang.Object")
            || clazzName.equals(n.getType().getName());
    }

    private static boolean fallbackIsA(TypeNode n, String clazzName) {
        if (clazzName.equals(n.getImage()) || clazzName.endsWith("." + n.getImage())) {
            return true;
        }

        if (n instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceType superClass = ((ASTClassOrInterfaceDeclaration) n).getSuperClassTypeNode();
            if (superClass != null) {
                return isA(superClass, clazzName);
            }

            for (ASTClassOrInterfaceType itf : ((ASTClassOrInterfaceDeclaration) n).getSuperInterfacesTypeNodes()) {
                if (isA(itf, clazzName)) {
                    return true;
                }
            }
        } else if (n instanceof ASTEnumDeclaration) {

            ASTImplementsList implemented = n.getFirstChildOfType(ASTImplementsList.class);
            if (implemented != null) {
                for (ASTClassOrInterfaceType itf : implemented) {
                    if (isA(itf, clazzName)) {
                        return true;
                    }
                }
            }

            return "java.lang.Enum".equals(clazzName)
                // supertypes of Enum
                || "java.lang.Comparable".equals(clazzName)
                || "java.io.Serializable".equals(clazzName);
        } else if (n instanceof ASTAnnotationTypeDeclaration) {
            return "java.lang.annotation.Annotation".equals(clazzName);
        }

        return false;
    }

    /**
     * Checks whether the resolved type of the given {@link TypeNode} n is exactly of the type
     * given by the clazzName.
     *
     * @param n the type node to check
     * @param clazzName the class name to compare to
     * @return <code>true</code> if type node n is exactly of type clazzName.
     */
    public static boolean isExactlyA(final TypeNode n, final String clazzName) {
        if (n.getType() != null && n.getType().getName().equals(clazzName)) {
            // fast path avoiding classloading
            return true;
        }

        final Class<?> clazz = loadClassWithNodeClassloader(n, clazzName);

        if (clazz != null) {
            return n.getType() == clazz;
        }

        return clazzName.equals(n.getImage()) || clazzName.endsWith("." + n.getImage());
    }

    private static Class<?> loadClassWithNodeClassloader(final TypeNode n, final String clazzName) {
        if (n.getType() != null) {
            return loadClass(n.getRoot().getClassTypeResolver(), clazzName);
        }

        return null;
    }

    private static Class<?> loadClass(NullableClassLoader ctr, String className) {
        Class<?> clazz;
        if (namePrimitiveMap.containsKey(className)) {
            clazz = namePrimitiveMap.get(className);
        } else {
            clazz = ctr.loadClassOrNull(toCanonicalName(className));
        }
        if (clazz != null) {
            return clazz;
        }

        // allow path separators (.) as inner class name separators
        final int lastDotIndex = className.lastIndexOf('.');

        if (lastDotIndex != -1) {
            String asInner = className.substring(0, lastDotIndex)
                + '$' + className.substring(lastDotIndex + 1);
            return loadClass(ctr, asInner);
        }
        return null;
    }


    private static String toCanonicalName(String className) {
        className = StringUtils.deleteWhitespace(className);
        Validate.notNull(className, "className must not be null.");
        if (className.endsWith("[]")) {
            final StringBuilder classNameBuffer = new StringBuilder();
            while (className.endsWith("[]")) {
                className = className.substring(0, className.length() - 2);
                classNameBuffer.append("[");
            }
            final String abbreviation = abbreviationMap.get(className);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(className).append(";");
            }
            className = classNameBuffer.toString();
        }
        return className;
    }


    /** @see #isA(TypeNode, String) */
    public static boolean isA(TypeNode n, Class<?> clazz) {
        return subclasses(n, clazz);
    }

    public static boolean isEither(TypeNode n, Class<?> class1, Class<?> class2) {
        return subclasses(n, class1) || subclasses(n, class2);
    }

    public static boolean isExactlyAny(TypedNameDeclaration vnd, Class<?>... clazzes) {
        Class<?> type = vnd.getType();
        for (final Class<?> clazz : clazzes) {
            if (type != null && type.equals(clazz) || type == null
                && (clazz.getSimpleName().equals(vnd.getTypeImage()) || clazz.getName().equals(vnd.getTypeImage()))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isExactlyNone(TypedNameDeclaration vnd, Class<?>... clazzes) {
        return !isExactlyAny(vnd, clazzes);
    }

    /**
     * @deprecated use {@link #isExactlyAny(TypedNameDeclaration, Class...)}
     */
    @Deprecated
    public static boolean isA(TypedNameDeclaration vnd, Class<?> clazz) {
        return isExactlyAny(vnd, clazz);
    }

    /**
     * @deprecated use {@link #isExactlyAny(TypedNameDeclaration, Class...)}
     */
    @Deprecated
    public static boolean isEither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
        return isExactlyAny(vnd, class1, class2);
    }

    /**
     * @deprecated use {@link #isExactlyNone(TypedNameDeclaration, Class...)}
     */
    @Deprecated
    public static boolean isNeither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
        return !isA(vnd, class1) && !isA(vnd, class2);
    }

    public static boolean subclasses(TypeNode n, Class<?> clazz) {
        Class<?> type = n.getType();
        if (clazz == null) {
            return false; // If in auxclasspath, both should be resolvable, or are not the same
        } else if (type == null) {
            return fallbackIsA(n, clazz.getName());
        }

        return clazz.isAssignableFrom(type);
    }

    public static boolean isA(TypedNameDeclaration vnd, String className) {
        Class<?> type = vnd.getType();
        if (type != null) {
            Class<?> clazz = loadClass(ClassLoaderWrapper.wrapNullable(type.getClassLoader()), className);
            if (clazz != null) {
                return clazz.isAssignableFrom(type);
            }
        }
        return false;
    }
}
