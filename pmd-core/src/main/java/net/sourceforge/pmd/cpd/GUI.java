/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageModuleBase.LanguageMetadata;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.InternalApiBridge;
import net.sourceforge.pmd.lang.impl.CpdOnlyLanguageModuleBase;
import net.sourceforge.pmd.util.CollectionUtil;

public class GUI implements CPDListener {

    private static final Object[][] RENDERER_SETS = {
        { "Text", new SimpleRenderer(), },
        { "XML", new XMLRenderer(), },
        { "CSV (comma)", new CSVRenderer(','), },
        { "CSV (tab)", new CSVRenderer('\t'), }, };

    private abstract static class LanguageConfig {

        public abstract Language getLanguage();

        boolean canUseCustomExtension() {
            return false;
        }

        void setExtension(String extension) {
            // by default do nothing
        }

        public boolean canIgnoreIdentifiers() {
            return getLanguage().newPropertyBundle().hasDescriptor(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS);
        }

        public boolean canIgnoreLiterals() {
            return getLanguage().newPropertyBundle().hasDescriptor(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS);
        }

        public boolean canIgnoreAnnotations() {
            return getLanguage().newPropertyBundle().hasDescriptor(CpdLanguageProperties.CPD_IGNORE_METADATA);
        }

        public boolean canIgnoreUsings() {
            return getLanguage().newPropertyBundle().hasDescriptor(CpdLanguageProperties.CPD_IGNORE_IMPORTS);
        }

        public boolean canIgnoreLiteralSequences() {
            return getLanguage().newPropertyBundle().hasDescriptor(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES);
        }

        public boolean canIgnoreIdentifierAndLiteralSequences() {
            return getLanguage().newPropertyBundle().hasDescriptor(CpdLanguageProperties.CPD_IGNORE_LITERAL_AND_IDENTIFIER_SEQUENCES);
        }

    }

    private static final List<LanguageConfig> LANGUAGE_SETS;


