/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 *
 */
public final class JavaAstUtils {

    private JavaAstUtils() {
        // utility class
    }


    public static boolean isConditional(JavaNode ifx) {
        if (ifx instanceof ASTInfixExpression) {
            BinaryOp op = ((ASTInfixExpression) ifx).getOperator();
            return op == BinaryOp.CONDITIONAL_AND
                || op == BinaryOp.CONDITIONAL_OR;
        }
        return false;
    }

    public static int numAlternatives(ASTSwitchBranch n) {
        return n.isDefault() ? 1 : n.getLabel().getExprList().count();
    }
}
