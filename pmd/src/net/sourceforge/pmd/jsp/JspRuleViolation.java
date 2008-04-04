package net.sourceforge.pmd.jsp;

import net.sourceforge.pmd.AbstractRuleViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.jsp.ast.JspNode;

public class JspRuleViolation extends AbstractRuleViolation {
    public JspRuleViolation(Rule rule, RuleContext ctx, JspNode node) {
	super(rule, ctx, node);
    }

    public JspRuleViolation(Rule rule, RuleContext ctx, JspNode node, String specificMsg) {
	super(rule, ctx, node, specificMsg);
    }
}