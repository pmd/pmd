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
 */
public class DefaultRuleViolationFactory implements RuleViolationFactory {

    private static final Object[] NO_ARGS = new Object[0];

    private static final DefaultRuleViolationFactory DEFAULT = new DefaultRuleViolationFactory();
    private Set<ViolationSuppressor> allSuppressors;

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

        if (node != null) {
            // note: no suppression when node is null.
            // Node should in fact never be null, this is todo for later

            for (ViolationSuppressor suppressor : getAllSuppressors()) {
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

    private Set<ViolationSuppressor> getAllSuppressors() {
        if (allSuppressors == null) {
            // lazy loaded because calling getSuppressors in constructor
            // is not safe wrt initialization of static constants
            // (order dependent otherwise)
            this.allSuppressors = new LinkedHashSet<>(getSuppressors());
            allSuppressors.add(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR);
            allSuppressors.add(ViolationSuppressor.REGEX_SUPPRESSOR);
            allSuppressors.add(ViolationSuppressor.XPATH_SUPPRESSOR);
        }
        return allSuppressors;
    }

    /** Returns the default instance (no additional suppressors, creates a ParametricRuleViolation). */
    public static RuleViolationFactory defaultInstance() {
        return DEFAULT;
    }
}
