/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class UnnecessaryBlockRule extends AbstractJavaRulechainRule {

    public UnnecessaryBlockRule() {
        super(ASTBlock.class);
    }

    @Override
    public RuleContext visit(ASTBlock block, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (!isInsideAnotherBlock(block)) {
            return ctx;
        }

        if (containsVariableDeclaration(block)) {
            return ctx;
        }

        ctx.addViolation(block);

        return ctx;
    }

    private boolean isInsideAnotherBlock(ASTBlock block) {
        return block.getParent() instanceof ASTBlock;
    }

    private boolean containsVariableDeclaration(ASTBlock block) {
        return block.children(ASTLocalVariableDeclaration.class).nonEmpty();
    }
}
