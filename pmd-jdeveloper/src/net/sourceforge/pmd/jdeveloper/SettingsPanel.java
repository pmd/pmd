package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleSetNotFoundException;
import oracle.ide.Ide;
import oracle.ide.panels.DefaultTraversablePanel;
import oracle.ide.panels.TraversableContext;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class SettingsPanel extends DefaultTraversablePanel {


    private class FindListener implements ActionListener {
        public void actionPerformed(ActionEvent evt){
            FileDialog fdlg = new FileDialog(new Frame(), "Find", FileDialog.LOAD);
            fdlg.setVisible(true);
            String selected = fdlg.getDirectory() + fdlg.getFile();
            if (fdlg.getFile() == null) {
                return;
            }
            selectedRulesSeparateFileNameField.setText(selected);
        }
    }

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

    public static final String RULE_SELECTIONS_STORED_SEPARATELY = "pmd.settings.separate";
    public static final String RULE_SELECTIONS_FILENAME = "pmd.settings.separate.name";

    private JTextArea exampleTextArea= new JTextArea(10, 50);
    private JCheckBox selectedRulesStoredSeparatelyBox = new JCheckBox("", Boolean.valueOf(Ide.getProperty(RULE_SELECTIONS_STORED_SEPARATELY)).booleanValue());
    private JTextField selectedRulesSeparateFileNameField = new JTextField(30);
    private SelectedRules rules;

    public static SettingsStorage createSettingsStorage() {
        if (Boolean.valueOf(Ide.getProperty(RULE_SELECTIONS_STORED_SEPARATELY)).booleanValue()) {
            return new FileStorage(new File(Ide.getProperty(RULE_SELECTIONS_FILENAME)));
        }
        return new IDEStorage();
    }

    public void onEntry(TraversableContext tc) {
        removeAll();
        try {
            rules = new SelectedRules(createSettingsStorage());
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createRulesSelectionPanel(), BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createRulesSelectionPanel() {
        JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        JList rulesList = new CheckboxList(rules.getAllBoxes());
        rulesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        checkBoxesPanel.add(new JScrollPane(rulesList), BorderLayout.NORTH);
        JPanel examplePanel = new JPanel();
        examplePanel.setBorder(BorderFactory.createTitledBorder("Example"));
        examplePanel.add(new JScrollPane(exampleTextArea));
        JPanel rulesSelectionPanel = new JPanel();
        rulesSelectionPanel.setLayout(new BorderLayout());
        rulesSelectionPanel.add(checkBoxesPanel, BorderLayout.NORTH);
        rulesSelectionPanel.add(examplePanel, BorderLayout.CENTER);
        return rulesSelectionPanel;
    }

    private JPanel createTopPanel() {
        selectedRulesSeparateFileNameField.setText(Ide.getProperty(RULE_SELECTIONS_FILENAME));
        selectedRulesStoredSeparatelyBox.setSelected(Boolean.valueOf(Ide.getProperty(RULE_SELECTIONS_STORED_SEPARATELY)).booleanValue());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Please see http://pmd.sf.net/ for more information"), BorderLayout.NORTH);
        JPanel customStoragePanel = new JPanel(new BorderLayout());
        customStoragePanel.setBorder(BorderFactory.createTitledBorder("Settings storage"));

        JPanel customStorageCheckBoxPanel = new JPanel();
        customStorageCheckBoxPanel.add(new JLabel("Use centrally managed rule settings?"));
        customStorageCheckBoxPanel.add(selectedRulesStoredSeparatelyBox);
        customStoragePanel.add(customStorageCheckBoxPanel, BorderLayout.NORTH);

        JPanel customStorageTextFieldPanel = new JPanel();
        customStorageTextFieldPanel.add(new JLabel("File:"));
        customStorageTextFieldPanel.add(selectedRulesSeparateFileNameField);
        JButton findButton = new JButton("Find file");
        findButton.addActionListener(new FindListener());
        customStorageTextFieldPanel.add(findButton);

        customStoragePanel.add(customStorageTextFieldPanel, BorderLayout.SOUTH);
        topPanel.add(customStoragePanel, BorderLayout.CENTER);
        return topPanel;
    }

    public void onExit(TraversableContext tc) {
        Ide.setProperty(RULE_SELECTIONS_STORED_SEPARATELY, String.valueOf(selectedRulesStoredSeparatelyBox.isSelected()));
        Ide.setProperty(RULE_SELECTIONS_FILENAME, selectedRulesSeparateFileNameField.getText());
        try {
            rules.save(createSettingsStorage());
        } catch (SettingsException se) {
            JOptionPane.showMessageDialog(null, "Can't save selected rules to the file " + selectedRulesSeparateFileNameField.getText() + ":" + se.getMessage(), "Can't save settings", JOptionPane.ERROR_MESSAGE);
        }
    }
}
