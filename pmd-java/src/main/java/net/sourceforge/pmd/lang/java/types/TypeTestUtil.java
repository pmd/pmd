/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.UnresolvedClassStore;
import net.sourceforge.pmd.util.OptionalBool;
import net.sourceforge.pmd.util.StringUtil;

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


    private static boolean isA(@NonNull Class<?> clazz, @Nullable JTypeMirror type) {
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
        if (node == null) {
            return false;
        }
        return isExactlyAOrAnon(canonicalName, node.getTypeMirror()) == OptionalBool.YES;
    }

    private static OptionalBool isExactlyAOrAnon(@NonNull String canonicalName, final @NonNull JTypeMirror node) {
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


    /**
     * Matches a method or constructor call against a particular overload.
     * Use {@link #parse(String)} to create one. For example,
     *
     * <pre>
     *     java.lang.String#toString()   // match calls to toString on String instances
     *     _#toString()                  // match calls to toString on any receiver
     *     _#_()                         // match all calls to a method with no parameters
     *     _#toString(_*)                // match calls to a "toString" method with any number of parameters
     *     _#eq(_, _)                    // match calls to an "eq" method that has 2 parameters of unspecified type
     *     _#eq(java.lang.String, _)     // like the previous, but the first parameter must be String
     *     java.util.ArrayList#new(int)  // match constructor calls of this overload of the ArrayList constructor
     * </pre>
     *
     * <p>The receiver matcher (first half) is matched against the
     * static type of the <i>receiver</i> of the call, and not the
     * declaration site of the method, unless the called method is
     * static, or a constructor.
     *
     * <p>The parameters are matched against the declared parameters
     * types of the called overload, and not the actual argument types.
     * In particular, for vararg methods, the signature should mention
     * a single parameter, with an array type.
     *
     * <p>For example {@code Integer.valueOf('0')} will be matched by
     * {@code _#valueOf(int)} but not {@code _#valueOf(char)}, which is
     * an overload that does not exist (the char is widened to an int,
     * so the int overload is selected).
     *
     * <h5 id='ebnf'>Full EBNF grammar</h5>
     *
     * <p>(no whitespace is tolerated anywhere):
     * <pre>{@code
     * sig         ::= type '#' method_name param_list
     * type        ::= qname ( '[]' )* | '_'
     * method_name ::= '_' | ident | 'new'
     * param_list  ::= '(_*)' | '(' type (',' type )* ')'
     * qname       ::= java binary name
     * }</pre>
     */
    public static final class InvocationMatcher {

        final @Nullable String expectedName;
        final @Nullable List<TypeMatcher> argMatchers;
        final TypeMatcher qualifierMatcher;

        InvocationMatcher(TypeMatcher qualifierMatcher, String expectedName, @Nullable List<TypeMatcher> argMatchers) {
            this.expectedName = "_".equals(expectedName) ? null : expectedName;
            this.argMatchers = argMatchers;
            this.qualifierMatcher = qualifierMatcher;
        }

        /**
         * Returns true if the call matches this matcher. This means,
         * the called overload is the one identified by the argument
         * matchers, and the actual qualifier type is a subtype of the
         * one mentioned by the qualifier matcher.
         */
        public boolean matchesCall(InvocationNode node) {
            if (expectedName != null && !node.getMethodName().equals(expectedName)
                || argMatchers != null && ASTList.sizeOrZero(node.getArguments()) != argMatchers.size()) {
                return false;
            }
            OverloadSelectionResult info = node.getOverloadSelectionInfo();
            if (info.isFailed() || !matchQualifier(node)) {
                return false;
            }
            return argsMatchOverload(info.getMethodType());
        }

        private boolean matchQualifier(InvocationNode node) {
            if (qualifierMatcher == TypeMatcher.ANY) {
                return true;
            }
            if (node instanceof ASTConstructorCall) {
                JTypeMirror newType = ((ASTConstructorCall) node).getTypeNode().getTypeMirror();
                return qualifierMatcher.matches(newType, true);
            }
            JMethodSig m = node.getMethodType();
            JTypeMirror qualType;
            if (node instanceof QualifiableExpression) {
                ASTExpression qualifier = ((QualifiableExpression) node).getQualifier();
                if (qualifier != null) {
                    qualType = qualifier.getTypeMirror();
                } else {
                    // todo: if qualifier == null, then we should take the type of the
                    // implicit receiver, ie `this` or `SomeOuter.this`
                    qualType = m.getDeclaringType();
                }
            } else {
                qualType = m.getDeclaringType();
            }

            return qualifierMatcher.matches(qualType, m.isStatic());
        }

        private boolean argsMatchOverload(JMethodSig invoc) {
            if (argMatchers == null) {
                return true;
            }
            List<JTypeMirror> formals = invoc.getFormalParameters();
            if (invoc.getArity() != argMatchers.size()) {
                return false;
            }
            for (int i = 0; i < formals.size(); i++) {
                if (!argMatchers.get(i).matches(formals.get(i), true)) {
                    return false;
                }
            }
            return true;
        }


        /**
         * Parses an {@link InvocationMatcher}.
         *
         * @param sig A signature in the format described on this class
         *
         * @return A sig matcher
         *
         * @throws IllegalArgumentException If the signature is malformed (see <a href='#ebnf'>EBNF</a>)
         * @throws NullPointerException     If the signature is null
         */
        public static InvocationMatcher parse(String sig) {
            int i = parseType(sig, 0);
            final TypeMatcher qualifierMatcher = newMatcher(sig.substring(0, i));
            i = consumeChar(sig, i, '#');
            final int nameStart = i;
            i = parseSimpleName(sig, i);
            final String methodName = sig.substring(nameStart, i);
            i = consumeChar(sig, i, '(');
            if (isChar(sig, i, ')')) {
                return new InvocationMatcher(qualifierMatcher, methodName, Collections.emptyList());
            }
            // (_*) matches any argument list
            List<TypeMatcher> argMatchers;
            if (isChar(sig, i, '_')
                && isChar(sig, i + 1, '*')
                && isChar(sig, i + 2, ')')) {
                argMatchers = null;
                i = i + 3;
            } else {
                argMatchers = new ArrayList<>();
                i = parseArgList(sig, i, argMatchers);
            }
            if (i != sig.length()) {
                throw new IllegalArgumentException("Not a valid signature " + sig);
            }
            return new InvocationMatcher(qualifierMatcher, methodName, argMatchers);
        }

        private static int parseSimpleName(String sig, final int start) {
            int i = start;
            while (i < sig.length() && Character.isJavaIdentifierPart(sig.charAt(i))) {
                i++;
            }
            if (i == start) {
                throw new IllegalArgumentException("Not a valid signature " + sig);
            }
            return i;
        }

        private static int parseArgList(String sig, int i, List<TypeMatcher> argMatchers) {
            while (i < sig.length()) {
                i = parseType(sig, i, argMatchers);
                if (isChar(sig, i, ')')) {
                    return i + 1;
                }
                i = consumeChar(sig, i, ',');
            }
            throw new IllegalArgumentException("Not a valid signature " + sig);
        }

        private static int consumeChar(String source, int i, char c) {
            if (isChar(source, i, c)) {
                return i + 1;
            }
            throw newParseException(source, i, "character '" + c + "'");
        }

        private static RuntimeException newParseException(String source, int i, String expectedWhat) {
            final String indent = "    ";
            String message = "Expected " + expectedWhat + " at index " + i + ":\n";
            message += indent + "\"" + StringUtil.escapeJava(source) + "\"\n";
            message += indent + StringUtils.repeat(' ', i + 1) + '^' + "\n";
            return new IllegalArgumentException(message);
        }

        private static boolean isChar(String source, int i, char c) {
            return i < source.length() && source.charAt(i) == c;
        }

        private static int parseType(String source, int i, List<TypeMatcher> result) {
            final int start = i;
            i = parseType(source, i);
            result.add(newMatcher(source.substring(start, i)));
            return i;
        }

        private static int parseType(String source, int i) {
            final int start = i;
            while (i < source.length() && (Character.isJavaIdentifierPart(source.charAt(i))
                || source.charAt(i) == '.')) {
                i++;
            }
            if (i == start) {
                throw newParseException(source, i, "type");
            }

            AssertionUtil.assertValidJavaBinaryName(source.substring(start, i));
            // array dimensions
            while (isChar(source, i, '[')) {
                i = consumeChar(source, i + 1, ']');
            }
            return i;
        }

        private static TypeMatcher newMatcher(String name) {
            return "_".equals(name) ? TypeMatcher.ANY : new TypeMatcher(name);
        }

        private static final class TypeMatcher {

            /** Matches any type. */
            public static final TypeMatcher ANY = new TypeMatcher(null);

            final @Nullable String name;

            private TypeMatcher(@Nullable String name) {
                this.name = name;
            }

            boolean matches(JTypeMirror type, boolean exact) {
                if (name == null) {
                    return true;
                }
                return exact ? TypeTestUtil.isExactlyAOrAnon(name, type) == OptionalBool.YES
                             : TypeTestUtil.isA(name, type);
            }
        }
    }

}
