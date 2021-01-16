/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Modifier;
import java.util.List;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * Public utilities to test the type of nodes.
 *
 * <p>This replaces {@link TypeHelper}. Note that in contrast to methods
 * in {@link TypeHelper}, these methods:
 * <ul>
 * <li>Take the node as the second parameter
 * <li>Systematically return false if the node argument is null
 * <li>Systematically throw if the other argument is null
 * <li>Do not sanitize string arguments, they must be exactly canonical names,
 * with no whitespace characters.
 * </ul>
 */
public final class TypeTestUtil {

    private TypeTestUtil() {
        // utility class
    }


    /**
     * Checks whether the static type of the node is a subtype of the
     * class identified by the given name. This ignores type arguments,
     * if the type of the node is parameterized. Examples:
     *
     * <pre>{@code
     * isA(List.class, <new ArrayList<String>()>)      = true
     * isA(ArrayList.class, <new ArrayList<String>()>) = true
     * isA(int[].class, <new int[0]>)                  = true
     * isA(Object[].class, <new String[0]>)            = true
     * isA(_, null) = false
     * isA(null, _) = NullPointerException
     * }</pre>
     *
     * @param clazz a class (non-null)
     * @param node  the type node to check
     *
     * @return true if the type test matches
     *
     * @throws NullPointerException if the class parameter is null
     */
    public static boolean isA(/*@NonNull*/ Class<?> clazz, /*@Nullable*/ TypeNode node) {
        AssertionUtil.requireParamNotNull("class", clazz);
        if (node == null) {
            return false;
        } else if (node.getType() == clazz) {
            return true;
        }

        if (hasNoSubtypes(clazz)) {
            return isExactlyA(clazz, node);
        }
        String canoName = clazz.getCanonicalName();
        return canoName != null && isA(canoName, node);
    }


    /**
     * Checks whether the static type of the node is a subtype of the
     * class identified by the given name. This ignores type arguments,
     * if the type of the node is parameterized. Examples:
     *
     * <pre>{@code
     * isA("java.util.List", <new ArrayList<String>()>)      = true
     * isA("java.util.ArrayList", <new ArrayList<String>()>) = true
     * isA("int[]", <new int[0]>)                            = true
     * isA("java.lang.Object[]", <new String[0]>)            = true
     * isA(_, null) = false
     * isA(null, _) = NullPointerException
     * }</pre>
     *
     * @param canonicalName the canonical name of a class or array type (without whitespace)
     * @param node          the type node to check
     *
     * @return true if the type test matches
     *
     * @throws NullPointerException if the class name parameter is null
     */
    public static boolean isA(/*@NonNull*/ String canonicalName, /*@Nullable*/ TypeNode node) {
        AssertionUtil.assertValidJavaBinaryName(canonicalName);
        if (node == null) {
            return false;
        }

        Class<?> nodeType = node.getType();
        if (nodeType == null) {
            return fallbackIsA(node, canonicalName, true);
        } else if (nodeType.isAnnotation()) {
            return isAnnotationSubtype(nodeType, canonicalName);
        }

        final Class<?> clazz = loadClassWithNodeClassloader(node, canonicalName);


        if (clazz != null) {
            if (clazz.getCanonicalName() == null) {
                return false; // no canonical name, give up: we shouldn't be able to access them
            }
            return clazz.isAssignableFrom(nodeType);
        } else {
            return fallbackIsA(node, canonicalName, true);
        }
    }


    /**
     * Checks whether the static type of the node is exactly the type
     * of the class. This ignores strict supertypes, and type arguments,
     * if the type of the node is parameterized.
     *
     * <pre>{@code
     * isExactlyA(List.class, <new ArrayList<String>()>)      = false
     * isExactlyA(ArrayList.class, <new ArrayList<String>()>) = true
     * isExactlyA(int[].class, <new int[0]>)                  = true
     * isExactlyA(Object[].class, <new String[0]>)            = false
     * isExactlyA(_, null) = false
     * isExactlyA(null, _) = NullPointerException
     * }</pre>
     *
     * @param clazz a class (non-null)
     * @param node  the type node to check
     *
     * @return true if the node is non-null and has the given type
     *
     * @throws NullPointerException if the class parameter is null
     */
    public static boolean isExactlyA(/*@NonNull*/ Class<?> clazz, /*@Nullable*/ TypeNode node) {
        AssertionUtil.requireParamNotNull("class", clazz);
        if (node == null) {
            return false;
        }

        return node.getType() == null ? fallbackIsA(node, clazz.getName(), false)
                                      : node.getType() == clazz;
    }


