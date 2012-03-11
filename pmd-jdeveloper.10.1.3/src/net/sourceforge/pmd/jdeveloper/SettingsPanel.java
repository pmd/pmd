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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleSetWriter;

import oracle.ide.Ide;
import oracle.ide.panels.DefaultTraversablePanel;
import oracle.ide.panels.TraversableContext;


public class SettingsPanel extends DefaultTraversablePanel {

    private static final String PLUGIN_HOME =
        "/extensions/net.sourceforge.pmd.jdeveloper/conf/";
    private static final String PLUGIN_PROPS =
        PLUGIN_HOME + "pmd.plugin.properties";
    private static final String RULE_PROPS =
        PLUGIN_HOME + "pmd.rule.properties";

    private class ImportListener implements ActionListener {
        public void actionPerformed(final ActionEvent evt) {
            final FileDialog fdlg =
                new FileDialog(new Frame(), "Import", FileDialog.LOAD);
            fdlg.setVisible(true);
            if (fdlg.getFile() == null) {
                return;
            }
            final String selected = fdlg.getDirectory() + fdlg.getFile();
            importFile(selected);
        }
    }

    private class ExportListener implements ActionListener {
        public void actionPerformed(final ActionEvent evt) {
            final FileDialog fdlg =
                new FileDialog(new Frame(), "Export", FileDialog.SAVE);
            fdlg.setVisible(true);
            if (fdlg.getFile() == null) {
                return;
            }
            final String selected = fdlg.getDirectory() + fdlg.getFile();
            exportFile(selected);
        }
    }

    private class FindListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            FileDialog fdlg =
                new FileDialog(new Frame(), "Find", FileDialog.LOAD);
            fdlg.setVisible(true);
            String selected = fdlg.getDirectory() + fdlg.getFile();
            if (fdlg.getFile() == null) {
                return;
            }
            selectedRulesSeparateFileNameField.setText(selected);
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
                    final JCheckBox box =
                        (JCheckBox)getModel().getElementAt(index);
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
                    final JCheckBox box =
                        (JCheckBox)getModel().getElementAt(index);
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

    public static final String STORED_SEPARATELY = "pmd.settings.separate";
    public static final String SEL_FILENAME = "pmd.settings.separate.name";

    private final transient JTextArea exampleTextArea = new JTextArea(10, 50);
    private JCheckBox selectedRulesStoredSeparatelyBox;
    private JTextField selectedRulesSeparateFileNameField = new JTextField(30);
    private transient SelectedRules rules;
    private transient JList rulesList;
    private static FileStorage pluginProps =
        new FileStorage(new File(Version.getJdevHome() + PLUGIN_PROPS));

    public static SettingsStorage createSettingsStorage() {
        try {
            if (Boolean.valueOf(pluginProps.load(STORED_SEPARATELY)).booleanValue()) {
                return new FileStorage(new File(pluginProps.load(SEL_FILENAME)));
            }
        } catch (SettingsException se) {
            Util.logMessage(se.getStackTrace());
            Util.showError(se, Plugin.PMD_TITLE);
        }
        return new FileStorage(new File(Version.getJdevHome() + RULE_PROPS));
    }


