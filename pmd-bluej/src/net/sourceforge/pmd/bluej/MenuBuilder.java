package net.sourceforge.pmd.bluej;

import bluej.extensions.BClass;
import bluej.extensions.BObject;
import bluej.extensions.BPackage;
import bluej.extensions.MenuGenerator;
import bluej.extensions.editor.Editor;
import bluej.extensions.editor.TextLocation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.Iterator;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.RuleViolation;

public class MenuBuilder extends MenuGenerator {

    private BClass curClass;

    public JMenuItem getClassMenuItem(BClass aClass) {
        JMenu jm = new JMenu("PMD");
        jm.add(new JMenuItem(new SimpleAction("Check code", "Class menu:")));
        return jm;
    }

    public void notifyPostClassMenu(BClass bc, JMenuItem jmi) {
        curClass = bc;
    }

    // The nested class that instantiates the different (simple) menus.
    class SimpleAction extends AbstractAction {
        private String msgHeader;
        public SimpleAction(String menuName, String msg) {
            putValue(AbstractAction.NAME, menuName);
            msgHeader = msg;
        }
        public void actionPerformed(ActionEvent anEvent) {
            try {

                int textLen = curClass.getEditor().getTextLength();
                TextLocation lastLine = curClass.getEditor().getTextLocationFromOffset(textLen);
                String code = curClass.getEditor().getText(new TextLocation(0,0), lastLine);

                StringBuffer msg = new StringBuffer("");

                RuleSetFactory rsf = new RuleSetFactory();
                RuleSet rs = new RuleSet();
                rs.addRuleSet(rsf.createRuleSet("rulesets/basic.xml"));
                rs.addRuleSet(rsf.createRuleSet("rulesets/unusedcode.xml"));
                RuleContext ctx = new RuleContext();
                ctx.setSourceCodeFilename("[no filename]");
                StringReader reader = new StringReader(code);
                new PMD(new TargetJDK1_4()).processFile(reader, rs, ctx);
                int violations = ctx.getReport().size();

                msg.append(" " + violations + " rule violation(s)");

                msg.append(System.getProperty("line.separator"));
                for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                    RuleViolation rv = (RuleViolation)i.next();
                    msg.append(rv.getDescription());
                    msg.append(System.getProperty("line.separator"));
                }


                JOptionPane.showMessageDialog(null, msgHeader + msg);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }
}