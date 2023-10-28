/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.LONG;
import static net.sourceforge.pmd.util.CollectionUtil.immutableSetOf;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher.CompoundInvocationMatcher;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Utilities shared between rules.
 */
public final class JavaRuleUtil {

    // this is a hacky way to do it, but let's see where this goes
    private static final CompoundInvocationMatcher KNOWN_PURE_METHODS = InvocationMatcher.parseAll(
        "_#toString()",
        "_#hashCode()",
        "_#equals(java.lang.Object)",
        "java.lang.String#_(_*)",
        // actually not all of them, probs only stream of some type
        // arg which doesn't implement Closeable...
        "java.util.stream.Stream#_(_*)",
        "java.util.Collection#size()",
        "java.util.List#get(int)",
        "java.util.Map#get(_)",
        "java.lang.Iterable#iterator()",
        "java.lang.Comparable#compareTo(_)"
    );

    public static final Set<String> LOMBOK_ANNOTATIONS = immutableSetOf(
        "lombok.Data",
        "lombok.Getter",
        "lombok.Setter",
        "lombok.Value",
        "lombok.RequiredArgsConstructor",
        "lombok.AllArgsConstructor",
        "lombok.NoArgsConstructor",
        "lombok.Builder",
        "lombok.EqualsAndHashCode",
        "lombok.experimental.Delegate"
    );

    private JavaRuleUtil() {
        // utility class
    }


    /**
     * Return true if the given expression is enclosed in a zero check.
     * The expression must evaluate to a natural number (ie >= 0), so that
     * {@code e < 1} actually means {@code e == 0}.
     *
     * @param e Expression
     */
    public static boolean isZeroChecked(ASTExpression e) {
        JavaNode parent = e.getParent();
        if (parent instanceof ASTInfixExpression) {
            BinaryOp op = ((ASTInfixExpression) parent).getOperator();
            int checkLiteralAtIdx = 1 - e.getIndexInParent();
            JavaNode comparand = parent.getChild(checkLiteralAtIdx);
            int expectedValue;
            if (op == BinaryOp.NE || op == BinaryOp.EQ) {
                // e == 0, e != 0, symmetric
                expectedValue = 0;
            } else if (op == BinaryOp.LT || op == BinaryOp.GE) {
                // e < 1
                // 0 < e
                // e >= 1     (e != 0)
                // 1 >= e     (e == 0 || e == 1)
                // 0 >= e     (e == 0)
                // e >= 0     (true)
                expectedValue = checkLiteralAtIdx;
            } else if (op == BinaryOp.GT || op == BinaryOp.LE) {
                // 1 > e
                // e > 0

                // 1 <= e     (e != 0)
                // e <= 1     (e == 0 || e == 1)
                // e <= 0     (e == 0)
                // 0 <= e     (true)
                expectedValue = 1 - checkLiteralAtIdx;
            } else {
                return false;
            }

            return JavaAstUtils.isLiteralInt(comparand, expectedValue);
        }
        return false;
    }


    /**
     * Returns true if the expression is a stringbuilder (or stringbuffer)
     * append call, or a constructor call for one of these classes.
     *
     * <p>If it is a constructor call, returns false if this is a call to
     * the constructor with a capacity parameter.
     */
    public static boolean isStringBuilderCtorOrAppend(@Nullable ASTExpression e) {
        if (e instanceof ASTMethodCall) {
            ASTMethodCall call = (ASTMethodCall) e;
            if ("append".equals(call.getMethodName())) {
                ASTExpression qual = ((ASTMethodCall) e).getQualifier();
                return qual != null && isStringBufferOrBuilder(qual);
            }
        } else if (e instanceof ASTConstructorCall) {
            return isStringBufferOrBuilder(((ASTConstructorCall) e).getTypeNode());
        }
        return false;
    }

    private static boolean isStringBufferOrBuilder(TypeNode node) {
        return TypeTestUtil.isExactlyA(StringBuilder.class, node)
            || TypeTestUtil.isExactlyA(StringBuffer.class, node);
    }

    /**
     * Returns true if the node is a utility class, according to this
     * custom definition.
     */
    public static boolean isUtilityClass(ASTAnyTypeDeclaration node) {
        if (!node.isRegularClass()) {
            return false;
        }

        ASTClassOrInterfaceDeclaration classNode = (ASTClassOrInterfaceDeclaration) node;

        // A class with a superclass or interfaces should not be considered
        if (classNode.getSuperClassTypeNode() != null
            || !classNode.getSuperInterfaceTypeNodes().isEmpty()) {
            return false;
        }

        // A class without declarations shouldn't be reported
        boolean hasAny = false;

        for (ASTBodyDeclaration declNode : classNode.getDeclarations()) {
            if (declNode instanceof ASTFieldDeclaration
                || declNode instanceof ASTMethodDeclaration) {

                hasAny = isNonPrivate(declNode) && !JavaAstUtils.isMainMethod(declNode);
                if (!((AccessNode) declNode).hasModifiers(JModifier.STATIC)) {
                    return false;
                }

            } else if (declNode instanceof ASTInitializer) {
                if (!((ASTInitializer) declNode).isStatic()) {
                    return false;
                }
            }
        }

        return hasAny;
    }

