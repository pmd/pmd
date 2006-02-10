/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.CanSuppressWarnings;
import net.sourceforge.pmd.ast.SimpleNode;
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
                res.append(hours + "h ");
            }
            if (hours > 0 || minutes > 0) {
                res.append(minutes + "m ");
            }
            res.append(seconds + "s");
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
        private RuleViolation rv;
        private boolean isNOPMD;

        public SuppressedViolation(RuleViolation rv, boolean isNOPMD) {
            this.isNOPMD = isNOPMD;
            this.rv = rv;
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
    private Set linesToExclude = new HashSet();
    private long start;
    private long end;

    private List suppressedRuleViolations = new ArrayList();

    public void exclude(Set lines) {
        linesToExclude = lines;
    }

    public Map getCountSummary() {
        Map summary = new HashMap();
        for (Iterator iter = violationTree.iterator(); iter.hasNext();) {
            RuleViolation rv = (RuleViolation) iter.next();
            String key = (rv.getPackageName() == "" ? "" : (rv.getPackageName() + ".")) + rv.getClassName();
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

    public List getSuppressedRuleViolations() {
        return this.suppressedRuleViolations;
    }

    public void addRuleViolation(RuleViolation violation) {
        // NOPMD excluder
        if (linesToExclude.contains(new Integer(violation.getNode().getBeginLine()))) {
            suppressedRuleViolations.add(new SuppressedViolation(violation, true));
            return;
        }

        // Annotation excluder
        SimpleNode node = violation.getNode();

        // TODO combine this duplicated code
        // TODO same for duplicated code in ASTTypeDeclaration && ASTClassOrInterfaceBodyDeclaration
        List parentTypes = node.getParentsOfType(ASTTypeDeclaration.class);
        if (node instanceof ASTTypeDeclaration) {
            parentTypes.add(node);
        }
        parentTypes.addAll(node.getParentsOfType(ASTClassOrInterfaceBodyDeclaration.class));
        if (node instanceof ASTClassOrInterfaceBodyDeclaration) {
            parentTypes.add(node);
        }
        parentTypes.addAll(node.getParentsOfType(ASTFormalParameter.class));
        if (node instanceof ASTFormalParameter) {
            parentTypes.add(node);
        }
        parentTypes.addAll(node.getParentsOfType(ASTLocalVariableDeclaration.class));
        if (node instanceof ASTLocalVariableDeclaration) {
            parentTypes.add(node);
        }
        for (Iterator i = parentTypes.iterator(); i.hasNext();) {
            CanSuppressWarnings t = (CanSuppressWarnings) i.next();
            if (t.hasSuppressWarningsAnnotationFor(violation.getRule())) {
                suppressedRuleViolations.add(new SuppressedViolation(violation, false));
                return;
            }
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
