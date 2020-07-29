/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.text.MessageFormat;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.processor.FileAnalysisListener;

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

    private boolean ignoreExceptions = true;

    private final FileAnalysisListener listener;

    /**
     * Default constructor.
     */
    @Deprecated
    public RuleContext() {
        this(FileAnalysisListener.noop());
    }

    public RuleContext(FileAnalysisListener listener) {
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
        RuleViolationFactory fact = location.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory();

        RuleViolation violation = fact.createViolation(rule, location, location.getSourceCodeFile(), makeMessage(message, formatArgs));
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

}
