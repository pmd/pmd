/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.ViolationDecorator;

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
    private static final List<ViolationSuppressor> DEFAULT_SUPPRESSORS = listOf(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR,
                                                                                ViolationSuppressor.REGEX_SUPPRESSOR,
                                                                                ViolationSuppressor.XPATH_SUPPRESSOR);

    private final FileAnalysisListener listener;
    private final Rule rule;

    private RuleContext(FileAnalysisListener listener, Rule rule) {
        Objects.requireNonNull(listener, "Listener was null");
        Objects.requireNonNull(rule, "Rule was null");
        this.listener = listener;
        this.rule = rule;
    }

    /**
     * @deprecated Used in {@link AbstractRule#asCtx(Object)}, when that is gone, will be removed.
     */
    @Deprecated
    @InternalApi
    public Rule getRule() {
        return rule;
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
     * @param node       Location of the violation
     * @param message    Violation message
     * @param formatArgs Format arguments for the message
     */
    public void addViolationWithPosition(Node node, int beginLine, int endLine, String message, Object... formatArgs) {
        Objects.requireNonNull(node, "Node was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        LanguageVersionHandler handler = node.getAstInfo().getLanguageProcessor().services();

        FileLocation location = node.getReportLocation();
        if (beginLine != -1 && endLine != -1) {
            location = FileLocation.range(location.getFileName(), TextRange2d.range2d(beginLine, 1, endLine, 1));
        }

        final Map<String, String> extraVariables = ViolationDecorator.apply(handler.getViolationDecorator(), node);
        final String description = makeMessage(message, formatArgs, extraVariables);
        final RuleViolation violation = new ParametricRuleViolation(rule, location, description, extraVariables);

        final SuppressedViolation suppressed = suppressOrNull(node, violation, handler);

        if (suppressed != null) {
            listener.onSuppressedRuleViolation(suppressed);
        } else {
            listener.onRuleViolation(violation);
        }
    }

    private static @Nullable SuppressedViolation suppressOrNull(Node location, RuleViolation rv, LanguageVersionHandler handler) {
        SuppressedViolation suppressed = ViolationSuppressor.suppressOrNull(handler.getExtraViolationSuppressors(), rv, location);
        if (suppressed == null) {
            suppressed = ViolationSuppressor.suppressOrNull(DEFAULT_SUPPRESSORS, rv, location);
        }
        return suppressed;
    }

    /**
     * Force the recording of a violation, ignoring the violation
     * suppression mechanism ({@link ViolationSuppressor}).
     *
     * @param rv A violation
     */
    @InternalApi
    public void addViolationNoSuppress(RuleViolation rv) {
        listener.onRuleViolation(rv);
    }

    private String makeMessage(@NonNull String message, Object[] args, Map<String, String> extraVars) {
        // Escape PMD specific variable message format, specifically the {
        // in the ${, so MessageFormat doesn't bitch.
        final String escapedMessage = StringUtils.replace(message, "${", "$'{'");
        String formatted = MessageFormat.format(escapedMessage, args);
        return expandVariables(formatted, extraVars);
    }


    private String expandVariables(String message, Map<String, String> extraVars) {

        if (!message.contains("${")) {
            return message;
        }

        StringBuilder buf = new StringBuilder(message);
        int startIndex = -1;
        while ((startIndex = buf.indexOf("${", startIndex + 1)) >= 0) {
            final int endIndex = buf.indexOf("}", startIndex);
            if (endIndex >= 0) {
                final String name = buf.substring(startIndex + 2, endIndex);
                String variableValue = getVariableValue(name, extraVars);
                if (variableValue != null) {
                    buf.replace(startIndex, endIndex + 1, variableValue);
                }
            }
        }
        return buf.toString();
    }

    private String getVariableValue(String name, Map<String, String> extraVars) {
        String value = extraVars.get(name);
        if (value != null) {
            return value;
        }
        final PropertyDescriptor<?> propertyDescriptor = rule.getPropertyDescriptor(name);
        return propertyDescriptor == null ? null : String.valueOf(rule.getProperty(propertyDescriptor));
    }


    /**
     * Create a new RuleContext.
     *
     * The listener must be closed by its creator.
     */
    @InternalApi
    public static RuleContext create(FileAnalysisListener listener, Rule rule) {
        return new RuleContext(listener, rule);
    }

}
