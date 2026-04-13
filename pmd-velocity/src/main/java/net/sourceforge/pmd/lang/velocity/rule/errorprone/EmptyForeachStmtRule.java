/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.rule.errorprone;

import net.sourceforge.pmd.lang.velocity.ast.ASTBlock;
import net.sourceforge.pmd.lang.velocity.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.velocity.rule.AbstractVtlRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class EmptyForeachStmtRule extends AbstractVtlRule {

    @Override
    public RuleContext visit(final ASTForeachStatement node, final RuleContext data) {
        if (node.firstChild(ASTBlock.class).isEmpty()) {
            data.addViolation(node);
        }
        return super.visit(node, data);
    }

}
