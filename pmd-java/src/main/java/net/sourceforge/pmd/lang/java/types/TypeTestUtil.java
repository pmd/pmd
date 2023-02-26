/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Modifier;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.UnresolvedClassStore;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Public utilities to test the type of nodes.
 *
 * @see InvocationMatcher
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
     * <p>Note that primitives are NOT considered subtypes of one another
     * by this method, even though {@link JTypeMirror#isSubtypeOf(JTypeMirror)} does.
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
        return node != null && (hasNoSubtypes(clazz) ? isExactlyA(clazz, node)
                                    : isA(clazz, node.getTypeMirror()));
    }

    /**
     * Checks whether the given type of the node is a subtype of the
     * first argument. See {@link #isA(Class, TypeNode)} for examples
     * and more info.
     *
     * @param clazz a class or array type (without whitespace)
     * @param type  the type node to check
     *
     * @return true if the second argument is not null and the type test matches
     *
     * @throws NullPointerException     if the class parameter is null
     * @see #isA(Class, TypeNode)
     */
    public static boolean isA(@NonNull Class<?> clazz, @Nullable JTypeMirror type) {
        AssertionUtil.requireParamNotNull("klass", clazz);
        if (type == null) {
            return false;
        }

        JTypeMirror otherType = TypesFromReflection.fromReflect(clazz, type.getTypeSystem());

        if (otherType == null || TypeOps.isUnresolved(type) || hasNoSubtypes(clazz)) {
            // We'll return true if the types have equal symbols (same binary name),
            // but we ignore subtyping.
            return otherType != null && Objects.equals(otherType.getSymbol(), type.getSymbol());
        }

        return isA(otherType, type);
    }


    /**
     * Checks whether the static type of the node is a subtype of the
     * class identified by the given name. See {@link #isA(Class, TypeNode)}
     * for examples and more info.
     *
     * @param canonicalName the canonical name of a class or array type (without whitespace)
     * @param node          the type node to check
     *
     * @return true if the type test matches
     *
     * @throws NullPointerException     if the class name parameter is null
     * @throws IllegalArgumentException if the class name parameter is not a valid java binary name,
     *                                  eg it has type arguments
     * @see #isA(Class, TypeNode)
     */
    public static boolean isA(final @NonNull String canonicalName, final @Nullable TypeNode node) {
        AssertionUtil.requireParamNotNull("canonicalName", (Object) canonicalName);
        AssertionUtil.assertValidJavaBinaryName(canonicalName);
        if (node == null) {
            return false;
        }

        UnresolvedClassStore unresolvedStore = InternalApiBridge.getProcessor(node).getUnresolvedStore();
        return isA(canonicalName, node.getTypeMirror(), unresolvedStore);
    }

    public static boolean isA(@NonNull String canonicalName, @Nullable JTypeMirror thisType) {
        AssertionUtil.requireParamNotNull("canonicalName", (Object) canonicalName);
        return thisType != null && isA(canonicalName, thisType, null);
    }

    public static boolean isA(@NonNull JTypeMirror t1, @Nullable TypeNode t2) {
        return t2 != null && isA(t1, t2.getTypeMirror());
    }

    /**
     * Checks whether the second type is a subtype of the first. This
     * removes some behavior of isSubtypeOf that we don't want (eg, that
     * unresolved types are subtypes of everything).
     *
     * @param t1 A supertype
     * @param t2 A type
     *
     * @return Whether t1 is a subtype of t2
     */
    public static boolean isA(@Nullable JTypeMirror t1, @NonNull JTypeMirror t2) {
        if (t1 == null) {
            return false;
        } else if (t2.isPrimitive() || t1.isPrimitive()) {
            return t2.equals(t1); // isSubtypeOf considers primitive widening like subtyping
        } else if (TypeOps.isUnresolved(t2)) {
            // we can't get any useful info from this, isSubtypeOf would return true
            return false;
        } else if (t1.isClassOrInterface() && ((JClassType) t1).getSymbol().isAnonymousClass()) {
            return false; // conventionally
        } else if (t2 instanceof JTypeVar) {
            return t1.isTop() || isA(t1, ((JTypeVar) t2).getUpperBound());
        }

        return t2.isSubtypeOf(t1);
    }

    private static boolean isA(@NonNull String canonicalName, @NonNull JTypeMirror thisType, @Nullable UnresolvedClassStore unresolvedStore) {
        OptionalBool exactMatch = isExactlyAOrAnon(canonicalName, thisType);
        if (exactMatch != OptionalBool.NO) {
            return exactMatch == OptionalBool.YES; // otherwise anon, and we return false
        }

        JTypeDeclSymbol thisClass = thisType.getSymbol();

        if (thisClass != null && thisClass.isUnresolved()) {
            // we can't get any useful info from this, isSubtypeOf would return true
            // do not test for equality, we already checked isExactlyA, which has its fallback
            return false;
        }

        TypeSystem ts = thisType.getTypeSystem();
        @Nullable JTypeMirror otherType = TypesFromReflection.loadType(ts, canonicalName, unresolvedStore);

        return isA(otherType, thisType);
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
        return node != null && isExactlyA(clazz, node.getTypeMirror().getSymbol());
    }

    public static boolean isExactlyA(@NonNull Class<?> klass, @Nullable JTypeMirror type) {
        AssertionUtil.requireParamNotNull("class", klass);
        return type != null && isExactlyA(klass, type.getSymbol());
    }

    public static boolean isExactlyA(@NonNull Class<?> klass, @Nullable JTypeDeclSymbol type) {
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
     * Returns true if the signature is that of a method declared in the
     * given class.
     *
     * @param klass Class
     * @param sig   Method signature to test
     *
     * @throws NullPointerException If any argument is null
     */
    public static boolean isDeclaredInClass(@NonNull Class<?> klass, @NonNull JMethodSig sig) {
        return isExactlyA(klass, sig.getDeclaringType().getSymbol());
    }


    /**
     * Checks whether the static type of the node is exactly the type
     * given by the name. See {@link #isExactlyA(Class, TypeNode)} for
     * examples and more info.
     *
     * @param canonicalName a canonical name of a class or array type
     * @param node          the type node to check
     *
     * @return true if the node is non-null and has the given type
     *
     * @throws NullPointerException     if the class name parameter is null
     * @throws IllegalArgumentException if the class name parameter is not a valid java binary name,
     *                                  eg it has type arguments
     * @see #isExactlyA(Class, TypeNode)
     */
    public static boolean isExactlyA(@NonNull String canonicalName, final @Nullable TypeNode node) {
        AssertionUtil.assertValidJavaBinaryName(canonicalName);
        return node != null && isExactlyAOrAnon(canonicalName, node.getTypeMirror()) == OptionalBool.YES;
    }

    static OptionalBool isExactlyAOrAnon(@NonNull String canonicalName, final @NonNull JTypeMirror node) {
        AssertionUtil.requireParamNotNull("canonicalName", canonicalName);

        JTypeDeclSymbol sym = node.getSymbol();
        if (sym == null || sym instanceof JTypeParameterSymbol) {
            return OptionalBool.NO;
        }

        canonicalName = StringUtils.deleteWhitespace(canonicalName);

        JClassSymbol klass = (JClassSymbol) sym;
        String canonical = klass.getCanonicalName();
        if (canonical == null) {
            return OptionalBool.UNKNOWN; // anonymous
        }
        return OptionalBool.definitely(canonical.equals(canonicalName));
    }


    private static boolean hasNoSubtypes(Class<?> clazz) {
        // Neither final nor an annotation. Enums & records have ACC_FINAL
        // Note: arrays have ACC_FINAL, but have subtypes by covariance
        // Note: annotations may be implemented by classes
        return Modifier.isFinal(clazz.getModifiers()) && !clazz.isArray() || clazz.isPrimitive();
    }
}
