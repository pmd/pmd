/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 2:33:24 PM
 */
package net.sourceforge.pmd.jedit;

import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.io.VFSManager;
import org.gjt.sp.jedit.gui.OptionsDialog;
import org.gjt.sp.jedit.gui.DockableWindowManager;
import errorlist.ErrorSource;
import errorlist.DefaultErrorSource;
import javax.swing.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

import net.sourceforge.pmd.*;

public class PMDJEditPlugin extends EditPlugin {

    public static final String NAME = "PMD";
    public static final String PROPERTY_PREFIX = "plugin.net.sourceforge.pmd.jedit.";
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
    
    public void createMenuItems(Vector menuItems) {
        menuItems.addElement(GUIUtilities.loadMenu("pmd-menu"));
    }

    public void createOptionPanes(OptionsDialog optionsDialog) {
        optionsDialog.addOptionPane(new PMDOptionPane());
    }
    // boilerplate JEdit code

    public static void checkDirectory(View view) {
        instance.instanceCheckDirectory(view);
    }

    public void instanceCheckDirectory(View view) {
        errorSource.clear();

        DockableWindowManager wm = view.getDockableWindowManager();
        VFSBrowser browser = (VFSBrowser)wm.getDockable("vfs.browser");
        if(browser == null) {
            JOptionPane.showMessageDialog(jEdit.getFirstView(), "Can't run PMD on a directory unless the file browser is open", "PMD", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PMD pmd = new PMD();
        SelectedRuleSetsMap selectedRuleSets = null;
        try {
            selectedRuleSets = new SelectedRuleSetsMap();
        } catch (RuleSetNotFoundException rsne) {
            // should never happen since rulesets are fetched via getRegisteredRuleSet, nonetheless:
            System.out.println("PMD ERROR: Couldn't find a ruleset");
            rsne.printStackTrace();
            JOptionPane.showMessageDialog(jEdit.getFirstView(), "Unable to find rulesets, halting PMD", "PMD", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RuleContext ctx = new RuleContext();
        ctx.setReport(new Report());
        List files = findFilesInDirectory(browser.getDirectory());
        for (Iterator i = files.iterator(); i.hasNext();) {
            File file = (File)i.next();
            ctx.setReport(new Report());
            ctx.setSourceCodeFilename(file.getAbsolutePath());
            try {
                pmd.processFile(new FileInputStream(file), selectedRuleSets.getSelectedRuleSets(), ctx);
            } catch (FileNotFoundException fnfe) {
                // should never happen, but if it does, carry on to the next file
                System.out.println("PMD ERROR: Unable to open file " + file.getAbsolutePath());
            }
            for (Iterator j = ctx.getReport().iterator(); j.hasNext();) {
                RuleViolation rv = (RuleViolation)j.next();
                errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING,  file.getAbsolutePath(), rv.getLine()-1,0,0,rv.getDescription()));
            }
        }
/*
        if (ctx.getReport().isEmpty()) {
            JOptionPane.showMessageDialog(jEdit.getFirstView(), "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
            errorSource.clear();
        }
*/
    }

    public static void checkDirectoryRecursively(View view) {
        instance.instanceCheckDirectoryRecursively(view);
    }

    public void instanceCheckDirectoryRecursively(View view) {
        DockableWindowManager wm = view.getDockableWindowManager();
        VFSBrowser browser = (VFSBrowser)wm.getDockable("vfs.browser");
        if(browser == null) {
            JOptionPane.showMessageDialog(jEdit.getFirstView(), "Can't run PMD on a directory unless the file browser is open", "PMD", JOptionPane.ERROR_MESSAGE);
            return;
        }
        PMD pmd = new PMD();
        SelectedRuleSetsMap selectedRuleSets = null;
        try {
            selectedRuleSets = new SelectedRuleSetsMap();
        } catch (RuleSetNotFoundException rsne) {
            // should never happen since rulesets are fetched via getRegisteredRuleSet, nonetheless:
            System.out.println("PMD ERROR: Couldn't find a ruleset");
            rsne.printStackTrace();
            JOptionPane.showMessageDialog(jEdit.getFirstView(), "Unable to find rulesets, halting PMD", "PMD", JOptionPane.ERROR_MESSAGE);
            return;
        }

        RuleContext ctx = new RuleContext();
        ctx.setReport(new Report());
        List files = findFilesRecursively(browser.getDirectory());
        for (Iterator i = files.iterator(); i.hasNext();) {
            File file = (File)i.next();
            ctx.setReport(new Report());
            ctx.setSourceCodeFilename(file.getAbsolutePath());
            try {
                pmd.processFile(new FileInputStream(file), selectedRuleSets.getSelectedRuleSets(), ctx);
            } catch (FileNotFoundException fnfe) {
                // should never happen, but if it does, carry on to the next file
                System.out.println("PMD ERROR: Unable to open file " + file.getAbsolutePath());
            }
            for (Iterator j = ctx.getReport().iterator(); j.hasNext();) {
                RuleViolation rv = (RuleViolation)j.next();
                errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING,  file.getAbsolutePath(), rv.getLine()-1,0,0,rv.getDescription()));
            }
        }
    }
		
		

    public static void check(Buffer buffer, View view) {
        instance.instanceCheck(buffer, view);
    }

    public void instanceCheck(Buffer buffer, View view) {
        try {
            errorSource.clear();

            PMD pmd = new PMD();
            SelectedRuleSetsMap selectedRuleSets = new SelectedRuleSetsMap();
            RuleContext ctx = new RuleContext();
            ctx.setReport(new Report());
            ctx.setSourceCodeFilename(buffer.getPath());
            pmd.processFile(new StringReader(view.getTextArea().getText()), selectedRuleSets.getSelectedRuleSets(), ctx);
            if (ctx.getReport().isEmpty()) {
                JOptionPane.showMessageDialog(jEdit.getFirstView(), "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
                errorSource.clear();
            } else {
                String path = buffer.getPath();
                for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                    RuleViolation rv = (RuleViolation)i.next();
                    errorSource.addError(new DefaultErrorSource.DefaultError(errorSource, ErrorSource.WARNING, path, rv.getLine()-1,0,0,rv.getDescription()));
                }
            }
        } catch (RuleSetNotFoundException rsne) {
            rsne.printStackTrace();
        }
    }

    private List findFilesInDirectory(String dir) {
      FilenameFilter filter = new FilenameFilter() {
          public boolean accept(File pathname, String filename) {
              return filename.endsWith("java");
          }
      };
      String[] files = (new File(dir)).list(filter);
      List result = new ArrayList();
      for (int i=0; i<files.length; i++) {
         File sourceFile = new File(dir + System.getProperty("file.separator") + files[i]);
         if (sourceFile.isDirectory()) {
            continue;
         }
         result.add(sourceFile);
      }
      return result;
   }
	 
	 private List findFilesRecursively(String dir) {
      File root = new File(dir);
      List list = new ArrayList();
      scanDirectory(root, list);
      return list;
   }

   private void scanDirectory(File dir, List list) {
      FilenameFilter filter = new FilenameFilter() {
          public boolean accept(File dir, String filename) {
              return filename.endsWith("java") || (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory());
          }
      };
      String[] possibles = dir.list(filter);
      for (int i=0; i<possibles.length; i++) {
         File tmp = new File(dir + System.getProperty("file.separator") + possibles[i]);
         if (tmp.isDirectory()) {
            scanDirectory(tmp, list);
         } else { 
					 list.add(new File(dir + System.getProperty("file.separator") + possibles[i]));
				 }
      }
   }
}
