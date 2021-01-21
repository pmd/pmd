/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * @deprecated Use the similar {@link TypeTestUtil}
 */
@Deprecated
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
     * @deprecated Use {@link TypeTestUtil#isA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean isA(final TypeNode n, final String clazzName) {
        Objects.requireNonNull(n);
        return clazzName != null && TypeTestUtil.isA(StringUtils.deleteWhitespace(clazzName), n);
    }

    /**
     * Checks whether the resolved type of the given {@link TypeNode} n is exactly of the type
     * given by the clazzName.
     *
     * @param n the type node to check
     * @param clazzName the class name to compare to
     * @return <code>true</code> if type node n is exactly of type clazzName.
     * @deprecated Use {@link TypeTestUtil#isExactlyA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean isExactlyA(final TypeNode n, final String clazzName) {
        Objects.requireNonNull(n);
        return clazzName != null && TypeTestUtil.isExactlyA(StringUtils.deleteWhitespace(clazzName), n);
    }


    /**
     * @deprecated Use {@link TypeTestUtil#isA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean isA(TypeNode n, Class<?> clazz) {
        Objects.requireNonNull(n);
        return clazz != null && TypeTestUtil.isA(clazz, n);
    }

    /**
     * @deprecated Not useful, use {@link TypeTestUtil#isA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean isEither(TypeNode n, Class<?> class1, Class<?> class2) {
        return subclasses(n, class1) || subclasses(n, class2);
    }

    /**
     * @deprecated Not useful, use {@link TypedNameDeclaration#getTypeNode()} and {@link TypeTestUtil#isExactlyA(Class, TypeNode)}
     */
    @Deprecated
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

    /**
     * @deprecated Not useful, use a negated {@link TypeTestUtil#isExactlyA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean isExactlyNone(TypedNameDeclaration vnd, Class<?>... clazzes) {
        return !isExactlyAny(vnd, clazzes);
    }

    /**
     * @deprecated use {@link TypeTestUtil#isA(Class, TypeNode) TypeTestUtil.isA(vnd.getTypeNode(), clazz)}
     */
    @Deprecated
    public static boolean isA(TypedNameDeclaration vnd, Class<?> clazz) {
        return isExactlyAny(vnd, clazz);
    }


    /**
     * @deprecated Not useful, use {@link TypeTestUtil#isA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean isEither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
        return isExactlyAny(vnd, class1, class2);
    }

    /**
     * @deprecated Not useful, use a negated {@link TypeTestUtil#isA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean isNeither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
        return !isA(vnd, class1) && !isA(vnd, class2);
    }

    /**
     * @deprecated Use {@link TypeTestUtil#isA(Class, TypeNode)}
     */
    @Deprecated
    public static boolean subclasses(TypeNode n, Class<?> clazz) {
        Objects.requireNonNull(n);
        return clazz != null && TypeTestUtil.isA(clazz, n);
    }


    /**
     * @deprecated use {@link TypeTestUtil#isA(Class, TypeNode) TypeTestUtil.isA(vnd.getTypeNode(), className)}
     */
    public static boolean isA(TypedNameDeclaration vnd, String className) {
        return isA(vnd.getTypeNode(), className);
    }
}
