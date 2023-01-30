/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;
import net.sourceforge.pmd.util.StringUtil;

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
public final class InvocationMatcher {

    final @Nullable String expectedName;
    final @Nullable List<TypeMatcher> argMatchers;
    final TypeMatcher qualifierMatcher;

    InvocationMatcher(TypeMatcher qualifierMatcher, String expectedName, @Nullable List<TypeMatcher> argMatchers) {
        this.expectedName = "_".equals(expectedName) ? null : expectedName;
        this.argMatchers = argMatchers;
        this.qualifierMatcher = qualifierMatcher;
    }

    /**
     * See {@link #matchesCall(InvocationNode)}.
     */
    public boolean matchesCall(@Nullable JavaNode node) {
        return node instanceof InvocationNode && matchesCall((InvocationNode) node);
    }

    /**
     * Returns true if the call matches this matcher. This means,
     * the called overload is the one identified by the argument
     * matchers, and the actual qualifier type is a subtype of the
     * one mentioned by the qualifier matcher.
     */
    public boolean matchesCall(@Nullable InvocationNode node) {
        if (node == null) {
            return false;
        }
        if (expectedName != null && !node.getMethodName().equals(expectedName)
            || argMatchers != null && ASTList.sizeOrZero(node.getArguments()) != argMatchers.size()) {
            return false;
        }
        OverloadSelectionResult info = node.getOverloadSelectionInfo();
        return !info.isFailed() && matchQualifier(node)
                && argsMatchOverload(info.getMethodType());
    }

    private boolean matchQualifier(InvocationNode node) {
        if (qualifierMatcher == TypeMatcher.ANY) { // NOPMD CompareObjectsWithEquals
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
     * Parses a {@link CompoundInvocationMatcher} which matches any of
     * the provided matchers.
     *
     * @param first First signature, in the format described on this class
     * @param rest  Other signatures, in the format described on this class
     *
     * @return A sig matcher
     *
     * @throws IllegalArgumentException If any signature is malformed (see <a href='#ebnf'>EBNF</a>)
     * @throws NullPointerException     If any signature is null
     * @see #parse(String)
     */
    public static CompoundInvocationMatcher parseAll(String first, String... rest) {
        List<InvocationMatcher> matchers = CollectionUtil.map(listOf(first, rest), InvocationMatcher::parse);
        return new CompoundInvocationMatcher(matchers);
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
     * @see #parseAll(String, String...)
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
            return name == null
                   || (exact ? TypeTestUtil.isExactlyAOrAnon(name, type) == OptionalBool.YES
                         : TypeTestUtil.isA(name, type));
        }
    }

    /**
     * A compound of several matchers (logical OR). Get one from
     * {@link InvocationMatcher#parseAll(String, String...)};
     */
    public static final class CompoundInvocationMatcher {

        private final List<InvocationMatcher> matchers;

        private CompoundInvocationMatcher(List<InvocationMatcher> matchers) {
            this.matchers = matchers;
        }

        // todo make this smarter. Like collecting all possible names
        //  into a set to do a quick pre-screening before we test
        //  everything linearly

        /**
         * Returns true if any of the matchers match the node.
         *
         * @see #matchesCall(JavaNode)
         */
        public boolean anyMatch(InvocationNode node) {
            return CollectionUtil.any(matchers, it -> it.matchesCall(node));
        }

        /**
         * Returns true if any of the matchers match the node.
         *
         * @see #matchesCall(JavaNode)
         */
        public boolean anyMatch(JavaNode node) {
            return CollectionUtil.any(matchers, it -> it.matchesCall(node));
        }
    }
}
