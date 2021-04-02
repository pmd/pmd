/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Comparator;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Represents the operator of an {@linkplain ASTInfixExpression infix expression}.
 * Constants are roughly ordered by precedence, except some of them have the same
 * precedence.
 *
 * <p>All of those operators are left-associative.
 *
 * @see UnaryOp
 * @see AssignmentOp
 */
public enum BinaryOp implements InternalInterfaces.OperatorLike {

    // shortcut boolean ops

    /** Conditional (shortcut) OR {@code "||"} operator. */
    CONDITIONAL_OR("||"),
    /** Conditional (shortcut) AND {@code "&&"} operator. */
    CONDITIONAL_AND("&&"),

    // non-shortcut (also bitwise)

    /** OR {@code "|"} operator. Either logical or bitwise depending on the type of the operands. */
    OR("|"),
    /** XOR {@code "^"} operator. Either logical or bitwise depending on the type of the operands. */
    XOR("^"),
    /** AND {@code "&"} operator. Either logical or bitwise depending on the type of the operands. */
    AND("&"),

    // equality

    /** Equals {@code "=="} operator. */
    EQ("=="),
    /** Not-equals {@code "!="} operator. */
    NE("!="),

    // relational

    /** Lower-or-equal {@code "<="} operator. */
    LE("<="),
    /** Greater-or-equal {@code ">="} operator. */
    GE(">="),
    /** Greater-than {@code ">"} operator. */
    GT(">"),
    /** Lower-than {@code "<"} operator. */
    LT("<"),
    /** Type test {@code "instanceof"} operator. */
    INSTANCEOF("instanceof"),

    // shift

    /** Left shift {@code "<<"} operator. */
    LEFT_SHIFT("<<"),
    /** Right shift {@code ">>"} operator. */
    RIGHT_SHIFT(">>"),
    /** Unsigned right shift {@code ">>>"} operator. */
    UNSIGNED_RIGHT_SHIFT(">>>"),

    // additive

    /** Addition {@code "+"} operator, or string concatenation. */
    ADD("+"),
    /** Subtraction {@code "-"} operator. */
    SUB("-"),

    // multiplicative

    /** Multiplication {@code "*"} operator. */
    MUL("*"),
    /** Division {@code "/"} operator. */
    DIV("/"),
    /** Modulo {@code "%"} operator. */
    MOD("%");

    /**
     * Use with {@link #isInfixExprWithOperator(JavaNode, Set)}.
     */
    public static final Set<BinaryOp> COMPARISON_OPS = CollectionUtil.immutableEnumSet(LE, GE, GT, LT);

    private final String code;


    BinaryOp(String code) {
        this.code = code;
    }


    @Override
    public String getToken() {
        return code;
    }


    @Override
    public String toString() {
        return this.code;
    }

    /**
     * Compare the precedence of this operator with that of the other,
     * as if with a {@link Comparator}. Returns a positive integer if
     * this operator has a higher precedence as the argument, zero if
     * they have the same precedence, etc.
     *
     * @throws NullPointerException If the argument is null
     */
    public int comparePrecedence(@NonNull BinaryOp other) {
        // arguments are flipped because precedence class decreases
        return Integer.compare(other.precedenceClass(), this.precedenceClass());
    }

    /**
     * Returns true if this operator has the same relative precedence
     * as the argument. For example, {@link #ADD} and {@link #SUB} have
     * the same precedence.
     *
     * @throws NullPointerException If the argument is null
     */
    public boolean hasSamePrecedenceAs(@NonNull BinaryOp other) {
        return comparePrecedence(other) == 0;
    }

    private int precedenceClass() {
        switch (this) {
        case CONDITIONAL_OR:
            return 9;
        case CONDITIONAL_AND:
            return 8;
        case OR:
            return 7;
        case XOR:
            return 6;
        case AND:
            return 5;
        case EQ:
        case NE:
            return 4;
        case LE:
        case GE:
        case GT:
        case LT:
        case INSTANCEOF:
            return 3;
        case LEFT_SHIFT:
        case RIGHT_SHIFT:
        case UNSIGNED_RIGHT_SHIFT:
            return 2;
        case ADD:
        case SUB:
            return 1;
        case MUL:
        case DIV:
        case MOD:
            return 0;
        default:
            return -1;
        }
    }


    /**
     * Complement, for boolean operators. Eg for {@code ==}, return {@code !=},
     * for {@code <=}, returns {@code >}. Returns null if this is another kind
     * of operator.
     */
    public @Nullable BinaryOp getComplement() {
        switch (this) {
        case CONDITIONAL_OR: return CONDITIONAL_AND;
        case CONDITIONAL_AND: return CONDITIONAL_OR;
        case OR: return AND;
        case AND: return OR;

        case EQ: return NE;
        case NE: return EQ;
        case LE: return GT;
        case GE: return LT;
        case GT: return LE;
        case LT: return GE;

        default: return null;
        }
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
}
