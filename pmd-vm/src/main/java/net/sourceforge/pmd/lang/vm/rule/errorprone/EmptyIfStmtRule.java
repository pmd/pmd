/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.errorprone;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.vm.ast.ASTBlock;
import net.sourceforge.pmd.lang.vm.ast.ASTElseIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTElseStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTText;
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;
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

    private void handleIf(final AbstractVmNode node, final Object data) {
        final ASTBlock block = node.getFirstChildOfType(ASTBlock.class);
        if (block.getNumChildren() == 0) {
            addViolation(data, node);
        } else if (block.getNumChildren() == 1 && block.getChild(0) instanceof ASTText
                && StringUtils.isBlank(((AbstractVmNode) block.getChild(0)).getFirstToken().toString())) {
            addViolation(data, node);
        }
    }
}
