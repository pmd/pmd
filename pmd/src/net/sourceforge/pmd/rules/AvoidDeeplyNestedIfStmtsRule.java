package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.ast.*;

public class AvoidDeeplyNestedIfStmtsRule extends AbstractRule {
	
	private int depth;
	
	public Object visit(ASTCompilationUnit node, Object data) {
		depth = 0;
		return super.visit(node, data);
	}
	
	public Object visit(ASTIfStatement node, Object data) {
		depth++;
		super.visit(node, data);
		if (depth == 3) {
			RuleContext ctx = (RuleContext)data;
			ctx.getReport().addRuleViolation(super.createRuleViolation(ctx, node.getBeginLine()));
		}
		depth--;
		return data;
	}
	
}
