/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This class handles of producing a Language specific RuleViolation and adding
 * to a Report.
 *
 * <p>Since PMD 6.21.0, implementations of this interface are considered internal
 * API and hence deprecated. Clients should exclusively use this interface and obtain
 * instances through {@link LanguageVersionHandler#getRuleViolationFactory()}.
 */
public interface RuleViolationFactory {
    // todo, upcoming refactorings will remove the deprecated addViolation
    //  and move the formatting logic into RuleContext


    /**
     * Adds a violation to the report.
     *
     * @param ruleContext
     *            the RuleContext
     * @param rule
     *            the rule
     * @param node
     *            the node that produces the violation
     * @param message
     *            specific message to put in the report
     * @param args
     *            arguments to embed in the rule violation message
     */
    @Deprecated
    default void addViolation(RuleContext ruleContext, Rule rule, @NonNull Node node, @NonNull String message, Object[] args) {
        addViolation(ruleContext, rule, node, message, node.getBeginLine(), node.getEndLine(), args);
    }


    @Deprecated
    default void addViolation(RuleContext ruleContext, Rule rule, @NonNull Node node, @NonNull String message, int beginLine, int endLine, @NonNull Object[] args) {
        Objects.requireNonNull(node);
        Objects.requireNonNull(message);
        Objects.requireNonNull(args);
        RuleViolation rv = formatViolation(rule, node, String.valueOf(ruleContext.getSourceCodeFile()), message, args);
        ((ParametricRuleViolation<?>) rv).setLines(beginLine, endLine);
        SuppressedViolation suppressed = suppressOrNull(node, rv);
        if (suppressed != null) {
            ruleContext.getReport().addSuppressedViolation(suppressed);
        } else {
            ruleContext.getReport().addRuleViolation(rv);
        }
    }


    RuleViolation createViolation(Rule rule, @NonNull Node location, @NonNull String filename, @NonNull String formattedMessage);

    default RuleViolation formatViolation(Rule rule, @NonNull Node location, @NonNull String filename, @NonNull String messageTemplate, @NonNull Object[] formatArgs) {
        return createViolation(rule, location, filename, formatMessage(messageTemplate, formatArgs));
    }


    SuppressedViolation suppressOrNull(Node location, RuleViolation violation);

    static String formatMessage(@NonNull String message, @NonNull Object[] args) {
        // todo merge that with the formatting that ParametricRuleViolation does...

        // Escape PMD specific variable message format, specifically the {
        // in the ${, so MessageFormat doesn't bitch.
        final String escapedMessage = StringUtils.replace(message, "${", "$'{'");
        return MessageFormat.format(escapedMessage, args);
    }
}
