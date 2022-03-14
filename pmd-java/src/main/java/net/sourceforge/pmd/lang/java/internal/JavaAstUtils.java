/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

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
        return BinaryOp.isInfixExprWithOperator(ifx, BinaryOp.CONDITIONAL_OPS);
    }

    public static int numAlternatives(ASTSwitchBranch n) {
        return n.isDefault() ? 1 : n.getLabel().getExprList().count();
    }
}
