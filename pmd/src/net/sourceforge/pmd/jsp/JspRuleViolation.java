package net.sourceforge.pmd.jsp;

import net.sourceforge.pmd.AbstractRuleViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.SimpleNode;

public class JspRuleViolation extends AbstractRuleViolation {
    public JspRuleViolation(Rule rule, RuleContext ctx, SimpleNode node) {
	super(rule, ctx, node);
    }

    public JspRuleViolation(Rule rule, RuleContext ctx, SimpleNode node, String specificMsg) {
	super(rule, ctx, node, specificMsg);
    }
}