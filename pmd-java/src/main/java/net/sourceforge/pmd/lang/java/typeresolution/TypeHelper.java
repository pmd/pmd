/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import static net.sourceforge.pmd.util.CollectionUtil.any;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader.ClassLoaderWrapper;

public final class TypeHelper {

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


    private TypeHelper() {
        // utility class
    }

    /**
     * Returns true if the type of the given type node has the same symbol
     * as the given class. This ignores type parameters and such. It is not
     * equivalent to a subtyping check, unless {@code someClass} is final.
     * Returns false if the node is null.
     *
     * @param someClass A class
     * @param node      A node
     *
     * @throws NullPointerException If the class is null
     */
    public static boolean symbolEquals(@NonNull Class<?> someClass, @Nullable TypeNode node) {
        Objects.requireNonNull(someClass);
        if (node == null) {
            return false;
        }

        JTypeDeclSymbol symbol = node.getTypeMirror().getSymbol();
        return symbol != null && symbol.equals(node.getTypeSystem().getClassSymbol(someClass));
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
            return isAnnotationSubtype(n.getType(), clazzName);
        }

        final Class<?> clazz = loadClassWithNodeClassloader(n, clazzName);

        if (clazz != null || n.getType() != null) {
            return isA(n, clazz);
        }

        // FIXME checking against the image is for the most part meaningless.
        //   Many type nodes don't have an image or have one that has no relation
        //   to their type. The TypeNode interface should have a method getTypeImage,
        //   or better, we could use symbols instead.
        return fallbackIsA(n, clazzName);
    }

    /**
     * Returns true if the class n is a subtype of clazzName, given n
     * is an annotationt type.
     */
    private static boolean isAnnotationSubtype(Class<?> n, String clazzName) {
        assert n != null && n.isAnnotation() : "Not an annotation type";
        // then, the supertype may only be Object, j.l.Annotation, or the class name
        // this avoids classloading altogether
        // this is used e.g. by the typeIs function in XPath
        return "java.lang.annotation.Annotation".equals(clazzName)
            || "java.lang.Object".equals(clazzName)
            || clazzName.equals(n.getName());
    }

    private static boolean fallbackIsA(final TypeNode n, String clazzName) {
        // Later we won't need a fallback. Symbols already contain subclass information.

        if (n instanceof ASTAnyTypeDeclaration && ((ASTAnyTypeDeclaration) n).getBinaryName().equals(clazzName)) {
            return true;
        } else if (n instanceof ASTClassOrInterfaceType || n instanceof ASTAnnotation) {
            ASTClassOrInterfaceType classType;
            if (n instanceof ASTAnnotation) {
                classType = ((ASTAnnotation) n).getTypeNode();
            } else {
                classType = (ASTClassOrInterfaceType) n;
            }

            JTypeDeclSymbol sym = classType.getReferencedSym();
            return sym instanceof JClassSymbol && ((JClassSymbol) sym).getBinaryName().equals(clazzName);
        }

        if (n instanceof ASTClassOrInterfaceDeclaration) {
            ASTClassOrInterfaceType superClass = ((ASTClassOrInterfaceDeclaration) n).getSuperClassTypeNode();
            if (superClass != null) {
                return isA(superClass, clazzName);
            }

            return any(((ASTClassOrInterfaceDeclaration) n).getSuperInterfaceTypeNodes(), itf -> isA(itf, clazzName));

        } else if (n instanceof ASTEnumDeclaration) {

            if (any(((ASTEnumDeclaration) n).getSuperInterfaceTypeNodes(), itf -> isA(itf, clazzName))) {
                return true;
            }

            return "java.lang.Enum".equals(clazzName)
                // supertypes of Enum
                || "java.lang.Comparable".equals(clazzName)
                || "java.io.Serializable".equals(clazzName)
                || "java.lang.Object".equals(clazzName);
        } else if (n instanceof ASTAnnotationTypeDeclaration) {
            return "java.lang.annotation.Annotation".equals(clazzName)
                || "java.lang.Object".equals(clazzName);
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
     *
     * @throws NullPointerException if n is null
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


    /**
     * Load a class. Supports loading array types like 'java.lang.String[]' and
     * converting a canonical name to a binary name (eg 'java.util.Map.Entry' ->
     * 'java.util.Map$Entry').
     */
    // test only
    static Class<?> loadClass(NullableClassLoader ctr, String className) {
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
