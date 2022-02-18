/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * The API for rules to report violations or errors during analysis.
 * The non-deprecated API of this class represents what will be left
 * in PMD 7.
 *
 * In PMD 6, the RuleContext provides access to Rule processing state.
 * This information includes the following global information:
 * <ul>
 * <li>The Report to which Rule Violations are sent.</li>
 * <li>Named attributes.</li>
 * </ul>
 * As well as the following source file specific information:
 * <ul>
 * <li>A File for the source file.</li>
 * <li>The Language Version of the source file.</li>
 * </ul>
 * It is <strong>required</strong> that all source file specific options be set
 * between calls to difference source files. Failure to do so, may result in
 * undefined behavior.
 */
public class RuleContext {

    private static final Logger LOG = Logger.getLogger(RuleContext.class.getName());
    private static final Object[] NO_ARGS = new Object[0];

    private Report report = new Report();
    private File sourceCodeFile;
    private LanguageVersion languageVersion;
    private final ConcurrentMap<String, Object> attributes;
    private boolean ignoreExceptions = true;

    private Rule currentRule;

    /**
     * Default constructor.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public RuleContext() {
        attributes = new ConcurrentHashMap<>();
    }

    /**
     * Constructor which shares attributes and report listeners with the given
     * RuleContext.
     *
     * @param ruleContext the context from which the values are shared
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public RuleContext(RuleContext ruleContext) {
        this.attributes = ruleContext.attributes;
        this.report.addListeners(ruleContext.getReport().getListeners());
    }

    private String getDefaultMessage() {
        return Objects.requireNonNull(getCurrentRule().getMessage(), "rule has no message");
    }


    /**
     * @deprecated Internal API.
     */
    @InternalApi
    @Deprecated
    public Rule getCurrentRule() {
        return Objects.requireNonNull(currentRule, "rule was not set");
    }

