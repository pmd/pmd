/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.errorprone;

import net.sourceforge.pmd.lang.vm.ast.ASTBlock;
import net.sourceforge.pmd.lang.vm.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTElseStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.vm.ast.VmNode;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class EmptyIfStmtRule extends AbstractVmRule {
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

    private void handleIf(final VmNode node, final Object data) {
        if (node.getFirstChildOfType(ASTBlock.class).isEmpty()) {
            addViolation(data, node);
        }
    }
}
