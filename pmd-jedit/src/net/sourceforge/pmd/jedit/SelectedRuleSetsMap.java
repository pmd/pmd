/*
 * User: tom
 * Date: Jul 9, 2002
 * Time: 1:18:38 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.jEdit;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;

public class SelectedRuleSetsMap {
    private Map selections = new HashMap();

    public SelectedRuleSetsMap() {
        selections.put("basic", createCheckBox("basic"));
        selections.put("unusedcode", createCheckBox("unusedcode"));
        selections.put("design", createCheckBox("design"));
        selections.put("naming", createCheckBox("naming"));
    }

    public Iterator keys() {
        return selections.keySet().iterator();
    }

    public int size() {
        return selections.size();
    }

    public JCheckBox get(Object key) {
        return (JCheckBox)selections.get(key);
    }

    public void save() {
        for (Iterator i = keys(); i.hasNext();) {
            String key = (String)i.next();
            jEdit.setBooleanProperty(PMDJEditPlugin.OPTION_RULESETS_PREFIX + key, get(key).isSelected());
        }
    }

    public Iterator getSelectedRuleSetFileNames() {
        List selected = new ArrayList();
        for (Iterator i = keys(); i.hasNext();) {
            String key = (String)i.next();
            if (get(key).isSelected()) {
                selected.add("rulesets/" + key + ".xml");
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
