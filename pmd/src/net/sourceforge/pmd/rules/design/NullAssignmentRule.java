/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTNullLiteral;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.SimpleNode;

/**
 * @author dpeugh
 *
 * This checks for excessive Null Assignments.
 *
 * For instance:
 *
 * public void foo() {
 *   Object x = null; // OK
 *   // Some stuff
 *   x = new Object(); // Also OK
 *   // Some more stuff
 *   x = null; // BAD
 * }
 */

public class NullAssignmentRule extends AbstractRule {

    public Object visit(ASTStatementExpression expr, Object data) {
        if (expr.jjtGetNumChildren() <= 2) {
            return expr.childrenAccept(this, data);
        }

        if (expr.jjtGetChild(1) instanceof ASTAssignmentOperator) {
            SimpleNode curr = (SimpleNode) expr.jjtGetChild(2);
            for (int i = 0; i < 8; i++) {
                if (curr.jjtGetNumChildren() != 0) {
                    curr = (SimpleNode) curr.jjtGetChild(0);
                }
            }

            if (curr instanceof ASTNullLiteral) {
                RuleContext ctx = (RuleContext) data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, expr.getBeginLine()));
            }

            return data;
        } else {
            return expr.childrenAccept(this, data);
        }
    }
}
