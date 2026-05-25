/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.25.0
 */
public class UnnecessaryBlockRule extends AbstractJavaRulechainRule {

    public UnnecessaryBlockRule() {
        super(ASTBlock.class);
    }

    @Override
    public RuleContext visit(ASTBlock block, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (doesntRestrictScope(block) || blockDirectlyInBlock(block)) {
            ctx.addViolation(block);
        }

        return ctx;
    }

    private boolean doesntRestrictScope(ASTBlock block) {
        return isInsideAnotherBlock(block)
                && !(containsVariableDeclaration(block)
                    || containsClassDeclaration(block));
    }

    private boolean isInsideAnotherBlock(ASTBlock block) {
        return block.getParent() instanceof ASTBlock;
    }

    private boolean containsVariableDeclaration(ASTBlock block) {
        return block.children(ASTLocalVariableDeclaration.class).nonEmpty();
    }

    private boolean containsClassDeclaration(ASTBlock block) {
        return block.children(ASTLocalClassStatement.class).nonEmpty();
    }

    private boolean blockDirectlyInBlock(ASTBlock block) {
        JavaNode parent = block.getParent();

        return (parent instanceof ASTBlock) && (parent.getNumChildren() == 1);
    }
}
