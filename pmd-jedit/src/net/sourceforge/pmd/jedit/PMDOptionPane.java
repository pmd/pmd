/*
 * User: tom
 * Date: Jul 8, 2002
 * Time: 4:29:19 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.OptionPane;
import org.gjt.sp.jedit.AbstractOptionPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import net.sourceforge.pmd.RuleSetNotFoundException;

public class PMDOptionPane extends AbstractOptionPane implements OptionPane {

    public class CheckboxList extends JList {

        private class MyMouseAdapter extends MouseAdapter {
            public void mouseEntered(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    String example = rules.getRule(box).getExample();
                    exampleTextArea.setText(example);
                    exampleTextArea.setCaretPosition(0);
                }
            }

            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    box.setSelected(!box.isSelected());
                    repaint();
                }
            }
        }

        public class CheckboxListCellRenderer implements ListCellRenderer {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JCheckBox box = (JCheckBox)value;
                box.setEnabled(isEnabled());
                box.setFont(getFont());
                box.setFocusPainted(false);
                box.setBorderPainted(true);
                box.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : new EmptyBorder(1,1,1,1));
                return box;
            }
        }

        public CheckboxList(Object[] args) {
            super(args);
            setCellRenderer(new CheckboxListCellRenderer());
            addMouseListener(new MyMouseAdapter());
        }

    }

    private SelectedRules rules;
    private JTextArea exampleTextArea= new JTextArea(10, 50);
    public PMDOptionPane() {
        super(PMDJEditPlugin.NAME);
        try {
            rules = new SelectedRules();
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }
    }

    public void init() {
        removeAll();

        addComponent(new JLabel("Please see http://pmd.sf.net/ for more information"));

        JPanel rulesPanel = new JPanel();
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        JList list = new CheckboxList(rules.getAllBoxes());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rulesPanel.add(new JScrollPane(list), BorderLayout.NORTH);

        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createTitledBorder("Example"));
        textPanel.add(new JScrollPane(exampleTextArea));

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(rulesPanel, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.SOUTH);
        addComponent(panel);
    }

    public void save() {
        rules.save();
    }
}
