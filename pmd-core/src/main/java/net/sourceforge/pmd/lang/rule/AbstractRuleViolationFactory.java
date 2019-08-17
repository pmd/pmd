/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
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
        maybeSuppress(ruleContext, node, rv, rule);
    }

    @Override
    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, int beginLine, int endLine,
            Object[] args) {

        String formattedMessage = cleanup(message, args);

        RuleViolation rv = createRuleViolation(rule, ruleContext, node, formattedMessage, beginLine, endLine);
        maybeSuppress(ruleContext, node, rv, rule);
    }

    private void maybeSuppress(RuleContext ruleContext, Node node, RuleViolation rv, Rule rule) {
        List<ViolationSuppressor> suppressors = getSuppressors(node, rule);
        for (ViolationSuppressor suppressor : suppressors) {
            SuppressedViolation suppressed = suppressor.suppressOrNull(rv);
            if (suppressed != null) {
                ruleContext.getReport().addSuppressedViolation(suppressed);
                return;
            }
        }
        ruleContext.getReport().addRuleViolation(rv);
    }


    protected List<ViolationSuppressor> getSuppressors(Node node, Rule rule) {
        return Collections.emptyList();
    }

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message);

    protected abstract RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine);
}
