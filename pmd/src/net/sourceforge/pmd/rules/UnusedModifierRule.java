package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

public class UnusedModifierRule extends AbstractRule {

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!node.isAbstract()) {
            return data;
        }
        if (node.jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTInterfaceDeclaration) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(super.createRuleViolation(ctx, node.getBeginLine()));
        }
        return data;
    }

}
