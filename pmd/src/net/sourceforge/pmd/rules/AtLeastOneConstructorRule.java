package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTNestedClassDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.List;

public class AtLeastOneConstructorRule extends AbstractRule implements Rule {

    public Object visit(ASTClassDeclaration node, Object data) {
        if (node.findChildrenOfType(ASTConstructorDeclaration.class).isEmpty()) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node, data);
    }

    public Object visit(ASTNestedClassDeclaration node, Object data) {
        return check(node, data);
    }

    private Object check(SimpleNode node, Object data) {
        List constructors = new ArrayList();
        node.findChildrenOfType(ASTConstructorDeclaration.class, constructors, false);
        if (constructors.isEmpty()) {
            RuleContext ctx = (RuleContext) data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return data;
    }
}
