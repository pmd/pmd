package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

/**
 * This rule looks for unnecessary modifiers in interfaces:
 *
 * public interface Foo {
 *  public void bar();      // no need for public
 *  abstract void baz();    // no need for abstract
 * }
 */
public class UnusedModifierRule extends AbstractRule {

    public Object visit(ASTMethodDeclaration node, Object data) {
        if ((node.isAbstract() || node.isPublic()) && node.jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTInterfaceDeclaration) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(super.createRuleViolation(ctx, node.getBeginLine()));
        }
        return data;
    }

}