    private static boolean isNonPrivate(ASTBodyDeclaration decl) {
        return ((AccessNode) decl).getVisibility() != Visibility.V_PRIVATE;
    }

    /**
     * Whether the name may be ignored by unused rules like UnusedAssignment.
     */
    public static boolean isExplicitUnusedVarName(String name) {
        return name.startsWith("ignored")
            || name.startsWith("unused")
            // before java 9 it's ok, after that, "_" is a reserved keyword
            // with Java 21 Preview (JEP 443), "_" means explicitly unused
            || "_".equals(name);
    }

    /**
     * Returns true if the string has the given word as a strict prefix.
     * There needs to be a camelcase word boundary after the prefix.
     *
     * <code>
     * startsWithCamelCaseWord("getter", "get") == false
     * startsWithCamelCaseWord("get", "get")    == false
     * startsWithCamelCaseWord("getX", "get")   == true
     * </code>
     *
     * @param camelCaseString A string
     * @param prefixWord      A prefix
     */
    public static boolean startsWithCamelCaseWord(String camelCaseString, String prefixWord) {
        return camelCaseString.startsWith(prefixWord)
            && camelCaseString.length() > prefixWord.length()
            && Character.isUpperCase(camelCaseString.charAt(prefixWord.length()));
    }


    /**
     * Returns true if the string has the given word as a word, not at the start.
     * There needs to be a camelcase word boundary after the prefix.
     *
     * <code>
     * containsCamelCaseWord("isABoolean", "Bool") == false
     * containsCamelCaseWord("isABoolean", "A")    == true
     * containsCamelCaseWord("isABoolean", "is")   == error (not capitalized)
     * </code>
     *
     * @param camelCaseString A string
     * @param capitalizedWord A word, non-empty, capitalized
     *
     * @throws AssertionError If the word is empty or not capitalized
     */
    public static boolean containsCamelCaseWord(String camelCaseString, String capitalizedWord) {
        assert capitalizedWord.length() > 0 && Character.isUpperCase(capitalizedWord.charAt(0))
            : "Not a capitalized string \"" + capitalizedWord + "\"";

        int index = camelCaseString.indexOf(capitalizedWord);
        if (index >= 0 && camelCaseString.length() > index + capitalizedWord.length()) {
            return Character.isUpperCase(camelCaseString.charAt(index + capitalizedWord.length()));
        }
        return index >= 0 && camelCaseString.length() == index + capitalizedWord.length();
    }

    public static boolean isGetterOrSetterCall(ASTMethodCall call) {
        return isGetterCall(call) || isSetterCall(call);
    }

    private static boolean isSetterCall(ASTMethodCall call) {
        return call.getArguments().size() > 0 && startsWithCamelCaseWord(call.getMethodName(), "set");
    }

    public static boolean isGetterCall(ASTMethodCall call) {
        return call.getArguments().size() == 0
            && (startsWithCamelCaseWord(call.getMethodName(), "get")
            || startsWithCamelCaseWord(call.getMethodName(), "is"));
    }


    public static boolean isGetterOrSetter(ASTMethodDeclaration node) {
        return isGetter(node) || isSetter(node);
    }

    /** Attempts to determine if the method is a getter. */
    private static boolean isGetter(ASTMethodDeclaration node) {

        if (node.getArity() != 0 || node.isVoid()) {
            return false;
        }

        ASTAnyTypeDeclaration enclosing = node.getEnclosingType();
        if (startsWithCamelCaseWord(node.getName(), "get")) {
            return JavaAstUtils.hasField(enclosing, node.getName().substring(3));
        } else if (startsWithCamelCaseWord(node.getName(), "is")
                && TypeTestUtil.isA(boolean.class, node.getResultTypeNode())) {
            return JavaAstUtils.hasField(enclosing, node.getName().substring(2));
        }

        return JavaAstUtils.hasField(enclosing, node.getName());
    }

    /** Attempts to determine if the method is a setter. */
    private static boolean isSetter(ASTMethodDeclaration node) {

        if (node.getArity() != 1 || !node.isVoid()) {
            return false;
        }

        ASTAnyTypeDeclaration enclosing = node.getEnclosingType();

        if (startsWithCamelCaseWord(node.getName(), "set")) {
            return JavaAstUtils.hasField(enclosing, node.getName().substring(3));
        }

        return JavaAstUtils.hasField(enclosing, node.getName());
    }

    // TODO at least UnusedPrivateMethod has some serialization-related logic.

    /**
     * Whether some variable declared by the given node is a serialPersistentFields
     * (serialization-specific field).
     */
    public static boolean isSerialPersistentFields(final ASTFieldDeclaration field) {
        return field.hasModifiers(JModifier.FINAL, JModifier.STATIC, JModifier.PRIVATE)
            && field.getVarIds().any(it -> "serialPersistentFields".equals(it.getName()) && TypeTestUtil.isA(ObjectStreamField[].class, it));
    }

