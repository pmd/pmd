package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractRuleViolationFactory implements RuleViolationFactory {

    public void addViolation(RuleContext ruleContext, Rule rule, Node node) {
	ruleContext.getReport().addRuleViolation(createRuleViolation(rule, ruleContext, node));
    }

    public void addViolationWithMessage(RuleContext ruleContext, Rule rule, Node node, String message) {
	ruleContext.getReport().addRuleViolation(createRuleViolation(rule, ruleContext, node, message));
    }

    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String arg) {
	ruleContext.getReport().addRuleViolation(
		createRuleViolation(rule, ruleContext, node, MessageFormat.format(rule.getMessage(), arg)));
    }

    public void addViolation(RuleContext ruleContext, Rule rule, Node node, Object[] args) {
	ruleContext.getReport().addRuleViolation(
		createRuleViolation(rule, ruleContext, node, MessageFormat.format(rule.getMessage(), args)));
    }

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node);

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node,
	    String message);
}
