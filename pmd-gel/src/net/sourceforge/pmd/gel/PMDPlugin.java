package net.sourceforge.pmd.gel;

import java.util.*;
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
             JFrame newFrame = createProblemFrame(ctx.getReport());
             newFrame.show();
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
            JFrame newFrame = new JFrame("Problems found");
            JDialog dialog = new JDialog(newFrame, "Modal dialog", true);
            dialog.setContentPane(createProblemListPanel(report));
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.pack();
            dialog.setLocationRelativeTo(newFrame);
            dialog.setVisible(true);
            return newFrame;
    }

    private JPanel createProblemListPanel(Report report) {
            JPanel container = new JPanel();
            container.add(new JLabel(report.size() + " problems"));
            /*
             for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                 RuleViolation rv = (RuleViolation)i.next();
                 p0.showMessage("File: " + rv.getFilename() + "\r\nLine: " + (rv.getLine()-1) + "\r\nProblem: " + rv.getDescription());
              }
              */
            return container;
    }
}