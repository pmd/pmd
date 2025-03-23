/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import static java.util.Collections.synchronizedList;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.util.BaseResultProducingCloseable;

/**
 * A {@link Report} collects all information during a PMD execution. This includes violations,
 * suppressed violations, metrics, recoverable errors (that occurred during processing)
 * and configuration errors.
 *
 * <p>A report may be created by a {@link GlobalReportBuilderListener} that you
 * use as the {@linkplain GlobalAnalysisListener} in {@link PmdAnalysis#performAnalysisAndCollectReport() PMD's entry point}.
 * You can also create one manually with {@link #buildReport(Consumer)}.
 *
 * <p>For special use cases, like filtering the report after PMD analysis and
 * before rendering the report, some transformation operations are provided:
 * <ul>
 *     <li>{@link #filterViolations(Predicate)}</li>
 *     <li>{@link #union(Report)}</li>
 * </ul>
 * These methods create a new {@link Report} rather than modifying their receiver.
 *
 */
public final class Report {
    private final List<RuleViolation> violations = synchronizedList(new ArrayList<>());
    private final List<SuppressedViolation> suppressedRuleViolations = synchronizedList(new ArrayList<>());
    private final List<ProcessingError> errors = synchronizedList(new ArrayList<>());
    private final List<ConfigurationError> configErrors = synchronizedList(new ArrayList<>());

    private Report() {
        // constructor is private
    }

    /**
     * Represents a configuration error for a specific rule.
     *
     * <p>This might be a missing rule property
     * or rule property with pointless values.
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
         * Gets the wrongly configured rule.
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
     * Represents a recovered error that occurred during analysis.
     *
     * <p>This might be a parse error or an unexpected error originating from a rule.
     * Such errors are called recoverable, because PMD can just skip
     * the problematic file, continue the analysis with the other files and still create a report.
     * However, due to these errors, the report might be incomplete.
     *
     * <p>Some report formats, such as {@link net.sourceforge.pmd.renderers.XMLRenderer}, include these
     * errors for further investigation.
     */
    public static class ProcessingError {

        private final Throwable error;
        private final FileId file;

        /**
         * Creates a new processing error.
         *
         * @param error
         *            the error
         * @param file
         *            the file during which the error occurred
         */
        public ProcessingError(Throwable error, FileId file) {
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

        public FileId getFileId() {
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
     * Adds a new rule violation to the report and notify the listeners.
     *
     * @param violation the violation to add
     */
    private void addRuleViolation(RuleViolation violation) {
        synchronized (violations) {
            // note that this binary search is inefficient as we usually
            // report violations file by file.
            int index = Collections.binarySearch(violations, violation, RuleViolation.DEFAULT_COMPARATOR);
            violations.add(index < 0 ? -index - 1 : index, violation);
        }
    }

    /**
     * Adds a new suppressed violation.
     */
    private void addSuppressedViolation(SuppressedViolation sv) {
        suppressedRuleViolations.add(sv);
    }

    /**
     * Adds a new configuration error to the report.
     *
     * @param error the error to add
     */
    private void addConfigError(ConfigurationError error) {
        configErrors.add(error);
    }

    /**
     * Adds a new processing error to the report.
     *
     * @param error the error to add
     */
    private void addError(ProcessingError error) {
        errors.add(error);
    }

    /**
     * Returns an unmodifiable list of violations that were suppressed.
     */
    public List<SuppressedViolation> getSuppressedViolations() {
        return Collections.unmodifiableList(suppressedRuleViolations);
    }

    /**
     * Returns an unmodifiable list of violations that have been
     * recorded until now. None of those violations were suppressed.
     *
     * <p>The violations list is sorted with {@link RuleViolation#DEFAULT_COMPARATOR}.
     */
    public List<RuleViolation> getViolations() {
        return Collections.unmodifiableList(violations);
    }


    /**
     * Returns an unmodifiable list of processing errors that have been
     * recorded until now.
     */
    public List<ProcessingError> getProcessingErrors() {
        return Collections.unmodifiableList(errors);
    }


    /**
     * Returns an unmodifiable list of configuration errors that have
     * been recorded until now.
     */
    public List<ConfigurationError> getConfigurationErrors() {
        return Collections.unmodifiableList(configErrors);
    }

    /**
     * Create a report by making side effects on a {@link FileAnalysisListener}.
     * This wraps a {@link ReportBuilderListener}.
     */
    public static Report buildReport(Consumer<? super FileAnalysisListener> lambda) {
        return BaseResultProducingCloseable.using(new ReportBuilderListener(), lambda);
    }

    /**
     * A {@link FileAnalysisListener} that accumulates events into a
     * {@link Report}.
     */
    public static final class ReportBuilderListener extends BaseResultProducingCloseable<Report> implements FileAnalysisListener {

        private final Report report;

        public ReportBuilderListener() {
            this(new Report());
        }

        ReportBuilderListener(Report report) {
            this.report = report;
        }

        @Override
        protected Report getResultImpl() {
            return report;
        }

        @Override
        public void onRuleViolation(RuleViolation violation) {
            report.addRuleViolation(violation);
        }

        @Override
        public void onSuppressedRuleViolation(SuppressedViolation violation) {
            report.addSuppressedViolation(violation);
        }

        @Override
        public void onError(ProcessingError error) {
            report.addError(error);
        }

        @Override
        public String toString() {
            return "ReportBuilderListener";
        }
    }

    /**
     * A {@link GlobalAnalysisListener} that accumulates the events of
     * all files into a {@link Report}.
     */
    public static final class GlobalReportBuilderListener extends BaseResultProducingCloseable<Report> implements GlobalAnalysisListener {

        private final Report report = new Report();

        @Override
        public FileAnalysisListener startFileAnalysis(TextFile file) {
            // note that the report is shared, but Report is now thread-safe
            return new ReportBuilderListener(this.report);
        }

        @Override
        public void onConfigError(ConfigurationError error) {
            report.addConfigError(error);
        }

        @Override
        public Report getResultImpl() {
            return report;
        }
    }

    /**
     * Creates a new report taking all the information from this report,
     * but filtering the violations.
     *
     * @param filter when true, the violation will be kept.
     * @return copy of this report
     */
    public Report filterViolations(Predicate<RuleViolation> filter) {
        Report copy = new Report();

        for (RuleViolation violation : violations) {
            if (filter.test(violation)) {
                copy.addRuleViolation(violation);
            }
        }

        copy.suppressedRuleViolations.addAll(suppressedRuleViolations);
        copy.errors.addAll(errors);
        copy.configErrors.addAll(configErrors);
        return copy;
    }

    /**
     * Creates a new report by combining this report with another report.
     * The lowest start time and greatest end time are kept in the copy.
     *
     * @param other the other report to combine
     * @return a new report which is the combination of this and the other report.
     */
    public Report union(Report other) {
        Report copy = new Report();

        for (RuleViolation violation : violations) {
            copy.addRuleViolation(violation);
        }
        for (RuleViolation violation : other.violations) {
            copy.addRuleViolation(violation);
        }

        copy.suppressedRuleViolations.addAll(suppressedRuleViolations);
        copy.suppressedRuleViolations.addAll(other.suppressedRuleViolations);

        copy.errors.addAll(errors);
        copy.errors.addAll(other.errors);
        copy.configErrors.addAll(configErrors);
        copy.configErrors.addAll(other.configErrors);

        return copy;
    }
}
