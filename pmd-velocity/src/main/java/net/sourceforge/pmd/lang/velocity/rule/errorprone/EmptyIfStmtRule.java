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

public class EmptyIfStmtRule extends AbstractVtlRule {
    @Override
    public Object visit(final ASTIfStatement node, final Object data) {
        handleIf(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTElseIfStatement node, final Object data) {
        handleIf(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTElseStatement node, final Object data) {
        handleIf(node, data);
        return super.visit(node, data);
    }

    private void handleIf(final VtlNode node, final Object data) {
        if (node.firstChild(ASTBlock.class).isEmpty()) {
            asCtx(data).addViolation(node);
        }
    }
}
