/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:45:54 PM
 */
package net.sourceforge.pmd.cpd;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import java.util.Calendar;

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

    //private JTextField rootDirectoryField= new JTextField("C:\\data\\datagrabber\\datagrabber\\src\\org\\cougaar\\mlm\\ui\\newtpfdd\\transit\\");
    //private JTextField rootDirectoryField= new JTextField("C:\\j2sdk1.4.0_01\\src\\java\\lang\\reflect");
    private JTextField rootDirectoryField = new JTextField(System.getProperty("user.home"));
    private JTextField minimumLengthField= new JTextField("75");
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

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3,2));
        inputPanel.add(new JLabel("Enter a root src directory"));
        JPanel littlePanel = new JPanel();
        littlePanel.add(rootDirectoryField);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(new BrowseListener());
        littlePanel.add(browseButton);
        inputPanel.add(littlePanel);
        inputPanel.add(new JLabel("Enter a minimum tile size"));
				minimumLengthField.setColumns(4);
        inputPanel.add(minimumLengthField);
        inputPanel.add(recurseCheckbox);

        JPanel buttonsPanel = new JPanel();
        JButton goButton = new JButton("Go");
        goButton.addActionListener(new GoListener());
        buttonsPanel.add(goButton);
        JButton cxButton = new JButton("Cancel");
        cxButton.addActionListener(new CancelListener());
        buttonsPanel.add(cxButton);
        inputPanel.add(buttonsPanel);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BorderLayout());
        JPanel panel1 = new JPanel();
        panel1.add(new JLabel("Tokenizing files"));
        panel1.add(tokenizingFilesBar);
        progressPanel.add(panel1, BorderLayout.NORTH);
        JPanel panel2 = new JPanel();
        panel2.add(new JLabel("Adding tokens"));
        panel2.add(addingTokensBar);
        progressPanel.add(panel2, BorderLayout.CENTER);
        JPanel panel3 = new JPanel();
        panel3.add(new JLabel("Current tile"));
        panel3.add(currentTileField);
				panel3.add(tilesOnThisPassBar);
				panel3.add(timeField);
        progressPanel.add(panel3, BorderLayout.SOUTH);
        progressPanel.setBorder(BorderFactory.createTitledBorder("Progress"));

        JPanel resultsPanel = new JPanel();
        JScrollPane areaScrollPane = new JScrollPane(resultsTextArea);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(600,300));
        resultsPanel.add(areaScrollPane);
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Results"));

        frame.getContentPane().setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(progressPanel, BorderLayout.CENTER);
        frame.getContentPane().add(topPanel, BorderLayout.NORTH);
        frame.getContentPane().add(resultsPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.show();
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
						javax.swing.Timer t = new javax.swing.Timer(1000,
              new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
										  long now = System.currentTimeMillis();
											long elapsedMillis = now-start;
											long elapsedSeconds = elapsedMillis/1000;
											long hours = (long)Math.floor(elapsedSeconds/3600);
											long minutes = (long)Math.floor((elapsedSeconds-(hours*3600))/60);
											long seconds = elapsedSeconds-((minutes*60)+(hours*3600));
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

    public void update(String msg) {
        //System.out.println(msg);
    }

    public void addedFile(int fileCount, File file) {
        tokenizingFilesBar.setMaximum(fileCount);
        tokenizingFilesBar.setValue(tokenizingFilesBar.getValue()+1);
    }

    public void addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID) {
        addingTokensBar.setMaximum(tokenSetCount);
        if (tokenSrcID.indexOf("/") != -1) {
            addingTokensBar.setString(findName(tokenSrcID, "/"));
        } else if (tokenSrcID.indexOf("\\") != -1) {
            addingTokensBar.setString(findName(tokenSrcID, "\\"));
        } else if (tokenSrcID.length() > 10) {
            addingTokensBar.setString(tokenSrcID.substring(tokenSrcID.length()-10));
        } else {
            addingTokensBar.setString(tokenSrcID);
        }
        addingTokensBar.setValue(doneSoFar);
    }

    public void addedNewTile(Tile tile, int tilesSoFar, int totalTiles) {
			addingTokensBar.setValue(addingTokensBar.getMaximum());
			addingTokensBar.setString("");
			if (tile.getImage().length() <= 20) {
				currentTileField.setText(tile.getImage());
			} else {
				currentTileField.setText(tile.getImage().substring(0,20));
			}
			tilesOnThisPassBar.setMaximum(totalTiles);
      tilesOnThisPassBar.setValue(tilesSoFar);
			tilesOnThisPassBar.setString(tilesSoFar + "/" + totalTiles);
		}
		
    private String findName(String name, String slash) {
        int lastSlash = name.lastIndexOf(slash)+1;
        return name.substring(lastSlash);
    }

}

