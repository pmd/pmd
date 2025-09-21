/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class UselessConditionRule extends AbstractJavaRulechainRule {

    public UselessConditionRule() {
        super(ASTIfStatement.class, ASTConditionalExpression.class);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        if (node.getElseBranch() != null
                && JavaAstUtils.tokenEquals(node.getThenBranch(), node.getElseBranch())) {
            asCtx(data).addViolation(node);
        }
        return data;
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        if (node.getElseBranch() != null
            && JavaAstUtils.tokenEquals(node.getThenBranch(), node.getElseBranch())) {
            asCtx(data).addViolation(node);
        }
        return data;
    }
}
