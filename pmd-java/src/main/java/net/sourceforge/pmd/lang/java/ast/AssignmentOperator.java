package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.ADD;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.DIV;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.LEFT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.MUL;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.RIGHT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.SUB;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.UNSIGNED_RIGHT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOperator.XOR;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * An assignment operator for {@link ASTAssignmentExpression}.
 */
public enum AssignmentOperator {
    EQ("=", null),
    AND_EQ("&=", AND),
    OR_EQ("|=", OR),
    XOR_EQ("^=", XOR),
    ADD_EQ("+=", ADD),
    SUB_EQ("-=", SUB),
    MUL_EQ("*=", MUL),
    DIV_EQ("/=", DIV),
    LEFT_SHIFT_EQ("<<=", LEFT_SHIFT),
    RIGHT_SHIFT_EQ(">>=", RIGHT_SHIFT),
    UNSIGNED_RIGHT_SHIFT_EQ(">>>=", UNSIGNED_RIGHT_SHIFT);

    private static final Map<String, AssignmentOperator> LOOKUP = Collections.unmodifiableMap(
        Arrays.stream(values()).collect(Collectors.toMap(Object::toString, op -> op))
    );

    private final String code;
    private final BinaryOperator binaryOperator;


    AssignmentOperator(String code, BinaryOperator binaryOperator) {
        this.code = code;
        this.binaryOperator = binaryOperator;
    }


    public String toString() {
        return this.code;
    }


    /**
     * Returns true if this operator combines
     * a binary operator with the assignment.
     */
    public boolean isCompound() {
        return this != EQ;
    }


    /**
     * Returns the binary operator this corresponds to
     * if this is a compound operator, otherwise returns
     * null.
     */
    public BinaryOperator getBinaryOp() {
        return binaryOperator;
    }


    // parser only for now
    static AssignmentOperator fromImage(String image) {
        return LOOKUP.get(image);
    }
}
