package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractRuleViolationFactory implements RuleViolationFactory {

    private static final Object[] NO_ARGS = new Object[0];

    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, Object[] args) {
	final String formattedMessage;
	if (message != null) {
	    // Escape PMD specific variable message format, specifically the { in the ${, so MessageFormat doesn't bitch.
	    final String escapedMessage = message.replace("${", "$'{'");
	    formattedMessage = MessageFormat.format(escapedMessage, args != null ? args : NO_ARGS);
	} else {
	    formattedMessage = message;
	}
	ruleContext.getReport().addRuleViolation(createRuleViolation(rule, ruleContext, node, formattedMessage));
    }

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message);
}
