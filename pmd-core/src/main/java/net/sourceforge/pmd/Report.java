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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.dfa.report.ReportTree;
import net.sourceforge.pmd.renderers.AbstractAccumulatingRenderer;
import net.sourceforge.pmd.util.DateTimeUtil;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * A {@link Report} collects all informations during a PMD execution. This
 * includes violations, suppressed violations, metrics, error during processing
 * and configuration errors.
 */
public class Report implements Iterable<RuleViolation> {

    /*
     * The idea is to store the violations in a tree instead of a list, to do
     * better and faster sort and filter mechanism and to visualize the result
     * as tree. (ide plugins).
     */
    private final ReportTree violationTree = new ReportTree();

    // Note that this and the above data structure are both being maintained for
    // a bit
    private final List<RuleViolation> violations = new ArrayList<>();
    private final List<ThreadSafeReportListener> listeners = new ArrayList<>();
    private final List<ProcessingError> errors = new ArrayList<>();
    private final List<ConfigurationError> configErrors = new ArrayList<>();
    private long start;
    private long end;
    private final List<SuppressedViolation> suppressedRuleViolations = new ArrayList<>();

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
     * Represents a duration. Useful for reporting processing time.
     *
     * @deprecated Not used within PMD. Rendering durations is format-specific.
     */
    @Deprecated
    public static class ReadableDuration {
        private final long duration;

        /**
         * Creates a new duration.
         *
         * @param duration
         *            the duration in milliseconds.
         */
        public ReadableDuration(long duration) {
            this.duration = duration;
        }

        /**
         * Gets a human readable representation of the duration, such as "1h 3m
         * 5s".
         *
         * @return human readable representation of the duration
         */
        public String getTime() {
            return DateTimeUtil.asHoursMinutesSeconds(duration);
        }
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


    private static String keyFor(RuleViolation rv) {

        return StringUtils.isNotBlank(rv.getPackageName()) ? rv.getPackageName() + '.' + rv.getClassName() : "";
    }

    /**
     * Calculate a summary of violation counts per fully classified class name.
     *
     * @return violations per class name
     *
     * @deprecated This is too specific. Not every violation has a qualified name.
     */
    @Deprecated
    public Map<String, Integer> getCountSummary() {
        Map<String, Integer> summary = new HashMap<>();
        for (RuleViolation rv : violationTree) {
            String key = keyFor(rv);
            Integer o = summary.get(key);
            summary.put(key, o == null ? NumericConstants.ONE : o + 1);
        }
        return summary;
    }

    /**
     * @deprecated The {@link ReportTree} is deprecated
     */
    @Deprecated
    public ReportTree getViolationTree() {
        return this.violationTree;
    }

    /**
     * Calculate a summary of violations per rule.
     *
     * @return a Map summarizing the Report: String (rule name) -&gt; Integer (count
     *         of violations)
     *
     * @deprecated This is too specific, only used by one renderer.
     */
    @Deprecated
    public Map<String, Integer> getSummary() {
        Map<String, Integer> summary = new HashMap<>();
        for (RuleViolation rv : violations) {
            String name = rv.getRule().getName();
            if (!summary.containsKey(name)) {
                summary.put(name, NumericConstants.ZERO);
            }
            Integer count = summary.get(name);
            summary.put(name, count + 1);
        }
        return summary;
    }

    /**
     * Registers a report listener
     *
     * @param listener
     *            the listener
     */
    public void addListener(ThreadSafeReportListener listener) {
        listeners.add(listener);
    }

    /**
     * Returns the suppressed violations.
     *
     * @deprecated Use {@link #getSuppressedViolations()} (be aware, that that method returns an unmodifiable list)
     */
    @Deprecated
    public List<SuppressedViolation> getSuppressedRuleViolations() {
        return suppressedRuleViolations;
    }

    /**
     * Represents a violation, that has been suppressed.
     * TODO this should implement RuleViolation
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


    public void addSuppressedViolation(SuppressedViolation sv) {
        suppressedRuleViolations.add(sv);
    }

    /**
     * Adds a new rule violation to the report and notify the listeners.
     *
     * @param violation
     *            the violation to add
     */
    public void addRuleViolation(RuleViolation violation) {
        int index = Collections.binarySearch(violations, violation, RuleViolationComparator.INSTANCE);
        violations.add(index < 0 ? -index - 1 : index, violation);
        violationTree.addRuleViolation(violation);
        for (ThreadSafeReportListener listener : listeners) {
            listener.ruleViolationAdded(violation);
        }
    }

