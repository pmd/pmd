/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.util.document.FileLocation;

/**
 * The API for rules to report violations or errors during analysis.
 * This forwards events to a {@link FileAnalysisListener}. It implements
 * violation suppression by filtering some violations out, according to
 * the {@link ViolationSuppressor}s for the language.
 *
 * A RuleContext contains a Rule instance and violation reporting methods
 * implicitly report only for that rule. Contrary to PMD 6, RuleContext is
 * not unique throughout the analysis, a separate one is used per file and rule.
 */
public final class RuleContext {
    // todo move to package reporting

    // Rule contexts do not need to be thread-safe, within PmdRunnable
    // they are stack-local

    private static final Object[] NO_ARGS = new Object[0];

    private final FileAnalysisListener listener;
    private final Rule rule;

    private RuleContext(FileAnalysisListener listener, Rule rule) {
        Objects.requireNonNull(listener, "Listener was null");
        Objects.requireNonNull(rule, "Rule was null");
        this.listener = listener;
        this.rule = rule;
    }

    private String getDefaultMessage() {
        return rule.getMessage();
    }

    /**
     * Record a new violation of the contextual rule, at the given node.
     *
     * @param location Location of the violation
     */
    public void addViolation(Node location) {
        addViolationWithMessage(location, getDefaultMessage(), NO_ARGS);
    }

    /**
     * Record a new violation of the contextual rule, at the given node.
     * The default violation message ({@link Rule#getMessage()}) is formatted
     * using the given format arguments.
     *
     * @param location   Location of the violation
     * @param formatArgs Format arguments for the message
     *
     * @see MessageFormat
     */
    public void addViolation(Node location, Object... formatArgs) {
        addViolationWithMessage(location, getDefaultMessage(), formatArgs);
    }

    /**
     * Record a new violation of the contextual rule, at the given node.
     * The given violation message ({@link Rule#getMessage()}) is treated
     * as a format string for a {@link MessageFormat} and should hence use
     * appropriate escapes. No formatting arguments are provided.
     *
     * @param location Location of the violation
     * @param message  Violation message
     */
    public void addViolationWithMessage(Node location, String message) {
        addViolationWithPosition(location, -1, -1, message, NO_ARGS);
    }

    /**
     * Record a new violation of the contextual rule, at the given node.
     * The given violation message ({@link Rule#getMessage()}) is treated
     * as a format string for a {@link MessageFormat} and should hence use
     * appropriate escapes. The given formatting arguments are used.
     *
     * @param location   Location of the violation
     * @param message    Violation message
     * @param formatArgs Format arguments for the message
     */
    public void addViolationWithMessage(Node location, String message, Object... formatArgs) {
        addViolationWithPosition(location, -1, -1, message, formatArgs);
    }

    /**
     * Record a new violation of the contextual rule, at the given node.
     * The position is refined using the given begin and end line numbers.
     * The given violation message ({@link Rule#getMessage()}) is treated
     * as a format string for a {@link MessageFormat} and should hence use
     * appropriate escapes. The given formatting arguments are used.
     *
     * @param location   Location of the violation
     * @param message    Violation message
     * @param formatArgs Format arguments for the message
     */
    public void addViolationWithPosition(Node node, int beginLine, int endLine, String message, Object... formatArgs) {
        Objects.requireNonNull(node, "Node was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        RuleViolationFactory fact = node.getTextDocument().getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory();


        FileLocation location = node.getReportLocation();
        if (beginLine != -1 && endLine != -1) {
            location = FileLocation.range(location.getFileName(), beginLine, 1, endLine, 1);
        }

        RuleViolation violation = fact.createViolation(rule, node, location, makeMessage(message, formatArgs));

        SuppressedViolation suppressed = fact.suppressOrNull(node, violation);

        if (suppressed != null) {
            listener.onSuppressedRuleViolation(suppressed);
        } else {
            listener.onRuleViolation(violation);
        }
    }

    /**
     * Force the recording of a violation, ignoring the violation
     * suppression mechanism ({@link ViolationSuppressor}).
     * 
     * @param rv A violation
     */
    public void addViolationNoSuppress(RuleViolation rv) {
        listener.onRuleViolation(rv);
    }

    private static String makeMessage(@NonNull String message, Object[] args) {
        // Escape PMD specific variable message format, specifically the {
        // in the ${, so MessageFormat doesn't bitch.
        final String escapedMessage = StringUtils.replace(message, "${", "$'{'");
        return MessageFormat.format(escapedMessage, args);
    }

    /**
     * Create a new RuleContext. This is internal API owned by {@link AbstractPMDProcessor}
     * (can likely be hidden when everything relevant is moved into rule package).
     *
     * The listener must be closed by its creator.
     */
    @InternalApi
    public static RuleContext create(FileAnalysisListener listener, Rule rule) {
        return new RuleContext(listener, rule);
    }

}
