/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.renderers.AbstractAccumulatingRenderer;

/**
 * A {@link Report} collects all informations during a PMD execution. This
 * includes violations, suppressed violations, metrics, error during processing
 * and configuration errors.
 */
public class Report {

    private final List<ThreadSafeReportListener> listeners = new ArrayList<>();

    private final List<RuleViolation> violations = new ArrayList<>();
    private final List<SuppressedViolation> suppressedRuleViolations = new ArrayList<>();
    private final List<ProcessingError> errors = new ArrayList<>();
    private final List<ConfigurationError> configErrors = new ArrayList<>();
    private final Object lock = new Object();

    /**
     * Creates a new, initialized, empty report for the given file name.
     *
     * @param ctx
     *            The context to use to connect to the report
     * @param fileName
     *            the filename used to report any violations
     * @return the new report
     */
    public static Report createReport(RuleContext ctx, String fileName) {
        Report report = new Report();

        // overtake the listener
        report.addListeners(ctx.getReport().getListeners());

        ctx.setReport(report);
        ctx.setSourceCodeFile(new File(fileName));
        return report;
    }

    /**
     * Represents a configuration error.
     */
    public static class ConfigurationError {
        private final Rule rule;
        private final String issue;

        /**
         * Creates a new configuration error for a specific rule.
         *
         * @param theRule
         *            the rule which is configured wrongly
         * @param theIssue
         *            the reason, why the configuration is wrong
         */
        public ConfigurationError(Rule theRule, String theIssue) {
            rule = theRule;
            issue = theIssue;
        }

        /**
         * Gets the wrongly configured rule
         *
         * @return the wrongly configured rule
         */
        public Rule rule() {
            return rule;
        }

        /**
         * Gets the reason for the configuration error.
         *
         * @return the issue
         */
        public String issue() {
            return issue;
        }
    }

    /**
     * Represents a processing error, such as a parse error.
     */
    public static class ProcessingError {
        private final Throwable error;
        private final String file;

        /**
         * Creates a new processing error
         *
         * @param error
         *            the error
         * @param file
         *            the file during which the error occurred
         */
        public ProcessingError(Throwable error, String file) {
            this.error = error;
            this.file = file;
        }

        public String getMsg() {
            return error.getClass().getSimpleName() + ": " + error.getMessage();
        }

        public String getDetail() {
            try (StringWriter stringWriter = new StringWriter();
                    PrintWriter writer = new PrintWriter(stringWriter)) {
                error.printStackTrace(writer);
                return stringWriter.toString();
            } catch (IOException e) {
                // IOException on close - should never happen when using StringWriter
                throw new RuntimeException(e);
            }
        }

        public String getFile() {
            return file;
        }

        public Throwable getError() {
            return error;
        }
    }

    /**
     * Represents a violation, that has been suppressed.
     */
    public static class SuppressedViolation {
        private final RuleViolation rv;
        private final String userMessage;
        private final ViolationSuppressor suppressor;

        /**
         * Creates a suppressed violation.
         *
         * @param rv          The violation, that has been suppressed
         * @param suppressor  The suppressor which suppressed the violation
         * @param userMessage Any relevant info given by the suppressor
         */
        public SuppressedViolation(RuleViolation rv, ViolationSuppressor suppressor, String userMessage) {
            this.suppressor = suppressor;
            this.rv = rv;
            this.userMessage = userMessage;
        }

        public ViolationSuppressor getSuppressor() {
            return suppressor;
        }

        public RuleViolation getRuleViolation() {
            return this.rv;
        }

        public String getUserMessage() {
            return userMessage;
        }
    }

    /**
     * Registers a report listener
     *
     * @param listener the listener
     */
    public void addListener(ThreadSafeReportListener listener) {
        listeners.add(listener);
    }

    /**
     * Adds a new rule violation to the report and notify the listeners.
     *
     * @param violation the violation to add
     */
    public void addRuleViolation(RuleViolation violation) {
        int index = Collections.binarySearch(violations, violation, RuleViolation.DEFAULT_COMPARATOR);
        violations.add(index < 0 ? -index - 1 : index, violation);
        for (ThreadSafeReportListener listener : listeners) {
            listener.ruleViolationAdded(violation);
        }
    }

    /**
     * Adds a new suppressed violation.
     */
    public void addSuppressedViolation(SuppressedViolation sv) {
        suppressedRuleViolations.add(sv);
    }

    /**
     * Adds a new configuration error to the report.
     *
     * @param error the error to add
     */
    public void addConfigError(ConfigurationError error) {
        configErrors.add(error);
    }

    /**
     * Adds a new processing error to the report.
     *
     * @param error
     *            the error to add
     */
    public void addError(ProcessingError error) {
        errors.add(error);
    }

    /**
     * Merges the given report into this report. This might be necessary, if a
     * summary over all violations is needed as PMD creates one report per file
     * by default.
     *
     * <p>This is synchronized on an internal lock (note that other mutation
     * operations are not synchronized, todo for pmd 7).
     *
     * @param r the report to be merged into this.
     *
     * @see AbstractAccumulatingRenderer
     */
    public void merge(Report r) {
        synchronized (lock) {
            errors.addAll(r.errors);
            configErrors.addAll(r.configErrors);
            suppressedRuleViolations.addAll(r.suppressedRuleViolations);

            for (RuleViolation violation : r.getViolations()) {
                int index = Collections.binarySearch(violations, violation, RuleViolation.DEFAULT_COMPARATOR);
                violations.add(index < 0 ? -index - 1 : index, violation);
            }
        }
    }


    /**
     * Returns an unmodifiable list of violations that were suppressed.
     */
    public final List<SuppressedViolation> getSuppressedViolations() {
        return Collections.unmodifiableList(suppressedRuleViolations);
    }

    /**
     * Returns an unmodifiable list of violations that have been
     * recorded until now. None of those violations were suppressed.
     *
     * <p>The violations list is sorted with {@link RuleViolation#DEFAULT_COMPARATOR}.
     */
    public final List<RuleViolation> getViolations() {
        return Collections.unmodifiableList(violations);
    }


    /**
     * Returns an unmodifiable list of processing errors that have been
     * recorded until now.
     */
    public final List<ProcessingError> getProcessingErrors() {
        return Collections.unmodifiableList(errors);
    }


    /**
     * Returns an unmodifiable list of configuration errors that have
     * been recorded until now.
     */
    public final List<ConfigurationError> getConfigurationErrors() {
        return Collections.unmodifiableList(configErrors);
    }


    public List<ThreadSafeReportListener> getListeners() {
        return listeners;
    }

    /**
     * Adds all given listeners to this report
     *
     * @param allListeners
     *            the report listeners
     */
    public void addListeners(List<ThreadSafeReportListener> allListeners) {
        listeners.addAll(allListeners);
    }
}
