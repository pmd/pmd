/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.internal;

import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.FORMAL_COMMENT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.MULTI_LINE_COMMENT;
import static net.sourceforge.pmd.lang.java.ast.JavaTokenKinds.SINGLE_LINE_COMMENT;

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
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.ast.AstLocalVarSym;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Common utility functions to work with the Java AST. See also
 * {@link TypeTestUtil}. Only add here things that are not specific to
 * rules (use {@link JavaRuleUtil} for that). This API may be eventually
 * published.
 */
public final class JavaAstUtils {

    private JavaAstUtils() {
        // utility class
    }


    public static boolean isConditional(JavaNode ifx) {
        return isInfixExprWithOperator(ifx, BinaryOp.CONDITIONAL_OPS);
    }

    public static int numAlternatives(ASTSwitchBranch n) {
        return n.isDefault() ? 1 : n.getLabel().getExprList().count();
    }

    /**
     * Returns true if this is a numeric literal with the given int value.
     * This also considers long literals.
     */
    public static boolean isLiteralInt(JavaNode e, int value) {
        return e instanceof ASTNumericLiteral
                && ((ASTNumericLiteral) e).isIntegral()
                && ((ASTNumericLiteral) e).getValueAsInt() == value;
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

    public static @Nullable ASTExpression getOtherOperandIfInAssignmentExpr(@Nullable JavaNode e) {
        if (e != null && e.getParent() instanceof ASTAssignmentExpression) {
            return (ASTExpression) e.getParent().getChild(1 - e.getIndexInParent());
        }
        return null;
    }

    /**
     * Returns true if the node is a {@link ASTMethodDeclaration} that
     * is a main method.
     */
    public static boolean isMainMethod(JavaNode node) {
        return node instanceof ASTMethodDeclaration
                && ((ASTMethodDeclaration) node).isMainMethod();
    }

    public static boolean hasField(ASTAnyTypeDeclaration node, String name) {
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
        return CollectionUtil.none(varId.getLocalUsages(), JavaAstUtils::isReadUsage);
    }

    private static boolean isReadUsage(ASTNamedReferenceExpr expr) {
        return expr.getAccessType() == AccessType.READ
            // x++ as a method argument or used in other expression
            || expr.getParent() instanceof ASTUnaryExpression
            && !(expr.getParent().getParent() instanceof ASTExpressionStatement);
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
        return expression instanceof ASTConstructorCall
                && ((ASTConstructorCall) expression).isAnonymousClass();
    }

    /**
     * Will cut through argument lists, except those of enum constants
     * and explicit invocation nodes.
     */
    public static @NonNull ASTExpression getTopLevelExpr(ASTExpression expr) {
        JavaNode last = expr.ancestorsOrSelf()
                            .takeWhile(it -> it instanceof ASTExpression
                                || it instanceof ASTArgumentList && it.getParent() instanceof ASTExpression)
                            .last();
        return (ASTExpression) Objects.requireNonNull(last);
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

        Iterator<JavaccToken> thisIt = GenericToken.range(node.getFirstToken(), node.getLastToken()).iterator();
        Iterator<JavaccToken> thatIt = GenericToken.range(other.getFirstToken(), other.getLastToken()).iterator();
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

    public static boolean isNullLiteral(ASTExpression node) {
        return node instanceof ASTNullLiteral;
    }

    /** Returns true if the node is a boolean literal with any value. */
    public static boolean isBooleanLiteral(JavaNode e) {
        return e instanceof ASTBooleanLiteral;
    }

    /** Returns true if the node is a boolean literal with the given constant value. */
    public static boolean isBooleanLiteral(JavaNode e, boolean value) {
        return e instanceof ASTBooleanLiteral && ((ASTBooleanLiteral) e).isTrue() == value;
    }

    public static boolean isBooleanNegation(JavaNode e) {
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
        return sym instanceof JFieldSymbol
                && !((JFieldSymbol) sym).isStatic()
                // not inherited
                && ((JFieldSymbol) sym).getEnclosingClass().equals(e.getEnclosingType().getSymbol())
                // correct syntactic form
                && (e instanceof ASTVariableAccess || isSyntacticThisFieldAccess(e));
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
        return expression instanceof ASTNamedReferenceExpr
            && symbol.equals(((ASTNamedReferenceExpr) expression).getReferencedSym());
    }

    public static boolean isUnqualifiedThis(ASTExpression e) {
        return e instanceof ASTThisExpression && ((ASTThisExpression) e).getQualifier() == null;
    }

    public static boolean isUnqualifiedSuper(ASTExpression e) {
        return e instanceof ASTSuperExpression && ((ASTSuperExpression) e).getQualifier() == null;
    }

    public static boolean isUnqualifiedThisOrSuper(ASTExpression e) {
        return isUnqualifiedSuper(e) || isUnqualifiedThis(e);
    }

    /**
     * Returns true if the expression is a {@link ASTNamedReferenceExpr}
     * that references any of the symbol in the set.
     */
    public static boolean isReferenceToVar(@Nullable ASTExpression expression, @NonNull Set<? extends JVariableSymbol> symbols) {
        return expression instanceof ASTNamedReferenceExpr
            && symbols.contains(((ASTNamedReferenceExpr) expression).getReferencedSym());
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
            if (OptionalBool.YES != referenceSameSymbol((ASTNamedReferenceExpr) e1, (ASTNamedReferenceExpr) e2)) {
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

    private static OptionalBool referenceSameSymbol(ASTNamedReferenceExpr e1, ASTNamedReferenceExpr e2) {
        if (!e1.getName().equals(e2.getName())) {
            return OptionalBool.NO;
        }
        JVariableSymbol ref1 = e1.getReferencedSym();
        JVariableSymbol ref2 = e2.getReferencedSym();
        if (ref1 == null || ref2 == null) {
            return OptionalBool.UNKNOWN;
        }
        return OptionalBool.definitely(ref1.equals(ref2));
    }

    /**
     * Returns true if the expression is a reference to a local variable.
     */
    public static boolean isReferenceToLocal(ASTExpression expr) {
        return expr instanceof ASTVariableAccess
                && ((ASTVariableAccess) expr).getReferencedSym() instanceof AstLocalVarSym;
    }

    /**
     * Returns true if the expression has the form `field`, or `this.field`,
     * where `field` is a field declared in the enclosing class. Considers
     * inherited fields. Assumes we're not in a static context.
     */
    public static boolean isRefToFieldOfThisInstance(ASTExpression usage) {
        if (!(usage instanceof ASTNamedReferenceExpr)) {
            return false;
        }
        JVariableSymbol symbol = ((ASTNamedReferenceExpr) usage).getReferencedSym();
        if (!(symbol instanceof JFieldSymbol)) {
            return false;
        }

        if (usage instanceof ASTVariableAccess) {
            return !Modifier.isStatic(((JFieldSymbol) symbol).getModifiers());
        } else if (usage instanceof ASTFieldAccess) {
            return isUnqualifiedThisOrSuper(((ASTFieldAccess) usage).getQualifier());
        }
        return false;
    }

    /**
     * Returns true if the expression is a reference to a field declared
     * in this class (not a superclass), on any instance (not just `this`).
     */
    public static boolean isRefToFieldOfThisClass(ASTExpression usage) {
        if (!(usage instanceof ASTNamedReferenceExpr)) {
            return false;
        }
        JVariableSymbol symbol = ((ASTNamedReferenceExpr) usage).getReferencedSym();
        if (!(symbol instanceof JFieldSymbol)) {
            return false;
        }

        if (usage instanceof ASTVariableAccess) {
            return !Modifier.isStatic(((JFieldSymbol) symbol).getModifiers());
        } else if (usage instanceof ASTFieldAccess) {
            return Objects.equals(((JFieldSymbol) symbol).getEnclosingClass(),
                                  usage.getEnclosingType().getSymbol());
        }
        return false;
    }

    public static boolean isCallOnThisInstance(ASTMethodCall call) {
        // syntactic approach.
        if (call.getQualifier() != null) {
            return isUnqualifiedThisOrSuper(call.getQualifier());
        }

        // unqualified call
        JMethodSig mtype = call.getMethodType();
        return !mtype.getSymbol().isUnresolved()
            && mtype.getSymbol().getEnclosingClass().equals(call.getEnclosingType().getSymbol());
    }

    public static ASTClassOrInterfaceType getThisOrSuperQualifier(ASTExpression expr) {
        if (expr instanceof ASTThisExpression) {
            return ((ASTThisExpression) expr).getQualifier();
        } else if (expr instanceof ASTSuperExpression) {
            return ((ASTSuperExpression) expr).getQualifier();
        }
        return null;
    }

    public static boolean isThisOrSuper(ASTExpression expr) {
        return expr instanceof ASTThisExpression || expr instanceof ASTSuperExpression;
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
        if (isStringConcatExpr(e)) {
            ASTInfixExpression infix = (ASTInfixExpression) e;
            flattenOperandsRec(infix.getLeftOperand(), result);
            flattenOperandsRec(infix.getRightOperand(), result);
        } else {
            result.add(e);
        }
    }

    /**
     * Returns true if the node is the last child of its parent. Returns
     * false if this is the root node.
     */
    public static boolean isLastChild(Node it) {
        Node parent = it.getParent();
        return parent != null && it.getIndexInParent() == parent.getNumChildren() - 1;
    }

    /**
     * Returns a node stream of enclosing expressions in the same call chain.
     * For instance in {@code a.b().c().d()}, called on {@code a}, this will
     * yield {@code a.b()}, and {@code a.b().c()}.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static NodeStream<QualifiableExpression> followingCallChain(ASTExpression expr) {
        return (NodeStream) expr.ancestors().takeWhile(it -> it instanceof QualifiableExpression);
    }

    public static ASTExpression peelCasts(@Nullable ASTExpression expr) {
        while (expr instanceof ASTCastExpression) {
            expr = ((ASTCastExpression) expr).getOperand();
        }
        return expr;
    }

    public static boolean isArrayInitializer(ASTExpression expr) {
        return expr instanceof ASTArrayAllocation && ((ASTArrayAllocation) expr).getArrayInitializer() != null;
    }

    public static boolean isCloneMethod(ASTMethodDeclaration node) {
        // this is enough as in valid code, this signature overrides Object#clone
        // and the other things like visibility are checked by the compiler
        return "clone".equals(node.getName())
            && node.getArity() == 0
            && !node.isStatic();
    }

    public static boolean isArrayLengthFieldAccess(ASTExpression node) {
        if (node instanceof ASTFieldAccess) {
            ASTFieldAccess field = (ASTFieldAccess) node;
            return "length".equals(field.getName())
                && field.getQualifier().getTypeMirror().isArray();
        }
        return false;
    }

    /**
     * @see ASTBreakStatement#getTarget()
     */
    public static boolean mayBeBreakTarget(JavaNode it) {
        return it instanceof ASTLoopStatement
            || it instanceof ASTSwitchStatement
            || it instanceof ASTLabeledStatement;
    }

    /**
     * Tests if the node is an {@link ASTInfixExpression} with one of the given operators.
     */
    public static boolean isInfixExprWithOperator(@Nullable JavaNode e, Set<BinaryOp> operators) {
        if (e instanceof ASTInfixExpression) {
            ASTInfixExpression infix = (ASTInfixExpression) e;
            return operators.contains(infix.getOperator());
        }
        return false;
    }

    /**
     * Tests if the node is an {@link ASTInfixExpression} with the given operator.
     */
    public static boolean isInfixExprWithOperator(@Nullable JavaNode e, BinaryOp operator) {
        if (e instanceof ASTInfixExpression) {
            ASTInfixExpression infix = (ASTInfixExpression) e;
            return operator == infix.getOperator();
        }
        return false;
    }

    /**
     * Returns true if the given token is a Java comment.
     */
    public static boolean isComment(JavaccToken t) {
        switch (t.kind) {
        case FORMAL_COMMENT:
        case MULTI_LINE_COMMENT:
        case SINGLE_LINE_COMMENT:
            return true;
        default:
            return false;
        }
    }
}