    /**
     * @deprecated Internal API.
     */
    @InternalApi
    @Deprecated
    public void setCurrentRule(Rule currentRule) {
        this.currentRule = currentRule;
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
    public void addViolationWithPosition(Node location, int beginLine, int endLine, String message, Object... formatArgs) {
        Objects.requireNonNull(location, "Node was null");
        Objects.requireNonNull(message, "Message was null");
        Objects.requireNonNull(formatArgs, "Format arguments were null, use an empty array");

        RuleViolationFactory fact = getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory();
        if (beginLine != -1 && endLine != -1) {
            fact.addViolation(this, getCurrentRule(), location, message, beginLine, endLine, formatArgs);
        } else {
            fact.addViolation(this, getCurrentRule(), location, message, formatArgs);
        }
    }


    /**
     * Get the Report to which Rule Violations are sent.
     *
     * @return The Report.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public Report getReport() {
        return report;
    }

    /**
     * Set the Report to which Rule Violations are sent.
     *
     * @param report The Report.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public void setReport(Report report) {
        this.report = report;
    }

    /**
     * Get the File associated with the current source file.
     *
     * @return The File.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public File getSourceCodeFile() {
        return sourceCodeFile;
    }

    /**
     * Set the File associated with the current source file. While this may be
     * set to <code>null</code>, the exclude/include facilities will not work
     * properly without a File.
     *
     * @param sourceCodeFile The File.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public void setSourceCodeFile(File sourceCodeFile) {
        this.sourceCodeFile = sourceCodeFile;
    }

    /**
     * Get the file name associated with the current source file.
     * If there is no source file, then an empty string is returned.
     *
     * @return The file name.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public String getSourceCodeFilename() {
        if (sourceCodeFile != null) {
            return sourceCodeFile.getName();
        }
        return "";
    }

    /**
     * Set the file name associated with the current source file.
     *
     * @param filename
     *            The file name.
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public void setSourceCodeFilename(String filename) {
        // ignored, does nothing.
        LOG.warning("The method RuleContext::setSourceCodeFilename(String) has been deprecated and will be removed.");
    }

    /**
     * Get the LanguageVersion associated with the current source file.
     *
     * @return The LanguageVersion, <code>null</code> if unknown.
     *
     * @deprecated Will be removed in PMD 7, as the nodes have access
     *     to their language version. In PMD 6, this is still the only way
     *     to access the language version within a rule, and cannot be replaced.
     *     The deprecation warning hints that the method should be replaced
     *     in PMD 7.
     */
    @Deprecated
    public LanguageVersion getLanguageVersion() {
        return this.languageVersion;
    }

    /**
     * Set the LanguageVersion associated with the current source file. This may
     * be set to <code>null</code> to indicate the version is unknown and should
     * be automatically determined.
     *
     * @param languageVersion The LanguageVersion.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public void setLanguageVersion(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
    }

    /**
     * Set an attribute value on the RuleContext, if it does not already exist.
     * <p>
     * Attributes can be shared between RuleContext instances. This operation is
     * thread-safe.
     * <p>
     * Attribute values should be modified directly via the reference provided.
     * It is not necessary to call <code>setAttribute(String, Object)</code> to
     * update an attribute value. Modifications made to the attribute value will
     * automatically be seen by other threads. Because of this, you must ensure
     * the attribute values are themselves thread safe.
     *
     * @param name
     *            The attribute name.
     * @param value
     *            The attribute value.
     * @exception IllegalArgumentException
     *                if <code>name</code> or <code> value</code> are
     *                <code>null</code>
     * @return <code>true</code> if the attribute was set, <code>false</code>
     *         otherwise.
     *
     * @deprecated Stateful methods of the rule context will be removed.
     * Their interaction with incremental analysis are unspecified.
     */
    @Deprecated
    public boolean setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("Parameter 'name' cannot be null.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Parameter 'value' cannot be null.");
        }
        return this.attributes.putIfAbsent(name, value) == null;
    }

    /**
     * Get an attribute value on the RuleContext.
     * <p>
     * Attributes can be shared between RuleContext instances. This operation is
     * thread-safe.
     * <p>
     * Attribute values should be modified directly via the reference provided.
     * It is not necessary to call <code>setAttribute(String, Object)</code> to
     * update an attribute value. Modifications made to the attribute value will
     * automatically be seen by other threads. Because of this, you must ensure
     * the attribute values are themselves thread safe.
     *
     * @param name
     *            The attribute name.
     * @return The current attribute value, or <code>null</code> if the
     *         attribute does not exist.
     *
     * @deprecated Stateful methods of the rule context will be removed.
     * Their interaction with incremental analysis are unspecified.
     */
    @Deprecated
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    /**
     * Remove an attribute value on the RuleContext.
     * <p>
     * Attributes can be shared between RuleContext instances. This operation is
     * thread-safe.
     * <p>
     * Attribute values should be modified directly via the reference provided.
     * It is not necessary to call <code>setAttribute(String, Object)</code> to
     * update an attribute value. Modifications made to the attribute value will
     * automatically be seen by other threads. Because of this, you must ensure
     * the attribute values are themselves thread safe.
     *
     * @param name
     *            The attribute name.
     * @return The current attribute value, or <code>null</code> if the
     *         attribute does not exist.
     *
     * @deprecated Stateful methods of the rule context will be removed.
     * Their interaction with incremental analysis are unspecified.
     */
    @Deprecated
    public Object removeAttribute(String name) {
        return this.attributes.remove(name);
    }

    /**
     * Configure whether exceptions during applying a rule should be ignored or
     * not. If set to <code>true</code> then such exceptions are logged as
     * warnings and the processing is continued with the next rule - the failing
     * rule is simply skipped. This is the default behavior. <br>
     * If set to <code>false</code> then the processing will be aborted with the
     * exception. This is especially useful during unit tests, in order to not
     * oversee any exceptions.
     *
     * @param ignoreExceptions if <code>true</code> simply skip failing rules (default).
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public void setIgnoreExceptions(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    /**
     * Gets the configuration whether to skip failing rules (<code>true</code>)
     * or whether to throw a a RuntimeException and abort the processing for the
     * first failing rule.
     *
     * @return <code>true</code> when failing rules are skipped,
     *     <code>false</code> otherwise.
     *
     * @deprecated Internal API, removed in PMD 7
     */
    @Deprecated
    @InternalApi
    public boolean isIgnoreExceptions() {
        return ignoreExceptions;
    }
}
