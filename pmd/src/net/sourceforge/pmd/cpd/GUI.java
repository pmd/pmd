/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
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

public class GUI implements CPDListener {

	private interface Renderer {
		String render(Iterator items);
	}
	
	private static final Object[][] rendererSets = new Object[][] {
		{ "Text", 		new Renderer() { public String render(Iterator items) { return new SimpleRenderer().render(items); } } },
		{ "XML", 		new Renderer() { public String render(Iterator items) { return new XMLRenderer().render(items); } } },
		{ "CSV (comma)",new Renderer() { public String render(Iterator items) { return new CSVRenderer(',').render(items); } } },
		{ "CSV (tab)",	new Renderer() { public String render(Iterator items) { return new CSVRenderer('\t').render(items); } } }
		};
	
	private interface LanguageConfig {
		Language languageFor(LanguageFactory lf, Properties p);
		boolean ignoreLiteralsByDefault();
		String[] extensions();
	};
	
	private static final Object[][] languageSets = new Object[][] {
		{"Java", 			new LanguageConfig() { 
									public Language languageFor(LanguageFactory lf, Properties p) { return lf.createLanguage(LanguageFactory.JAVA_KEY); }
									public boolean ignoreLiteralsByDefault() { return true; }
									public String[] extensions() { return new String[] {".java", ".class" }; }; } },
		{"JSP", 			new LanguageConfig() { 
									public Language languageFor(LanguageFactory lf, Properties p) { return lf.createLanguage(LanguageFactory.JSP_KEY); }
									public boolean ignoreLiteralsByDefault() { return false; }
									public String[] extensions() { return new String[] {".jsp" }; }; } },
		{"C++", 			new LanguageConfig() { 
									public Language languageFor(LanguageFactory lf, Properties p) { return lf.createLanguage(LanguageFactory.CPP_KEY); }
									public boolean ignoreLiteralsByDefault() { return false; }
									public String[] extensions() { return new String[] {".cpp", ".c" }; }; } },
		{"Ruby",			new LanguageConfig() { 
									public Language languageFor(LanguageFactory lf, Properties p) { return lf.createLanguage(LanguageFactory.RUBY_KEY); }
									public boolean ignoreLiteralsByDefault() { return false; }
									public String[] extensions() { return new String[] {".rb" }; }; } },
		{"by extension...", new LanguageConfig() { 
									public Language languageFor(LanguageFactory lf, Properties p) { return lf.createLanguage(LanguageFactory.BY_EXTENSION, p); }
									public boolean ignoreLiteralsByDefault() { return false; }
									public String[] extensions() { return new String[] {"" }; }; } },
		{"PHP", 			new LanguageConfig() { 
									public Language languageFor(LanguageFactory lf, Properties p) { return lf.createLanguage(LanguageFactory.PHP_KEY); }
									public boolean ignoreLiteralsByDefault() { return false; }
									public String[] extensions() { return new String[] {".php" }; };	} },
		};
	
