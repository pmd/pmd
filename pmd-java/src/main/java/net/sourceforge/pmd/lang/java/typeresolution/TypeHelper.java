/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;

public final class TypeHelper {
    private static final Logger LOG = Logger.getLogger(TypeHelper.class.getName());

    private TypeHelper() {
        // utility class
    }

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
            } catch (final ClassNotFoundException e) {
                // The requested type is not on the auxclasspath
                LOG.log(Level.WARNING, "Incomplete auxclasspath: The class " + clazzName + " was not found");
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