    private static final LanguageConfig CUSTOM_EXTENSION_LANG = new LanguageConfig() {
        private String extension = "custom_ext";

        @Override
        void setExtension(String extension) {
            this.extension = extension;
        }

        @Override
        boolean canUseCustomExtension() {
            return true;
        }

        @Override
        public Language getLanguage() {
            return new CpdOnlyLanguageModuleBase(
                LanguageMetadata.withId("custom_extension")
                                .extensions(extension)
                                .name("By extension...")) {
                @Override
                public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
                    return new AnyCpdLexer();
                }
            };
        }
    };


    static {
        List<LanguageConfig> languages = new ArrayList<>();
        LanguageRegistry.CPD.getLanguages().stream().map(l -> new LanguageConfig() {
            @Override
            public Language getLanguage() {
                return l;
            }
        }).forEach(languages::add);
        Collections.sort(languages, Comparator.comparing(LanguageConfig::getLanguage));
        languages.add(CUSTOM_EXTENSION_LANG);
        LANGUAGE_SETS = languages;
    }


    private static final int DEFAULT_CPD_MINIMUM_LENGTH = 75;
    private static final Map<String, LanguageConfig> LANGUAGE_CONFIGS_BY_LABEL =
        CollectionUtil.associateBy(LANGUAGE_SETS, l -> l.getLanguage().getName());
    private static final KeyStroke COPY_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK,
                                                                            false);
    private static final KeyStroke DELETE_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

    private static class ColumnSpec {

        private final String label;
        private final int alignment;
        private final int width;
        private final Comparator<Match> sorter;

        ColumnSpec(String aLabel, int anAlignment, int aWidth, Comparator<Match> aSorter) {
            label = aLabel;
            alignment = anAlignment;
            width = aWidth;
            sorter = aSorter;
        }

        public String label() {
            return label;
        }

        public int alignment() {
            return alignment;
        }

        public int width() {
            return width;
        }

        public Comparator<Match> sorter() {
            return sorter;
        }
    }

    public static final Comparator<Match> LABEL_COMPARATOR = Comparator.comparing(GUI::getLabel);
    private final ColumnSpec[] matchColumns = {
        new ColumnSpec("Source", SwingConstants.LEFT, -1, LABEL_COMPARATOR),
        new ColumnSpec("Matches", SwingConstants.RIGHT, 60, Match.MATCHES_COMPARATOR),
        new ColumnSpec("Lines", SwingConstants.RIGHT, 45, Match.LINES_COMPARATOR), };

    private static LanguageConfig languageConfigFor(String label) {
        return LANGUAGE_CONFIGS_BY_LABEL.get(label);
    }

    private static final class ExitAction extends AbstractAction {
        private final Runnable cleanupTask;

        private ExitAction(Runnable cleanupTask) {
            this.cleanupTask = cleanupTask;
            this.putValue(Action.NAME, "Exit");
            this.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (cleanupTask != null) {
                try {
                    cleanupTask.run();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            System.exit(0);
        }
    }

    private final class GoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                tokenizingFilesBar.setValue(0);
                tokenizingFilesBar.setString("");
                resultsTextArea.setText("");
                phaseLabel.setText("");
                timeField.setText("");
                go();
            }).start();
        }
    }

    private class SaveListener implements ActionListener {

        final CPDReportRenderer renderer;

        SaveListener(CPDReportRenderer theRenderer) {
            renderer = theRenderer;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            JFileChooser fcSave = new JFileChooser();
            int ret = fcSave.showSaveDialog(GUI.this.frame);
            File f = fcSave.getSelectedFile();
            if (f == null || ret != JFileChooser.APPROVE_OPTION) {
                return;
            }

            if (!f.canWrite()) {
                final CPDReport report = new CPDReport(sourceManager, matches, numberOfTokensPerFile, Collections.emptyList());
                try (PrintWriter pw = new PrintWriter(Files.newOutputStream(f.toPath()))) {
                    renderer.render(report, pw);
                    pw.flush();
                    JOptionPane.showMessageDialog(frame, "Saved " + matches.size() + " matches");
                } catch (IOException e) {
                    error("Couldn't save file" + f.getAbsolutePath(), e);
                }
            } else {
                error("Could not write to file " + f.getAbsolutePath(), null);
            }
        }

        private void error(String message, Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(GUI.this.frame, message);
        }

    }

    private final class BrowseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser(rootDirectoryField.getText());
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.showDialog(frame, "Select");
            if (fc.getSelectedFile() != null) {
                rootDirectoryField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        }
    }

    private static class AlignmentRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -2190382865483285032L;
        private final int[] alignments;

        AlignmentRenderer(int[] theAlignments) {
            alignments = theAlignments;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(alignments[column]);

            return this;
        }
    }

    private final JTextField rootDirectoryField = new JTextField(System.getProperty("user.home"));
    private final JTextField minimumLengthField = new JTextField(Integer.toString(DEFAULT_CPD_MINIMUM_LENGTH));
    private final JTextField encodingField = new JTextField(System.getProperty("file.encoding"));
    private final JTextField timeField = new JTextField(6);
    private final JLabel phaseLabel = new JLabel();
    private final JProgressBar tokenizingFilesBar = new JProgressBar();
    private final JTextArea resultsTextArea = new JTextArea();
    private final JCheckBox recurseCheckbox = new JCheckBox("", true);
    private final JCheckBox ignoreIdentifiersCheckbox = new JCheckBox("", false);
    private final JCheckBox ignoreLiteralsCheckbox = new JCheckBox("", false);
    private final JCheckBox ignoreAnnotationsCheckbox = new JCheckBox("", false);
    private final JCheckBox ignoreUsingsCheckbox = new JCheckBox("", false);
    private final JCheckBox ignoreLiteralSequencesCheckbox = new JCheckBox("", false);
    private final JCheckBox ignoreIdentifierAndLiteralSequencesCheckbox = new JCheckBox("", false);
    private final JComboBox<String> languageBox = new JComboBox<>();
    private final JTextField extensionField = new JTextField();
    private final JLabel extensionLabel = new JLabel("Extension:", SwingConstants.RIGHT);
    private final JTable resultsTable = new JTable();
    private final JButton goButton;
    private final JButton cancelButton;
    private final JPanel progressPanel;
    private final JFrame frame;
    private boolean trimLeadingWhitespace;

    private List<Match> matches = new ArrayList<>();
    private SourceManager sourceManager;
    private Map<FileId, Integer> numberOfTokensPerFile;

    private void addSaveOptionsTo(JMenu menu) {

        JMenuItem saveItem;

        for (final Object[] rendererSet : RENDERER_SETS) {
            saveItem = new JMenuItem("Save as " + rendererSet[0]);
            saveItem.addActionListener(new SaveListener((CPDReportRenderer) rendererSet[1]));
            menu.add(saveItem);
        }
    }

    public GUI() {
        frame = new JFrame("PMD Duplicate Code Detector (v " + PMDVersion.VERSION + ')');

        timeField.setEditable(false);

        final ExitAction exitAction = new ExitAction(this::closeSourceManager);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');

        addSaveOptionsTo(fileMenu);

        fileMenu.add(new JMenuItem(exitAction));
        JMenu viewMenu = new JMenu("View");
        fileMenu.setMnemonic('v');
        JMenuItem trimItem = new JCheckBoxMenuItem("Trim leading whitespace");
        trimItem.addItemListener(e -> {
            AbstractButton button = (AbstractButton) e.getItem();
            this.trimLeadingWhitespace = button.isSelected();
        });
        viewMenu.add(trimItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        frame.setJMenuBar(menuBar);

        // first make all the buttons
        JButton browseButton = new JButton("Browse");
        browseButton.setMnemonic('b');
        browseButton.addActionListener(new BrowseListener());
        goButton = new JButton("Go");
        goButton.setMnemonic('g');
        goButton.addActionListener(new GoListener());
        cancelButton = new JButton(exitAction);
        cancelButton.setText("Cancel");

        JPanel settingsPanel = makeSettingsPanel(browseButton, goButton, cancelButton);
        progressPanel = makeProgressPanel();
        JPanel resultsPanel = makeResultsPanel();

        adjustLanguageControlsFor(LANGUAGE_SETS.get(0));

        frame.getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(settingsPanel, BorderLayout.NORTH);
        topPanel.add(progressPanel, BorderLayout.CENTER);
        setProgressControls(false); // not running now
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(resultsPanel, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeSourceManager();
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
    }

    private void adjustLanguageControlsFor(LanguageConfig current) {
        ignoreIdentifiersCheckbox.setEnabled(current.canIgnoreIdentifiers());
        ignoreLiteralsCheckbox.setEnabled(current.canIgnoreLiterals());
        ignoreAnnotationsCheckbox.setEnabled(current.canIgnoreAnnotations());
        ignoreUsingsCheckbox.setEnabled(current.canIgnoreUsings());
        ignoreLiteralSequencesCheckbox.setEnabled(current.canIgnoreLiteralSequences());
        ignoreIdentifierAndLiteralSequencesCheckbox.setEnabled(current.canIgnoreIdentifierAndLiteralSequences());
        boolean enableExtension = current.canUseCustomExtension();
        if (enableExtension) {
            extensionField.setText("");
        } else {
            String firstExt = current.getLanguage().getExtensions().get(0);
            extensionField.setText(firstExt);
        }
        extensionField.setEnabled(enableExtension);
        extensionLabel.setEnabled(enableExtension);
    }

    private JPanel makeSettingsPanel(JButton browseButton, JButton goButton, JButton cxButton) {
        JPanel settingsPanel = new JPanel();
        GridBagHelper helper = new GridBagHelper(settingsPanel, new double[] { 0.2, 0.7, 0.1, 0.1 });
        helper.addLabel("Root source directory:");
        helper.add(rootDirectoryField);
        helper.add(browseButton, 2);
        helper.nextRow();
        helper.addLabel("Report duplicate chunks larger than:");
        minimumLengthField.setColumns(4);
        helper.add(minimumLengthField);
        helper.addLabel("Language:");
        for (LanguageConfig lconf : LANGUAGE_SETS) {
            languageBox.addItem(lconf.getLanguage().getName());
        }
        languageBox.addActionListener(e -> adjustLanguageControlsFor(languageConfigFor((String) languageBox.getSelectedItem())));
        helper.add(languageBox);
        helper.nextRow();
        helper.addLabel("Also scan subdirectories?");
        helper.add(recurseCheckbox);

        helper.add(extensionLabel);
        helper.add(extensionField);

        helper.nextRow();
        helper.addLabel("Ignore literals?");
        helper.add(ignoreLiteralsCheckbox);
        helper.addLabel("");
        helper.addLabel("");
        helper.nextRow();

        helper.nextRow();
        helper.addLabel("Ignore identifiers?");
        helper.add(ignoreIdentifiersCheckbox);
        helper.addLabel("");
        helper.addLabel("");
        helper.nextRow();

        helper.nextRow();
        helper.addLabel("Ignore annotations?");
        helper.add(ignoreAnnotationsCheckbox);
        helper.addLabel("");
        helper.addLabel("");
        helper.nextRow();

        helper.nextRow();
        helper.addLabel("Ignore usings?");
        helper.add(ignoreUsingsCheckbox);
        helper.addLabel("");
        helper.addLabel("");
        helper.nextRow();

        helper.nextRow();
        helper.addLabel("Ignore literal sequences?");
        helper.add(ignoreLiteralSequencesCheckbox);
        helper.add(goButton);
        helper.add(cxButton);
        helper.nextRow();

        helper.nextRow();
        helper.addLabel("Ignore identifier and literal sequences?");
        helper.add(ignoreIdentifierAndLiteralSequencesCheckbox);
        helper.add(goButton);
        helper.add(cxButton);
        helper.nextRow();

        helper.addLabel("File encoding (defaults based upon locale):");
        encodingField.setColumns(1);
        helper.add(encodingField);
        helper.addLabel("");
        helper.addLabel("");
        helper.nextRow();
        // settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        return settingsPanel;
    }

    private JPanel makeProgressPanel() {
        JPanel progressPanel = new JPanel();
        final double[] weights = { 0.0, 0.8, 0.4, 0.2 };
        GridBagHelper helper = new GridBagHelper(progressPanel, weights);
        helper.addLabel("Tokenizing files:");
        helper.add(tokenizingFilesBar, 3);
        helper.nextRow();
        helper.addLabel("Phase:");
        helper.add(phaseLabel);
        helper.addLabel("Time elapsed:");
        helper.add(timeField);
        helper.nextRow();
        progressPanel.setBorder(BorderFactory.createTitledBorder("Progress"));
        return progressPanel;
    }

    private JPanel makeResultsPanel() {
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());
        JScrollPane areaScrollPane = new JScrollPane(resultsTextArea);
        resultsTextArea.setEditable(false);
        areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(600, 300));

        resultsPanel.add(makeMatchList(), BorderLayout.WEST);
        resultsPanel.add(areaScrollPane, BorderLayout.CENTER);
        return resultsPanel;
    }

    private void populateResultArea() {
        int[] selectionIndices = resultsTable.getSelectedRows();
        TableModel model = resultsTable.getModel();
        List<Match> selections = new ArrayList<>(selectionIndices.length);
        for (int selectionIndex : selectionIndices) {
            selections.add((Match) model.getValueAt(selectionIndex, 99));
        }
        CPDReport toRender = new CPDReport(sourceManager, selections, Collections.emptyMap(), Collections.emptyList());
        String report = new SimpleRenderer(trimLeadingWhitespace).renderToString(toRender);
        resultsTextArea.setText(report);
        resultsTextArea.setCaretPosition(0); // move to the top
    }

    private void copyMatchListSelectionsToClipboard() {

        int[] selectionIndices = resultsTable.getSelectedRows();
        int colCount = resultsTable.getColumnCount();

        StringBuilder sb = new StringBuilder();

        for (int r = 0; r < selectionIndices.length; r++) {
            if (r > 0) {
                sb.append('\n');
            }
            sb.append(resultsTable.getValueAt(selectionIndices[r], 0));
            for (int c = 1; c < colCount; c++) {
                sb.append('\t');
                sb.append(resultsTable.getValueAt(selectionIndices[r], c));
            }
        }

        StringSelection ss = new StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    private void deleteMatchlistSelections() {

        int[] selectionIndices = resultsTable.getSelectedRows();

        for (int i = selectionIndices.length - 1; i >= 0; i--) {
            matches.remove(selectionIndices[i]);
        }

        resultsTable.getSelectionModel().clearSelection();
        resultsTable.addNotify();
    }

    private JComponent makeMatchList() {

        resultsTable.getSelectionModel().addListSelectionListener(e -> populateResultArea());

        resultsTable.registerKeyboardAction(e -> copyMatchListSelectionsToClipboard(), "Copy", COPY_KEY_STROKE, JComponent.WHEN_FOCUSED);

        resultsTable.registerKeyboardAction(e -> deleteMatchlistSelections(), "Del", DELETE_KEY_STROKE, JComponent.WHEN_FOCUSED);

        int[] alignments = new int[matchColumns.length];
        for (int i = 0; i < alignments.length; i++) {
            alignments[i] = matchColumns[i].alignment();
        }

        resultsTable.setDefaultRenderer(Object.class, new AlignmentRenderer(alignments));

        final JTableHeader header = resultsTable.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                sortOnColumn(header.columnAtPoint(new Point(e.getX(), e.getY())));
            }
        });

        return new JScrollPane(resultsTable);
    }

    private static String getLabel(Match match) {

        Set<FileId> sourceIDs = new HashSet<>(match.getMarkCount());
        for (Mark mark : match) {
            sourceIDs.add(mark.getLocation().getFileId());
        }

        if (sourceIDs.size() == 1) {
            FileId sourceId = sourceIDs.iterator().next();
            return "..." + sourceId.getFileName();
        } else {
            return String.format("(%d separate files)", sourceIDs.size());
        }
    }

    private void setProgressControls(boolean isRunning) {
        progressPanel.setVisible(isRunning);
        goButton.setEnabled(!isRunning);
        cancelButton.setEnabled(isRunning);
    }

    private void go() {
        closeSourceManager();
        try {
            File dirPath = new File(rootDirectoryField.getText());
            if (!dirPath.exists()) {
                JOptionPane.showMessageDialog(frame, "Can't read from that root source directory", "Error",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }

            setProgressControls(true);

            CPDConfiguration config = new CPDConfiguration();
            config.setMinimumTileSize(Integer.parseInt(minimumLengthField.getText()));
            try {
                config.setSourceEncoding(Charset.forName(encodingField.getText()));
            } catch (IllegalArgumentException ignored) {
            }
            config.setIgnoreIdentifiers(ignoreIdentifiersCheckbox.isSelected());
            config.setIgnoreLiterals(ignoreLiteralsCheckbox.isSelected());
            config.setIgnoreAnnotations(ignoreAnnotationsCheckbox.isSelected());
            config.setIgnoreUsings(ignoreUsingsCheckbox.isSelected());
            config.setIgnoreLiteralSequences(ignoreLiteralSequencesCheckbox.isSelected());
            config.setIgnoreIdentifierAndLiteralSequences(ignoreIdentifierAndLiteralSequencesCheckbox.isSelected());
            if (extensionField.isEnabled()) {
                CUSTOM_EXTENSION_LANG.setExtension(extensionField.getText());
            }

            LanguageConfig conf = languageConfigFor((String) languageBox.getSelectedItem());
            Language language = conf.getLanguage();
            config.setOnlyRecognizeLanguage(language);

            try (CpdAnalysis cpd = CpdAnalysis.create(config)) {
                cpd.setCpdListener(this);

                tokenizingFilesBar.setMinimum(0);
                phaseLabel.setText("");
                cpd.files().addFileOrDirectory(dirPath.toPath(), recurseCheckbox.isSelected());
                Timer t = createTimer();
                t.start();
                cpd.performAnalysis(report -> {
                    t.stop();
                    numberOfTokensPerFile = report.getNumberOfTokensPerFile();
                    matches = new ArrayList<>(report.getMatches());
                    setListDataFrom(matches);
                    prepareNewSourceManager(config, dirPath.toPath(), recurseCheckbox.isSelected());
                    String reportString = new SimpleRenderer().renderToString(report);
                    if (reportString.isEmpty()) {
                        JOptionPane.showMessageDialog(frame,
                                                      "Done. Couldn't find any duplicates longer than " + minimumLengthField.getText() + " tokens");
                    } else {
                        resultsTextArea.setText(reportString);
                    }
                });
            }
        } catch (IOException | RuntimeException t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Halted due to " + t.getClass().getName() + "; " + t.getMessage());
        }
        setProgressControls(false);
    }

    private void closeSourceManager() {
        try {
            if (sourceManager != null) {
                sourceManager.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void prepareNewSourceManager(CPDConfiguration config, Path dirPath, boolean recurse) {
        try {
            closeSourceManager();
            // fileCollector itself is empty, contains no closable resources.
            // the created sourceManager will be closed when exiting or when a new analysis is started,
            // see #closeSourceManager().
            @SuppressWarnings("PMD.CloseResource")
            FileCollector fileCollector = InternalApiBridge.newCollector(config.getLanguageVersionDiscoverer(), config.getReporter());
            fileCollector.addFileOrDirectory(dirPath, recurse);
            sourceManager = new SourceManager(fileCollector.getCollectedFiles());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Timer createTimer() {

        final long start = System.currentTimeMillis();

        return new Timer(1000, e -> {
            long now = System.currentTimeMillis();
            long elapsedMillis = now - start;
            long elapsedSeconds = elapsedMillis / 1000;
            long minutes = elapsedSeconds / 60;
            long seconds = elapsedSeconds - minutes * 60;
            timeField.setText(formatTime(minutes, seconds));
        });
    }

    private static String formatTime(long minutes, long seconds) {

        StringBuilder sb = new StringBuilder(5);
        if (minutes < 10) {
            sb.append('0');
        }
        sb.append(minutes).append(':');
        if (seconds < 10) {
            sb.append('0');
        }
        sb.append(seconds);
        return sb.toString();
    }

    private abstract static class SortingTableModel<E> extends AbstractTableModel {
        abstract int sortColumn();

        abstract void sortColumn(int column);

        abstract boolean sortDescending();

        abstract void sortDescending(boolean flag);

        abstract void sort(Comparator<E> comparator);
    }

    private TableModel tableModelFrom(final List<Match> items) {

        return new SortingTableModel<Match>() {

            private int sortColumn;
            private boolean sortDescending;

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Match match = items.get(rowIndex);
                switch (columnIndex) {
                case 0:
                    return getLabel(match);
                case 2:
                    return Integer.toString(match.getLineCount());
                case 1:
                    return match.getMarkCount() > 2 ? Integer.toString(match.getMarkCount()) : "";
                case 99:
                    return match;
                default:
                    return "";
                }
            }

            @Override
            public int getColumnCount() {
                return matchColumns.length;
            }

            @Override
            public int getRowCount() {
                return items.size();
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Object.class;
            }

            @Override
            public String getColumnName(int i) {
                return matchColumns[i].label();
            }

            @Override
            public int sortColumn() {
                return sortColumn;
            }

            @Override
            public void sortColumn(int column) {
                sortColumn = column;
            }

            @Override
            public boolean sortDescending() {
                return sortDescending;
            }

            @Override
            public void sortDescending(boolean flag) {
                sortDescending = flag;
            }

            @Override
            public void sort(Comparator<Match> comparator) {
                if (sortDescending) {
                    comparator = comparator.reversed();
                }
                items.sort(comparator);
            }
        };
    }

    private void sortOnColumn(int columnIndex) {
        Comparator<Match> comparator = matchColumns[columnIndex].sorter();
        SortingTableModel<Match> model = (SortingTableModel<Match>) resultsTable.getModel();
        if (model.sortColumn() == columnIndex) {
            model.sortDescending(!model.sortDescending());
        }
        model.sortColumn(columnIndex);
        model.sort(comparator);

        resultsTable.getSelectionModel().clearSelection();
        resultsTable.repaint();
    }

    private void setListDataFrom(List<Match> matches) {

        resultsTable.setModel(tableModelFrom(matches));

        TableColumnModel colModel = resultsTable.getColumnModel();
        TableColumn column;
        int width;

        for (int i = 0; i < matchColumns.length; i++) {
            if (matchColumns[i].width() > 0) {
                column = colModel.getColumn(i);
                width = matchColumns[i].width();
                column.setPreferredWidth(width);
                column.setMinWidth(width);
                column.setMaxWidth(width);
            }
        }
    }

    // CPDListener
    @Override
    public void phaseUpdate(int phase) {
        phaseLabel.setText(getPhaseText(phase));
    }

    public String getPhaseText(int phase) {
        switch (phase) {
        case CPDListener.INIT:
            return "Initializing";
        case CPDListener.HASH:
            return "Hashing";
        case CPDListener.MATCH:
            return "Matching";
        case CPDListener.GROUPING:
            return "Grouping";
        case CPDListener.DONE:
            return "Done";
        default:
            return "Unknown";
        }
    }

    @Override
    public void addedFile(int fileCount) {
        tokenizingFilesBar.setMaximum(fileCount);
        tokenizingFilesBar.setValue(tokenizingFilesBar.getValue() + 1);
    }
    // CPDListener

    public static void main(String[] args) {
        // this should prevent the disk not found popup
        // System.setSecurityManager(null);
        new GUI();
    }

}
