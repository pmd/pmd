/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 2:33:24 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;

import javax.swing.*;
import java.util.Vector;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.StringBufferInputStream;

import net.sourceforge.pmd.*;
import net.sourceforge.pmd.reports.ReportFactory;

public class PMDJEditPlugin extends EBPlugin {


/*
    private static class PMDJEditOptionPane extends AbstractOptionPane implements OptionPane {
        public PMDJEditOptionPane() {
            super("pmd.general");
        }
        public String getName() {
            return "PMD Option Pane";
        }
    }
*/

    public static final String NAME = "PMD";
    public static final String MENU = "pmd-menu";
    public static final String PROPERTY_PREFIX = "plugin.net.sourceforge.pmd.jedit.";
//    public static final String OPTION_PREFIX = "options.pmd.";

    private static PMDJEditPlugin instance = new PMDJEditPlugin();

    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu(MENU));
    }

/*
    public void createOptionPanes(OptionsDialog dialog) {
        dialog.addOptionPane(new PMDJEditOptionPane());
    }
*/

    public void instanceCheck(View view) {
        //view.getStatus().setMessage("HELLO PMD");
        String text = view.getTextArea().getText();

        PMD pmd = new PMD();

        ReportFactory rf = new ReportFactory();
        RuleContext ctx = new RuleContext();
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet rules = ruleSetFactory.createRuleSet(pmd.getClass().getClassLoader().getResourceAsStream("rulesets/unusedcode.xml"));

        ctx.setReport(rf.createReport("xml"));
        ctx.setSourceCodeFilename("this");
        try {
            // TODO switch to use StringReader once PMD 0.4 gets released
            pmd.processFile(new StringBufferInputStream(text), rules, ctx);

            String msg = "No errors found";
            for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                RuleViolation rv = (RuleViolation)i.next();
                msg += rv.getDescription() + " at line " + rv.getLine() + System.getProperty("line.separator");
            }
            JOptionPane.showMessageDialog(view, msg, "PMD Results", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    public void instanceDisplayPreferencesDialog(View view) {
        //view.getStatus().setMessage("HELLO PMD DIALOG");
    }

    public static void check(View view) {
        instance.instanceCheck(view);
    }

    public static void displayPreferencesDialog(View view) {
        instance.instanceDisplayPreferencesDialog(view);
    }

}
