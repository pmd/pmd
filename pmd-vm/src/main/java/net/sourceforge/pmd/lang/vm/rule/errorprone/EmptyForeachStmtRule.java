/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule.errorprone;

import net.sourceforge.pmd.lang.vm.ast.ASTBlock;
import net.sourceforge.pmd.lang.vm.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.vm.rule.AbstractVmRule;

public class EmptyForeachStmtRule extends AbstractVmRule {

    @Override
    public Object visit(final ASTForeachStatement node, final Object data) {
        if (node.firstChild(ASTBlock.class).isEmpty()) {
            asCtx(data).addViolation(node);
        }
        return super.visit(node, data);
    }

}
