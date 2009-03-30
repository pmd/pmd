package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractRuleViolationFactory implements RuleViolationFactory {

    private static final Object[] NO_ARGS = new Object[0];

    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, Object[] args) {
	final String formattedMessage;
	if (message != null) {
	    final String expandedMessage = expandVariables(rule, message);
	    formattedMessage = MessageFormat.format(expandedMessage, args != null ? args : NO_ARGS);
	} else {
	    formattedMessage = message;
	}
	ruleContext.getReport().addRuleViolation(createRuleViolation(rule, ruleContext, node, formattedMessage));
    }

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message);

    protected String expandVariables(Rule rule, String message) {
	StringBuilder buf = new StringBuilder(message);
	int startIndex = -1;
	while ((startIndex = buf.indexOf("${", startIndex+1)) >= 0) {
	    final int endIndex = buf.indexOf("}", startIndex);
	    if (endIndex >= 0) {
		final String name = buf.substring(startIndex+2, endIndex);
		final PropertyDescriptor<?> propertyDescriptor = rule.getPropertyDescriptor(name);
		if (propertyDescriptor != null) {
		    buf.replace(startIndex, endIndex+1, String.valueOf(rule.getProperty(propertyDescriptor)));
		}
	    }
	}
	return buf.toString();
    }
}
