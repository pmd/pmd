/*
 * User: tom
 * Date: Jul 2, 2002
 * Time: 12:13:03 PM
 */
package net.sourceforge.pmd.reports;

import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;

public interface Report {
    void addRuleViolation(RuleViolation violation);

    String render();

    boolean isEmpty();

    Iterator iterator();

    int size();
}
