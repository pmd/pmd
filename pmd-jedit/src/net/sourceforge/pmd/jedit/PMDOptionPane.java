/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 4:29:19 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;
import org.gjt.sp.jedit.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSet;

public class PMDOptionPane extends AbstractOptionPane implements OptionPane {

    private SelectedRuleSetsMap selectedRuleSets;

    public PMDOptionPane() {
        super(PMDJEditPlugin.NAME);
        try {
            selectedRuleSets = new SelectedRuleSetsMap();
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }
    }

    public void init() {
        removeAll();
        addComponent(new JLabel("Please see http://pmd.sourceforge.net/ for more information on what's in each rule set."));
        for (Iterator i = selectedRuleSets.keys(); i.hasNext();) {
            RuleSet rs = (RuleSet)i.next();
            JPanel oneBoxPanel = new JPanel();
            oneBoxPanel.add((JCheckBox)selectedRuleSets.get(rs));
            oneBoxPanel.add(new JLabel(rs.getDescription()));
            addComponent(oneBoxPanel);
        }
    }

    public void save() {
        selectedRuleSets.save();
    }
}
