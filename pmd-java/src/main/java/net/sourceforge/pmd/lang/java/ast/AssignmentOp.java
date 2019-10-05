/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.DIV;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.LEFT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.MOD;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.MUL;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.RIGHT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.SUB;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.UNSIGNED_RIGHT_SHIFT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.XOR;
import static net.sourceforge.pmd.lang.java.ast.InternalInterfaces.OperatorLike;

import org.checkerframework.checker.nullness.qual.Nullable;


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
public enum AssignmentOp implements OperatorLike {
    ASSIGN("=", null),
    AND_ASSIGN("&=", AND),
    OR_ASSIGN("|=", OR),
    XOR_ASSIGN("^=", XOR),
    ADD_ASSIGN("+=", ADD),
    SUB_ASSIGN("-=", SUB),
    MUL_ASSIGN("*=", MUL),
    DIV_ASSIGN("/=", DIV),
    MOD_ASSIGN("%=", MOD),
    LEFT_SHIFT_ASSIGN("<<=", LEFT_SHIFT),
    RIGHT_SHIFT_ASSIGN(">>=", RIGHT_SHIFT),
    UNSIGNED_RIGHT_SHIFT_ASSIGN(">>>=", UNSIGNED_RIGHT_SHIFT);

    private final String code;
    private final BinaryOp binaryOp;


    AssignmentOp(String code,
                 @Nullable BinaryOp binaryOp) {
        this.code = code;
        this.binaryOp = binaryOp;
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
     * Returns true if this operator combines
     * a binary operator with the assignment.
     */
    public boolean isCompound() {
        return this != ASSIGN;
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


}
