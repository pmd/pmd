/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Modifier;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.UnresolvedClassStore;
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
        if (node == null) {
            return false;
        }

        return hasNoSubtypes(clazz) ? isExactlyA(clazz, node)
                                    : isA(clazz, node.getTypeMirror());
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

        if (otherType == null || TypeOps.isUnresolved(type) || otherType.isPrimitive()) {
            // We'll return true if the types have equal symbols (same binary name),
            // but we ignore subtyping.
            return isExactlyA(clazz, type.getSymbol());
        }

        return isA(type, otherType);
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
        if (thisType == null) {
            return false;
        }

        return isA(canonicalName, thisType, null);
    }

    /**
     * This is the subtyping routine we use, which prunes some behavior
     * of isSubtypeOf that we don't want (eg, that unresolved types are
     * subtypes of everything).
     */
    private static boolean isA(JTypeMirror t1, JTypeMirror t2) {
        if (t1 == null || t2 == null) {
            return false;
        } else if (t1.isPrimitive() || t2.isPrimitive()) {
            return t1.equals(t2); // isSubtypeOf considers primitive widening like subtyping
        } else if (TypeOps.isUnresolved(t1)) {
            // we can't get any useful info from this, isSubtypeOf would return true
            return false;
        } else if (t2.isClassOrInterface() && ((JClassType) t2).getSymbol().isAnonymousClass()) {
            return false; // conventionally
        } else if (t1 instanceof JTypeVar) {
            return t2.isTop() || isA(((JTypeVar) t1).getUpperBound(), t2);
        }

        return t1.isSubtypeOf(t2);
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

        return isA(thisType, otherType);
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

    public static boolean isExactlyA(@NonNull Class<?> klass, @Nullable JTypeMirror type) {
        AssertionUtil.requireParamNotNull("class", klass);
        if (type == null) {
            return false;
        }
        return isExactlyA(klass, type.getSymbol());
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
        if (node == null) {
            return false;
        }
        return isExactlyAOrAnon(canonicalName, node.getTypeMirror()) == OptionalBool.YES;
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
