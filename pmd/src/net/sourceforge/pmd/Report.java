/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

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

    private Set violations = new TreeSet(new RuleViolation.RuleViolationComparator());
    private Set metrics = new HashSet();
    private List listeners = new ArrayList();
    private List errors = new ArrayList();

    /**
     *
     * @return a Map summarizing the Report: String (rule name) ->Integer (count of violations)
     */
    public Map getSummary() {
        Map summary = new HashMap();
        for (Iterator i = violations.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation)i.next();
            if (!summary.containsKey(rv.getRule().getName())) {
                summary.put(rv.getRule().getName(), new Integer(0));
            }
            Integer count = (Integer)summary.get(rv.getRule().getName());
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
        return violations.isEmpty();
    }

    public Iterator iterator() {
        return violations.iterator();
    }

    public Iterator errors() {
        return errors.iterator();
    }

    public int size() {
        return violations.size();
    }

}
