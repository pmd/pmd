/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.lang.annotation.Documented;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.internal.NodeFindingUtil;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.impl.CannotBeSuppressed;
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
    private final RootNode rootNode;

    /**
     * @apiNote Internal API
     */
    RuleContext(FileAnalysisListener listener, Rule rule, RootNode rootNode) {
        Objects.requireNonNull(listener, "Listener was null");
        Objects.requireNonNull(rule, "Rule was null");
        Objects.requireNonNull(rootNode, "Root node was null");
        this.listener = listener;
        this.rule = rule;
        this.rootNode = rootNode;
    }

    /**
     * @apiNote Internal API. Used in {@link AbstractRule} in {@code asCtx(Object)},
     * through {@link InternalApiBridge}.
     */
    Rule getRule() {
        return rule;
    }

    private String getDefaultMessage() {
        return rule.getMessage();
    }

    private LanguageVersionHandler getLanguageServices() {
        return rootNode.getAstInfo().getLanguageProcessor().services();
    }

    /**
     * Place a violation at the given Reportable instance (node, token,
     * etc.). If the parameter is a node, it is used to determine
     * suppressions. Otherwise, the deepest node enclosing the location
     * is found from the tree and used for that purpose.
     *
     * <p>Note that if the argument is a node, the source range that will
     * be reported may be different from the full source range the node
     * covers. For instance a warning emitted on a class declaration node
     * will typically just have the source range of the class identifier.
     * This is controlled by the implementation of {@link Node#getReportLocation()}.
     *
     * @param reportable A node or token
     * @return A violation builder
     */
    @CheckReturnValue
    public ViolationBuilder at(Reportable reportable) {
        LanguageVersionHandler services = getLanguageServices();
        Node node = reportable.getSuppressionNode(rootNode.getAstInfo());
        return new ViolationBuilder(node, reportable.getReportLocation(), services);
    }

    /**
     * Place a violation at the given line in the source file. The location
     * of the violation will enclose the full range of the line, from
     * the first to the last character.
     *
     * @param lineNumber A line number (>= 1)
     * @return A violation builder
     */
    @CheckReturnValue
    public ViolationBuilder atLine(int lineNumber) {
        AstInfo<? extends RootNode> astInfo = rootNode.getAstInfo();
        LanguageVersionHandler services = astInfo.getLanguageProcessor().services();

        TextDocument textDocument = rootNode.getTextDocument(); // NOPMD CloseResource
        TextRegion lineRange = textDocument.createLineRange(lineNumber, lineNumber);
        FileLocation location =
            FileLocation.range(textDocument.getFileId(),
                TextRange2d.range2d(lineNumber, 1, lineNumber, lineRange.getLength()));
        Node nearestNode = NodeFindingUtil.findNodeAt(rootNode, lineRange.getStartOffset()).orElse(rootNode);
        return new ViolationBuilder(nearestNode, location, services);
    }

    /**
     * A staged builder for violations. Instances should not be discarded,
     * you need to call one of the methods of this class to emit the violation.
     */
    public final class ViolationBuilder {
        private final Node nearestNode;
        private final FileLocation location;
        private final LanguageVersionHandler languageServices;

        ViolationBuilder(Node nearestNode, FileLocation location, LanguageVersionHandler languageServices) {
            this.nearestNode = nearestNode;
            this.location = location;
            this.languageServices = languageServices;
        }

        /**
         * Emit the violation with the given message (overriding the default
         * rule message specified in the XML rule definition) and the given
         * extra arguments.
         *
         * <p>Note that the message must be in valid {@link MessageFormat}
         * format (even if it has no arguments). Single quotes and curly
         * braces must be escaped by prepending a single quote.
         */
        public void warnWithMessage(String message, Object... formatArgs) {
            recordViolation(this, message, formatArgs);
        }

        /**
         * Emit the violation with the given message (overriding the default
         * rule message specified in the XML rule definition) and no
         * extra arguments.
         *
         * <p>Note that the message must be in valid {@link MessageFormat}
         * format (even if it has no arguments). Single quotes and curly
         * braces must be escaped by prepending a single quote.
         */
        public void warnWithMessage(String message) {
            warnWithMessage(message, NO_ARGS);
        }

        /**
         * Emit the violation with the default message (specified in the XML
         * rule definition) and the given extra arguments.
         */
        public void warnWithArgs(Object... formatArgs) {
            warnWithMessage(getDefaultMessage(), formatArgs);
        }

        /**
         * Emit the violation with the default message (specified in the XML
         * rule definition) and no extra arguments.
         */
        public void warn() {
            warnWithArgs(NO_ARGS);
        }

    }

    /** Marker annotation for Intellij inspection to warn on unused return value. */
    @Documented
    @interface CheckReturnValue {

    }

    /**
     * Record a new violation of the contextual rule, at the given node.
     *
     * @param location Location of the violation
     */
    public void addViolation(Node location) {
        at(location).warn();
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
        at(location).warnWithArgs(formatArgs);
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
        at(location).warnWithMessage(message);
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
        at(location).warnWithMessage(message, formatArgs);
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
     * @deprecated Since 7.20.0, use the new reporting API (See {@link #at(Reportable)})
     */
    @Deprecated
    public void addViolationWithPosition(Node node, int beginLine, int endLine, String message, Object... formatArgs) {
        if (beginLine != -1 && endLine != -1) {
            FileLocation location = FileLocation.range(node.getTextDocument().getFileId(),
                TextRange2d.range2d(beginLine, 1, endLine, 1));

            at(node.atLocation(location)).warnWithMessage(message, formatArgs);
        } else {
            at(node).warnWithMessage(message, formatArgs);
        }
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
     * @deprecated Since 7.20.0, use the new reporting API (See {@link #at(Reportable)})
     */
    @Deprecated
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
     * @deprecated Since 7.20.0, use the new reporting API (See {@link #at(Reportable)})
     */
    @Deprecated
    public void addViolationWithPosition(Reportable reportable, AstInfo<?> astInfo, FileLocation location,
                                         String message, Object... formatArgs) {
        Objects.requireNonNull(reportable, "Node was null");
        LanguageVersionHandler services = astInfo.getLanguageProcessor().services();
        new ViolationBuilder(reportable.getSuppressionNode(astInfo), location, services)
            .warnWithMessage(message, formatArgs);
    }

    private void recordViolation(ViolationBuilder builder, String message, Object[] formatArgs) {
        Objects.requireNonNull(builder, "Builder was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        RuleViolation violation = createViolation(() -> builder.location, builder.nearestNode, builder.languageServices, message, formatArgs);

        SuppressedViolation suppressed = null;
        if (!(rule instanceof CannotBeSuppressed)) {
            suppressed = suppressOrNull(builder.nearestNode, violation, builder.languageServices);
        }

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
    @Deprecated
    public void addViolationNoSuppress(Reportable reportable, AstInfo<?> astInfo,
                                String message, Object... formatArgs) {
        Objects.requireNonNull(reportable, "Node was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        Node nearestNode = reportable.getSuppressionNode(astInfo);
        RuleViolation violation = createViolation(reportable, nearestNode, astInfo.getLanguageProcessor().services(), message, formatArgs);
        listener.onRuleViolation(violation);
    }

    private RuleViolation createViolation(Reportable reportable, Node nearestNode, LanguageVersionHandler handler, String message, Object[] formatArgs) {
        Map<String, String> extraVariables = ViolationDecorator.apply(handler.getViolationDecorator(), nearestNode);
        String description = makeMessage(message, formatArgs, extraVariables);
        FileLocation location = reportable.getReportLocation();
        return new ParametricRuleViolation(rule, location, description, extraVariables);
    }

    private static @Nullable SuppressedViolation suppressOrNull(Node location, RuleViolation rv, LanguageVersionHandler handler) {
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
