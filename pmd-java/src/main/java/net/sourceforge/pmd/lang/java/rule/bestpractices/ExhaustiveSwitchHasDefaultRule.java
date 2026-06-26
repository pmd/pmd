/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.10.0 (as XPath) / 7.27.0 (as Java)
 */
public class ExhaustiveSwitchHasDefaultRule extends AbstractJavaRulechainRule {
    public ExhaustiveSwitchHasDefaultRule() {
        super(ASTSwitchExpression.class, ASTStatement.class);
    }

    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        visitSwitchLike(node, (RuleContext) data);
        return null;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        visitSwitchLike(node, (RuleContext) data);
        return null;
    }

    private void visitSwitchLike(ASTSwitchLike node, RuleContext ctx) {
        if (node.isExhaustive() && node.hasDefaultCase()) {
            if (!defaultBranchJustThrows(node.getDefaultCase())) {
                ctx.addViolation(node);
            }
        }
    }

    // visible for testing
    /* private */ static boolean defaultBranchJustThrows(ASTSwitchBranch branch) {
        if (branch instanceof ASTSwitchFallthroughBranch) {
            ASTSwitchFallthroughBranch fallthroughBranch = (ASTSwitchFallthroughBranch) branch;
            return fallthroughBranch.getStatements().count() == 1
                    && fallthroughBranch.getStatements().first() instanceof ASTThrowStatement;
        }
        ASTSwitchArrowBranch arrowBranch = (ASTSwitchArrowBranch) branch;
        if (arrowBranch.getRightHandSide() instanceof ASTThrowStatement) {
            return true;
        }
        if (arrowBranch.getRightHandSide() instanceof ASTBlock) {
            ASTBlock block = (ASTBlock) arrowBranch.getRightHandSide();
            return block.size() == 1 && block.get(0) instanceof ASTThrowStatement;
        }
        return false;
    }
}
