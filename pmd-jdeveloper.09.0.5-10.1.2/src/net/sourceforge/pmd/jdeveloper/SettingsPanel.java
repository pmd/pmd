package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.RuleSetNotFoundException;

import oracle.ide.Ide;
import oracle.ide.panels.DefaultTraversablePanel;
import oracle.ide.panels.TraversableContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.MouseMotionListener;

import java.io.File;

import java.util.List;

public class SettingsPanel extends DefaultTraversablePanel {

    private class FindListener implements ActionListener {
        public void actionPerformed(final ActionEvent evt) {
            final FileDialog fdlg = 
                new FileDialog(new Frame(), "Find", FileDialog.LOAD);
            fdlg.setVisible(true);
            final String selected = fdlg.getDirectory() + fdlg.getFile();
            if (fdlg.getFile() == null) {
                return;
            }
            sepFileName.setText(selected);
        }
    }

    private class CheckboxList extends JList {

        private class MyMouseAdapter extends MouseAdapter {
            public void mouseEntered(final MouseEvent evt) {
                // No action needed when mouse is entered
            }

            public void mousePressed(final MouseEvent evt) {
                final int index = locationToIndex(evt.getPoint());
                if (index != -1) {
                    final JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    box.setSelected(!box.isSelected());
                    repaint();
                }
            }
        }

        private class MyMouseMotionListener implements MouseMotionListener {

            public void mouseDragged(final MouseEvent evt) {
                // No dragging actions needed
            }

            public void mouseMoved(final MouseEvent evt) {
                final int index = locationToIndex(evt.getPoint());
                if (index != -1) {
                    final JCheckBox box = (JCheckBox)getModel().getElementAt(index);
                    final List examples = rules.getRule(box).getExamples();
                    final StringBuffer examplesBuffer = new StringBuffer();
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
            public Component getListCellRendererComponent(final JList list, 
                                                          final Object value, 
                                                          final int index, 
                                                          final boolean isSelected, 
                                                          final boolean cellHasFocus) {
                final JCheckBox box = (JCheckBox)value;
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

        public CheckboxList(final Object[] args) {
            super(args);
            setCellRenderer(new CheckboxListCellRenderer());
            addMouseListener(new MyMouseAdapter());
            addMouseMotionListener(new MyMouseMotionListener());
        }
    }

    public static final String STORED_SEPARATELY = 
        "pmd.settings.separate";
    public static final String SEL_FILENAME = 
        "pmd.settings.separate.name";

    private final transient JTextArea exampleTextArea = new JTextArea(10, 50);
    private final transient JCheckBox storedSepBox = 
        new JCheckBox("", Boolean.valueOf(Ide.getProperty(STORED_SEPARATELY)).booleanValue());
    private final transient JTextField sepFileName = new JTextField(30);
    private transient SelectedRules rules;

    public static SettingsStorage createSettingsStorage() {
        if (Boolean.valueOf(Ide.getProperty(STORED_SEPARATELY)).booleanValue()) {
            return new FileStorage(new File(Ide.getProperty(SEL_FILENAME)));
        }
        return new IDEStorage();
    }

    public void onEntry(final TraversableContext tcon) {
        removeAll();
        try {
            rules = new SelectedRules(createSettingsStorage());
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }

        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createRulesSelectionPanel(), BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createRulesSelectionPanel() {
        final JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        final JList rulesList = new CheckboxList(rules.getAllBoxes());
        rulesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        checkBoxesPanel.add(new JScrollPane(rulesList), BorderLayout.NORTH);
        final JPanel examplePanel = new JPanel();
        examplePanel.setBorder(BorderFactory.createTitledBorder("Example"));
        examplePanel.add(new JScrollPane(exampleTextArea));
        final JPanel rulesSelPanel = new JPanel();
        rulesSelPanel.setLayout(new BorderLayout());
        rulesSelPanel.add(checkBoxesPanel, BorderLayout.NORTH);
        rulesSelPanel.add(examplePanel, BorderLayout.CENTER);
        return rulesSelPanel;
    }

    private JPanel createTopPanel() {
        sepFileName.setText(Ide.getProperty(SEL_FILENAME));
        storedSepBox.setSelected(Boolean.valueOf(Ide.getProperty(STORED_SEPARATELY)).booleanValue());

        final JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("                        InfoEther(tm) PMD JDeveloper plugin"), 
                     BorderLayout.NORTH);
        final JPanel custStorPanel = new JPanel(new BorderLayout());
        custStorPanel.setBorder(BorderFactory.createTitledBorder("Settings storage"));

        final JPanel custStorCbPanel = new JPanel();
        custStorCbPanel.add(new JLabel("Use centrally managed rule settings?"));
        custStorCbPanel.add(storedSepBox);
        custStorPanel.add(custStorCbPanel, BorderLayout.NORTH);

        final JPanel custStorTfPanel = new JPanel();
        custStorTfPanel.add(new JLabel("File:"));
        custStorTfPanel.add(sepFileName);
        final JButton findButton = new JButton("Find file");
        findButton.addActionListener(new FindListener());
        custStorTfPanel.add(findButton);

        custStorPanel.add(custStorTfPanel, 
                               BorderLayout.SOUTH);
        topPanel.add(custStorPanel, BorderLayout.CENTER);
        return topPanel;
    }

    public void onExit(final TraversableContext tcon) {
        Ide.setProperty(STORED_SEPARATELY, 
                        String.valueOf(storedSepBox.isSelected()));
        Ide.setProperty(SEL_FILENAME, 
                        sepFileName.getText());
        try {
            rules.save(createSettingsStorage());
        } catch (SettingsException se) {
            JOptionPane.showMessageDialog(null, 
                                          "Can't save selected rules to the file " + 
                                          sepFileName.getText() + 
                                          ":" + se.getMessage(), 
                                          "Can't save settings", 
                                          JOptionPane.ERROR_MESSAGE);
        }
    }
}
