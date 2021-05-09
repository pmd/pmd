/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.LONG;
import static net.sourceforge.pmd.util.CollectionUtil.immutableSetOf;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher.CompoundInvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;

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
        "lombok.Builder"
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

            return isLiteralInt(comparand, expectedValue);
        }
        return false;
    }


    /**
     * Returns true if this is a numeric literal with the given int value.
     * This also considers long literals.
     */
    public static boolean isLiteralInt(JavaNode e, int value) {
        if (e instanceof ASTNumericLiteral) {
            return ((ASTNumericLiteral) e).isIntegral() && ((ASTNumericLiteral) e).getValueAsInt() == value;
        }
        return false;
    }

    /** This is type-aware, so will not pick up on numeric addition. */
    public static boolean isStringConcatExpr(@Nullable JavaNode e) {
        if (e instanceof ASTInfixExpression) {
            ASTInfixExpression infix = (ASTInfixExpression) e;
            return infix.getOperator() == BinaryOp.ADD && TypeTestUtil.isA(String.class, infix);
        }
        return false;
    }

    /**
     * If the parameter is an operand of a binary infix expression,
     * returns the other operand. Otherwise returns null.
     */
    public static @Nullable ASTExpression getOtherOperandIfInInfixExpr(@Nullable JavaNode e) {
        if (e != null && e.getParent() instanceof ASTInfixExpression) {
            return (ASTExpression) e.getParent().getChild(1 - e.getIndexInParent());
        }
        return null;
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
     * Returns true if the node is a {@link ASTMethodDeclaration} that
     * is a main method.
     */
    public static boolean isMainMethod(JavaNode node) {
        if (node instanceof ASTMethodDeclaration) {
            return ((ASTMethodDeclaration) node).isMainMethod();
        }
        return false;
    }

    /**
     * Returns true if the node is a utility class, according to this
     * custom definition.
     */
    public static boolean isUtilityClass(ASTAnyTypeDeclaration node) {
        if (node.isInterface() || node.isEnum()) {
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

                hasAny = isNonPrivate(declNode) && !isMainMethod(declNode);
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
            || "_".equals(name); // before java 9 it's ok
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
            return hasField(enclosing, node.getName().substring(3));
        } else if (startsWithCamelCaseWord(node.getName(), "is")) {
            return hasField(enclosing, node.getName().substring(2));
        }

        return hasField(enclosing, node.getName());
    }

    /** Attempts to determine if the method is a setter. */
    private static boolean isSetter(ASTMethodDeclaration node) {

        if (node.getArity() != 1 || !node.isVoid()) {
            return false;
        }

        ASTAnyTypeDeclaration enclosing = node.getEnclosingType();

        if (startsWithCamelCaseWord(node.getName(), "set")) {
            return hasField(enclosing, node.getName().substring(3));
        }

        return hasField(enclosing, node.getName());
    }

    private static boolean hasField(ASTAnyTypeDeclaration node, String name) {
        for (JFieldSymbol f : node.getSymbol().getDeclaredFields()) {
            String fname = f.getSimpleName();
            if (fname.startsWith("m_") || fname.startsWith("_")) {
                fname = fname.substring(fname.indexOf('_') + 1);
            }
            if (fname.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the formal parameters of the method or constructor
     * match the given types exactly. Note that for varargs methods, the
     * last param must have an array type (but it is not checked to be varargs).
     * This will return false if we're not sure.
     *
     * @param node  Method or ctor
     * @param types List of types to match (may be empty)
     *
     * @throws NullPointerException If any of the classes is null, or the node is null
     * @see TypeTestUtil#isExactlyA(Class, TypeNode)
     */
    public static boolean hasParameters(ASTMethodOrConstructorDeclaration node, Class<?>... types) {
        ASTFormalParameters formals = node.getFormalParameters();
        if (formals.size() != types.length) {
            return false;
        }
        for (int i = 0; i < formals.size(); i++) {
            ASTFormalParameter fi = formals.get(i);
            if (!TypeTestUtil.isExactlyA(types[i], fi)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the {@code throws} declaration of the method or constructor
     * matches the given types exactly.
     *
     * @param node  Method or ctor
     * @param types List of exception types to match (may be empty)
     *
     * @throws NullPointerException If any of the classes is null, or the node is null
     * @see TypeTestUtil#isExactlyA(Class, TypeNode)
     */
    @SafeVarargs
    public static boolean hasExceptionList(ASTMethodOrConstructorDeclaration node, Class<? extends Throwable>... types) {
        @NonNull List<ASTClassOrInterfaceType> formals = ASTList.orEmpty(node.getThrowsList());
        if (formals.size() != types.length) {
            return false;
        }
        for (int i = 0; i < formals.size(); i++) {
            ASTClassOrInterfaceType fi = formals.get(i);
            if (!TypeTestUtil.isExactlyA(types[i], fi)) {
                return false;
            }
        }
        return true;
    }

    /**
     * True if the variable is never used. Note that the visibility of
     * the variable must be less than {@link Visibility#V_PRIVATE} for
     * us to be sure of it.
     */
    public static boolean isNeverUsed(ASTVariableDeclaratorId varId) {
        return CollectionUtil.none(varId.getLocalUsages(), JavaRuleUtil::isReadUsage);
    }

    private static boolean isReadUsage(ASTNamedReferenceExpr expr) {
        return expr.getAccessType() == AccessType.READ
            // foo(x++)
            || expr.getParent() instanceof ASTUnaryExpression
            && expr.getParent().getParent() instanceof ASTArgumentList;
    }

    /**
     * True if the variable is incremented or decremented via a compound
     * assignment operator, or a unary increment/decrement expression.
     */
    public static boolean isVarAccessReadAndWrite(ASTNamedReferenceExpr expr) {
        return expr.getAccessType() == AccessType.WRITE
            && (!(expr.getParent() instanceof ASTAssignmentExpression)
            || ((ASTAssignmentExpression) expr.getParent()).getOperator().isCompound());
    }

    /**
     * True if the variable access is a non-compound assignment.
     */
    public static boolean isVarAccessStrictlyWrite(ASTNamedReferenceExpr expr) {
        return expr.getParent() instanceof ASTAssignmentExpression
            && expr.getIndexInParent() == 0
            && !((ASTAssignmentExpression) expr.getParent()).getOperator().isCompound();
    }

    /**
     * Returns the set of labels on this statement.
     */
    public static Set<String> getStatementLabels(ASTStatement node) {
        if (!(node.getParent() instanceof ASTLabeledStatement)) {
            return Collections.emptySet();
        }

        return node.ancestors().takeWhile(it -> it instanceof ASTLabeledStatement)
                   .toStream()
                   .map(it -> ((ASTLabeledStatement) it).getLabel())
                   .collect(Collectors.toSet());
    }

    public static boolean isAnonymousClassCreation(@Nullable ASTExpression expression) {
        if (expression instanceof ASTConstructorCall) {
            return ((ASTConstructorCall) expression).isAnonymousClass();
        }
        return false;
    }

    /**
     * Will cut through argument lists, except those of enum constants
     * and explicit invocation nodes.
     */
    public static @NonNull ASTExpression getTopLevelExpr(ASTExpression expr) {
        return (ASTExpression) expr.ancestorsOrSelf()
                                   .takeWhile(it -> it instanceof ASTExpression
                                       || it instanceof ASTArgumentList && it.getParent() instanceof ASTExpression)
                                   .last();
    }

    /**
     * Returns the variable IDS corresponding to variables declared in
     * the init clause of the loop.
     */
    public static NodeStream<ASTVariableDeclaratorId> getLoopVariables(ASTForStatement loop) {
        return NodeStream.of(loop.getInit())
                         .filterIs(ASTLocalVariableDeclaration.class)
                         .flatMap(ASTLocalVariableDeclaration::getVarIds);
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
            && hasExceptionList(node, InvalidObjectException.class)
            && hasParameters(node, ObjectInputStream.class);
    }

    /**
     * Whether one expression is the boolean negation of the other. Many
     * forms are not yet supported. This method is symmetric so only needs
     * to be called once.
     */
    public static boolean areComplements(ASTExpression e1, ASTExpression e2) {
        if (isBooleanNegation(e1)) {
            return areEqual(unaryOperand(e1), e2);
        } else if (isBooleanNegation(e2)) {
            return areEqual(e1, unaryOperand(e2));
        } else if (e1 instanceof ASTInfixExpression && e2 instanceof ASTInfixExpression) {
            ASTInfixExpression ifx1 = (ASTInfixExpression) e1;
            ASTInfixExpression ifx2 = (ASTInfixExpression) e2;
            if (ifx1.getOperator().getComplement() != ifx2.getOperator()) {
                return false;
            }
            if (ifx1.getOperator().hasSamePrecedenceAs(BinaryOp.EQ)) {
                // NOT(a == b, a != b)
                // NOT(a == b, b != a)
                return areEqual(ifx1.getLeftOperand(), ifx2.getLeftOperand())
                    && areEqual(ifx1.getRightOperand(), ifx2.getRightOperand())
                    || areEqual(ifx2.getLeftOperand(), ifx1.getLeftOperand())
                    && areEqual(ifx2.getRightOperand(), ifx1.getRightOperand());
            }
            // todo we could continue with de Morgan and such
        }
        return false;
    }

    private static boolean areEqual(ASTExpression e1, ASTExpression e2) {
        return tokenEquals(e1, e2);
    }

    /**
     * Returns true if both nodes have exactly the same tokens.
     *
     * @param node First node
     * @param that Other node
     */
    public static boolean tokenEquals(JavaNode node, JavaNode that) {
        return tokenEquals(node, that, null);
    }

    /**
     * Returns true if both nodes have the same tokens, modulo some renaming
     * function. The renaming function maps unqualified variables and type
     * identifiers of the first node to the other. This should be used
     * in nodes living in the same lexical scope, so that unqualified
     * names mean the same thing.
     *
     * @param node       First node
     * @param other      Other node
     * @param varRenamer A renaming function. If null, no renaming is applied.
     *                   Must not return null, if no renaming occurs, returns its argument.
     */
    public static boolean tokenEquals(@NonNull JavaNode node,
                                      @NonNull JavaNode other,
                                      @Nullable Function<String, @NonNull String> varRenamer) {
        // Since type and variable names obscure one another,
        // it's ok to use a single renaming function.

        Iterator<JavaccToken> thisIt = GenericToken.range(node.getFirstToken(), node.getLastToken());
        Iterator<JavaccToken> thatIt = GenericToken.range(other.getFirstToken(), other.getLastToken());
        int lastKind = 0;
        while (thisIt.hasNext()) {
            if (!thatIt.hasNext()) {
                return false;
            }
            JavaccToken o1 = thisIt.next();
            JavaccToken o2 = thatIt.next();
            if (o1.kind != o2.kind) {
                return false;
            }

            String mappedImage = o1.getImage();
            if (varRenamer != null
                && o1.kind == JavaTokenKinds.IDENTIFIER
                && lastKind != JavaTokenKinds.DOT
                && lastKind != JavaTokenKinds.METHOD_REF
                //method name
                && o1.getNext() != null && o1.getNext().kind != JavaTokenKinds.LPAREN) {
                mappedImage = varRenamer.apply(mappedImage);
            }

            if (!o2.getImage().equals(mappedImage)) {
                return false;
            }

            lastKind = o1.kind;
        }
        return !thatIt.hasNext();
    }

    public static boolean isBooleanLiteral(ASTExpression e) {
        return e instanceof ASTBooleanLiteral;
    }

    public static boolean isBooleanNegation(ASTExpression e) {
        return e instanceof ASTUnaryExpression && ((ASTUnaryExpression) e).getOperator() == UnaryOp.NEGATION;
    }

    /**
     * If the argument is a unary expression, returns its operand, otherwise
     * returns null.
     */
    public static @Nullable ASTExpression unaryOperand(@Nullable ASTExpression e) {
        return e instanceof ASTUnaryExpression ? ((ASTUnaryExpression) e).getOperand()
                                               : null;
    }


    /**
     * Whether the expression is an access to a field of this instance,
     * not inherited, qualified or not ({@code this.field} or just {@code field}).
     */
    public static boolean isThisFieldAccess(ASTExpression e) {
        if (!(e instanceof ASTNamedReferenceExpr)) {
            return false;
        }
        JVariableSymbol sym = ((ASTNamedReferenceExpr) e).getReferencedSym();
        if (sym instanceof JFieldSymbol) {
            return !((JFieldSymbol) sym).isStatic()
                // not inherited
                && ((JFieldSymbol) sym).getEnclosingClass().equals(e.getEnclosingType().getSymbol())
                // correct syntactic form
                && e instanceof ASTVariableAccess || isSyntacticThisFieldAccess(e);
        }
        return false;
    }

    /**
     * Whether the expression is a {@code this.field}, with no outer
     * instance qualifier ({@code Outer.this.field}). The field symbol
     * is not checked to resolve to a field declared in this class (it
     * may be inherited)
     */
    public static boolean isSyntacticThisFieldAccess(ASTExpression e) {
        if (e instanceof ASTFieldAccess) {
            ASTExpression qualifier = ((ASTFieldAccess) e).getQualifier();
            if (qualifier instanceof ASTThisExpression) {
                // unqualified this
                return ((ASTThisExpression) qualifier).getQualifier() == null;
            }
        }
        return false;
    }

    public static boolean hasAnyAnnotation(Annotatable node, Collection<String> qualifiedNames) {
        return qualifiedNames.stream().anyMatch(node::isAnnotationPresent);
    }


    /**
     * Returns true if the expression is the default field value for
     * the given type.
     */
    public static boolean isDefaultValue(JTypeMirror type, ASTExpression expr) {
        if (type.isPrimitive()) {
            if (type.isPrimitive(PrimitiveTypeKind.BOOLEAN)) {
                return expr instanceof ASTBooleanLiteral && !((ASTBooleanLiteral) expr).isTrue();
            } else {
                Object constValue = expr.getConstValue();
                return constValue instanceof Number && ((Number) constValue).doubleValue() == 0d
                    || constValue instanceof Character && constValue.equals('\u0000');
            }
        } else {
            return expr instanceof ASTNullLiteral;
        }
    }

    /**
     * Returns true if the expression is a {@link ASTNamedReferenceExpr}
     * that references the symbol.
     */
    public static boolean isReferenceToVar(@Nullable ASTExpression expression, @NonNull JVariableSymbol symbol) {
        if (expression instanceof ASTNamedReferenceExpr) {
            return symbol.equals(((ASTNamedReferenceExpr) expression).getReferencedSym());
        }
        return false;
    }

    public static boolean isUnqualifiedThis(ASTExpression e) {
        return e instanceof ASTThisExpression && ((ASTThisExpression) e).getQualifier() == null;
    }

    /**
     * Returns true if the expression is a {@link ASTNamedReferenceExpr}
     * that references any of the symbol in the set.
     */
    public static boolean isReferenceToVar(@Nullable ASTExpression expression, @NonNull Set<? extends JVariableSymbol> symbols) {
        if (expression instanceof ASTNamedReferenceExpr) {
            return symbols.contains(((ASTNamedReferenceExpr) expression).getReferencedSym());
        }
        return false;
    }

    /**
     * Returns true if both expressions refer to the same variable.
     * A "variable" here can also means a field path, eg, {@code this.field.a}.
     * This method unifies {@code this.field} and {@code field} if possible,
     * and also considers {@code this}.
     *
     * <p>Note that while this is more useful than just checking whether
     * both expressions access the same symbol, it still does not mean that
     * they both access the same <i>value</i>. The actual value is data-flow
     * dependent.
     */
    public static boolean isReferenceToSameVar(ASTExpression e1, ASTExpression e2) {
        if (e1 instanceof ASTNamedReferenceExpr && e2 instanceof ASTNamedReferenceExpr) {
            if (!Objects.equals(((ASTNamedReferenceExpr) e2).getReferencedSym(),
                                ((ASTNamedReferenceExpr) e1).getReferencedSym())) {
                return false;
            }

            if (e1.getClass() != e2.getClass()) {
                // unify `this.f` and `f`
                // note, we already know that the symbol is the same so there's no scoping problem
                return isSyntacticThisFieldAccess(e1) || isSyntacticThisFieldAccess(e2);
            } else if (e1 instanceof ASTFieldAccess && e2 instanceof ASTFieldAccess) {
                return isReferenceToSameVar(((ASTFieldAccess) e1).getQualifier(),
                                            ((ASTFieldAccess) e2).getQualifier());
            }
            return e1 instanceof ASTVariableAccess && e2 instanceof ASTVariableAccess;
        } else if (e1 instanceof ASTThisExpression || e2 instanceof ASTThisExpression) {
            return e1.getClass() == e2.getClass();
        }
        return false;
    }

    private static boolean isSyntacticThisFieldAccess(ASTExpression v1) {
        if (v1 instanceof ASTFieldAccess) {
            return ((ASTFieldAccess) v1).getQualifier() instanceof ASTThisExpression;
        }
        return false;
    }

    /**
     * Returns true if the expression is a reference to a local variable.
     */
    public static boolean isReferenceToLocal(ASTExpression expr) {
        if (expr instanceof ASTVariableAccess) {
            JVariableSymbol sym = ((ASTVariableAccess) expr).getReferencedSym();
            return sym != null && !sym.isField();
        }
        return false;
    }

    /**
     * Returns true if the expression has the form `field`, or `this.field`,
     * where `field` is a field declared in the enclosing class.
     * Assumes we're not in a static context.
     * todo this should probs consider super.field and superclass
     */
    public static boolean isRefToFieldOfThisInstance(ASTExpression usage) {
        if (!(usage instanceof ASTNamedReferenceExpr)) {
            return false;
        }
        JVariableSymbol symbol = ((ASTNamedReferenceExpr) usage).getReferencedSym();
        if (!(symbol instanceof JFieldSymbol)
            || !((JFieldSymbol) symbol).getEnclosingClass().equals(usage.getEnclosingType().getSymbol())
            || Modifier.isStatic(((JFieldSymbol) symbol).getModifiers())) {
            return false;
        }

        if (usage instanceof ASTVariableAccess) {
            return true;
        } else if (usage instanceof ASTFieldAccess) {
            ASTExpression qualifier = ((ASTFieldAccess) usage).getQualifier();
            return qualifier instanceof ASTThisExpression
                || qualifier instanceof ASTSuperExpression;
        }
        return false;
    }

    /**
     * Return a node stream containing all the operands of an addition expression.
     * For instance, {@code a+b+c} will be parsed as a tree with two levels.
     * This method will return a flat node stream containing {@code a, b, c}.
     *
     * @param e An expression, if it is not a string concatenation expression,
     *          then returns an empty node stream.
     */
    public static NodeStream<ASTExpression> flattenOperands(ASTExpression e) {
        List<ASTExpression> result = new ArrayList<>();
        flattenOperandsRec(e, result);
        return NodeStream.fromIterable(result);
    }

    private static void flattenOperandsRec(ASTExpression e, List<ASTExpression> result) {
        if (isStringConcatExpression(e)) {
            ASTInfixExpression infix = (ASTInfixExpression) e;
            flattenOperandsRec(infix.getLeftOperand(), result);
            flattenOperandsRec(infix.getRightOperand(), result);
        } else {
            result.add(e);
        }
    }

    private static boolean isStringConcatExpression(ASTExpression e) {
        return BinaryOp.isInfixExprWithOperator(e, BinaryOp.ADD) && TypeTestUtil.isA(String.class, e);
    }

    /**
     * Returns true if the node is the last child of its parent (or is the root node).
     */
    public static boolean isLastChild(Node it) {
        Node parent = it.getParent();
        return parent == null || it.getIndexInParent() == parent.getNumChildren() - 1;
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
            return isNonLocalLhs(lhs) || isReferenceToVar(lhs, localVarsToTrack);
        } else if (e instanceof ASTUnaryExpression) {
            ASTUnaryExpression unary = (ASTUnaryExpression) e;
            ASTExpression lhs = unary.getOperand();
            return !unary.getOperator().isPure()
                && (isNonLocalLhs(lhs) || isReferenceToVar(lhs, localVarsToTrack));
        }

        if (e.ancestors(ASTThrowStatement.class).nonEmpty()) {
            // then this side effect can never be observed in containing code,
            // because control flow jumps out of the method
            return false;
        }

        return e instanceof ASTMethodCall && !isPure((ASTMethodCall) e)
            || e instanceof ASTConstructorCall;
    }

    private static boolean isNonLocalLhs(ASTExpression lhs) {
        return lhs instanceof ASTArrayAccess || !isReferenceToLocal(lhs);
    }

    /**
     * Whether the invocation has no side-effects. Very conservative.
     */
    private static boolean isPure(ASTMethodCall call) {
        return isGetterCall(call) || KNOWN_PURE_METHODS.anyMatch(call);
    }
}
