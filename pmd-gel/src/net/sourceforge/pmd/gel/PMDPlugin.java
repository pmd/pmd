package net.sourceforge.pmd.gel;

import java.util.*;
import java.io.*;
import javax.swing.*;
import com.gexperts.gel.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.cpd.*;

public class PMDPlugin implements GelAction {


    public boolean isActive(Gel p0) {
           System.out.println("isActive");
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
             for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                 RuleViolation rv = (RuleViolation)i.next();
                 p0.showMessage("File: " + rv.getFilename() + "\r\nLine: " + (rv.getLine()-1) + "\r\nProblem: " + rv.getDescription());
              }
             }
        } catch (Exception rsne) {
             JOptionPane.showMessageDialog(null, "ERROR" + rsne.getMessage());
            rsne.printStackTrace();
        }
    }

    public String getName() {
           return "PMDPlugin";
    }
}