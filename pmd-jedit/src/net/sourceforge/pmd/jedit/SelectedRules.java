/*
 * User: tom
 * Date: Jul 9, 2002
 * Time: 1:18:38 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.util.*;
import java.awt.Color;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.Rule;

public class SelectedRules {

    // Rule -> JCheckBox
    private Map rules = new TreeMap(new Comparator() {
        public int compare(Object o1, Object o2) {
            Rule r1 = (Rule)o1;
            Rule r2 = (Rule)o2;
            return r1.getName().compareTo(r2.getName());
        }
    });

    public SelectedRules() throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory();
        for (Iterator i = rsf.getRegisteredRuleSets(); i.hasNext();) {
            RuleSet rs = (RuleSet)i.next();
            for (Iterator j = rs.getRules().iterator(); j.hasNext();) {
                Rule rule = (Rule)j.next();
                rules.put(rule,  createCheckBox(rule.getName()));
            }
        }
    }

    public int size() {
        return rules.size();
    }

    public Rule getRule(JCheckBox candidate) {
        for (Iterator i = rules.keySet().iterator(); i.hasNext();) {
            Rule rule = (Rule)i.next();
            JCheckBox box = (JCheckBox)rules.get(rule);
            if (box.equals(candidate)) {
                return rule;
            }
        }
        throw new RuntimeException("Couldn't find a rule that mapped to the passed in JCheckBox " + candidate);
    }

    public JCheckBox get(Object key) {
        return (JCheckBox)rules.get(key);
    }

    public Object[] getAllBoxes() {
        Object[] foo = new Object[rules.size()];
        int idx = 0;
        for (Iterator i = rules.values().iterator(); i.hasNext();) {
            foo[idx] = i.next();
            idx++;
        }
        return foo;
    }

    public void save() {
        for (Iterator i = rules.keySet().iterator(); i.hasNext();) {
            Rule rule = (Rule)i.next();
            jEdit.setBooleanProperty(PMDJEditPlugin.OPTION_RULES_PREFIX + rule.getName(), get(rule).isSelected());
        }
    }

    public RuleSet getSelectedRules() {
        RuleSet newRuleSet = new RuleSet();
        for (Iterator i = rules.keySet().iterator(); i.hasNext();) {
            Rule rule = (Rule)i.next();
            if (get(rule).isSelected()) {
                newRuleSet.addRule(rule);
            }
        }
        return newRuleSet;
    }

    private JCheckBox createCheckBox(String name) {
        JCheckBox box = new JCheckBox(name);
        box.setSelected(jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_RULES_PREFIX + name, true));
        return box;
    }
}
