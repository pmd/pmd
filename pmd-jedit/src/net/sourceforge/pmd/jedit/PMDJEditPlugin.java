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
import java.io.StringReader;

import net.sourceforge.pmd.*;

public class PMDJEditPlugin extends EBPlugin {

    public static final String NAME = "PMD";
    public static final String MENU = "pmd-menu";
    public static final String PROPERTY_PREFIX = "plugin.net.sourceforge.pmd.jedit.";
    public static final String OPTION_PREFIX = "options.pmd.";
    public static final String OPTION_RULESETS_PREFIX = "options.pmd.rulesets.";

    private static PMDJEditPlugin instance = new PMDJEditPlugin();

    // boilerplate JEdit code
    public static void check(View view) {
        instance.instanceCheck(view);
    }

    public static void displayPreferencesDialog(View view) {
        instance.instanceDisplayPreferencesDialog(view);
    }

    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu(MENU));
    }
    // boilerplate JEdit code

    public void instanceCheck(View view) {
        PMD pmd = new PMD();
        RuleContext ctx = new RuleContext();
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        SelectedRuleSetsMap selectedRuleSets = new SelectedRuleSetsMap();
        RuleSet rules = new RuleSet();
        for (Iterator i = selectedRuleSets.getSelectedRuleSetFileNames(); i.hasNext();) {
            rules.addRuleSet(ruleSetFactory.createRuleSet(pmd.getClass().getClassLoader().getResourceAsStream((String)i.next())));
        }

        ctx.setReport(new Report());
        ctx.setSourceCodeFilename("this");
        try {
            pmd.processFile(new StringReader(view.getTextArea().getText()), rules, ctx);

            // TODO put this in a list box or something
            StringBuffer msg = new StringBuffer();
            for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                RuleViolation rv = (RuleViolation)i.next();
                msg.append(rv.getDescription() + System.getProperty("line.separator"));
            }
            if (msg.length() == 0) {
                msg.append("No errors found");
            }
            JOptionPane.showMessageDialog(view, msg.toString(), "PMD Results", JOptionPane.INFORMATION_MESSAGE);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
    }

    public void instanceDisplayPreferencesDialog(View view) {
        PMDOptionPane options = new PMDOptionPane();
    }
}
