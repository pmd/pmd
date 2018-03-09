/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import net.sourceforge.pmd.lang.ast.Node;
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
     * @param n the type node to check
     * @param clazzName the class name to compare to
     * @return <code>true</code> if type node n is of type clazzName or a subtype of clazzName
     */
    public static boolean isA(final TypeNode n, final String clazzName) {
        if (n.getType() != null) {
            try {
                ClassLoader classLoader = n.getType().getClassLoader();
                if (classLoader == null) {
                    // Using the system classloader then
                    classLoader = ClassLoader.getSystemClassLoader();
                }

                // If the requested type is in the classpath, using the same classloader should work
                final Class<?> clazz = classLoader.loadClass(clazzName);

                if (clazz != null) {
                    return isA(n, clazz);
                }
            } catch (final ClassNotFoundException ignored) {
                // The requested type is not on the auxclasspath. This might happen, if the type node
                // is probed for a specific type (e.g. is is a JUnit5 Test Annotation class).
                // Failing to resolve clazzName does not necessarily indicate an incomplete auxclasspath.
            }
        }

        return clazzName.equals(n.getImage()) || clazzName.endsWith("." + n.getImage());
    }
    
    public static boolean isA(TypeNode n, Class<?> clazz) {
        return subclasses(n, clazz);
    }

    public static boolean isEither(TypeNode n, Class<?> class1, Class<?> class2) {
        return subclasses(n, class1) || subclasses(n, class2);
    }

    public static boolean isA(TypedNameDeclaration vnd, Class<?> clazz) {
        Class<?> type = vnd.getType();
        return type != null && type.equals(clazz) || type == null
                && (clazz.getSimpleName().equals(vnd.getTypeImage()) || clazz.getName().equals(vnd.getTypeImage()));
    }

    public static boolean isEither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
        return isA(vnd, class1) || isA(vnd, class2);
    }

    public static boolean isNeither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
        return !isA(vnd, class1) && !isA(vnd, class2);
    }

    public static boolean subclasses(TypeNode n, Class<?> clazz) {
        Class<?> type = n.getType();
        if (type == null) {
            return clazz.getSimpleName().equals(((Node) n).getImage()) || clazz.getName().equals(((Node) n).getImage());
        }

        return clazz.isAssignableFrom(type);
    }
}
