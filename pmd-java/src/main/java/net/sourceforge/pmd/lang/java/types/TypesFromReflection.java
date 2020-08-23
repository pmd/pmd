/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.apache.commons.lang3.reflect.Typed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Builds type mirrors from {@link Type} instances.
 *
 * <p>This is intended as a public API to help rules build types.
 */
public final class TypesFromReflection {

    private TypesFromReflection() {
        // util class
    }

    /**
     * Builds a type from reflection. This overload expects a ground type,
     * ie it will fail if the given type mentions type variables. This can
     * be used to get a type quickly, eg:
     * <pre>{@code
     *
     * // note the anonymous class body
     * JTypeMirror streamOfInt = fromReflect(new TypeLiteral<Stream<Integer>>() {}, node.getTypeSystem());
     *
     * if (node.getTypeMirror().equals(streamOfInt))
     *   addViolation(node, "Use IntStream instead of Stream<Integer>");
     *
     * // going the long way:
     * TypeSystem ts = node.getTypeSystem();
     * JTypeMirror streamOfInt = ts.typeOf(ts.getClassSymbol(Stream.class), false)
     *                             .withTypeParameters(listOf(ts.INT.box()));
     *
     * }</pre>
     *
     * @param ts        Type system that will build the type
     * @param reflected A {@link Typed} instance, eg a {@link TypeLiteral}.
     *
     * @throws IllegalArgumentException If the given type mentions type variables
     * @throws NullPointerException If the type, or the type system, are null
     */
    public static JTypeMirror fromReflect(Typed<?> reflected, TypeSystem ts) {
        return fromReflect(ts, reflected.getType(), LexicalScope.EMPTY, Substitution.EMPTY);
    }

    public static JTypeMirror fromReflect(Type reflected, TypeSystem ts) {
        return fromReflect(ts, reflected, LexicalScope.EMPTY, Substitution.EMPTY);
    }

    /**
     * Builds a type from reflection. This takes care of preserving the
     * identity of type variables.
     *
     * @param ts           Type system
     * @param reflected    A type instance obtained from reflection
     * @param lexicalScope An index for the in-scope type variables. All
     *                     type variables occurring in the type must be
     *                     referenced.
     * @param subst        Substitution to apply to tvars
     *
     * @return A type, or null if the type system's symbol resolver cannot map
     *     the types to its own representation
     *
     * @throws IllegalArgumentException If there are free type variables in the type.
     *                                  Any type variable should be accessible in the
     *                                  lexical scope parameter.
     * @throws NullPointerException     If any parameter is null
     */
    public static @Nullable JTypeMirror fromReflect(TypeSystem ts, @NonNull Type reflected, LexicalScope lexicalScope, Substitution subst) {
        Objects.requireNonNull(reflected, "Null type");
        Objects.requireNonNull(ts, "Null type system");
        Objects.requireNonNull(lexicalScope, "Null lexical scope, use the empty scope");
        Objects.requireNonNull(subst, "Null substitution, use the empty subst");

        if (reflected instanceof Class) {

            return ts.rawType(ts.getClassSymbol((Class<?>) reflected));

        } else if (reflected instanceof ParameterizedType) {


            ParameterizedType parameterized = (ParameterizedType) reflected;
            Class<?> raw = (Class<?>) parameterized.getRawType();
            JClassSymbol sym = ts.getClassSymbol(raw);

            if (sym == null) {
                return null;
            }

            Type[] typeArguments = parameterized.getActualTypeArguments();
            List<JTypeMirror> mapped = CollectionUtil.map(
                typeArguments,
                a -> fromReflect(ts, a, lexicalScope, subst)
            );

            if (CollectionUtil.any(mapped, Objects::isNull)) {
                return null;
            }

            Type ownerType = parameterized.getOwnerType();
            if (ownerType != null && !Modifier.isStatic(raw.getModifiers())) {
                JClassType owner = (JClassType) fromReflect(ts, ownerType, lexicalScope, subst);
                if (owner == null) {
                    return null;
                }
                return owner.selectInner(sym, mapped);
            }
            return ts.parameterise(sym, mapped);

        } else if (reflected instanceof TypeVariable<?>) {

            TypeVariable<?> typeVariable = (TypeVariable<?>) reflected;

            @Nullable SubstVar mapped = lexicalScope.apply(typeVariable.getName());
            if (mapped == null) {
                throw new IllegalArgumentException(
                    "The lexical scope " + lexicalScope + " does not contain an entry for type variable "
                        + typeVariable.getName() + " (declared on " + typeVariable.getGenericDeclaration() + ")"
                );
            }

            return subst.apply(mapped);

        } else if (reflected instanceof WildcardType) {

            Type[] lowerBounds = ((WildcardType) reflected).getLowerBounds();
            if (lowerBounds.length == 0) {
                // no explicit lower bound, ie an upper bound
                return makeWildcard(ts, true, ((WildcardType) reflected).getUpperBounds(), lexicalScope, subst);
            } else {
                return makeWildcard(ts, false, lowerBounds, lexicalScope, subst);
            }

        } else if (reflected instanceof GenericArrayType) {

            JTypeMirror comp = fromReflect(ts, ((GenericArrayType) reflected).getGenericComponentType(), lexicalScope, subst);

            if (comp == null) {
                return null;
            }

            return ts.arrayType(comp);
        }

        throw new IllegalStateException("Illegal type " + reflected.getClass() + " " + reflected.getTypeName());
    }

    private static JTypeMirror makeWildcard(TypeSystem ts,
                                            boolean isUpper,
                                            Type[] bounds,
                                            LexicalScope lexicalScope,
                                            Substitution subst) {

        List<JTypeMirror> boundsMapped = new ArrayList<>(bounds.length);
        for (Type a : bounds) {
            JTypeMirror jTypeMirror = fromReflect(ts, a, lexicalScope, subst);
            if (jTypeMirror == null) {
                return null;
            }
            boundsMapped.add(jTypeMirror);
        }
        // we intersect
        return ts.wildcard(isUpper, ts.glb(boundsMapped));
    }



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


    /**
     * Load a class. Supports loading array types like 'java.lang.String[]' and
     * converting a canonical name to a binary name (eg 'java.util.Map.Entry' ->
     * 'java.util.Map$Entry').
     */
    public static Class<?> loadClass(NullableClassLoader ctr, String className) {
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

}