    /**
     * Whether some variable declared by the given node is a serialVersionUID
     * (serialization-specific field).
     */
    public static boolean isSerialVersionUID(ASTFieldDeclaration field) {
        return field.hasModifiers(JModifier.FINAL, JModifier.STATIC)
            && field.getVarIds().any(it -> "serialVersionUID".equals(it.getName()) && it.getTypeMirror().isPrimitive(LONG));
    }

    /**
     * True if the method is a {@code readObject} method defined for serialization.
     */
    public static boolean isSerializationReadObject(ASTMethodDeclaration node) {
        return node.getVisibility() == Visibility.V_PRIVATE
            && "readObject".equals(node.getName())
            && JavaAstUtils.hasExceptionList(node, InvalidObjectException.class)
            && JavaAstUtils.hasParameters(node, ObjectInputStream.class);
    }


    /**
     * Whether the node or one of its descendants is an expression with
     * side effects. Conservatively, any method call is a potential side-effect,
     * as well as assignments to fields or array elements. We could relax
     * this assumption with (much) more data-flow logic, including a memory model.
     *
     * <p>By default assignments to locals are not counted as side-effects,
     * unless the lhs is in the given set of symbols.
     *
     * @param node             A node
     * @param localVarsToTrack Local variables to track
     */
    public static boolean hasSideEffect(@Nullable JavaNode node, Set<? extends JVariableSymbol> localVarsToTrack) {
        return node != null && node.descendantsOrSelf()
                                   .filterIs(ASTExpression.class)
                                   .any(e -> hasSideEffectNonRecursive(e, localVarsToTrack));
    }

    /**
     * Returns true if the expression has side effects we don't track.
     * Does not recurse into sub-expressions.
     */
    private static boolean hasSideEffectNonRecursive(ASTExpression e, Set<? extends JVariableSymbol> localVarsToTrack) {
        if (e instanceof ASTAssignmentExpression) {
            ASTAssignableExpr lhs = ((ASTAssignmentExpression) e).getLeftOperand();
            return isNonLocalLhs(lhs) || JavaAstUtils.isReferenceToVar(lhs, localVarsToTrack);
        } else if (e instanceof ASTUnaryExpression) {
            ASTUnaryExpression unary = (ASTUnaryExpression) e;
            ASTExpression lhs = unary.getOperand();
            return !unary.getOperator().isPure()
                && (isNonLocalLhs(lhs) || JavaAstUtils.isReferenceToVar(lhs, localVarsToTrack));
        }

        // when there are throw statements,
        // then this side effect can never be observed in containing code,
        // because control flow jumps out of the method
        return e.ancestors(ASTThrowStatement.class).isEmpty()
                && (e instanceof ASTMethodCall && !isPure((ASTMethodCall) e)
                        || e instanceof ASTConstructorCall);
    }

    private static boolean isNonLocalLhs(ASTExpression lhs) {
        return lhs instanceof ASTArrayAccess || !JavaAstUtils.isReferenceToLocal(lhs);
    }

    /**
     * Whether the invocation has no side-effects. Very conservative.
     */
    private static boolean isPure(ASTMethodCall call) {
        return isGetterCall(call) || KNOWN_PURE_METHODS.anyMatch(call);
    }

    public static @Nullable ASTVariableDeclaratorId getReferencedNode(ASTNamedReferenceExpr expr) {
        JVariableSymbol referencedSym = expr.getReferencedSym();
        return referencedSym == null ? null : referencedSym.tryGetNode();
    }

    /**
     * Checks whether the given node is annotated with any lombok annotation.
     * The node should be annotateable.
     *
     * @param node
     *            the Annotatable node to check
     * @return <code>true</code> if a lombok annotation has been found
     */
    public static boolean hasLombokAnnotation(Annotatable node) {
        return LOMBOK_ANNOTATIONS.stream().anyMatch(node::isAnnotationPresent);
    }

    /**
     * Returns true if the expression is a null check on the given variable.
     */
    public static boolean isNullCheck(ASTExpression expr, JVariableSymbol var) {
        return isNullCheck(expr, StablePathMatcher.matching(var));
    }

    public static boolean isNullCheck(ASTExpression expr, StablePathMatcher matcher) {
        if (expr instanceof ASTInfixExpression) {
            ASTInfixExpression condition = (ASTInfixExpression) expr;
            if (condition.getOperator().hasSamePrecedenceAs(BinaryOp.EQ)) {
                ASTNullLiteral nullLit = condition.firstChild(ASTNullLiteral.class);
                if (nullLit != null) {
                    return matcher.matches(JavaAstUtils.getOtherOperandIfInInfixExpr(nullLit));
                }
            }
        }
        return false;
    }

    /**
     * Returns true if the expr is in a null check (its parent is a null check).
     */
    public static boolean isNullChecked(ASTExpression expr) {
        if (expr.getParent() instanceof ASTInfixExpression) {
            ASTInfixExpression infx = (ASTInfixExpression) expr.getParent();
            if (infx.getOperator().hasSamePrecedenceAs(BinaryOp.EQ)) {
                return JavaAstUtils.getOtherOperandIfInInfixExpr(expr) instanceof ASTNullLiteral;
            }
        }
        return false;
    }


}
