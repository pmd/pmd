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

public class SelectedRuleSetsMap {

    private Map ruleSets = new TreeMap(new Comparator() {
        public int compare(Object o1, Object o2) {
            RuleSet r1 = (RuleSet)o1;
            RuleSet r2 = (RuleSet)o2;
            return r1.getName().compareTo(r2.getName());
        }
    });

    public SelectedRuleSetsMap() throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory();
        for (Iterator i = rsf.getRegisteredRuleSets(); i.hasNext();) {
            RuleSet rs = (RuleSet)i.next();
            ruleSets.put(rs, createCheckBox(rs.getName()));
        }
    }

    public Iterator keys() {
        return ruleSets.keySet().iterator();
    }

    public int size() {
        return ruleSets.size();
    }

    public JCheckBox get(Object key) {
        return (JCheckBox)ruleSets.get(key);
    }

    public void save() {
        for (Iterator i = keys(); i.hasNext();) {
            RuleSet rs = (RuleSet)i.next();
            jEdit.setBooleanProperty(PMDJEditPlugin.OPTION_RULESETS_PREFIX + rs.getName(), get(rs).isSelected());
        }
    }

    public RuleSet getSelectedRuleSets() {
        RuleSet newRuleSet = new RuleSet();
        for (Iterator i = keys(); i.hasNext();) {
            RuleSet rs = (RuleSet)i.next();
            if (get(rs).isSelected()) {
                newRuleSet.addRuleSet(rs);
            }
        }
        return newRuleSet;
    }

    private JCheckBox createCheckBox(String name) {
        JCheckBox box = new JCheckBox(name);
        box.setSelected(jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_RULESETS_PREFIX + name, true));
        return box;
    }
}
