/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.rule.errorprone;

import net.sourceforge.pmd.lang.velocity.ast.ASTBlock;
import net.sourceforge.pmd.lang.velocity.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.velocity.ast.ASTElseStatement;
import net.sourceforge.pmd.lang.velocity.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.velocity.ast.VtlNode;
import net.sourceforge.pmd.lang.velocity.rule.AbstractVtlRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class EmptyIfStmtRule extends AbstractVtlRule {
    @Override
    public RuleContext visit(final ASTIfStatement node, final RuleContext data) {
        handleIf(node, data);
        return super.visit(node, data);
    }

    @Override
    public RuleContext visit(final ASTElseIfStatement node, final RuleContext data) {
        handleIf(node, data);
        return super.visit(node, data);
    }

    @Override
    public RuleContext visit(final ASTElseStatement node, final RuleContext data) {
        handleIf(node, data);
        return super.visit(node, data);
    }

    private void handleIf(final VtlNode node, final RuleContext data) {
        if (node.firstChild(ASTBlock.class).isEmpty()) {
            data.addViolation(node);
        }
    }
}
