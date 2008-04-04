package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.AbstractRuleViolation;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;

public class JspRuleViolation extends AbstractRuleViolation {
    public JspRuleViolation(Rule rule, RuleContext ctx, JspNode node) {
	super(rule, ctx, node);
    }

    public JspRuleViolation(Rule rule, RuleContext ctx, JspNode node, String specificMsg) {
	super(rule, ctx, node, specificMsg);
    }
}