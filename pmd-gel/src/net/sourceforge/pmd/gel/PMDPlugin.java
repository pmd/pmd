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
            int x =2;
            int y =2;
            int z =2;
            int ssdsa =2;
            int xasdsa =2;
            PMD pmd = new PMD();
            RuleContext ctx = new RuleContext();
            RuleSetFactory rsf = new RuleSetFactory();
            RuleSet ruleSet = new RuleSet();
            ruleSet.addRuleSet(rsf.createRuleSet("rulesets/unusedcode.xml"));
            ctx.setReport(new Report());
            for (Iterator iter = p0.getProject().getSourcePaths().iterator(); iter.hasNext();) {
                String srcDir = (String)iter.next();
                FileFinder ff = new FileFinder();
                List files = ff.findFilesFrom(srcDir, new JavaFileOrDirectoryFilter(), true);
                for (Iterator fileIter = files.iterator(); fileIter.hasNext();) {
                    File fileName = (File)fileIter.next();
                    ctx.setSourceCodeFilename(fileName.getAbsolutePath());
                    Reader reader = new FileReader(fileName);
                    pmd.processFile(reader, ruleSet, ctx);
                }
           }
           if (ctx.getReport().isEmpty()) {
             JOptionPane.showMessageDialog(null, "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
           } else {
             createProblemFrame(ctx.getReport());
           }
        } catch (Exception rsne) {
             JOptionPane.showMessageDialog(null, "ERROR" + rsne.getMessage());
            rsne.printStackTrace();
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
            list.setForeground(Color.RED);
            list.setBackground(Color.WHITE);
            list.setVisibleRowCount(20);

            JPanel container = new JPanel(new BorderLayout());
            container.add(list, BorderLayout.CENTER);
            return container;
    }
}