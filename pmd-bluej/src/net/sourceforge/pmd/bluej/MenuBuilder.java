package net.sourceforge.pmd.bluej;

import bluej.extensions.BClass;
import bluej.extensions.MenuGenerator;
import bluej.extensions.BlueJ;
import bluej.extensions.editor.TextLocation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.TargetJDK1_4;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.io.StringReader;
import java.util.Iterator;

public class MenuBuilder extends MenuGenerator {

    private BClass curClass;
    private Frame frame;

    public MenuBuilder(Frame frame) {
        this.frame = frame;
    }

    public JMenuItem getClassMenuItem(BClass aClass) {
        JMenu jm = new JMenu("PMD");
        jm.add(new JMenuItem(new SimpleAction("Check code")));
        return jm;
    }

    public void notifyPostClassMenu(BClass bc, JMenuItem jmi) {
        curClass = bc;
    }

    // The nested class that instantiates the different (simple) menus.
    class SimpleAction extends AbstractAction {

        public SimpleAction(String menuName) {
            putValue(AbstractAction.NAME, menuName);
        }

        public void actionPerformed(ActionEvent anEvent) {
            try {

                int textLen = curClass.getEditor().getTextLength();
                TextLocation lastLine = curClass.getEditor().getTextLocationFromOffset(textLen);
                String code = curClass.getEditor().getText(new TextLocation(0,0), lastLine);

                RuleSetFactory rsf = new RuleSetFactory();
                RuleSet rs = new RuleSet();
                rs.addRuleSet(rsf.createRuleSet("rulesets/basic.xml"));
                rs.addRuleSet(rsf.createRuleSet("rulesets/unusedcode.xml"));
                RuleContext ctx = new RuleContext();
                ctx.setSourceCodeFilename(curClass.getJavaClass().getName());
                StringReader reader = new StringReader(code);
                new PMD(new TargetJDK1_4()).processFile(reader, rs, ctx);

                StringBuffer msg = new StringBuffer("");
                if (ctx.getReport().size() == 0) {
                    msg.append("No problems found!");
                } else {
                    if (ctx.getReport().size() == 1) {
                        msg.append(" " + ctx.getReport().size() + " problem found");
                    } else {
                        msg.append(" " + ctx.getReport().size() + " problems found");
                    }
                    msg.append(System.getProperty("line.separator"));
                    for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                        RuleViolation rv = (RuleViolation)i.next();
                        msg.append("Line " + rv.getNode().getBeginLine() + ": " + rv.getDescription());
                        msg.append(System.getProperty("line.separator"));
                    }
                }
                JOptionPane.showMessageDialog(frame, msg);
            } catch (Exception exc) {
                exc.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Couldn't run PMD: " + exc.getMessage());
            }
        }
    }
}