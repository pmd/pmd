package net.sourceforge.pmd.gel;

import java.awt.Color;
import java.awt.BorderLayout;
import java.util.List;
import java.util.Iterator;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import com.gexperts.gel.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.cpd.*;

public class PMDPlugin implements GelAction {

    // GelAction
    public boolean isActive(Gel p0) {
           return true;
    }

    public void perform(Gel p0) {
        try {
            PMD pmd = new PMD();
            RuleContext ctx = new RuleContext();
            RuleSetFactory rsf = new RuleSetFactory();
            RuleSet ruleSet = new RuleSet();
            ruleSet.addRuleSet(rsf.createRuleSet("rulesets/unusedcode.xml,rulesets/basic.xml"));
            ctx.setReport(new Report());
            if (p0.getProject() == null) {
                String code = p0.getEditor().getContents();
                String name = p0.getEditor().getFileName();
                if (name == null) {
                   name = "Unnamed.java";
                }
                ctx.setSourceCodeFilename(name);
                Reader reader = new StringReader(code);
                pmd.processFile(reader, ruleSet, ctx);
            } else {
                for (Iterator iter = p0.getProject().getSourcePaths().iterator(); iter.hasNext();) {
                    String srcDir = (String)iter.next();
                    FileFinder ff = new FileFinder();
                    List files = ff.findFilesFrom(srcDir, new JavaLanguage.JavaFileOrDirectoryFilter(), true);
                    for (Iterator fileIter = files.iterator(); fileIter.hasNext();) {
                        File fileName = (File)fileIter.next();
                        ctx.setSourceCodeFilename(fileName.getAbsolutePath());
                        Reader reader = new FileReader(fileName);
                        pmd.processFile(reader, ruleSet, ctx);
                    }
               }
           }
           if (ctx.getReport().isEmpty()) {
             JOptionPane.showMessageDialog(null, "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
           } else {
             createProblemFrame(ctx.getReport());
           }
        } catch (Exception e) {
             JOptionPane.showMessageDialog(null, "ERROR " + e.getClass().getName() + ":" + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getName() {
           return "PMD";
    }
    // GelAction

    private JFrame createProblemFrame(Report report) {
            JFrame newFrame = new JFrame();
            JDialog dialog = new JDialog(newFrame, report.size() +  " problems found");
            dialog.setContentPane(createProblemListPanel(report));
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.pack();
            dialog.setLocationRelativeTo(newFrame);
            dialog.setVisible(true);
            return newFrame;
    }

    private JPanel createProblemListPanel(Report report) {
            Vector v = new Vector();
             for (Iterator i = report.iterator(); i.hasNext();) {
                 RuleViolation rv = (RuleViolation)i.next();
                 String msg = rv.getFilename() + " - line " + (rv.getLine()-1) + " - " + rv.getDescription();
                 v.add(msg);

              }
            JList list = new JList(v);
            list.setForeground(Color.red);
            list.setBackground(Color.white);
            list.setVisibleRowCount(20);

            JPanel container = new JPanel(new BorderLayout());
            container.add(list, BorderLayout.CENTER);
            return container;
    }
}