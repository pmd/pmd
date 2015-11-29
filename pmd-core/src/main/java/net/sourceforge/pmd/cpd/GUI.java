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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractButton;
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
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import net.sourceforge.pmd.PMD;

import org.apache.commons.io.IOUtils;

public class GUI implements CPDListener {

//	private interface Renderer {
//		String render(Iterator<Match> items);
//	}

	private static final Object[][] RENDERER_SETS = new Object[][] {
		{ "Text", 		new Renderer() { public String render(Iterator<Match> items) { return new SimpleRenderer().render(items); } } },
		{ "XML", 		new Renderer() { public String render(Iterator<Match> items) { return new XMLRenderer().render(items); } } },
		{ "CSV (comma)",new Renderer() { public String render(Iterator<Match> items) { return new CSVRenderer(',').render(items); } } },
		{ "CSV (tab)",	new Renderer() { public String render(Iterator<Match> items) { return new CSVRenderer('\t').render(items); } } }
		};

	private static abstract class LanguageConfig {
		public abstract Language languageFor(Properties p);
		public boolean canIgnoreIdentifiers() { return false; }
		public boolean canIgnoreLiterals() { return false; }
		public boolean canIgnoreAnnotations() { return false; }
		public boolean canIgnoreUsings() { return false; }
		public abstract String[] extensions();
	}

    private static final Object[][] LANGUAGE_SETS;

    static {
        LANGUAGE_SETS = new Object[LanguageFactory.supportedLanguages.length + 1][2];

        int index;
        for (index = 0; index < LanguageFactory.supportedLanguages.length; index++) {
            final String terseName = LanguageFactory.supportedLanguages[index];
            final Language lang = LanguageFactory.createLanguage(terseName);
            LANGUAGE_SETS[index][0] = lang.getName();
            LANGUAGE_SETS[index][1] = new LanguageConfig() {
                @Override
                public Language languageFor(Properties p) {
                    lang.setProperties(p);
                    return lang;
                }
                @Override
                public String[] extensions() {
                    List<String> exts = lang.getExtensions();
                    return exts.toArray(new String[exts.size()]);
                }
                @Override
                public boolean canIgnoreAnnotations() {
                    return "java".equals(terseName);
                }
                @Override
                public boolean canIgnoreIdentifiers() {
                    return "java".equals(terseName);
                }
                @Override
                public boolean canIgnoreLiterals() {
                    return "java".equals(terseName);
                }
                @Override
                public boolean canIgnoreUsings() {
                    return "cs".equals(terseName);
                }
            };
        }
        LANGUAGE_SETS[index][0] = "by extension...";
        LANGUAGE_SETS[index][1] = new LanguageConfig() {
            @Override
            public Language languageFor(Properties p) {
                return LanguageFactory.createLanguage(LanguageFactory.BY_EXTENSION, p);
            }
            @Override
            public String[] extensions() {
                return new String[] {"" };
            }
        };
    }

