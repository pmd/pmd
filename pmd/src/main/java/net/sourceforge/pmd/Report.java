/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.dfa.report.ReportTree;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.DateTimeUtil;
import net.sourceforge.pmd.util.EmptyIterator;
import net.sourceforge.pmd.util.NumericConstants;
import net.sourceforge.pmd.util.StringUtil;

public class Report implements Iterable<RuleViolation> {

    public static Report createReport(RuleContext ctx, String fileName) {
        Report report = new Report();

        // overtake the listener
        report.addSynchronizedListeners(ctx.getReport().getSynchronizedListeners());

        ctx.setReport(report);
        ctx.setSourceCodeFilename(fileName);
        ctx.setSourceCodeFile(new File(fileName));
        return report;
    }

    public static class ReadableDuration {
        private final long duration;

        public ReadableDuration(long duration) {
            this.duration = duration;
        }

        public String getTime() {
            return DateTimeUtil.asHoursMinutesSeconds(duration);
        }
    }

    public static class RuleConfigurationError {
        private final Rule rule;
        private final String issue;

        public RuleConfigurationError(Rule theRule, String theIssue) {
            rule = theRule;
            issue = theIssue;
        }

        public Rule rule() {
            return rule;
        }

        public String issue() {
            return issue;
        }
    }

    public static class ProcessingError {
        private final String msg;
        private final String file;

        public ProcessingError(String msg, String file) {
            this.msg = msg;
            this.file = file;
        }

        public String getMsg() {
            return msg;
        }

        public String getFile() {
            return file;
        }
    }

    public static class SuppressedViolation {
        private final RuleViolation rv;
        private final boolean isNOPMD;
        private final String userMessage;

        public SuppressedViolation(RuleViolation rv, boolean isNOPMD, String userMessage) {
            this.isNOPMD = isNOPMD;
            this.rv = rv;
            this.userMessage = userMessage;
        }

        public boolean suppressedByNOPMD() {
            return this.isNOPMD;
        }

        public boolean suppressedByAnnotation() {
            return !this.isNOPMD;
        }

        public RuleViolation getRuleViolation() {
            return this.rv;
        }

        public String getUserMessage() {
            return userMessage;
        }
    }

    /*
     * The idea is to store the violations in a tree instead of a list, to do
     * better and faster sort and filter mechanism and to visualize the result
     * as tree. (ide plugins).
     */
    private final ReportTree violationTree = new ReportTree();

    // Note that this and the above data structure are both being maintained for
    // a bit
    private final List<RuleViolation> violations = new ArrayList<RuleViolation>();
    private final Set<Metric> metrics = new HashSet<Metric>();
    private final List<SynchronizedReportListener> listeners = new ArrayList<SynchronizedReportListener>();
    private List<ProcessingError> errors;
    private List<RuleConfigurationError> configErrors;
    private Map<Integer, String> linesToSuppress = new HashMap<Integer, String>();
    private long start;
    private long end;

    private List<SuppressedViolation> suppressedRuleViolations = new ArrayList<SuppressedViolation>();

    public void suppress(Map<Integer, String> lines) {
        linesToSuppress = lines;
    }

    private static String keyFor(RuleViolation rv) {

        return StringUtil.isNotEmpty(rv.getPackageName()) ? rv.getPackageName() + '.' + rv.getClassName() : "";
    }

    public Map<String, Integer> getCountSummary() {
        Map<String, Integer> summary = new HashMap<String, Integer>();
        for (RuleViolation rv : violationTree) {
            String key = keyFor(rv);
            Integer o = summary.get(key);
            summary.put(key, o == null ? NumericConstants.ONE : o + 1);
        }
        return summary;
    }

    public ReportTree getViolationTree() {
        return this.violationTree;
    }

