/*
 * Created on 30.06.2004
 */
package net.sourceforge.pmd.util.dfagraph;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;

/**
 * @author raik
 */
public class DFAGrapher {

    private DFAGraphRule dfaGraphRule = new DFAGraphRule();
    private JFrame myFrame;

    public void process(String filename) {
        try {
            RuleSet rs = new RuleSet();
            rs.addRule(dfaGraphRule);
            RuleContext ctx = new RuleContext();
            ctx.setSourceCodeFilename(filename);
            new PMD().processFile(new FileReader(filename), rs, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show() {
        myFrame = new JFrame("DFA graph for " + dfaGraphRule.getSrc().getName());
        myFrame.setSize(600, 800);
        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        JScrollPane scrollPane = new JScrollPane(new DFAPanel((ASTMethodDeclaration)dfaGraphRule.getMethods().iterator().next(), dfaGraphRule.getSrc()));
        myFrame.getContentPane().add(scrollPane);
        myFrame.setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java net.sourceforge.pmd.util.dfagraph.DFAGrapher /home/tom/tmp/Foo.java");
            System.exit(1);
        }
        DFAGrapher dfa = new DFAGrapher();
        dfa.process(args[0]);
        dfa.show();
    }
}
