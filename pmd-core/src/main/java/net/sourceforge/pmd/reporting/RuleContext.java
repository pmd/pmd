/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.internal.NodeFindingUtil;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;

/**
 * The API for rules to report violations or errors during analysis.
 * This forwards events to a {@link FileAnalysisListener}. It implements
 * violation suppression by filtering some violations out, according to
 * the {@link ViolationSuppressor}s for the language.
 * <p>
 * A RuleContext contains a Rule instance and violation reporting methods
 * implicitly report only for that rule. Contrary to PMD 6, RuleContext is
 * not unique throughout the analysis, a separate one is used per file and rule.
 */
public final class RuleContext {
    // Rule contexts do not need to be thread-safe, within PmdRunnable
    // they are stack-local

    private static final Object[] NO_ARGS = new Object[0];
    static final List<ViolationSuppressor> DEFAULT_SUPPRESSORS = listOf(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR,
                                                                        ViolationSuppressor.REGEX_SUPPRESSOR,
                                                                        ViolationSuppressor.XPATH_SUPPRESSOR);

    private final FileAnalysisListener listener;
    private final Rule rule;

    /**
     * @internalApi None of this is published API, and compatibility can be broken anytime! Use this only at your own risk.
     */
    RuleContext(FileAnalysisListener listener, Rule rule) {
        Objects.requireNonNull(listener, "Listener was null");
        Objects.requireNonNull(rule, "Rule was null");
        this.listener = listener;
        this.rule = rule;
    }

    /**
     * @internalApi None of this is published API, and compatibility can be broken anytime! Use this only at your own risk.
     * Used in {@link AbstractRule} in {@code asCtx(Object)}, through {@link InternalApiBridge}.
     */
    Rule getRule() {
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
        FileLocation location;
        if (beginLine != -1 && endLine != -1) {
            location = FileLocation.range(node.getTextDocument().getFileId(),
                                          TextRange2d.range2d(beginLine, 1, endLine, 1));
        } else {
            location = node.getReportLocation();
        }
        addViolationWithPosition(node, node.getAstInfo(), location, message, formatArgs);
    }

    /**
     * Record a new violation of the contextual rule, at the given token location.
     * The position is refined using the given begin and end line numbers.
     * The given violation message ({@link Rule#getMessage()}) is treated
     * as a format string for a {@link MessageFormat} and should hence use
     * appropriate escapes. The given formatting arguments are used.
     *
     * @param node Location of the violation (node or token) - only used to determine suppression
     * @param token   Report location of the violation
     * @param message    Violation message
     * @param formatArgs Format arguments for the message
     * @experimental Since 7.17.0. This will probably never be stabilized, will instead be
     *      replaced by a fluent API or something to report violations. Do not use
     *      this outside of the PMD codebase. See <a href="https://github.com/pmd/pmd/issues/5039">[core] Add fluent API to report violations #5039</a>.
     */
    @Experimental
    public void addViolationWithPosition(Node node, JavaccToken token, String message, Object... formatArgs) {
        addViolationWithPosition(node, node.getAstInfo(), token.getReportLocation(), message, formatArgs);
    }

    /**
     * Record a new violation of the contextual rule, at the given location (node or token).
     * The position is refined using the given begin and end line numbers.
     * The given violation message ({@link Rule#getMessage()}) is treated
     * as a format string for a {@link MessageFormat} and should hence use
     * appropriate escapes. The given formatting arguments are used.
     *
     * @param reportable Location of the violation (node or token) - only used to determine suppression
     * @param astInfo    Info about the root of the tree ({@link Node#getAstInfo()})
     * @param location   Report location of the violation
     * @param message    Violation message
     * @param formatArgs Format arguments for the message
     * @experimental Since 7.9.0. This will probably never be stabilized, will instead be
     *      replaced by a fluent API or something to report violations. Do not use
     *      this outside of the PMD codebase. See <a href="https://github.com/pmd/pmd/issues/5039">[core] Add fluent API to report violations #5039</a>.
     */
    @Experimental
    public void addViolationWithPosition(Reportable reportable, AstInfo<?> astInfo, FileLocation location,
                                         String message, Object... formatArgs) {
        Objects.requireNonNull(reportable, "Node was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        Node suppressionNode = getNearestNode(reportable, astInfo);
        RuleViolation violation = createViolation(() -> location, astInfo, suppressionNode, message, formatArgs);
        SuppressedViolation suppressed = suppressOrNull(suppressionNode, violation, astInfo);

        if (suppressed != null) {
            listener.onSuppressedRuleViolation(suppressed);
        } else {
            listener.onRuleViolation(violation);
        }
    }

    /**
     * @experimental Since 7.14.0. See <a href="https://github.com/pmd/pmd/pull/5609">[core] Add rule to report unnecessary suppression comments/annotations #5609</a>
     */
    @Experimental
    public void addViolationNoSuppress(Reportable reportable, AstInfo<?> astInfo,
                                String message, Object... formatArgs) {
        Objects.requireNonNull(reportable, "Node was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        Node nearestNode = getNearestNode(reportable, astInfo);
        RuleViolation violation = createViolation(reportable, astInfo, nearestNode, message, formatArgs);
        listener.onRuleViolation(violation);
    }

    private RuleViolation createViolation(Reportable reportable, AstInfo<?> astInfo, Node nearestNode, String message, Object... formatArgs) {
        LanguageVersionHandler handler = astInfo.getLanguageProcessor().services();
        Map<String, String> extraVariables = ViolationDecorator.apply(handler.getViolationDecorator(), nearestNode);
        String description = makeMessage(message, formatArgs, extraVariables);
        FileLocation location = reportable.getReportLocation();
        return new ParametricRuleViolation(rule, location, description, extraVariables);
    }

    private Node getNearestNode(Reportable reportable, AstInfo<?> astInfo) {
        if (reportable instanceof Node) {
            return (Node) reportable;
        }
        int startOffset = getStartOffset(reportable, astInfo);
        Optional<Node> foundNode = NodeFindingUtil.findNodeAt(astInfo.getRootNode(), startOffset);
        // default to the root node
        return foundNode.orElse(astInfo.getRootNode());
    }

    private static int getStartOffset(Reportable reportable, AstInfo<?> astInfo) {
        if (reportable instanceof JavaccToken) {
            return ((JavaccToken) reportable).getRegion().getStartOffset();
        }
        FileLocation loc = reportable.getReportLocation();
        return astInfo.getTextDocument().offsetAtLineColumn(loc.getStartPos());
    }

    private static @Nullable SuppressedViolation suppressOrNull(Node location, RuleViolation rv, AstInfo<?> astInfo) {
        LanguageVersionHandler handler = astInfo.getLanguageProcessor().services();
        SuppressedViolation suppressed = ViolationSuppressor.suppressOrNull(handler.getExtraViolationSuppressors(), rv, location);
        if (suppressed == null) {
            suppressed = ViolationSuppressor.suppressOrNull(DEFAULT_SUPPRESSORS, rv, location);
        }
        return suppressed;
    }

    private String makeMessage(@NonNull String message, Object[] args, Map<String, String> extraVars) {
        // Escape PMD specific variable message format, specifically the {
        // in the ${, so MessageFormat doesn't bitch.
        final String escapedMessage = StringUtils.replace(message, "${", "$'{'");
        String formatted = new MessageFormat(escapedMessage, Locale.ROOT).format(args);
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
}
