/*
* User: tom
* Date: Aug 6, 2002
* Time: 2:45:54 PM
 */
package net.sourceforge.pmd.cpd;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import java.awt.BorderLayout;
import java.awt.Dimension;
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
                    addingTokensBar.setValue(0);
                    tokenizingFilesBar.setValue(0);
                    tilesOnThisPassBar.setValue(0);
                    addingTokensBar.setString("");
                    tokenizingFilesBar.setString("");
                    tilesOnThisPassBar.setString("");
                    resultsTextArea.setText("");
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

    private JTextField rootDirectoryField = new JTextField(System.getProperty("user.home"));
    private JTextField minimumLengthField = new JTextField("75");
    private JTextField timeField = new JTextField(6);

    private JProgressBar tokenizingFilesBar = new JProgressBar();
    private JProgressBar addingTokensBar = new JProgressBar();

    private JTextField currentTileField = new JTextField(20);
    private JProgressBar tilesOnThisPassBar = new JProgressBar();

    private JTextArea resultsTextArea = new JTextArea();

    private JCheckBox recurseCheckbox = new JCheckBox("Recurse?", true);

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
        browseButton.addActionListener(new BrowseListener());
        JButton goButton = new JButton("Go");
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
        helper.addLabel("Adding tokens:");
        helper.add(addingTokensBar, 3);
        helper.nextRow();
        helper.addLabel("Current tile:");
        helper.add(currentTileField);
        helper.add(tilesOnThisPassBar);
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
            CPD cpd = new CPD();
            cpd.setListener(this);
            cpd.setMinimumTileSize(Integer.parseInt(minimumLengthField.getText()));
            tilesOnThisPassBar.setMinimum(0);
            tilesOnThisPassBar.setStringPainted(true);
            addingTokensBar.setMinimum(0);
            tokenizingFilesBar.setMinimum(0);
            addingTokensBar.setStringPainted(true);
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
            javax.swing.Timer t = new javax.swing.Timer(1000, new ActionListener() {
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
            currentTileField.setText("");
            CPDRenderer renderer = new TextRenderer();
            String report = renderer.render(cpd);
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

    public boolean update(String msg) {
        //System.out.println(msg);
        return true;
    }

    public boolean addedFile(int fileCount, File file) {
        tokenizingFilesBar.setMaximum(fileCount);
        tokenizingFilesBar.setValue(tokenizingFilesBar.getValue() + 1);
        return true;
    }

    public boolean addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID) {
        addingTokensBar.setMaximum(tokenSetCount);
        if (tokenSrcID.indexOf("/") != -1) {
            addingTokensBar.setString(findName(tokenSrcID, "/"));
        } else if (tokenSrcID.indexOf("\\") != -1) {
            addingTokensBar.setString(findName(tokenSrcID, "\\"));
        } else if (tokenSrcID.length() > 10) {
            addingTokensBar.setString(tokenSrcID.substring(tokenSrcID.length() - 10));
        } else {
            addingTokensBar.setString(tokenSrcID);
        }
        addingTokensBar.setValue(doneSoFar);
        return true;
    }

    public boolean addedNewTile(Tile tile, int tilesSoFar, int totalTiles) {
        addingTokensBar.setValue(addingTokensBar.getMaximum());
        addingTokensBar.setString("");
        if (tile.getImage().length() <= 20) {
            currentTileField.setText(tile.getImage());
        } else {
            currentTileField.setText(tile.getImage().substring(0, 20));
        }
        tilesOnThisPassBar.setMaximum(totalTiles);
        tilesOnThisPassBar.setValue(tilesSoFar);
        tilesOnThisPassBar.setString(tilesSoFar + "/" + totalTiles);
        return true;
    }

    private String findName(String name, String slash) {
        int lastSlash = name.lastIndexOf(slash) + 1;
        return name.substring(lastSlash);
    }

}
