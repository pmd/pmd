/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:45:54 PM
 */
package net.sourceforge.pmd.cpd;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;

public class GUI implements CPDListener {

    public static void main(String[] args) {
        new GUI();
    }

    private class GoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new Thread(new Runnable() {
                public void run() {
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

    private JTextField rootDirectoryField= new JTextField("c:\\data\\pmd\\pmd\\src\\net\\sourceforge\\pmd\\cpd\\");
    //private JTextField rootDirectoryField= new JTextField("c:\\data\\cougaar\\core\\src");
    private JTextField minimumLengthField= new JTextField("50");

    private JProgressBar tokenizingFilesBar = new JProgressBar();
    private JProgressBar addingTokensBar = new JProgressBar();

    private JTextField expandingTileField = new JTextField(50);
    private JCheckBox recurseCheckbox = new JCheckBox("Recurse?", true);
    private JFrame f;
    public GUI() {
        f = new JFrame("PMD Cut and Paste Detector");
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4,2));
        inputPanel.add(new JLabel("Enter a root src directory"));
        inputPanel.add(rootDirectoryField);
        inputPanel.add(new JLabel("Enter a minimum tile size"));
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
        panel3.add(new JLabel("Expanding tile"));
        panel3.add(expandingTileField);
        progressPanel.add(panel3, BorderLayout.SOUTH);

        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(inputPanel, BorderLayout.NORTH);
        f.getContentPane().add(progressPanel, BorderLayout.CENTER);
        f.getContentPane().setSize(600,400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.show();
    }

    private void go() {
        try {
            CPD cpd = new CPD();
            cpd.setListener(this);
            cpd.setMinimumTileSize(Integer.parseInt(minimumLengthField.getText()));
            addingTokensBar.setStringPainted(true);
            if (recurseCheckbox.isSelected()) {
                cpd.addRecursively(rootDirectoryField.getText());
            } else {
                cpd.addAllInDirectory(rootDirectoryField.getText());
            }
            cpd.go();
            Results results = cpd.getResults();
            for (Iterator i = results.getTiles(); i.hasNext();) {
                Tile tile = (Tile)i.next();
                System.out.println("=============================================================");
                System.out.println("A " + cpd.getLineCountFor(tile) + " line (" + tile.getTokenCount() + " tokens) duplication in these files:");
                for (Iterator j = cpd.getResults().getOccurrences(tile); j.hasNext();) {
                    TokenEntry tok = (TokenEntry)j.next();
                    System.out.println(tok.getBeginLine() + "\t" + tok.getTokenSrcID());
                }
                System.out.println(cpd.getImage(tile));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void update(String msg) {
        //System.out.println(msg);
    }

    public void addedFile(int fileCount, File file) {
        tokenizingFilesBar.setMinimum(0);
        tokenizingFilesBar.setMaximum(fileCount);
        tokenizingFilesBar.setValue(tokenizingFilesBar.getValue()+1);
    }

    public void addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID) {
        addingTokensBar.setMinimum(0);
        addingTokensBar.setMaximum(tokenSetCount);
        if (tokenSrcID.indexOf("/") != -1) {
            int lastSlash = tokenSrcID.lastIndexOf("/")+1;
            addingTokensBar.setString(tokenSrcID.substring(lastSlash));
        } else if (tokenSrcID.indexOf("\\") != -1) {
            int lastSlash = tokenSrcID.lastIndexOf("\\")+1;
            addingTokensBar.setString(tokenSrcID.substring(lastSlash));
        } else if (tokenSrcID.length() > 10) {
            addingTokensBar.setString(tokenSrcID.substring(tokenSrcID.length()-10));
        } else {
            addingTokensBar.setString(tokenSrcID);
        }
        addingTokensBar.setValue(doneSoFar);
    }

    public void expandingTile(String tileImage) {
        addingTokensBar.setValue(addingTokensBar.getMaximum());
        addingTokensBar.setString("");
        expandingTileField.setText(tileImage);
    }

}
