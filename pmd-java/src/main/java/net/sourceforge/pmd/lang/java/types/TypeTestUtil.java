/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
     * isA(List.class, <new ArrayList<String>()>)      = true
     * isA(ArrayList.class, <new ArrayList<String>()>) = true
     * isA(int[].class, <new int[0]>)                  = true
     * isA(Object[].class, <new String[0]>)            = true
     * isA(_, null) = false
     * isA(null, _) = NullPointerException
     * }</pre>
     *
     * <p>If either type is unresolved, the types are tested for equality,
     * thus giving more useful results than {@link JTypeMirror#isSubtypeOf(JTypeMirror)}.
     *
     * @param clazz a class (non-null)
     * @param node  the type node to check
     *
     * @return true if the type test matches
     *
     * @throws NullPointerException if the class parameter is null
     */
    public static boolean isA(final @NonNull Class<?> clazz, final @Nullable TypeNode node) {
        AssertionUtil.requireParamNotNull("class", clazz);
        if (node == null) {
            return false;
        }

        return hasNoSubtypes(clazz) ? isExactlyA(clazz, node)
                                    : isA(clazz, node.getTypeMirror());
    }


    private static boolean isA(@NonNull Class<?> clazz, @Nullable JTypeMirror type) {
        AssertionUtil.requireParamNotNull("klass", clazz);
        if (type == null) {
            return false;
        }

        JTypeMirror otherType = TypesFromReflection.fromReflect(clazz, type.getTypeSystem());

        if (otherType == null || TypeOps.isUnresolved(type)) {
            // We'll return true if the types have equal symbols (same binary name),
            // but we ignore subtyping.
            return isExactlyA(clazz, type.getSymbol());
        }

        return type.isSubtypeOf(otherType);
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
            // do not test for equality, we already checked isExactlyA, which has its fallback
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
        AssertionUtil.requireParamNotNull("class", clazz);
        if (node == null) {
            return false;
        }

        return isExactlyA(clazz, node.getTypeMirror().getSymbol());
    }

    private static boolean isExactlyA(@NonNull Class<?> klass, @Nullable JTypeDeclSymbol type) {
        AssertionUtil.requireParamNotNull("klass", klass);
        if (!(type instanceof JClassSymbol)) {
            // Class cannot reference a type parameter
            return false;
        }

        JClassSymbol symClass = (JClassSymbol) type;

        if (klass.isArray()) {
            return symClass.isArray() && isExactlyA(klass.getComponentType(), symClass.getArrayComponent());
        }

        // Note: klass.getName returns a type descriptor for arrays,
        // which is why we have to destructure the array above
        return symClass.getBinaryName().equals(klass.getName());
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



    private static boolean hasNoSubtypes(Class<?> clazz) {
        // Neither final nor an annotation. Enums & records have ACC_FINAL
        // Note: arrays have ACC_FINAL, but have subtypes by covariance
        // Note: annotations may be implemented by classes
        return Modifier.isFinal(clazz.getModifiers()) && !clazz.isArray();
    }

}
