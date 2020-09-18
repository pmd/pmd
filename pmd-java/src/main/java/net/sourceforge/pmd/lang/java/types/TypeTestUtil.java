/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.objectweb.asm.Opcodes;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;

/**
 * Public utilities to test the type of nodes.
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
     * isA(<new ArrayList<String>()>, List.class)      = true
     * isA(<new ArrayList<String>()>, ArrayList.class) = true
     * isA(<new int[0]>, int[].class)                  = true
     * isA(<new String[0]>, Object[].class)            = true
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
    public static boolean isA(final @NonNull Class<?> clazz, final @Nullable TypeNode node) {
        AssertionUtil.requireParamNotNull("class", (Object) clazz);
        if (node == null) {
            return false;
        } else if (node.getType() == clazz) {
            return true;
        }

        return canBeExtended(clazz) ? isA(clazz.getName(), node)
                                    : isExactlyA(clazz, node);
    }


    /**
     * Checks whether the static type of the node is a subtype of the
     * class identified by the given name. This ignores type arguments,
     * if the type of the node is parameterized. Examples:
     *
     * <pre>{@code
     * isA(<new ArrayList<String>()>, "java.util.List")      = true
     * isA(<new ArrayList<String>()>, "java.util.ArrayList") = true
     * isA(<new int[0]>, "int[]")                            = true
     * isA(<new String[0]>, "java.lang.Object[]")            = true
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
    public static boolean isA(final @NonNull String canonicalName, final @Nullable TypeNode node) {
        AssertionUtil.requireParamNotNull("canonicalName", (Object) canonicalName);
        if (node == null) {
            return false;
        } else if (isExactlyA(canonicalName, node)) {
            return true;
        }

        JTypeMirror thisType = node.getTypeMirror();
        JTypeDeclSymbol thisClass = thisType.getSymbol();
        if (thisClass instanceof JClassSymbol && ((JClassSymbol) thisClass).isAnnotation()) {
            return isAnnotationSuperType(canonicalName);
        }

        if (thisClass != null && thisClass.isUnresolved()) {
            // we can't get any useful info from this, isSubtypeOf would return true
            return false;
        }

        TypeSystem ts = thisType.getTypeSystem();
        @Nullable JTypeMirror otherType = TypesFromReflection.loadType(ts, canonicalName);
        if (otherType == null) {
            return false; // we know isExactlyA(canonicalName, node); returned false
        }

        return thisType.isSubtypeOf(otherType);
    }

    private static boolean isAnnotationSuperType(String clazzName) {
        // then, the supertype may only be Object, j.l.Annotation
        // this is used e.g. by the typeIs function in XPath
        return "java.lang.annotation.Annotation".equals(clazzName)
            || "java.lang.Object".equals(clazzName);
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
    public static boolean isExactlyA(final @NonNull Class<?> clazz, final @Nullable TypeNode node) {
        AssertionUtil.requireParamNotNull("class", (Object) clazz);
        if (node == null) {
            return false;
        }

        JTypeDeclSymbol sym = node.getTypeMirror().getSymbol();
        if (sym == null || sym instanceof JTypeParameterSymbol) {
            return false;
        }

        return ((JClassSymbol) sym).getBinaryName().equals(clazz.getName());
    }


    /**
     * Checks whether the static type of the node is exactly the type
     * given by the name. This ignores strict supertypes, and type arguments
     * if the type of the node is parameterized.
     *
     * <pre>{@code
     * isExactlyA(<new ArrayList<String>()>, List.class)      = false
     * isExactlyA(<new ArrayList<String>()>, ArrayList.class) = true
     * isExactlyA(<new int[0]>, int[].class)                  = true
     * isExactlyA(<new String[0]>, Object[].class)            = false
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
    public static boolean isExactlyA(@NonNull String canonicalName, final @Nullable TypeNode node) {
        AssertionUtil.requireParamNotNull("canonicalName", canonicalName);
        if (node == null) {
            return false;
        }

        JTypeDeclSymbol sym = node.getTypeMirror().getSymbol();
        if (sym == null || sym instanceof JTypeParameterSymbol) {
            return false;
        }

        canonicalName = StringUtils.deleteWhitespace(canonicalName);

        JClassSymbol klass = (JClassSymbol) sym;
        String canonical = klass.getCanonicalName();
        return canonical != null && canonical.equals(canonicalName);
    }


    private static boolean canBeExtended(Class<?> clazz) {
        // Neither final nor an annotation. Enums & records have ACC_FINAL
        return (clazz.getModifiers() & (Opcodes.ACC_ANNOTATION | Opcodes.ACC_FINAL)) == 0;
    }

}