    public void onEntry(final TraversableContext tcon) {
        removeAll();
        try {
            rules = new SelectedRules(createSettingsStorage());
        } catch (RuleSetNotFoundException rsne) {
            Util.logMessage(rsne.getStackTrace());
            Util.showError(rsne, Plugin.PMD_TITLE);
        }

        try {
            selectedRulesStoredSeparatelyBox =
                    new JCheckBox("", Boolean.valueOf(pluginProps.load(STORED_SEPARATELY)).booleanValue());
        } catch (SettingsException se) {
            Util.logMessage(se.getStackTrace());
            Util.showError(se, Plugin.PMD_TITLE);
        }
        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        mainPanel.add(createRulesSelectionPanel(), BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createRulesSelectionPanel() {
        final JPanel checkBoxesPanel = new JPanel();
        checkBoxesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        rulesList = new CheckboxList(rules.getAllBoxes());
        rulesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        rulesList.setVisibleRowCount(5);
        checkBoxesPanel.add(new JScrollPane(rulesList), BorderLayout.NORTH);
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        final JButton selectAll = new JButton("Select all");
        selectAll.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    setSelected(true);
                }
            });
        buttonsPanel.add(selectAll);
        final JButton selectNone = new JButton("Deselect all");
        selectNone.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent evt) {
                    setSelected(false);
                }
            });
        buttonsPanel.add(selectNone);
        final JButton importButton = new JButton("Import rules file");
        importButton.addActionListener(new ImportListener());
        buttonsPanel.add(importButton, BorderLayout.NORTH);
        final JButton exportButton = new JButton("Export rules file");
        exportButton.addActionListener(new ExportListener());
        buttonsPanel.add(exportButton, BorderLayout.SOUTH);
        checkBoxesPanel.add(buttonsPanel, BorderLayout.EAST);
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

        try {
            selectedRulesSeparateFileNameField.setText(pluginProps.load(SEL_FILENAME));
            selectedRulesStoredSeparatelyBox.setSelected(Boolean.valueOf(pluginProps.load(STORED_SEPARATELY)).booleanValue());
        } catch (SettingsException se) {
            Util.logMessage(se.getStackTrace());
            Util.showError(se, Plugin.PMD_TITLE);
        }
        JPanel topPanel = new JPanel(new BorderLayout());
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

        customStoragePanel.add(customStorageTextFieldPanel,
                               BorderLayout.SOUTH);
        topPanel.add(customStoragePanel, BorderLayout.CENTER);
        return topPanel;
    }

    public void onExit(final TraversableContext tcon) {
        final Properties properties = new Properties();
        properties.setProperty(STORED_SEPARATELY,
                               String.valueOf(selectedRulesStoredSeparatelyBox.isSelected()));
        properties.setProperty(SEL_FILENAME,
                               selectedRulesSeparateFileNameField.getText());
        try {
            pluginProps.save(properties);
            rules.save(createSettingsStorage());
        } catch (SettingsException se) {
            JOptionPane.showMessageDialog(null,
                                          "Can't save settings :" + se.getMessage(),
                                          "Can't save settings",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setSelected(final Boolean selected) {
        final ListModel model = rulesList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            final JCheckBox box = (JCheckBox)model.getElementAt(i);
            box.setSelected(selected);
        }
        rulesList.repaint();
    }

    private void importFile(final String fileLocation) {
        final RuleSetFactory factory = new RuleSetFactory();
        RuleSets ruleSets = null;
        try {
            ruleSets = factory.createRuleSets(fileLocation);
        } catch (RuleSetNotFoundException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, Plugin.PMD_TITLE);
        }
        if (ruleSets == null) {
            Util.logMessage("No rules to import");
        } else {
            final ListModel model = rulesList.getModel();
            final Set<Rule> allRules = ruleSets.getAllRules();
            for (int i = 0; i < model.getSize(); i++) {
                final JCheckBox box = (JCheckBox)model.getElementAt(i);
                final Rule rule = rules.getRule(box);
                box.setSelected(isRuleAvailabel(allRules, rule));
            }
        }
        rulesList.repaint();
    }

    private Boolean isRuleAvailabel(final Set<Rule> allRules,
                                    final Rule requestedRule) {
        Boolean returnValue = Boolean.FALSE;
        for (Rule rule : allRules) {
            if (rule.getName().equals(requestedRule.getName())) {
                returnValue = Boolean.TRUE;
            }
        }

        return returnValue;
    }

    private void exportFile(final String fileLocation) {
        final RuleSet selectedRules = rules.getSelectedRules();
        OutputStream outputStream = null;
        RuleSetWriter ruleSetWriter = null;
        try {
            outputStream = new FileOutputStream(fileLocation);
            ruleSetWriter = new RuleSetWriter(outputStream, false);
            ruleSetWriter.write(selectedRules);
            outputStream.flush();
        } catch (IOException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, Plugin.PMD_TITLE);
        } finally {
            if (outputStream != null) {
                try {
                    ruleSetWriter.close();
                    outputStream.close();
                } catch (IOException e) {
                    Util.logMessage(e.getStackTrace());
                    Util.showError(e, Plugin.PMD_TITLE);
                }
            }
        }
    }
}
