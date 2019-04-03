/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.DIV;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.LEFT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.MUL;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.RIGHT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.SUB;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.UNSIGNED_RIGHT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.XOR;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;


/**
 * An assignment operator for {@link ASTAssignmentExpression}.
 *
 * <pre class="grammar">
 *
 * AssignmentOp ::= "=" | "*=" | "/=" | "%=" | "+=" | "-=" | "&lt;&lt;=" | "&gt;&gt;=" | "&gt;&gt;&gt;=" | "&amp;=" | "^=" | "|="
 *
 * </pre>
 *
 * @see BinaryOp
 * @see UnaryOp
 */
public enum AssignmentOp {
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

    private static final Map<String, AssignmentOp> LOOKUP = Collections.unmodifiableMap(
        Arrays.stream(values()).collect(Collectors.toMap(Object::toString, op -> op))
    );

    private final String code;
    private final BinaryOp binaryOp;


    AssignmentOp(String code,
                 @Nullable BinaryOp binaryOp) {
        this.code = code;
        this.binaryOp = binaryOp;
    }


    @Override
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
    @Nullable
    public BinaryOp getBinaryOp() {
        return binaryOp;
    }


    // parser only for now
    static AssignmentOp fromImage(String image) {
        return LOOKUP.get(image);
    }
}
