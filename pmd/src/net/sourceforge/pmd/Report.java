/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.dfa.report.ReportTree;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.util.NumericConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Report {

    public static class ReadableDuration {
        private final long duration;

        public ReadableDuration(long duration) {
            this.duration = duration;
        }

        public String getTime() {
            long seconds = 0;
            long minutes = 0;
            long hours = 0;

            if (duration > 1000) {
                seconds = duration / 1000;
            }

            if (seconds > 60) {
                minutes = seconds / 60;
                seconds = seconds % 60;
            }

            if (minutes > 60) {
                hours = minutes / 60;
                minutes = minutes % 60;
            }

            StringBuffer res = new StringBuffer();
            if (hours > 0) {
                res.append(hours).append("h ");
            }
            if (hours > 0 || minutes > 0) {
                res.append(minutes).append("m ");
            }
            res.append(seconds).append('s');
            return res.toString();
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
        private final IRuleViolation rv;
        private final boolean isNOPMD;
        private final String userMessage;

        public SuppressedViolation(IRuleViolation rv, boolean isNOPMD, String userMessage) {
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

        public IRuleViolation getRuleViolation() {
            return this.rv;
        }

        public String getUserMessage() {
            return userMessage;
        }
    }

    private static final RuleViolation.RuleViolationComparator COMPARATOR = new RuleViolation.RuleViolationComparator();

    /*
     * The idea is to store the violations in a tree instead of a list, to do
     * better and faster sort and filter mechanism and to visualize the result
     * als tree. (ide plugins).
     * */
    private final ReportTree violationTree = new ReportTree();

    // Note that this and the above data structure are both being maintained for a bit
    private final Set<IRuleViolation> violations = new TreeSet<IRuleViolation>(COMPARATOR);
    private final Set<Metric> metrics = new HashSet<Metric>();
    private final List<ReportListener> listeners = new ArrayList<ReportListener>();
    private final List<ProcessingError> errors = new ArrayList<ProcessingError>();
    private Map<Integer, String> linesToExclude = new HashMap<Integer, String>();
    private long start;
    private long end;

    private List<SuppressedViolation> suppressedRuleViolations = new ArrayList<SuppressedViolation>();

    public void exclude(Map<Integer, String> lines) {
        linesToExclude = lines;
    }

    public Map<String, Integer> getCountSummary() {
        Map<String, Integer> summary = new HashMap<String, Integer>();
        for (Iterator<IRuleViolation> iter = violationTree.iterator(); iter.hasNext();) {
            IRuleViolation rv = iter.next();
            String key = "";
            if (rv.getPackageName() != null && rv.getPackageName().length() != 0) {
                key = rv.getPackageName() + '.' + rv.getClassName();
            }
            Integer o = summary.get(key);
            if (o == null) {
                summary.put(key, NumericConstants.ONE);
            } else {
                summary.put(key, o+1);
            }
        }
        return summary;
    }

    public ReportTree getViolationTree() {
        return this.violationTree;
    }


    /**
     * @return a Map summarizing the Report: String (rule name) ->Integer (count of violations)
     */
    public Map<String, Integer> getSummary() {
        Map<String, Integer> summary = new HashMap<String, Integer>();
        for (IRuleViolation rv: violations) {
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
        listeners.add(listener);
    }

    public List<SuppressedViolation> getSuppressedRuleViolations() {
        return suppressedRuleViolations;
    }

    public void addRuleViolation(IRuleViolation violation) {

        // NOPMD excluder
        int line = violation.getBeginLine();
        if (linesToExclude.containsKey(line)) {
            suppressedRuleViolations.add(new SuppressedViolation(violation, true, linesToExclude.get(line)));
            return;
        }

        if (violation.isSuppressed()) {
            suppressedRuleViolations.add(new SuppressedViolation(violation, false, null));
            return;
        }


        violations.add(violation);
        violationTree.addRuleViolation(violation);
        for (ReportListener listener: listeners) {
            listener.ruleViolationAdded(violation);
        }
    }

    public void addMetric(Metric metric) {
        metrics.add(metric);
        for (ReportListener listener: listeners) {
            listener.metricAdded(metric);
        }
    }

    public void addError(ProcessingError error) {
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
        Iterator<IRuleViolation> v = r.iterator();
        while (v.hasNext()) {
            IRuleViolation violation = v.next();
            violations.add(violation);
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
        return !violations.iterator().hasNext() && errors.isEmpty();
    }

    public boolean treeIsEmpty() {
        return !violationTree.iterator().hasNext();
    }

    public Iterator<IRuleViolation> treeIterator() {
        return violationTree.iterator();
    }

    public Iterator<IRuleViolation> iterator() {
        return violations.iterator();
    }

    public Iterator<ProcessingError> errors() {
        return errors.iterator();
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
}
