/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity.rule.errorprone;

import net.sourceforge.pmd.lang.velocity.ast.ASTBlock;
import net.sourceforge.pmd.lang.velocity.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.velocity.rule.AbstractVtlRule;

public class EmptyForeachStmtRule extends AbstractVtlRule {

    @Override
    public Object visit(final ASTForeachStatement node, final Object data) {
        if (node.firstChild(ASTBlock.class).isEmpty()) {
            asCtx(data).addViolation(node);
        }
        return super.visit(node, data);
    }

}
