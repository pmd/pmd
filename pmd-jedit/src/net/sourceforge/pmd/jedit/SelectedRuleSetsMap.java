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

    private Map ruleSets = new HashMap();

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

    public Iterator getSelectedRuleSets() {
        List selected = new ArrayList();
        for (Iterator i = keys(); i.hasNext();) {
            RuleSet rs = (RuleSet)i.next();
            if (get(rs).isSelected()) {
                selected.add(rs);
            }
        }
        return selected.iterator();
    }

    private JCheckBox createCheckBox(String name) {
        JCheckBox box = new JCheckBox();
        box.setBackground(Color.white);
        box.setSelected(jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_RULESETS_PREFIX + name, true));
        return box;
    }
}
