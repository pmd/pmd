/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.dfa.report.ReportTree;
import net.sourceforge.pmd.stat.Metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Report {

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


    /*
     * The idea is to store the violations in a tree instead of a list, to do
     * better and faster sort and filter mechanism and to visualize the result
     * als tree. (ide plugins).
     * */
    private ReportTree violationTree = new ReportTree();

    // Note that this and the above data structure are both being maintained for a bit
    private Set violations = new TreeSet(new RuleViolation.RuleViolationComparator());
    private Set metrics = new HashSet();
    private List listeners = new ArrayList();
    private List errors = new ArrayList();


    public Map getCountSummary() {
        Map summary = new HashMap();
        for (Iterator iter = violationTree.iterator(); iter.hasNext();) {
            RuleViolation rv = (RuleViolation) iter.next();
            String key = rv.getPackageName() + "." + rv.getClassName();
            Object o = summary.get(key);
            if (o == null) {
                Integer value = new Integer(1);
                summary.put(key, value);
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
            RuleViolation rv = (RuleViolation) i.next();
            if (!summary.containsKey(rv.getRule().getName())) {
                summary.put(rv.getRule().getName(), new Integer(0));
            }
            Integer count = (Integer) summary.get(rv.getRule().getName());
            count = new Integer(count.intValue() + 1);
            summary.put(rv.getRule().getName(), count);
        }
        return summary;
    }

    public void addListener(ReportListener listener) {
        listeners.add(listener);
    }

    public void addRuleViolation(RuleViolation violation) {
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

    public boolean hasMetrics() {
        return !metrics.isEmpty();
    }

    public Iterator metrics() {
        return metrics.iterator();
    }

    public boolean isEmpty() {
        return !violations.iterator().hasNext();
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

}
