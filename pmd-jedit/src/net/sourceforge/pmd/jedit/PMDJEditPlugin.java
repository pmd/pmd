/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 2:33:24 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.OptionsDialog;
import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;
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

    private static PMDJEditPlugin instance;
    
    static {
	    instance = new PMDJEditPlugin();
	    instance.start();
    }
    
    private DefaultErrorSource errorSource;
    
    // boilerplate JEdit code
    public void start() {
	    errorSource = new DefaultErrorSource(NAME);
	    ErrorSource.registerErrorSource(errorSource);
    }
    
    public static void check(Buffer buffer, View view) {
        instance.instanceCheck(buffer, view);
    }

    public static void displayPreferencesDialog(View view) {
        instance.instanceDisplayPreferencesDialog(view);
    }

    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu(MENU));
    }
    // boilerplate JEdit code

    public void instanceCheck(Buffer buffer, View view) {
        try {
            errorSource.clear();
            RuleContext ctx = new RuleContext();
            RuleSetFactory ruleSetFactory = new RuleSetFactory();
            SelectedRuleSetsMap selectedRuleSets = new SelectedRuleSetsMap();
            RuleSet rules = new RuleSet();
            PMD pmd = new PMD();
            for (Iterator i = selectedRuleSets.getSelectedRuleSets(); i.hasNext();) {
                rules.addRuleSet((RuleSet)i.next());
            }
            ctx.setReport(new Report());
            ctx.setSourceCodeFilename(buffer.getPath());
            pmd.processFile(new StringReader(view.getTextArea().getText()), rules, ctx);
            for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                RuleViolation rv = (RuleViolation)i.next();
                String path = buffer.getPath();
                DefaultErrorSource.DefaultError err = new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING, path, rv.getLine()-1,0,0,rv.getDescription());
                errorSource.addError(err);
            }
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }
    }

    public void instanceDisplayPreferencesDialog(View view) {
        new PMDOptionPane();
    }
}
