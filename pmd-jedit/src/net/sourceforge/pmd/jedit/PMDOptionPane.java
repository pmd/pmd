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

    private class SaveAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            selectedRuleSets.save();
        }
    }

    private class CloseAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    }

    private SelectedRuleSetsMap selectedRuleSets;
    private JDialog dialog;

    public PMDOptionPane() {
        super(PMDJEditPlugin.NAME);
        _init();
    }

    public String getName() {
        return PMDJEditPlugin.NAME;
    }

    public void _init() {
        super._init();

        if (this.selectedRuleSets == null) {
            try {
                selectedRuleSets = new SelectedRuleSetsMap();
            } catch (RuleSetNotFoundException rsne) {
                rsne.printStackTrace();
            }
        }
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.white);
        textPanel.setLayout(new BorderLayout());
        textPanel.add(new JLabel("Select the rulesets you want to use and click 'Save'."), BorderLayout.NORTH);
        textPanel.add(new JLabel("Please see http://pmd.sourceforge.net/ for more information on what's in each rule set."), BorderLayout.SOUTH);

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setBackground(Color.white);
        checkBoxPanel.setBackground(Color.white);
        checkBoxPanel.setLayout(new GridLayout(selectedRuleSets.size(), 2));
        for (Iterator i = selectedRuleSets.keys(); i.hasNext();) {
            RuleSet rs = (RuleSet)i.next();
            JPanel oneBoxPanel = new JPanel();
            oneBoxPanel.setBackground(Color.white);
            oneBoxPanel.add(new JLabel(rs.getName(), JLabel.LEFT));
            oneBoxPanel.add((JCheckBox)selectedRuleSets.get(rs));
            checkBoxPanel.add(oneBoxPanel);
        }

        JButton saveButton = new JButton("Save");
        saveButton.setMnemonic('s');
        saveButton.addActionListener(new SaveAL());

        JButton closeButton = new JButton("Close");
        closeButton.setMnemonic('c');
        closeButton.addActionListener(new CloseAL());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        dialog = new JDialog(jEdit.getFirstView(), PMDJEditPlugin.NAME, true);
        dialog.setTitle(PMDJEditPlugin.NAME);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(textPanel, BorderLayout.NORTH);
        dialog.getContentPane().add(checkBoxPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(new Dimension(500,300));
        dialog.pack();
        dialog.setLocationRelativeTo(jEdit.getFirstView());
        dialog.setVisible(true);
    }

}
