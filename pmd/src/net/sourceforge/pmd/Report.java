/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
