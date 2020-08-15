/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the operator of an {@linkplain ASTInfixExpression infix expression}.
 * Constants are roughly ordered by precedence, except some of them have the same
 * precedence. TODO add method to compare precedence -> useful for UnnecessaryParenthesesRule
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


    private final String code;


    BinaryOp(String code) {
        this.code = code;
    }


    @Override
    public String getToken() {
        return code;
    }

    /**
     * Returns true if this is an equality operator, ie one of
     * {@link #EQ}, or {@link #NE}.
     */
    public boolean isEquality() {
        switch (this) {
        case EQ:
        case NE:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if this is a relational operator, ie one of
     * {@link #LE}, {@link #GE}, {@link #GT}, {@link #LT}, or {@link #INSTANCEOF}.
     */
    public boolean isRelational() {
        switch (this) {
        case LE:
        case GE:
        case GT:
        case LT:
        case INSTANCEOF:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if this is a multiplicative operator, ie one of
     * {@link #MUL}, {@link #DIV}, {@link #MOD}.
     */
    public boolean isMultiplicative() {
        switch (this) {
        case MUL:
        case DIV:
        case MOD:
            return true;
        default:
            return false;
        }
    }


    /**
     * Returns true if this is a shift operator, ie one of
     * {@link #LEFT_SHIFT}, {@link #RIGHT_SHIFT}, or {@link #UNSIGNED_RIGHT_SHIFT}.
     */
    public boolean isShift() {
        switch (this) {
        case LEFT_SHIFT:
        case RIGHT_SHIFT:
        case UNSIGNED_RIGHT_SHIFT:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns true if this is a bitwise (or logical) operator, ie one
     * of {@link #XOR}, {@link #AND}, or {@link #OR}.
     */
    public boolean isBitwise() {
        switch (this) {
        case XOR:
        case AND:
        case OR:
            return true;
        default:
            return false;
        }
    }

    @Override
    public String toString() {
        return this.code;
    }


}
