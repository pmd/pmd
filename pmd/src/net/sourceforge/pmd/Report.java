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
        private long duration;

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
        private String msg;
        private String file;

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
        private IRuleViolation rv;
        private boolean isNOPMD;
        private String userMessage;

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
    private ReportTree violationTree = new ReportTree();

    // Note that this and the above data structure are both being maintained for a bit
    private Set violations = new TreeSet(COMPARATOR);
    private Set metrics = new HashSet();
    private List listeners = new ArrayList();
    private List errors = new ArrayList();
    private Map linesToExclude = new HashMap();
    private long start;
    private long end;

    private List suppressedRuleViolations = new ArrayList();

    public void exclude(Map lines) {
        linesToExclude = lines;
    }

    public Map getCountSummary() {
        Map summary = new HashMap();
        for (Iterator iter = violationTree.iterator(); iter.hasNext();) {
            IRuleViolation rv = (IRuleViolation) iter.next();
            String key = "";
            if (rv.getPackageName() != null && rv.getPackageName().length() != 0) {
                key = rv.getPackageName() + '.' + rv.getClassName();
            }
            Object o = summary.get(key);
            if (o == null) {
                summary.put(key, NumericConstants.ONE);
            } else {
                Integer value = (Integer) o;
                summary.put(key, new Integer(value.intValue() + 1));
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
    public Map getSummary() {
        Map summary = new HashMap();
        for (Iterator i = violations.iterator(); i.hasNext();) {
            IRuleViolation rv = (IRuleViolation) i.next();
            String name = rv.getRule().getName();
            if (!summary.containsKey(name)) {
                summary.put(name, NumericConstants.ZERO);
            }
            Integer count = (Integer) summary.get(name);
            summary.put(name, new Integer(count.intValue() + 1));
        }
        return summary;
    }

    public void addListener(ReportListener listener) {
        listeners.add(listener);
    }

    public List getSuppressedRuleViolations() {
        return suppressedRuleViolations;
    }

    public void addRuleViolation(IRuleViolation violation) {

        // NOPMD excluder
        Integer line = new Integer(violation.getBeginLine());
        if (linesToExclude.keySet().contains(line)) {
            suppressedRuleViolations.add(new SuppressedViolation(violation, true, (String)linesToExclude.get(line)));
            return;
        }

        if (violation.isSuppressed()) {
            suppressedRuleViolations.add(new SuppressedViolation(violation, false, null));
            return;
        }


        violations.add(violation);
        violationTree.addRuleViolation(violation);
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ReportListener listener = (ReportListener) i.next();
            listener.ruleViolationAdded(violation);
        }
    }

    public void addMetric(Metric metric) {
        metrics.add(metric);
        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ReportListener listener = (ReportListener) i.next();
            listener.metricAdded(metric);
        }
    }

    public void addError(ProcessingError error) {
        errors.add(error);
    }

    public void merge(Report r) {
        Iterator i = r.errors();
        while (i.hasNext()) {
            addError((ProcessingError)i.next());
        }
        i = r.metrics();
        while (i.hasNext()) {
            addMetric((Metric)i.next());
        }
        i = r.iterator();
        while (i.hasNext()) {
            addRuleViolation((IRuleViolation)i.next());
        }
    }

    public boolean hasMetrics() {
        return !metrics.isEmpty();
    }

    public Iterator metrics() {
        return metrics.iterator();
    }

    public boolean isEmpty() {
        return !violations.iterator().hasNext() && errors.isEmpty();
    }

    public boolean treeIsEmpty() {
        return !violationTree.iterator().hasNext();
    }

    public Iterator treeIterator() {
        return violationTree.iterator();
    }

    public Iterator iterator() {
        return violations.iterator();
    }

    public Iterator errors() {
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
