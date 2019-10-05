/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This is a functional implementation of {@link RuleViolationFactory}.
 * It uses only the standard {@link ViolationSuppressor}s (constants in the interface).
 * It may be extended to add more suppression options.
 *
 * <p>Implementations should be internal. Only the interface should be exposed.
 *
 * TODO this should not be an abstract class anymore
 */
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
        maybeSuppress(ruleContext, node, rv);
    }

    @Override
    public void addViolation(RuleContext ruleContext, Rule rule, Node node, String message, int beginLine, int endLine, Object[] args) {

        String formattedMessage = cleanup(message, args);

        RuleViolation rv = createRuleViolation(rule, ruleContext, node, formattedMessage, beginLine, endLine);
        maybeSuppress(ruleContext, node, rv);
    }

    private void maybeSuppress(RuleContext ruleContext, @Nullable Node node, RuleViolation rv) {
        Set<ViolationSuppressor> suppressors = new LinkedHashSet<>(getSuppressors());
        suppressors.add(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR);
        suppressors.add(ViolationSuppressor.REGEX_SUPPRESSOR);
        suppressors.add(ViolationSuppressor.XPATH_SUPPRESSOR);

        if (node != null) {
            for (ViolationSuppressor suppressor : suppressors) {
                SuppressedViolation suppressed = suppressor.suppressOrNull(rv, node);
                if (suppressed != null) {
                    ruleContext.getReport().addSuppressedViolation(suppressed);
                    return;
                }
            }
        }
        ruleContext.getReport().addRuleViolation(rv);
    }

    /**
     * Returns a list of additional suppressors for this language. These
     * are added to regular //NOPMD, regex and XPath suppression.
     */
    protected List<ViolationSuppressor> getSuppressors() {
        return Collections.emptyList();
    }

    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new ParametricRuleViolation<>(rule, ruleContext, node, message);
    }

    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
                                                int beginLine, int endLine) {
        ParametricRuleViolation<Node> rv = new ParametricRuleViolation<>(rule, ruleContext, node, message);
        rv.setLines(beginLine, endLine);
        return rv;
    }
}
