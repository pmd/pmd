/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:10:00 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.renderers.Renderer;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class Report {

    private List violations = new ArrayList();

    public void addRuleViolation(RuleViolation violation) {
        violations.add(violation);
    }

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
