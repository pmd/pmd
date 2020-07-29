/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.processor.ThreadSafeAnalysisListener;

/**
 * The RuleContext provides access to Rule processing state. This information
 * includes the following global information:
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
public class RuleContext implements AutoCloseable {

    private static final Object[] NO_ARGS = new Object[0];

    private static final Logger LOG = Logger.getLogger(RuleContext.class.getName());

    private Report report = new Report();
    private File sourceCodeFile;
    private LanguageVersion languageVersion;
    private boolean ignoreExceptions = true;

    private final ThreadSafeAnalysisListener listener;

    /**
     * Default constructor.
     */
    public RuleContext() {
        listener = ThreadSafeAnalysisListener.noop();
    }

    /**
     * Constructor which shares attributes and report listeners with the given
     * RuleContext.
     *
     * @param ruleContext the context from which the values are shared
     */
    public RuleContext(RuleContext ruleContext) {
        this.listener = ruleContext.listener;
        this.setIgnoreExceptions(ruleContext.ignoreExceptions);
    }

    public RuleContext(ThreadSafeAnalysisListener listener) {
        this.listener = listener;
    }


    @Override
    public void close() throws Exception {
        listener.close();
    }

    public void reportError(ProcessingError error) {
        listener.onError(error);
    }


    public void addViolation(Rule rule, Node location) {
        addViolationWithMessage(rule, location, rule.getMessage(), NO_ARGS);
    }

    public void addViolation(Rule rule, Node location, Object... formatArgs) {
        addViolationWithMessage(rule, location, rule.getMessage(), formatArgs);
    }

    public void addViolationWithMessage(Rule rule, Node location, String message) {
        addViolationWithPosition(rule, location, -1, -1, message, NO_ARGS);
    }

    public void addViolationWithMessage(Rule rule, Node location, String message, Object... formatArgs) {
        addViolationWithPosition(rule, location, -1, -1, message, formatArgs);
    }

    public void addViolationNoSuppress(RuleViolation rv) {
        listener.onRuleViolation(rv);
    }

    public void addViolationWithPosition(Rule rule, Node location, int beginLine, int endLine, String message, Object... formatArgs) {
        Objects.requireNonNull(rule);
        Objects.requireNonNull(location);
        Objects.requireNonNull(message);
        Objects.requireNonNull(formatArgs);

        // at some point each Node will know its language version
        RuleViolationFactory fact = getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory();

        RuleViolation violation = fact.createViolation(rule, location, getSourceCodeFilename(), makeMessage(message, formatArgs));
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

    private String makeMessage(@NonNull String message, Object[] args) {
        // Escape PMD specific variable message format, specifically the {
        // in the ${, so MessageFormat doesn't bitch.
        final String escapedMessage = StringUtils.replace(message, "${", "$'{'");
        return MessageFormat.format(escapedMessage, args != null ? args : NO_ARGS);
    }


    /**
     * Get the Report to which Rule Violations are sent.
     *
     * @return The Report.
     */
    public Report getReport() {
        return report;
    }

    /**
     * Set the Report to which Rule Violations are sent.
     *
     * @param report
     *            The Report.
     */
    public void setReport(Report report) {
        this.report = report;
    }

    /**
     * Get the File associated with the current source file.
     *
     * @return The File.
     */
    public File getSourceCodeFile() {
        return sourceCodeFile;
    }

    /**
     * Set the File associated with the current source file. While this may be
     * set to <code>null</code>, the exclude/include facilities will not work
     * properly without a File.
     *
     * @param sourceCodeFile
     *            The File.
     */
    public void setSourceCodeFile(File sourceCodeFile) {
        this.sourceCodeFile = sourceCodeFile;
    }

    /**
     * Get the file name associated with the current source file.
     * If there is no source file, then an empty string is returned.
     *
     * @return The file name.
     *
     * @deprecated Will be replaced by the document API. Nodes will
     *     have access to their document.
     */
    @Deprecated
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
     * @deprecated This method will be removed. The file should only be
     * set with {@link #setSourceCodeFile(File)}. Setting the filename here
     * has no effect.
     */
    @Deprecated
    public void setSourceCodeFilename(String filename) {
        // ignored, does nothing.
        LOG.warning("The method RuleContext::setSourceCodeFilename(String) has been deprecated and will be removed."
                + "Setting the filename here has no effect. Use RuleContext::setSourceCodeFile(File) instead.");
    }

    /**
     * Get the LanguageVersion associated with the current source file.
     *
     * @return The LanguageVersion, <code>null</code> if unknown.
     *
     * @deprecated Will be replaced by a method on Node (nodes will
     *     know their language version).
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
     * @param languageVersion
     *            The LanguageVersion.
     */
    @Deprecated
    public void setLanguageVersion(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
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
     * @param ignoreExceptions
     *            if <code>true</code> simply skip failing rules (default).
     */
    public void setIgnoreExceptions(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    /**
     * Gets the configuration whether to skip failing rules (<code>true</code>)
     * or whether to throw a a RuntimeException and abort the processing for the
     * first failing rule.
     *
     * @return <code>true</code> when failing rules are skipped,
     *         <code>false</code> otherwise.
     */
    public boolean isIgnoreExceptions() {
        return ignoreExceptions;
    }

    public static RuleContext throwingExceptions() {
        RuleContext ctx = new RuleContext();
        ctx.setIgnoreExceptions(false);
        return ctx;
    }
}
