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

public class PMDOptionPane extends AbstractOptionPane implements OptionPane {

    private static class SelectedRuleSetsMap {
        private Map selections = new HashMap();

        public SelectedRuleSetsMap() {
            selections.put("basic", createCheckBox("basic"));
            selections.put("unusedcode", createCheckBox("unusedcode"));
            selections.put("design", createCheckBox("design"));
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

        private JCheckBox createCheckBox(String name) {
            JCheckBox box = new JCheckBox();
            box.setSelected(jEdit.getBooleanProperty(PMDJEditPlugin.OPTION_RULESETS_PREFIX + name, true));
            return box;
        }
    }

    private class SaveAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            selectedRuleSets.save();
        }
    }

    private class CloseAL implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "hi");
        }
    }

   private static final String NAME = "PMD Options";

    private SelectedRuleSetsMap selectedRuleSets = new SelectedRuleSetsMap();

    public PMDOptionPane() {
        super(NAME);
        _init();
    }

    public String getName() {
        return NAME;
    }

    public void _init() {
        super._init();

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setBackground(Color.white);
        checkBoxPanel.setLayout(new GridLayout(selectedRuleSets.size(), 2));
        for (Iterator i = selectedRuleSets.keys(); i.hasNext();) {
            String key = (String)i.next();
            JPanel oneBoxPanel = new JPanel();
            oneBoxPanel.add(new JLabel(key, JLabel.LEFT));
            oneBoxPanel.add((JCheckBox)selectedRuleSets.get(key));
            checkBoxPanel.add(oneBoxPanel);
        }

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new SaveAL());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new CloseAL());
        buttonPanel.add(closeButton);

        JDialog dialog = new JDialog(jEdit.getFirstView(), "PMD", true);
        dialog.setTitle("PMD");
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(checkBoxPanel, BorderLayout.CENTER);
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        dialog.setSize(new Dimension(500,300));
        dialog.pack();
        dialog.setLocationRelativeTo(jEdit.getFirstView());
        dialog.setVisible(true);
    }

}
