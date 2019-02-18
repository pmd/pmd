package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a binary operator.
 *
 * TODO create interface binary expression.
 */
public enum BinaryOp {
    ADD("+"),
    SUB("-"),
    MUL("*"),
    DIV("/"),
    LEFT_SHIFT("<<"),
    RIGHT_SHIFT(">>"),
    UNSIGNED_RIGHT_SHIFT(">>>"),
    XOR("^"),
    AND("&"),
    OR("|");

    private final String code;


    BinaryOp(String code) {
        this.code = code;
    }


    public boolean isArithmetic() {
        switch (this) {
        case ADD:
        case SUB:
        case MUL:
        case DIV:
            return true;
        default:
            return false;
        }
    }


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


    public String toString() {
        return this.code;
    }

}
