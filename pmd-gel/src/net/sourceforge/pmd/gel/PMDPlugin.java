package net.sourceforge.pmd.gel;

import java.util.*;
import java.io.*;
import javax.swing.*;
import com.gexperts.gel.*;
import net.sourceforge.pmd.*;

public class PMDPlugin implements GelAction {


    public boolean isActive(Gel p0) {
           System.out.println("isActive");
           return true;
    }

    public void perform(Gel p0) {
        try {
            PMD pmd = new PMD();
            RuleContext ctx = new RuleContext();
            RuleSetFactory rsf = new RuleSetFactory();
            RuleSet ruleSet = new RuleSet();
            ruleSet.addRuleSet(rsf.createRuleSet("rulesets/unusedcode.xml"));
            ruleSet.addRuleSet(rsf.createRuleSet("rulesets/basic.xml"));
            ctx.setReport(new Report());
            ctx.setSourceCodeFilename(p0.getEditor().getFileName());
            StringReader reader = new StringReader(p0.getEditor().getContents());
            pmd.processFile(reader, ruleSet, ctx);
            if (ctx.getReport().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                    RuleViolation rv = (RuleViolation)i.next();
                    JOptionPane.showMessageDialog(null, (rv.getLine()-1) + ":" + rv.getDescription(), "PMD", JOptionPane.INFORMATION_MESSAGE);
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