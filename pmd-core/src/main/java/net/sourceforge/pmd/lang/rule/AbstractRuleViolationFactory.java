/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
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

    @Override
    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, Object[] args) {

        String formattedMessage = cleanup(message, args);

        ruleContext.getReport().addRuleViolation(createRuleViolation(rule, ruleContext, node, formattedMessage));
    }

    @Override
    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, int beginLine, int endLine,
                             Object[] args) {

        String formattedMessage = cleanup(message, args);

        ruleContext.getReport()
                .addRuleViolation(createRuleViolation(rule, ruleContext, node, formattedMessage, beginLine, endLine));
    }

    @Override
    public void addViolation(final RuleContext ruleContext, final Rule rule, final Node node, final String message,
                             final Class<? extends RuleViolationFix> ruleViolationFixClass) {
        ruleContext.getReport().addRuleViolation(createRuleViolation(rule, ruleContext, node, message, ruleViolationFixClass));
    }

    @Override
    public void addViolation(final RuleContext ruleContext, final Rule rule, final Node node, final String message,
                             final int beginLine, final int endLine,
                             final Class<? extends RuleViolationFix> ruleViolationFixClass, final Object[] args) {
        ruleContext.getReport().addRuleViolation(createRuleViolation(rule, ruleContext, node, message, beginLine, endLine, ruleViolationFixClass));
    }

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
                                                         int beginLine, int endLine);

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message);

    /**
     * Default method to create a new AutoFixableRuleViolation. This is called by the
     * {@link #addViolation(RuleContext, Rule, Node, String, Class)}. Any language which wants to provide
     * auto fixable capability must override this method in its corresponding factory.
     *
     * @param rule                  the rule which reported the violation
     * @param ruleContext           the context of the rule at the moment of the violation
     * @param node                  the node in which the fixes to that rule violation will be applied
     * @param message               the message to put in the report
     * @param ruleViolationFixClass the class in which the fixes to that rule violation will be applied
     * @return a RuleViolation with the capability of telling its rule violation fix class
     */
    protected abstract AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext,
                                                                               Node node, String message,
                                                                               Class<? extends RuleViolationFix> ruleViolationFixClass);

    /**
     * Default method to create a new AutoFixableRuleViolation. This is called by the
     * {@link #addViolation(RuleContext, Rule, Node, String, Class)}. Any language which wants to provide
     * auto fixable capability must override this method in its corresponding factory.
     *
     * @param rule                  the rule which reported the violation
     * @param ruleContext           the context of the rule at the moment of the violation
     * @param node                  the node in which the fixes to that rule violation will be applied
     * @param message               the message to put in the report
     * @param ruleViolationFixClass the class in which the fixes to that rule violation will be applied
     * @return a RuleViolation with the capability of telling its rule violation fix class
     */
    protected abstract AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
                                                         int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass);
}
