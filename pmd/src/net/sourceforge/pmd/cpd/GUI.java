package net.sourceforge.pmd.cpd;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class GUI implements CPDListener {

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
                    comparisonsField.setText("");
                    timeField.setText("");
                    go();
                }
            }).start();
        }
    }

    private class SaveListener implements ActionListener {
        public void actionPerformed(ActionEvent evt){
            FileDialog fdlg = new FileDialog(GUI.this.frame, "Save", FileDialog.SAVE);
            fdlg.setVisible(true);
            String selected = fdlg.getDirectory() + System.getProperty("file.separator") + fdlg.getFile();
            if (fdlg.getFile() == null) {
                return;
            }
            if(!new File(selected).canWrite()){
                try{
                    PrintWriter pw = new PrintWriter(new FileOutputStream(selected));
                    String report = resultsTextArea.getText();
                    pw.print(report);
                    pw.flush();
                    pw.close();
                    JOptionPane.showMessageDialog(frame, "File saved");
                }catch(IOException e){
                    error("Couldn't save file"+new File(selected).getAbsolutePath(), e);
                }
            }else{
                error("Could not write to file "+new File(selected).getAbsolutePath(), null);
            }
        }

        private void error(String message, Exception e){
            if(e != null){
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

    private JTextField rootDirectoryField = new JTextField(System.getProperty("user.home"));
    private JTextField minimumLengthField = new JTextField("75");
    private JTextField timeField = new JTextField(6);
    private JTextField comparisonsField = new JTextField(8);
    private JProgressBar tokenizingFilesBar = new JProgressBar();
    private JTextArea resultsTextArea = new JTextArea();
    private JCheckBox recurseCheckbox = new JCheckBox("", true);
    private JComboBox languageBox = new JComboBox();

    private JFrame frame;

    public GUI() {
        frame = new JFrame("PMD Cut and Paste Detector");

        timeField.setEditable(false);
        comparisonsField.setEditable(false);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic('s');
        saveItem.addActionListener(new SaveListener());
        fileMenu.add(saveItem);        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(new CancelListener());
        fileMenu.add(exitItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        // first make all the buttons
        JButton browseButton = new JButton("Browse");
        browseButton.setMnemonic('b');
        browseButton.addActionListener(new BrowseListener());
        JButton goButton = new JButton("Go");
        goButton.setMnemonic('g');
        goButton.addActionListener(new GoListener());
        JButton cxButton = new JButton("Cancel");
        cxButton.addActionListener(new CancelListener());

        JPanel settingsPanel = makeSettingsPanel(browseButton, goButton, cxButton);
        JPanel progressPanel = makeProgressPanel();
        JPanel resultsPanel = makeResultsPanel();

        frame.getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(settingsPanel, BorderLayout.NORTH);
        topPanel.add(progressPanel, BorderLayout.CENTER);
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(resultsPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.show();
    }

    private JPanel makeSettingsPanel(JButton browseButton, JButton goButton, JButton cxButton) {
        JPanel settingsPanel = new JPanel();
        GridBagHelper helper = new GridBagHelper(settingsPanel, new double[]{0.2, 0.7, 0.1, 0.1});
        helper.addLabel("Root source directory:");
        helper.add(rootDirectoryField);
        helper.add(browseButton, 2);
        helper.nextRow();
        helper.addLabel("Minimum tile size:");
        minimumLengthField.setColumns(4);
        helper.add(minimumLengthField);
        helper.addLabel("Language:");
        languageBox.addItem("Java");
        languageBox.addItem("C++");
        languageBox.addItem("PHP");
        helper.add(languageBox);
        helper.nextRow();
        helper.addLabel("Also scan subdirectories?");
        helper.add(recurseCheckbox);
        helper.add(goButton);
        helper.add(cxButton);
        helper.nextRow();
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        return settingsPanel;
    }

    private JPanel makeProgressPanel() {
        JPanel progressPanel = new JPanel();
        final double[] weights = {0.0, 0.8, 0.4, 0.2};
        GridBagHelper helper = new GridBagHelper(progressPanel, weights);
        helper.addLabel("Tokenizing files:");
        helper.add(tokenizingFilesBar, 3);
        helper.nextRow();
        helper.addLabel("Comparisons so far:");
        helper.add(comparisonsField);
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
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(600, 300));
        resultsPanel.add(areaScrollPane, BorderLayout.CENTER);
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));
        return resultsPanel;
    }

    private void go() {
        try {
            if (!(new File(rootDirectoryField.getText())).exists()) {
                JOptionPane.showMessageDialog(frame, "Can't read from that root source directory", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Language language = null;
            LanguageFactory lf = new LanguageFactory();
            if (languageBox.getSelectedItem().equals("Java")) {
                language = lf.createLanguage(LanguageFactory.JAVA_KEY);
            } else if (languageBox.getSelectedItem().equals("C++")) {
                language = lf.createLanguage(LanguageFactory.CPP_KEY);
            } else if (languageBox.getSelectedItem().equals("PHP")) {
                language = lf.createLanguage(LanguageFactory.PHP_KEY);
            }

            CPD cpd = new CPD(Integer.parseInt(minimumLengthField.getText()), language);
            cpd.setCpdListener(this);
            tokenizingFilesBar.setMinimum(0);
            comparisonsField.setText("");
            if (rootDirectoryField.getText().endsWith(".class") || rootDirectoryField.getText().endsWith(".php") || rootDirectoryField.getText().endsWith(".java") || rootDirectoryField.getText().endsWith(".cpp") || rootDirectoryField.getText().endsWith(".c")) {
                cpd.add(new File(rootDirectoryField.getText()));
            } else {
                if (recurseCheckbox.isSelected()) {
                    cpd.addRecursively(rootDirectoryField.getText());
                } else {
                    cpd.addAllInDirectory(rootDirectoryField.getText());
                }
            }
            final long start = System.currentTimeMillis();
            Timer t = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    long now = System.currentTimeMillis();
                    long elapsedMillis = now - start;
                    long elapsedSeconds = elapsedMillis / 1000;
                    long hours = (long) Math.floor(elapsedSeconds / 3600);
                    long minutes = (long) Math.floor((elapsedSeconds - (hours * 3600)) / 60);
                    long seconds = elapsedSeconds - ((minutes * 60) + (hours * 3600));
                    timeField.setText("" + munge(String.valueOf(hours)) + ":" + munge(String.valueOf(minutes)) + ":" + munge(String.valueOf(seconds)));
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
            String report = new SimpleRenderer().render(cpd.getMatches());
            if (report.length() == 0) {
                JOptionPane.showMessageDialog(frame, "Done; couldn't find any duplicates longer than " + minimumLengthField.getText() + " tokens");
            } else {
                resultsTextArea.setText(report);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Halted due to " + t.getClass().getName() + "; " + t.getMessage());
        }
    }

    // CPDListener
    public void comparisonCountUpdate(long comparisons) {
        comparisonsField.setText(String.valueOf(comparisons));
    }

    public void addedFile(int fileCount, File file) {
        tokenizingFilesBar.setMaximum(fileCount);
        tokenizingFilesBar.setValue(tokenizingFilesBar.getValue() + 1);
    }
    // CPDListener

    public static void main(String[] args) {
        new GUI();
    }

}
