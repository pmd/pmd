package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleSetNotFoundException;
import oracle.ide.panels.DefaultTraversablePanel;
import oracle.ide.panels.TraversableContext;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsPanel extends DefaultTraversablePanel {

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

    public void onEntry(TraversableContext tc) {
        super.removeAll();
        try {
            rules = new SelectedRules();
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }

        JPanel boxesPanel = new JPanel();
        boxesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        JList list = new CheckboxList(rules.getAllBoxes());
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        boxesPanel.add(new JScrollPane(list), BorderLayout.NORTH);
        JPanel textPanel = new JPanel();
        textPanel.setBorder(BorderFactory.createTitledBorder("Example"));
        textPanel.add(new JScrollPane(exampleTextArea));
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BorderLayout());
        selectionPanel.add(boxesPanel, BorderLayout.NORTH);
        selectionPanel.add(textPanel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Please see http://pmd.sf.net/ for more information"), BorderLayout.NORTH);
        mainPanel.add(selectionPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    public void onExit(TraversableContext tc) {
        rules.save();
    }
}
