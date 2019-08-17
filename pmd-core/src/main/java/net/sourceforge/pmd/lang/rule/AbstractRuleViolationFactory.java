/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractRuleViolationFactory implements RuleViolationFactory {

    private static final Object[] NO_ARGS = new Object[0];

    private String cleanup(String message, Object[] args) {

        if (message != null) {
            // Escape PMD specific variable message format, specifically the {
            // in the ${, so MessageFormat doesn't bitch.
            final String escapedMessage = StringUtils.replace(message, "${", "$'{'");
            return MessageFormat.format(escapedMessage, args != null ? args : NO_ARGS);
        } else {
            return message;
        }
    }

    // TODO why do we need those two overloads??

    @Override
    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, Object[] args) {

        String formattedMessage = cleanup(message, args);

        RuleViolation rv = createRuleViolation(rule, ruleContext, node, formattedMessage);
        SuppressedViolation sup = node.getRoot().getSuppressor().suppressOrNull(rv);
        if (sup != null) {
            ruleContext.getReport().addSuppressedViolation(sup);
        } else {
            ruleContext.getReport().addRuleViolation(rv);
        }
    }

    @Override
    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, int beginLine, int endLine,
            Object[] args) {

        String formattedMessage = cleanup(message, args);

        RuleViolation rv = createRuleViolation(rule, ruleContext, node, formattedMessage, beginLine, endLine);
        SuppressedViolation sup = node.getRoot().getSuppressor().suppressOrNull(rv);
        if (sup != null) {
            ruleContext.getReport().addSuppressedViolation(sup);
        } else {
            ruleContext.getReport().addRuleViolation(rv);
        }
    }

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message);

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine);
}
