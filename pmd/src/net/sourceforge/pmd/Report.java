/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:10:00 PM
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.renderers.Renderer;

import java.util.*;

public class Report {

    private Set violations = new TreeSet(new RuleViolation.RuleViolationComparator());

    public void addRuleViolation(RuleViolation violation) {
        violations.add(violation);
    }

    public boolean isEmpty() {
        return violations.isEmpty();
    }

    public Iterator iterator() {
        return violations.iterator();
    }

    public int size() {
        return violations.size();
    }

}
