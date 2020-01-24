/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import org.apache.commons.lang3.ClassUtils;

import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;

public final class TypeHelper {

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
        final Class<?> clazz = loadClassWithNodeClassloader(n, clazzName);

        if (clazz != null || n.getType() != null) {
            return isA(n, clazz);
        }

        return clazzName.equals(n.getImage()) || clazzName.endsWith("." + n.getImage());
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
        final Class<?> clazz = loadClassWithNodeClassloader(n, clazzName);

        if (clazz != null) {
            return n.getType() == clazz;
        }

        return clazzName.equals(n.getImage()) || clazzName.endsWith("." + n.getImage());
    }

    private static Class<?> loadClassWithNodeClassloader(final TypeNode n, final String clazzName) {
        if (n.getType() != null) {
            return loadClass(n.getType().getClassLoader(), clazzName);
        }

        return null;
    }

    private static Class<?> loadClass(final ClassLoader nullableClassLoader, final String clazzName) {
        try {
            ClassLoader classLoader = nullableClassLoader;
            if (classLoader == null) {
                // Using the system classloader then
                classLoader = ClassLoader.getSystemClassLoader();
            }

            // If the requested type is in the classpath, using the same classloader should work
            return ClassUtils.getClass(classLoader, clazzName);
        } catch (ClassNotFoundException ignored) {
            // The requested type is not on the auxclasspath. This might happen, if the type node
            // is probed for a specific type (e.g. is is a JUnit5 Test Annotation class).
            // Failing to resolve clazzName does not necessarily indicate an incomplete auxclasspath.
        } catch (final LinkageError expected) {
            // We found the class but it's invalid / incomplete. This may be an incomplete auxclasspath
            // if it was a NoClassDefFoundError. TODO : Report it?
        }

        return null;
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
        if (type == null || clazz == null) {
            return false; // If in auxclasspath, both should be resolvable, or are not the same
        }

        return clazz.isAssignableFrom(type);
    }

    public static boolean isA(TypedNameDeclaration vnd, String className) {
        Class<?> type = vnd.getType();
        if (type != null) {
            Class<?> clazz = loadClass(type.getClassLoader(), className);
            if (clazz != null) {
                return clazz.isAssignableFrom(type);
            }
        }
        return false;
    }
}