    /**
     * @return a Map summarizing the Report: String (rule name) ->Integer (count
     *         of violations)
     */
    public Map<String, Integer> getSummary() {
        Map<String, Integer> summary = new HashMap<String, Integer>();
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

    public void addListener(ReportListener listener) {
        listeners.add(new SynchronizedReportListener(listener));
    }

    public List<SuppressedViolation> getSuppressedRuleViolations() {
        return suppressedRuleViolations;
    }

    public void addRuleViolation(RuleViolation violation) {

        // NOPMD suppress
        int line = violation.getBeginLine();
        if (linesToSuppress.containsKey(line)) {
            suppressedRuleViolations.add(new SuppressedViolation(violation, true, linesToSuppress.get(line)));
            return;
        }

        if (violation.isSuppressed()) {
            suppressedRuleViolations.add(new SuppressedViolation(violation, false, null));
            return;
        }

        int index = Collections.binarySearch(violations, violation, RuleViolationComparator.INSTANCE);
        violations.add(index < 0 ? -index - 1 : index, violation);
        violationTree.addRuleViolation(violation);
        for (ReportListener listener : listeners) {
            listener.ruleViolationAdded(violation);
        }
    }

    public void addMetric(Metric metric) {
        metrics.add(metric);
        for (ReportListener listener : listeners) {
            listener.metricAdded(metric);
        }
    }

    public void addConfigError(RuleConfigurationError error) {
        if (configErrors == null)
            configErrors = new ArrayList<RuleConfigurationError>();
        configErrors.add(error);
    }

    public void addError(ProcessingError error) {
        if (errors == null)
            errors = new ArrayList<ProcessingError>();
        errors.add(error);
    }

    public void merge(Report r) {
        Iterator<ProcessingError> i = r.errors();
        while (i.hasNext()) {
            addError(i.next());
        }
        Iterator<Metric> m = r.metrics();
        while (m.hasNext()) {
            addMetric(m.next());
        }
        Iterator<RuleViolation> v = r.iterator();
        while (v.hasNext()) {
            RuleViolation violation = v.next();
            int index = Collections.binarySearch(violations, violation, RuleViolationComparator.INSTANCE);
            violations.add(index < 0 ? -index - 1 : index, violation);
            violationTree.addRuleViolation(violation);
        }
        Iterator<SuppressedViolation> s = r.getSuppressedRuleViolations().iterator();
        while (s.hasNext()) {
            suppressedRuleViolations.add(s.next());
        }
    }

    public boolean hasMetrics() {
        return !metrics.isEmpty();
    }

    public Iterator<Metric> metrics() {
        return metrics.iterator();
    }

    public boolean isEmpty() {
        return !violations.iterator().hasNext() && !hasErrors();
    }

    public boolean hasErrors() {
        return errors != null;
    }

    public boolean hasConfigErrors() {
        return configErrors != null;
    }

    public boolean treeIsEmpty() {
        return !violationTree.iterator().hasNext();
    }

    public Iterator<RuleViolation> treeIterator() {
        return violationTree.iterator();
    }

    @Override
    public Iterator<RuleViolation> iterator() {
        return violations.iterator();
    }

    public Iterator<ProcessingError> errors() {
        return errors == null ? EmptyIterator.<ProcessingError> instance() : errors.iterator();
    }

    public Iterator<RuleConfigurationError> configErrors() {
        return configErrors == null ? EmptyIterator.<RuleConfigurationError> instance() : configErrors.iterator();
    }

    public int treeSize() {
        return violationTree.size();
    }

    public int size() {
        return violations.size();
    }

    public void start() {
        start = System.currentTimeMillis();
    }

    public void end() {
        end = System.currentTimeMillis();
    }

    public long getElapsedTimeInMillis() {
        return end - start;
    }

    public List<SynchronizedReportListener> getSynchronizedListeners() {
        return listeners;
    }

    public void addSynchronizedListeners(List<SynchronizedReportListener> synchronizedListeners) {
        listeners.addAll(synchronizedListeners);
    }
}
