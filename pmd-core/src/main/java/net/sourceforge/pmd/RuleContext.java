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
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.processor.AbstractPMDProcessor;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

/**
 * The API for rules to report violations or errors during analysis.
 * This forwards events to a {@link FileAnalysisListener}. It implements
 * violation suppression by filtering some violations out, according to
 * the {@link ViolationSuppressor}s for the language.
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

    // TODO document

    private String getDefaultMessage() {
        return rule.getMessage();
    }

    public void addViolation(Node location) {
        addViolationWithMessage(location, getDefaultMessage(), NO_ARGS);
    }

    public void addViolation(Node location, Object... formatArgs) {
        addViolationWithMessage(location, getDefaultMessage(), formatArgs);
    }

    public void addViolationWithMessage(Node location, String message) {
        addViolationWithPosition(location, -1, -1, message, NO_ARGS);
    }

    public void addViolationWithMessage(Node location, String message, Object... formatArgs) {
        addViolationWithPosition(location, -1, -1, message, formatArgs);
    }

    public void addViolationWithPosition(Node location, int beginLine, int endLine, String message, Object... formatArgs) {
        Objects.requireNonNull(location, "Node was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        RuleViolationFactory fact = location.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory();

        RuleViolation violation = fact.createViolation(rule, location, makeMessage(message, formatArgs));
        if (beginLine != -1 && endLine != -1) {
            // fixme, this is needed until we have actual Location objects
            ((ParametricRuleViolation<?>) violation).setLines(beginLine, endLine);
        }

        SuppressedViolation suppressed = fact.suppressOrNull(location, violation);

        if (suppressed != null) {
            listener.onSuppressedRuleViolation(suppressed);
        } else {
            listener.onRuleViolation(violation);
        }
    }

    public void addViolationNoSuppress(RuleViolation rv) {
        listener.onRuleViolation(rv);
    }

    private String makeMessage(@NonNull String message, Object[] args) {
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
