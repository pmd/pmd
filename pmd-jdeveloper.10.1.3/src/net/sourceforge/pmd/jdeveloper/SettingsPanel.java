package net.sourceforge.pmd.jdeveloper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;

import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;

import oracle.ide.panels.DefaultTraversablePanel;
import oracle.ide.panels.TraversableContext;


public class SettingsPanel extends DefaultTraversablePanel {

    private class ImportListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            FileDialog fdlg = 
                new FileDialog(new Frame(), "Import", FileDialog.LOAD);
            fdlg.setVisible(true);
            if (fdlg.getFile() == null) {
                return;
            }
            String selected = fdlg.getDirectory() + fdlg.getFile();
            importFile(selected);
        }
    }

    private class ExportListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            FileDialog fdlg = 
                new FileDialog(new Frame(), "Export", FileDialog.SAVE);
            fdlg.setVisible(true);
            if (fdlg.getFile() == null) {
                return;
            }
            String selected = fdlg.getDirectory() + fdlg.getFile();
            exportFile(selected);
        }
    }

    private class CheckboxList extends JList {

        private class MyMouseAdapter extends MouseAdapter {
            public void mouseEntered(MouseEvent e) {
                // No action needed when mouse is entered
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

        private class MyMouseMotionListener implements MouseMotionListener {

            public void mouseDragged(MouseEvent e) {
                // No dragging actions needed
            }

            public void mouseMoved(MouseEvent e) {
                int index = locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    List examples = rules.getRule(box).getExamples();
                    StringBuffer examplesBuffer = new StringBuffer();
                    if (!examples.isEmpty()) {
                        for (int i = 0; i < examples.size(); i++) {
                            examplesBuffer.append(examples.get(i));
                        }
                    }
                    String example = examplesBuffer.toString();

                    while (example.charAt(0) == '\r' || 
                           example.charAt(0) == '\n' || 
                           example.charAt(0) == '\t' || 
                           example.charAt(0) == ' ') {
                        example = example.substring(1);
                    }
                    exampleTextArea.setText(example);
                    exampleTextArea.setCaretPosition(0);
                }
            }
        }

        private class CheckboxListCellRenderer implements ListCellRenderer {
            public Component getListCellRendererComponent(JList list, 
                                                          Object value, 
                                                          int index, 
                                                          boolean isSelected, 
                                                          boolean cellHasFocus) {
                JCheckBox box = (JCheckBox)value;
                box.setEnabled(isEnabled());
                box.setFont(getFont());
                box.setFocusPainted(false);
                box.setBorderPainted(true);
                box.setBorder(isSelected ? 
                              UIManager.getBorder("List.focusCellHighlightBorder") : 
                              new EmptyBorder(1, 1, 1, 1));
                return box;
            }
        }

        public CheckboxList(Object[] args) {
            super(args);
            setCellRenderer(new CheckboxListCellRenderer());
            addMouseListener(new MyMouseAdapter());
            addMouseMotionListener(new MyMouseMotionListener());
        }
    }

    public static final String RULE_SELECTIONS_STORED_SEPARATELY = 
        "pmd.settings.separate";
    public static final String RULE_SELECTIONS_FILENAME = 
        "pmd.settings.separate.name";

    private JTextArea exampleTextArea = new JTextArea(10, 50);
    private SelectedRules rules;
    private JList rulesList;

    public static SettingsStorage createSettingsStorage() {
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
        mainPanel.add(createRulesSelectionPanel(), BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createRulesSelectionPanel() {
        JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        rulesList = new CheckboxList(rules.getAllBoxes());
        rulesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        checkBoxesPanel.add(new JScrollPane(rulesList), BorderLayout.NORTH);
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        JButton selectAll = new JButton("Select all");
        selectAll.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setSelected(true);
                    }
                });
        buttonsPanel.add(selectAll);
        JButton selectNone = new JButton("Deselect all");
        selectNone.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setSelected(false);
                    }
                });
        buttonsPanel.add(selectNone);
        JButton importButton = new JButton("Import rules file");
        importButton.addActionListener(new ImportListener());
        buttonsPanel.add(importButton, BorderLayout.NORTH);
        JButton exportButton = new JButton("Export rules file");
        exportButton.addActionListener(new ExportListener());
        buttonsPanel.add(exportButton, BorderLayout.SOUTH);
        checkBoxesPanel.add(buttonsPanel, BorderLayout.EAST);
        JPanel examplePanel = new JPanel();
        examplePanel.setBorder(BorderFactory.createTitledBorder("Example"));
        examplePanel.add(new JScrollPane(exampleTextArea));
        JPanel rulesSelectionPanel = new JPanel();
        rulesSelectionPanel.setLayout(new BorderLayout());
        rulesSelectionPanel.add(checkBoxesPanel, BorderLayout.NORTH);
        rulesSelectionPanel.add(examplePanel, BorderLayout.CENTER);
        return rulesSelectionPanel;
    }

    private void setSelected(Boolean selected) {
        ListModel model = rulesList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            JCheckBox box = (JCheckBox)model.getElementAt(i);
            box.setSelected(selected);
        }
        rulesList.repaint();
    }

    public void onExit(TraversableContext tc) {
        try {
            rules.save(createSettingsStorage());
        } catch (SettingsException se) {
            JOptionPane.showMessageDialog(null, 
                                          "Can't save selected rules to the file :" + 
                                          se.getMessage(), 
                                          "Can't save settings", 
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importFile(String fileLocation) {
        RuleSetFactory factory = new RuleSetFactory();
        RuleSets ruleSets = null;
        try {
            ruleSets = factory.createRuleSets(fileLocation);
        } catch (RuleSetNotFoundException e) {
            System.err.println("Error during reading ruleset : " + 
                               e.getMessage());
        }
        if (ruleSets == null) {
            System.out.println("No rules to import");
        } else {
            ListModel model = rulesList.getModel();
            Set<Rule> allRules = ruleSets.getAllRules();
            for (int i = 0; i < model.getSize(); i++) {
                JCheckBox box = (JCheckBox)model.getElementAt(i);
                Rule rule = rules.getRule(box);
                box.setSelected(isRuleAvailabel(allRules, rule));
            }
        }
        rulesList.repaint();
    }

    private Boolean isRuleAvailabel(Set<Rule> allRules, Rule requestedRule) {
        Boolean returnValue = Boolean.FALSE;
        for (Rule rule: allRules) {
            if (rule.getName().equals(requestedRule.getName())) {
                returnValue = Boolean.TRUE;
            }
        }

        return returnValue;
    }

    private void exportFile(String fileLocation) {
        RuleSet selectedRules = rules.getSelectedRules();
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            RuleSetWriterImpl ruleSetWriter = new RuleSetWriterImpl();
            ruleSetWriter.write(outputStream, selectedRules);
            outputStream.flush();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null, 
                                          "Can't save selected rules to the file :" + 
                                          e.getMessage(), 
                                          "Can't save settings", 
                                          JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            System.err.println("Error during file transfer : " + 
                               e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    System.err.println("Error during file transfer closing : " + 
                                       e.getMessage());
                }
            }
        }
    }
}