	private static final int		defaultCPDMinimumLength = 75;
	private static final Map		langConfigsByLabel = new HashMap(languageSets.length);
	private static final KeyStroke	copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK,false);
	private static final KeyStroke	delete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
	
	private class ColumnSpec {
		private String label;
		private int alignment;
		private int width;
		private Comparator sorter;
		
		public ColumnSpec(String aLabel, int anAlignment, int aWidth, Comparator aSorter) {
			label = aLabel;
			alignment = anAlignment;
			width = aWidth;
			sorter = aSorter;
		}
		public String label() { return label; };
		public int alignment() { return alignment; };
		public int width() { return width; };
		public Comparator sorter() { return sorter; };
	}

	private final ColumnSpec[] matchColumns = new ColumnSpec[] {
		new ColumnSpec("Source", 	SwingConstants.LEFT, -1, Match.LabelComparator),
		new ColumnSpec("Matches", 	SwingConstants.RIGHT, 60, Match.MatchesComparator),
		new ColumnSpec("Lines", 	SwingConstants.RIGHT, 45, Match.LinesComparator),
		};
    
	static {		
		for (int i=0; i<languageSets.length; i++) {
			langConfigsByLabel.put(languageSets[i][0], languageSets[i][1]);
		}
	}
	
	private static LanguageConfig languageConfigFor(String label) {
		return (LanguageConfig)langConfigsByLabel.get(label);
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
            if (f == null || ret != JFileChooser.APPROVE_OPTION) return;
                        
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
                    if (pw != null) pw.close();
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
		
		private int[] alignments;
		
		public AlignmentRenderer(int[] theAlignments) {
			alignments = theAlignments;
		};
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
 
			setHorizontalAlignment(alignments[column]);
 
			return this;
		}
	}
    
    private JTextField rootDirectoryField	= new JTextField(System.getProperty("user.home"));
    private JTextField minimumLengthField	= new JTextField(Integer.toString(defaultCPDMinimumLength));
    private JTextField timeField			= new JTextField(6);
    private JLabel phaseLabel				= new JLabel();
    private JProgressBar tokenizingFilesBar = new JProgressBar();
    private JTextArea resultsTextArea		= new JTextArea();
    private JCheckBox recurseCheckbox		= new JCheckBox("", true);
    private JCheckBox ignoreLiteralsCheckbox = new JCheckBox("", false);
    private JComboBox languageBox			= new JComboBox();
    private JTextField extensionField		= new JTextField();
    private JLabel extensionLabel			= new JLabel("Extension:", SwingConstants.RIGHT);
    private JTable resultsTable				= new JTable();    
    private JButton goButton;
    private JButton cancelButton;
    private JPanel progressPanel;
    private JFrame frame;
    private boolean trimLeadingWhitespace;

    private List matches = new ArrayList();

    private void addSaveOptionsTo(JMenu menu) {
    	
        JMenuItem saveItem;
        
        for (int i=0; i<rendererSets.length; i++) {
        	saveItem = new JMenuItem("Save as " + rendererSets[i][0]);
        	saveItem.addActionListener(new SaveListener((Renderer)rendererSets[i][1]));
        	menu.add(saveItem);
        }
    }
    
    public GUI() {
        frame = new JFrame("PMD Duplicate Code Detector");

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

        adjustLanguageControlsFor((LanguageConfig)languageSets[0][1]);
        
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
    	 ignoreLiteralsCheckbox.setEnabled(current.ignoreLiteralsByDefault());
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
        for (int i=0; i<languageSets.length; i++) {
        	languageBox.addItem(languageSets[i][0]);
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
        helper.addLabel("Ignore literals and identifiers?");
        helper.add(ignoreLiteralsCheckbox);
        helper.add(goButton);
        helper.add(cxButton);
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
    	List selections = new ArrayList(selectionIndices.length);
    	for (int i=0; i<selectionIndices.length; i++) {
    		selections.add(model.getValueAt(selectionIndices[i], 99));
    	}
    	String report = new SimpleRenderer(trimLeadingWhitespace).render(selections.iterator());
    	resultsTextArea.setText(report);
    	resultsTextArea.setCaretPosition(0);	// move to the top
    }
        
    private void copyMatchListSelectionsToClipboard() {
    	
    	int[] selectionIndices = resultsTable.getSelectedRows();
    	int colCount = resultsTable.getColumnCount();
    	
    	StringBuffer sb = new StringBuffer();
    	    	
    	for (int r=0; r<selectionIndices.length; r++) {
			if (r > 0) sb.append('\n');
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
    		},"Copy", copy, JComponent.WHEN_FOCUSED);
    	
    	resultsTable.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) { deleteMatchlistSelections(); } 
    		},"Del", delete, JComponent.WHEN_FOCUSED);
    	
    	int[] alignments = new int[matchColumns.length];
    	for (int i=0; i<alignments.length; i++) alignments[i] = matchColumns[i].alignment();

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
    		if (path.endsWith(extensions[i]) && extensions[i].length() > 0) return true;
    	}
    	return false;
    }
    
    private String setLabelFor(Match match) {
    	
    	Set sourceIDs = new HashSet(match.getMarkCount());
    	for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
             sourceIDs.add( ((TokenEntry) occurrences.next()).getTokenSrcID());
          }
    	String label;
    	
    	if (sourceIDs.size() == 1) {
    		String sourceId = (String)sourceIDs.iterator().next();
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
    	String dirPath = rootDirectoryField.getText();
        try {
            if (!(new File(dirPath)).exists()) {
                JOptionPane.showMessageDialog(frame,
                        "Can't read from that root source directory",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
      
            setProgressControls(true);

            Properties p = new Properties();
            p.setProperty(JavaTokenizer.IGNORE_LITERALS, String.valueOf(ignoreLiteralsCheckbox.isSelected()));
            p.setProperty(LanguageFactory.EXTENSION, extensionField.getText());
            LanguageConfig conf = languageConfigFor((String)languageBox.getSelectedItem());
            Language language = conf.languageFor(new LanguageFactory(), p);
            CPD cpd = new CPD(Integer.parseInt(minimumLengthField.getText()), language);
            cpd.setCpdListener(this);
            tokenizingFilesBar.setMinimum(0);
            phaseLabel.setText("");
            if (isLegalPath(dirPath, conf)) {	// should use the language file filter instead?
            	cpd.add(new File(dirPath));
            } else {
                if (recurseCheckbox.isSelected()) {
                    cpd.addRecursively(dirPath);
                } else {
                    cpd.addAllInDirectory(dirPath);
                }
            }
            final long start = System.currentTimeMillis();
            Timer t = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    long now = System.currentTimeMillis();
                    long elapsedMillis = now - start;
                    long elapsedSeconds = elapsedMillis / 1000;
                    long minutes = (long) Math.floor(elapsedSeconds / 60);
                    long seconds = elapsedSeconds - (minutes * 60);
                    timeField.setText(""
                            + munge(String.valueOf(minutes))
                            + ':'
                            + munge(String.valueOf(seconds)));
                }

                private String munge(String in) {
                    if (in.length() < 2) {
                        in = "0" + in;
                    }
                    return in;
                }
            });
            t.start();
            cpd.go();
            t.stop();
            
        	matches = new ArrayList();
        	Match match;
        	for (Iterator i = cpd.getMatches(); i.hasNext();) {
        		match = (Match)i.next();
        		setLabelFor(match);
        		matches.add(match);
        	}

            String report = new SimpleRenderer().render(cpd.getMatches());
            if (report.length() == 0) {
                JOptionPane.showMessageDialog(frame,
                        "Done; couldn't find any duplicates longer than " + minimumLengthField.getText() + " tokens");
            } else {
                resultsTextArea.setText(report);
                setListDataFrom(cpd.getMatches());
                
            }
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Halted due to " + t.getClass().getName() + "; " + t.getMessage());
        }
        setProgressControls(false);
    }
	
    private interface SortingTableModel extends TableModel {
    	public int sortColumn();
    	public void sortColumn(int column);
    	public boolean sortDescending();
    	public void sortDescending(boolean flag);
    	public void sort(Comparator comparator);
    }
    
    private TableModel tableModelFrom(final List items) {
    	
    	TableModel model = new SortingTableModel() {
    		
    		private int sortColumn;
    		private boolean sortDescending;
    		
    		 public Object getValueAt(int rowIndex, int columnIndex) {
    			Match match = (Match) items.get(rowIndex);
    			switch (columnIndex) {
    				case 0: return match.getLabel();
    				case 2: return Integer.toString(match.getLineCount());
    				case 1: return match.getMarkCount() > 2 ? Integer.toString(match.getMarkCount()) : "";
    				case 99: return match;
    				}
    			return "";
    		 	}
			public int getColumnCount() { return matchColumns.length;	}
			public int getRowCount() {	return items.size(); }
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false;	}
			public Class getColumnClass(int columnIndex) { return Object.class;	}
			public void setValueAt(Object aValue, int rowIndex, int columnIndex) {	}
			public String getColumnName(int i) {	return matchColumns[i].label();	}
			public void addTableModelListener(TableModelListener l) { }
			public void removeTableModelListener(TableModelListener l) { }
			public int sortColumn() { return sortColumn; };
			public void sortColumn(int column) { sortColumn = column; };
			public boolean sortDescending() { return sortDescending; };
			public void sortDescending(boolean flag) { sortDescending = flag; };
			public void sort(Comparator comparator) { 
				Collections.sort(items, comparator);
				if (sortDescending) Collections.reverse(items);
				}
    		};
    	
    	return model;
    }    
        
    private void sortOnColumn(int columnIndex) {
    	Comparator comparator = matchColumns[columnIndex].sorter();
    	SortingTableModel model = (SortingTableModel)resultsTable.getModel();
    	if (model.sortColumn() == columnIndex) {
    		model.sortDescending(!model.sortDescending());
    	}
    	model.sortColumn(columnIndex);
    	model.sort(comparator);
    	
    	resultsTable.getSelectionModel().clearSelection();    	
    	resultsTable.repaint();
    }
    
    private void setListDataFrom(Iterator iter) {

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
