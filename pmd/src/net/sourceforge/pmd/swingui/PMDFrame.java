/*
 * User: tom
 * Date: Jul 6, 2002
 * Time: 8:55:44 PM
 */
package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.renderers.XMLRenderer;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Iterator;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;

public class PMDFrame {

    private class GoListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            PMD pmd = new PMD();
            RuleContext ctx = new RuleContext();
            ctx.setSourceCodeFilename(fileNameField.getText());
            ctx.setReport(new Report());
            try {
                pmd.processFile(new FileInputStream(new File(fileNameField.getText())), ruleSet, ctx);
                reportTextArea.setText((new XMLRenderer()).render(ctx.getReport()));
            } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
        }
    }

    private JTextField fileNameField = new JTextField("c:\\data\\pmd\\pmd\\test-data\\Unused1.java");
    private JTextArea reportTextArea = new JTextArea();
    private JFrame frame;
    private RuleSet ruleSet;

    public PMDFrame() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.add(fileNameField);

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("unusedcode.xml");
        JTree ruleTree = new JTree(root);
        RuleSetFactory rf = new RuleSetFactory();
        ruleSet = rf.createRuleSet(getClass().getClassLoader().getResourceAsStream("rulesets/unusedcode.xml"));

        for (Iterator i = ruleSet.getRules().iterator();i.hasNext();) {
            root.add(new DefaultMutableTreeNode(i.next()));
        }

        settingsPanel.add(ruleTree);

        JButton goButton = new JButton("Go");
        goButton.addActionListener(new GoListener());
        settingsPanel.add(goButton);

        JPanel resultsPanel = new JPanel();
        resultsPanel.add(reportTextArea);
        reportTextArea.setSize(new Dimension(300,300));
        reportTextArea.setMinimumSize(new Dimension(300,300));
        reportTextArea.setPreferredSize(new Dimension(300,300));

        frame = new JFrame("PMD");
        frame.getContentPane().add(settingsPanel, BorderLayout.NORTH);
        frame.getContentPane().add(resultsPanel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));
        frame.setVisible(true);
    }

}
