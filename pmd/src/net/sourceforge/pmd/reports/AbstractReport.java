/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:10:00 PM
 */
package net.sourceforge.pmd.reports;

import net.sourceforge.pmd.RuleViolation;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public abstract class AbstractReport implements Report {

    private List violations = new ArrayList();

    public void addRuleViolation(RuleViolation violation) {
        violations.add(violation);
    }

    public abstract String render();

    public boolean isEmpty() {
        return violations.isEmpty();
    }

    public Iterator iterator() {
        sort();
        return violations.iterator();
    }


    public int size() {
        return violations.size();
    }

    private void sort() {
        Collections.sort(violations, new RuleViolation.RuleViolationComparator());
    }
}
