package net.sourceforge.pmd.lang.jsp.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolation;

public class JspRuleViolation extends AbstractRuleViolation {
    public JspRuleViolation(Rule rule, RuleContext ctx, JspNode node) {
	super(rule, ctx, node);
    }

    public JspRuleViolation(Rule rule, RuleContext ctx, JspNode node, String specificMsg) {
	super(rule, ctx, node, specificMsg);
    }
}