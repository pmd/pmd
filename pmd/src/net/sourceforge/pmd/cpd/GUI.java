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
import java.util.Iterator;

public class GUI implements CPDListener {

    public static void main(String[] args) {
        new GUI();
    }

    private class GoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            go();
        }
    }

    private JTextField rootDirectoryField= new JTextField("c:\\data\\pmd\\pmd\\src\\net\\sourceforge\\pmd\\cpd\\");
    private JTextField minimumLengthField= new JTextField("50");
    private JCheckBox recurseCheckbox = new JCheckBox("Recurse?", true);

    public GUI() {
        JFrame f = new JFrame("PMD Cut and Paste Detector");
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4,2));
        inputPanel.add(new JLabel("Enter a root src directory"));
        inputPanel.add(rootDirectoryField);
        inputPanel.add(new JLabel("Enter a minimum tile size"));
        inputPanel.add(minimumLengthField);
        inputPanel.add(recurseCheckbox);
        JButton goButton = new JButton("Go");
        goButton.addActionListener(new GoListener());
        inputPanel.add(goButton);

        f.getContentPane().add(inputPanel);
        f.getContentPane().setSize(600,400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.show();

    }

    private void go() {
        try {
            CPD cpd = new CPD();
            if (recurseCheckbox.isSelected()) {
                cpd.addRecursively(rootDirectoryField.getText());
            } else {
                cpd.addAllInDirectory(rootDirectoryField.getText());
            }
            cpd.setListener(this);
            cpd.setMinimumTileSize(Integer.parseInt(minimumLengthField.getText()));
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
        System.out.println(msg);
    }
}
