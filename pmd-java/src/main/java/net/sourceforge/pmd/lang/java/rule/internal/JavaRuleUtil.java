/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.LONG;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamField;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
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
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Utilities shared between rules.
 */
public final class JavaRuleUtil {

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

            return isIntLit(comparand, expectedValue);
        }
        return false;
    }

    private static boolean isIntLit(JavaNode e, int value) {
        if (e instanceof ASTNumericLiteral) {
            return ((ASTNumericLiteral) e).getValueAsInt() == value;
        }
        return false;
    }

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
            ASTMethodDeclaration decl = (ASTMethodDeclaration) node;


            return decl.hasModifiers(JModifier.PUBLIC, JModifier.STATIC)
                && "main".equals(decl.getName())
                && decl.isVoid()
                && decl.getArity() == 1
                && TypeTestUtil.isExactlyA(String[].class, decl.getFormalParameters().get(0));
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

    /**
     * True if the variable is incremented or decremented via a compound
     * assignment operator, or a unary increment/decrement expression.
     */
    public static boolean isInIfCondition(ASTExpression expr) {
        ASTExpression toplevel = getTopLevelExpr(expr);
        return toplevel.getIndexInParent() == 0 && toplevel.getParent() instanceof ASTIfStatement;
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
            && field.getVarIds().any(
            it -> "serialPersistentFields".equals(it.getName())
                && TypeTestUtil.isA(ObjectStreamField[].class, it)
        );
    }

    /**
     * Whether some variable declared by the given node is a serialVersionUID
     * (serialization-specific field).
     */
    public static boolean isSerialVersionUID(ASTFieldDeclaration field) {
        return field.hasModifiers(JModifier.FINAL, JModifier.STATIC)
            && field.getVarIds().any(
            it -> "serialVersionUID".equals(it.getName())
                && it.getTypeMirror().isPrimitive(LONG)
        );
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
    public static @Nullable ASTExpression unaryOperand(ASTExpression e) {
        return e instanceof ASTUnaryExpression ? ((ASTUnaryExpression) e).getOperand()
                                               : null;
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
}
