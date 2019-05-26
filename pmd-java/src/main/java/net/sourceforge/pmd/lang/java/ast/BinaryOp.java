/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a binary operator.
 *
 * @see UnaryOp
 * @see AssignmentOp
 */
public enum BinaryOp {

    // shortcut boolean ops
    BOOL_OR("||"),
    BOOL_AND("&&"),

    // non-shortcut (also bitwise)
    OR("|"),
    XOR("^"),
    AND("&"),

    // equality
    EQ("=="),
    NE("!="),

    // relational
    LE("<="),
    GE(">="),
    GT(">"),
    LT("<"),
    INSTANCEOF("instanceof"),

    // shift
    LEFT_SHIFT("<<"),
    RIGHT_SHIFT(">>"),
    UNSIGNED_RIGHT_SHIFT(">>>"),

    // additive
    ADD("+"),
    SUB("-"),

    // multiplicative
    MUL("*"),
    DIV("/"),
    MOD("%");


    private static final Map<String, BinaryOp> LOOKUP =
        Arrays.stream(values())
              .collect(
                  collectingAndThen(
                      toMap(Object::toString, op -> op),
                      Collections::unmodifiableMap
                  )
              );

    private final String code;


    BinaryOp(String code) {
        this.code = code;
    }


    public boolean isEquality() {
        switch (this) {
        case EQ:
        case NE:
            return true;
        default:
            return false;
        }
    }

    public boolean isRelational() {
        switch (this) {
        case LE:
        case GE:
        case GT:
        case LT:
            return true;
        default:
            return false;
        }
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


    @Override
    public String toString() {
        return this.code;
    }


    // parser only for now
    static BinaryOp fromImage(String image) {
        return LOOKUP.get(image);
    }

}
