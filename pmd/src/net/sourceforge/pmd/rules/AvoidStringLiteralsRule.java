/*
 * User: tom
 * Date: Nov 4, 2002
 * Time: 10:02:15 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

import java.text.MessageFormat;

public class AvoidStringLiteralsRule extends AbstractRule {

    public Object visit(ASTLiteral node, Object data) {
        if (!hasFourParents(node)) {
            return data;
        }

        if (!(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTArgumentList)) {
            return data;
        }

        if (node.getImage() == null || node.getImage().indexOf('\"') == -1) {
            return data;
        }

        RuleContext ctx = (RuleContext)data;
        String msg = MessageFormat.format(getMessage(), new Object[] {node.getImage()});
        ctx.getReport().addRuleViolation(super.createRuleViolation(ctx, node.getBeginLine(), msg));

        return data;
    }

    private boolean hasFourParents(Node node) {
        Node currentNode = node;
        for (int i=0; i<4; i++) {
            if (currentNode instanceof ASTCompilationUnit) {
                return false;
            }
            currentNode = currentNode.jjtGetParent();
        }
        return true;
    }
}

