package net.sourceforge.pmd.cpd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GUI implements CPDListener {

    public static void main(String[] args) {
        new GUI();
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

    private class CancelListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
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

    private JTextField rootDirectoryField = new JTextField(System.getProperty("user.home")/* + "/tmp/ant"*/);
    private JTextField minimumLengthField = new JTextField("75");
    private JTextField timeField = new JTextField(6);
    private JTextField comparisonsField = new JTextField(8);
    private JProgressBar tokenizingFilesBar = new JProgressBar();
    private JTextArea resultsTextArea = new JTextArea();
    private JCheckBox recurseCheckbox = new JCheckBox("", true);

    private JFrame frame;

    public GUI() {
        frame = new JFrame("PMD Cut and Paste Detector");

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        JMenuItem exitItem = new JMenuItem("Exit");
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
            CPD cpd = new CPD(Integer.parseInt(minimumLengthField.getText()));
            cpd.setCpdListener(this);
            tokenizingFilesBar.setMinimum(0);
            comparisonsField.setText("");
            if (rootDirectoryField.getText().endsWith(".java")) {
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
                    timeField.setText("" + hours + ":" + minutes + ":" + seconds);
                }
            });
            t.start();
            cpd.go();
            t.stop();
            String report = cpd.getReport();
            if (report.length() == 0) {
                JOptionPane.showMessageDialog(frame, "Done; couldn't find any duplicates longer than " + minimumLengthField.getText() + " tokens");
            } else {
                resultsTextArea.setText(report);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Halted due to " + ioe.getClass().getName() + "; " + ioe.getMessage());
        }
    }

    // CPDListener
    public void comparisonCountUpdate(int comparisons) {
        comparisonsField.setText(String.valueOf(comparisons));
    }

    public void addedFile(int fileCount, File file) {
        tokenizingFilesBar.setMaximum(fileCount);
        tokenizingFilesBar.setValue(tokenizingFilesBar.getValue() + 1);
    }
    // CPDListener
}