    /**
     * Adds a new configuration error to the report.
     *
     * @param error
     *            the error to add
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
     * @param r
     *            the report to be merged into this.
     * @see AbstractAccumulatingRenderer
     */
    public void merge(Report r) {
        errors.addAll(r.errors);
        configErrors.addAll(r.configErrors);
        suppressedRuleViolations.addAll(r.suppressedRuleViolations);

        for (RuleViolation violation : r.getViolations()) {
            int index = Collections.binarySearch(violations, violation, RuleViolationComparator.INSTANCE);
            violations.add(index < 0 ? -index - 1 : index, violation);
            violationTree.addRuleViolation(violation);
        }
    }


    /**
     * Checks whether there are no violations and no processing errors.
     * That means, that PMD analysis yielded nothing to worry about.
     *
     * @deprecated Use {@link #getViolations()} or {@link #getProcessingErrors()}
     */
    @Deprecated
    public boolean isEmpty() {
        return !violations.iterator().hasNext() && !hasErrors();
    }

    /**
     * Checks whether any processing errors have been reported.
     *
     * @return <code>true</code> if there were any processing errors,
     *         <code>false</code> otherwise
     *
     * @deprecated Use {@link #getProcessingErrors()}.isEmpty()
     */
    @Deprecated
    public boolean hasErrors() {
        return !getProcessingErrors().isEmpty();
    }

    /**
     * Checks whether any configuration errors have been reported.
     *
     * @return <code>true</code> if there were any configuration errors,
     *         <code>false</code> otherwise
     *
     * @deprecated Use {@link #getConfigurationErrors()}.isEmpty()
     */
    @Deprecated
    public boolean hasConfigErrors() {
        return !getConfigurationErrors().isEmpty();
    }

    /**
     * Checks whether no violations have been reported.
     *
     * @return <code>true</code> if no violations have been reported,
     *         <code>false</code> otherwise
     *
     * @deprecated The {@link ReportTree} is deprecated, use {@link #getViolations()}.isEmpty() instead.
     */
    @Deprecated
    public boolean treeIsEmpty() {
        return !violationTree.iterator().hasNext();
    }

    /**
     * Returns an iteration over the reported violations.
     *
     * @return an iterator
     *
     * @deprecated The {@link ReportTree} is deprecated
     */
    @Deprecated
    public Iterator<RuleViolation> treeIterator() {
        return violationTree.iterator();
    }

    /**
     * @deprecated Use {@link #getViolations()}
     */
    @Deprecated
    @Override
    public Iterator<RuleViolation> iterator() {
        return violations.iterator();
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
     * <p>The violations list is sorted with {@link RuleViolationComparator#INSTANCE}.
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


    /**
     * Returns an iterator of the reported processing errors.
     *
     * @return the iterator
     *
     * @deprecated Use {@link #getProcessingErrors()}
     */
    @Deprecated
    public Iterator<ProcessingError> errors() {
        return getProcessingErrors().iterator();
    }

    /**
     * Returns an iterator of the reported configuration errors.
     *
     * @return the iterator
     * @deprecated Use {@link #getConfigurationErrors()}
     */
    @Deprecated
    public Iterator<ConfigurationError> configErrors() {
        return getConfigurationErrors().iterator();
    }

    /**
     * The number of violations.
     *
     * @return number of violations.
     *
     * @deprecated The {@link ReportTree} is deprecated
     */
    @Deprecated
    public int treeSize() {
        return violationTree.size();
    }

    /**
     * The number of violations.
     *
     * @return number of violations.
     *
     * @deprecated Use {@link #getViolations()}
     */
    @Deprecated
    public int size() {
        return violations.size();
    }

    /**
     * Mark the start time of the report. This is used to get the elapsed time
     * in the end.
     *
     * @see #getElapsedTimeInMillis()
     *
     * @deprecated Not used, {@link #getElapsedTimeInMillis()} will be removed
     */
    @Deprecated
    public void start() {
        start = System.currentTimeMillis();
    }

    /**
     * Mark the end time of the report. This is ued to get the elapsed time.
     *
     * @see #getElapsedTimeInMillis()
     * @deprecated Not used, {@link #getElapsedTimeInMillis()} will be removed
     */
    @Deprecated
    public void end() {
        end = System.currentTimeMillis();
    }

    /**
     * @deprecated Unused
     */
    @Deprecated
    public long getElapsedTimeInMillis() {
        return end - start;
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