	private static final int		DEFAULT_CPD_MINIMUM_LENGTH = 75;
	private static final Map<String, LanguageConfig> LANGUAGE_CONFIGS_BY_LABEL = new HashMap<>(LANGUAGE_SETS.length);
	private static final KeyStroke	COPY_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
	private static final KeyStroke	DELETE_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);

	private class ColumnSpec {
		private String label;
		private int alignment;
		private int width;
		private Comparator<Match> sorter;

		public ColumnSpec(String aLabel, int anAlignment, int aWidth, Comparator<Match> aSorter) {
			label = aLabel;
			alignment = anAlignment;
			width = aWidth;
			sorter = aSorter;
		}
		public String label() { return label; }
		public int alignment() { return alignment; }
		public int width() { return width; }
		public Comparator<Match> sorter() { return sorter; }
	}

	private final ColumnSpec[] matchColumns = new ColumnSpec[] {
		new ColumnSpec("Source", 	SwingConstants.LEFT, -1, Match.LABEL_COMPARATOR),
		new ColumnSpec("Matches", 	SwingConstants.RIGHT, 60, Match.MATCHES_COMPARATOR),
		new ColumnSpec("Lines", 	SwingConstants.RIGHT, 45, Match.LINES_COMPARATOR),
		};

	static {
		for (int i=0; i<LANGUAGE_SETS.length; i++) {
			LANGUAGE_CONFIGS_BY_LABEL.put((String)LANGUAGE_SETS[i][0], (LanguageConfig)LANGUAGE_SETS[i][1]);
		}
	}

	private static LanguageConfig languageConfigFor(String label) {
		return LANGUAGE_CONFIGS_BY_LABEL.get(label);
	}

    private static class CancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    private class GoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {
                public void run() {
                    tokenizingFilesBar.setValue(0);
                    tokenizingFilesBar.setString("");
                    resultsTextArea.setText("");
                    phaseLabel.setText("");
                    timeField.setText("");
                    go();
                }
            }).start();
        }
    }

    private class SaveListener implements ActionListener {

    	final Renderer renderer;

    	public SaveListener(Renderer theRenderer) {
    		renderer = theRenderer;
    	}

        public void actionPerformed(ActionEvent evt) {
            JFileChooser fcSave	= new JFileChooser();
            int ret = fcSave.showSaveDialog(GUI.this.frame);
            File f = fcSave.getSelectedFile();
            if (f == null || ret != JFileChooser.APPROVE_OPTION) {
        	return;
            }

            if (!f.canWrite()) {
                PrintWriter pw = null;
                try {
                    pw = new PrintWriter(new FileOutputStream(f));
                    pw.write(renderer.render(matches.iterator()));
                    pw.flush();
                    JOptionPane.showMessageDialog(frame, "Saved " + matches.size() + " matches");
                } catch (IOException e) {
                    error("Couldn't save file" + f.getAbsolutePath(), e);
                } finally {
                    IOUtils.closeQuietly(pw);
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

    private class BrowseListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser(rootDirectoryField.getText());
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fc.showDialog(frame, "Select");
            if (fc.getSelectedFile() != null) {
                rootDirectoryField.setText(fc.getSelectedFile().getAbsolutePath());
            }
        }
    }

	private class AlignmentRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = -2190382865483285032L;
        private int[] alignments;

		public AlignmentRenderer(int[] theAlignments) {
			alignments = theAlignments;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			setHorizontalAlignment(alignments[column]);

			return this;
		}
	}

    private JTextField rootDirectoryField	= new JTextField(System.getProperty("user.home"));
    private JTextField minimumLengthField	= new JTextField(Integer.toString(DEFAULT_CPD_MINIMUM_LENGTH));
    private JTextField encodingField		= new JTextField(System.getProperty("file.encoding"));
    private JTextField timeField			= new JTextField(6);
    private JLabel phaseLabel				= new JLabel();
    private JProgressBar tokenizingFilesBar = new JProgressBar();
    private JTextArea resultsTextArea		= new JTextArea();
    private JCheckBox recurseCheckbox		= new JCheckBox("", true);
    private JCheckBox ignoreIdentifiersCheckbox = new JCheckBox("", false);
    private JCheckBox ignoreLiteralsCheckbox = new JCheckBox("", false);
    private JCheckBox ignoreAnnotationsCheckbox = new JCheckBox("", false);
    private JCheckBox ignoreUsingsCheckbox  = new JCheckBox("", false);
    private JComboBox<String> languageBox	= new JComboBox<>();
    private JTextField extensionField		= new JTextField();
    private JLabel extensionLabel			= new JLabel("Extension:", SwingConstants.RIGHT);
    private JTable resultsTable				= new JTable();
    private JButton goButton;
    private JButton cancelButton;
    private JPanel progressPanel;
    private JFrame frame;
    private boolean trimLeadingWhitespace;

    private List<Match> matches = new ArrayList<>();

    private void addSaveOptionsTo(JMenu menu) {

        JMenuItem saveItem;

        for (int i=0; i<RENDERER_SETS.length; i++) {
        	saveItem = new JMenuItem("Save as " + RENDERER_SETS[i][0]);
        	saveItem.addActionListener(new SaveListener((Renderer)RENDERER_SETS[i][1]));
        	menu.add(saveItem);
        }
    }

    public GUI() {
        frame = new JFrame("PMD Duplicate Code Detector (v " + PMD.VERSION + ')');

        timeField.setEditable(false);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');

        addSaveOptionsTo(fileMenu);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(new CancelListener());
        fileMenu.add(exitItem);
        JMenu viewMenu = new JMenu("View");
        fileMenu.setMnemonic('v');
        JMenuItem trimItem = new JCheckBoxMenuItem("Trim leading whitespace");
        trimItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                AbstractButton button = (AbstractButton)e.getItem();
                GUI.this.trimLeadingWhitespace = button.isSelected();
            }
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
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new CancelListener());

        JPanel settingsPanel = makeSettingsPanel(browseButton, goButton, cancelButton);
        progressPanel = makeProgressPanel();
        JPanel resultsPanel = makeResultsPanel();

        adjustLanguageControlsFor((LanguageConfig)LANGUAGE_SETS[0][1]);

        frame.getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(settingsPanel, BorderLayout.NORTH);
        topPanel.add(progressPanel, BorderLayout.CENTER);
        setProgressControls(false);	// not running now
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(resultsPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void adjustLanguageControlsFor(LanguageConfig current) {
         ignoreIdentifiersCheckbox.setEnabled(current.canIgnoreIdentifiers());
         ignoreLiteralsCheckbox.setEnabled(current.canIgnoreLiterals());
         ignoreAnnotationsCheckbox.setEnabled(current.canIgnoreAnnotations());
         ignoreUsingsCheckbox.setEnabled(current.canIgnoreUsings());
         extensionField.setText(current.extensions()[0]);
         boolean enableExtension = current.extensions()[0].length() == 0;
         extensionField.setEnabled(enableExtension);
         extensionLabel.setEnabled(enableExtension);
    }

    private JPanel makeSettingsPanel(JButton browseButton, JButton goButton, JButton cxButton) {
        JPanel settingsPanel = new JPanel();
        GridBagHelper helper = new GridBagHelper(settingsPanel, new double[]{0.2, 0.7, 0.1, 0.1});
        helper.addLabel("Root source directory:");
        helper.add(rootDirectoryField);
        helper.add(browseButton, 2);
        helper.nextRow();
        helper.addLabel("Report duplicate chunks larger than:");
        minimumLengthField.setColumns(4);
        helper.add(minimumLengthField);
        helper.addLabel("Language:");
        for (int i=0; i<LANGUAGE_SETS.length; i++) {
        	languageBox.addItem(String.valueOf(LANGUAGE_SETS[i][0]));
        }
        languageBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	adjustLanguageControlsFor(
            			languageConfigFor((String)languageBox.getSelectedItem())
            			);
            }
        });
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
        helper.add(goButton);
        helper.add(cxButton);
        helper.nextRow();

        helper.addLabel("File encoding (defaults based upon locale):");
        encodingField.setColumns(1);
        helper.add(encodingField);
        helper.addLabel("");
        helper.addLabel("");
        helper.nextRow();