    /**
     * Checks whether the static type of the node is exactly the type
     * given by the name. This ignores strict supertypes, and type arguments
     * if the type of the node is parameterized.
     *
     * <pre>{@code
     * isExactlyA(List.class, <new ArrayList<String>()>)      = false
     * isExactlyA(ArrayList.class, <new ArrayList<String>()>) = true
     * isExactlyA(int[].class, <new int[0]>)                  = true
     * isExactlyA(Object[].class, <new String[0]>)            = false
     * isExactlyA(_, null) = false
     * isExactlyA(null, _) = NullPointerException
     * }</pre>
     *
     * @param canonicalName a canonical name of a class or array type
     * @param node          the type node to check
     *
     * @return true if the node is non-null and has the given type
     *
     * @throws NullPointerException if the class name parameter is null
     */
    public static boolean isExactlyA(/*@Nullable*/ String canonicalName, TypeNode node /*@NonNull*/) {
        AssertionUtil.assertValidJavaBinaryName(canonicalName);
        if (node == null) {
            return false;
        }


        if (node.getType() == null) {
            return fallbackIsA(node, canonicalName, false);
        }

        String canoname = node.getType().getCanonicalName();
        if (canoname == null) {
            // anonymous/local class, or class nested within one of those
            return false;
        }
        return canoname.equals(canonicalName);
    }


    private static boolean hasNoSubtypes(Class<?> clazz) {
        // Neither final nor an annotation. Enums & records have ACC_FINAL
        // Note: arrays have ACC_FINAL, but have subtypes by covariance
        // Note: annotations may be implemented by classes
        return Modifier.isFinal(clazz.getModifiers()) && !clazz.isArray();
    }

    // those fallbacks can be removed with the newer symbol resolution,
    // symbols reflect enough information to do this fallback transparently.

    /**
     * Returns true if the class n is a subtype of clazzName, given n
     * is an annotation type.
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

    private static boolean fallbackIsA(TypeNode n, String canonicalName, boolean considerSubtype) {
        if (n.getImage() != null && !n.getImage().contains(".") && canonicalName.contains(".")) {
            // simple name detected, check the imports to get the full name and use that for fallback
            List<ASTImportDeclaration> imports = n.getRoot().findChildrenOfType(ASTImportDeclaration.class);
            for (ASTImportDeclaration importDecl : imports) {
                if (n.hasImageEqualTo(importDecl.getImportedSimpleName())) {
                    // found the import, compare the full names
                    return canonicalName.equals(importDecl.getImportedName());
                }
            }
        }

        if (n instanceof ASTAnyTypeDeclaration) {
            ASTAnyTypeDeclaration decl = (ASTAnyTypeDeclaration) n;
            if (decl.getBinaryName().equals(canonicalName)) {
                return true;
            } else if (!considerSubtype) { // otherwise fallthrough
                return false;
            } else {
                return isStrictSuperType(decl, canonicalName);
            }
        }

        // fall back on using the simple name of the class only
        return canonicalName.equals(n.getImage()) || canonicalName.endsWith("." + n.getImage());
    }

    private static boolean isStrictSuperType(ASTAnyTypeDeclaration n, String binaryName) {
        if (n instanceof ASTClassOrInterfaceDeclaration) {

            ASTClassOrInterfaceType superClass = ((ASTClassOrInterfaceDeclaration) n).getSuperClassTypeNode();
            if (superClass != null) {
                return isA(binaryName, superClass);
            }

            for (ASTClassOrInterfaceType itf : ((ASTClassOrInterfaceDeclaration) n).getSuperInterfacesTypeNodes()) {
                if (isA(binaryName, itf)) {
                    return true;
                }
            }
        } else if (n instanceof ASTEnumDeclaration) {

            ASTImplementsList implemented = n.getFirstChildOfType(ASTImplementsList.class);
            if (implemented != null) {
                for (ASTClassOrInterfaceType itf : implemented) {
                    if (isA(binaryName, itf)) {
                        return true;
                    }
                }
            }

            return "java.lang.Enum".equals(binaryName)
                // supertypes of Enum
                || "java.lang.Comparable".equals(binaryName)
                || "java.io.Serializable".equals(binaryName)
                || "java.lang.Object".equals(binaryName);
        } else if (n instanceof ASTAnnotationTypeDeclaration) {
            return "java.lang.annotation.Annotation".equals(binaryName)
                || "java.lang.Object".equals(binaryName);
        }

        return false;
    }

    static Class<?> loadClassWithNodeClassloader(final TypeNode n, final String clazzName) {
        if (n.getType() != null) {
            return TypesFromReflection.loadClass(n.getRoot().getClassTypeResolver(), clazzName);
        }

        return null;
    }
}
