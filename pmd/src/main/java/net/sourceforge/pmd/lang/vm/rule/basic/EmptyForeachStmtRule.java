package net.sourceforge.pmd.lang.vm.rule.basic;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.vm.ast.ASTBlock;
import net.sourceforge.pmd.lang.vm.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.vm.ast.ASTText;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class EmptyForeachStmtRule extends AbstractVmRule {

    @Override
    public Object visit(final ASTForeachStatement node, final Object data) {
        final ASTBlock block = node.getFirstChildOfType(ASTBlock.class);
        if (block.jjtGetNumChildren() == 0) {
            addViolation(data, node);
        }
        else if (block.jjtGetNumChildren() == 1 && block.jjtGetChild(0) instanceof ASTText
                && StringUtils.isBlank(block.jjtGetChild(0).getFirstToken().toString())) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

}