//        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        return settingsPanel;
    }

    private JPanel makeProgressPanel() {
        JPanel progressPanel = new JPanel();
        final double[] weights = {0.0, 0.8, 0.4, 0.2};
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
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(600, 300));

        resultsPanel.add(makeMatchList(), BorderLayout.WEST);
        resultsPanel.add(areaScrollPane, BorderLayout.CENTER);
        return resultsPanel;
    }

    private void populateResultArea() {
    	int[] selectionIndices = resultsTable.getSelectedRows();
    	TableModel model = resultsTable.getModel();
    	List<Match> selections = new ArrayList<>(selectionIndices.length);
    	for (int i=0; i<selectionIndices.length; i++) {
    		selections.add((Match)model.getValueAt(selectionIndices[i], 99));
    	}
    	String report = new SimpleRenderer(trimLeadingWhitespace).render(selections.iterator());
    	resultsTextArea.setText(report);
    	resultsTextArea.setCaretPosition(0);	// move to the top
    }

    private void copyMatchListSelectionsToClipboard() {

    	int[] selectionIndices = resultsTable.getSelectedRows();
    	int colCount = resultsTable.getColumnCount();

    	StringBuilder sb = new StringBuilder();

    	for (int r=0; r<selectionIndices.length; r++) {
			if (r > 0) {
			    sb.append('\n');
			}
			sb.append(resultsTable.getValueAt(selectionIndices[r], 0));
    		for (int c=1; c<colCount; c++) {
    			sb.append('\t');
    			sb.append(resultsTable.getValueAt(selectionIndices[r], c));
    		}
    	}

    	StringSelection ss = new StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    private void deleteMatchlistSelections() {

    	int[] selectionIndices = resultsTable.getSelectedRows();

    	for (int i=selectionIndices.length-1; i >=0; i--) {
    		matches.remove(selectionIndices[i]);
    	}

    	resultsTable.getSelectionModel().clearSelection();
    	resultsTable.addNotify();
    }

    private JComponent makeMatchList() {

    	resultsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				populateResultArea();
			}});

    	resultsTable.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) { copyMatchListSelectionsToClipboard(); }
    		},"Copy", COPY_KEY_STROKE, JComponent.WHEN_FOCUSED);

    	resultsTable.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) { deleteMatchlistSelections(); }
    		},"Del", DELETE_KEY_STROKE, JComponent.WHEN_FOCUSED);

    	int[] alignments = new int[matchColumns.length];
    	for (int i=0; i<alignments.length; i++) {
    	    alignments[i] = matchColumns[i].alignment();
    	}

    	resultsTable.setDefaultRenderer(Object.class, new AlignmentRenderer(alignments));

    	final JTableHeader header = resultsTable.getTableHeader();
    	header.addMouseListener( new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				sortOnColumn(header.columnAtPoint(new Point(e.getX(), e.getY())));
				}
			});

        return new JScrollPane(resultsTable);
    }

    private boolean isLegalPath(String path, LanguageConfig config) {
    	String[] extensions = config.extensions();
    	for (int i=0; i<extensions.length; i++) {
    		if (path.endsWith(extensions[i]) && extensions[i].length() > 0) {
    		    return true;
    		}
    	}
    	return false;
    }

    private String setLabelFor(Match match) {

    	Set<String> sourceIDs = new HashSet<>(match.getMarkCount());
    	for (Iterator<Mark> occurrences = match.iterator(); occurrences.hasNext();) {
             sourceIDs.add(occurrences.next().getFilename());
          }
    	String label;

    	if (sourceIDs.size() == 1) {
    		String sourceId = sourceIDs.iterator().next();
    		int separatorPos = sourceId.lastIndexOf(File.separatorChar);
    		label = "..." + sourceId.substring(separatorPos);
    		} else {
    	    	label = "(" + sourceIDs.size() + " separate files)";
    		}

    	match.setLabel(label);
    	return label;
    }

    private void setProgressControls(boolean isRunning) {
        progressPanel.setVisible(isRunning);
        goButton.setEnabled(!isRunning);
        cancelButton.setEnabled(isRunning);
    }

    private void go() {
        try {
            File dirPath = new File(rootDirectoryField.getText());
            if (!dirPath.exists()) {
                JOptionPane.showMessageDialog(frame,
                        "Can't read from that root source directory",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            setProgressControls(true);

            Properties p = new Properties();
            CPDConfiguration config = new CPDConfiguration();
            config.setMinimumTileSize(Integer.parseInt(minimumLengthField.getText()));
            config.setEncoding(encodingField.getText());
            config.setIgnoreIdentifiers(ignoreIdentifiersCheckbox.isSelected());
            config.setIgnoreLiterals(ignoreLiteralsCheckbox.isSelected());
            config.setIgnoreAnnotations(ignoreAnnotationsCheckbox.isSelected());
            config.setIgnoreUsings(ignoreUsingsCheckbox.isSelected());
            p.setProperty(LanguageFactory.EXTENSION, extensionField.getText());

            LanguageConfig conf = languageConfigFor((String)languageBox.getSelectedItem());
            Language language = conf.languageFor(p);
            config.setLanguage(language);

            CPDConfiguration.setSystemProperties(config);

            CPD cpd = new CPD(config);
            cpd.setCpdListener(this);
            tokenizingFilesBar.setMinimum(0);
            phaseLabel.setText("");
            if (isLegalPath(dirPath.getPath(), conf)) {	// should use the language file filter instead?
            	cpd.add(dirPath);
            } else {
                if (recurseCheckbox.isSelected()) {
                    cpd.addRecursively(dirPath);
                } else {
                    cpd.addAllInDirectory(dirPath);
                }
            }
            Timer t = createTimer();
            t.start();
            cpd.go();
            t.stop();

        	matches = new ArrayList<>();
        	for (Iterator<Match> i = cpd.getMatches(); i.hasNext();) {
        		Match match = i.next();
        		setLabelFor(match);
        		matches.add(match);
        	}

            setListDataFrom(matches);
            String report = new SimpleRenderer().render(cpd.getMatches());
            if (report.length() == 0) {
                JOptionPane.showMessageDialog(frame,
                        "Done. Couldn't find any duplicates longer than " + minimumLengthField.getText() + " tokens");
            } else {
                resultsTextArea.setText(report);
            }
        } catch (IOException t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Halted due to " + t.getClass().getName() + "; " + t.getMessage());
        } catch (RuntimeException t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Halted due to " + t.getClass().getName() + "; " + t.getMessage());
        }
        setProgressControls(false);
    }

	private Timer createTimer() {

		final long start = System.currentTimeMillis();

		Timer t = new Timer(1000, new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        long now = System.currentTimeMillis();
		        long elapsedMillis = now - start;
		        long elapsedSeconds = elapsedMillis / 1000;
		        long minutes = (long) Math.floor(elapsedSeconds / 60);
		        long seconds = elapsedSeconds - (minutes * 60);
		        timeField.setText(formatTime(minutes, seconds));
		    }
		});
		return t;
	}

	private static String formatTime(long minutes, long seconds) {

		StringBuilder sb = new StringBuilder(5);
		if (minutes < 10) { sb.append('0'); }
		sb.append(minutes).append(':');
		if (seconds < 10) { sb.append('0'); }
		sb.append(seconds);
		return sb.toString();
	}

    private interface SortingTableModel<E> extends TableModel {
    	int sortColumn();
    	void sortColumn(int column);
    	boolean sortDescending();
    	void sortDescending(boolean flag);
    	void sort(Comparator<E> comparator);
    }

    private TableModel tableModelFrom(final List<Match> items) {

    	TableModel model = new SortingTableModel<Match>() {

    		private int sortColumn;
    		private boolean sortDescending;

    		 public Object getValueAt(int rowIndex, int columnIndex) {
    			Match match = items.get(rowIndex);
    			switch (columnIndex) {
    				case 0: return match.getLabel();
    				case 2: return Integer.toString(match.getLineCount());
    				case 1: return match.getMarkCount() > 2 ? Integer.toString(match.getMarkCount()) : "";
    				case 99: return match;
    				default: return "";
    				}
    		 	}
			public int getColumnCount() { return matchColumns.length;	}
			public int getRowCount() {	return items.size(); }
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false;	}
			public Class<?> getColumnClass(int columnIndex) { return Object.class;	}
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {	}
			public String getColumnName(int i) {	return matchColumns[i].label();	}
			public void addTableModelListener(TableModelListener l) { }
			public void removeTableModelListener(TableModelListener l) { }
			public int sortColumn() { return sortColumn; }
			public void sortColumn(int column) { sortColumn = column; }
			public boolean sortDescending() { return sortDescending; }
			public void sortDescending(boolean flag) { sortDescending = flag; }
			public void sort(Comparator<Match> comparator) {
				Collections.sort(items, comparator);
				if (sortDescending) {
				    Collections.reverse(items);
				}
				}
    		};

    	return model;
    }

    private void sortOnColumn(int columnIndex) {
    	Comparator<Match> comparator = matchColumns[columnIndex].sorter();
    	SortingTableModel<Match> model = (SortingTableModel<Match>)resultsTable.getModel();
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

    	for (int i=0; i<matchColumns.length; i++) {
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
            default :
                return "Unknown";
        }
    }

    public void addedFile(int fileCount, File file) {
        tokenizingFilesBar.setMaximum(fileCount);
        tokenizingFilesBar.setValue(tokenizingFilesBar.getValue() + 1);
    }
    // CPDListener


    public static void main(String[] args) {
    	//this should prevent the disk not found popup
        // System.setSecurityManager(null);
        new GUI();
    }

}
