/*
 * Created on 30.06.2004
 */
package net.sourceforge.pmd.util.dfagraph;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * @author raik
 */
public class DFAGrapher {

    private JFrame myFrame;

    public DFAGrapher(SimpleNode node, SourceFile src) {
        myFrame = new JFrame("DFA graph for " + src);
        myFrame.setSize(600, 800);
        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JScrollPane scrollPane = new JScrollPane(new DFAPanel(node, src));
        myFrame.getContentPane().add(scrollPane);
        myFrame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java net.sourceforge.pmd.util.dfagraph.DFAGrapher /home/tom/tmp/Foo.java");
            System.exit(1);
        }
        try {
            RuleSet rs = new RuleSet();
            rs.addRule(new DFAGraphRule());
            RuleContext ctx = new RuleContext();
            ctx.setSourceCodeFilename(args[0]);
            new PMD().processFile(new FileReader(args[0]), rs, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
