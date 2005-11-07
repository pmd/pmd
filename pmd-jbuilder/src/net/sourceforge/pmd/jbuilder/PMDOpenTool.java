/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnACTION_PMDCheckt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

package net.sourceforge.pmd.jbuilder;

import com.borland.jbcl.control.MessageDialog;
import com.borland.jbuilder.JBuilderMenu;
import com.borland.jbuilder.node.JBProject;
import com.borland.jbuilder.node.JavaFileNode;
import com.borland.jbuilder.node.PackageNode;
import com.borland.primetime.PrimeTime;
import com.borland.primetime.actions.ActionGroup;
import com.borland.primetime.editor.EditorAction;
import com.borland.primetime.editor.EditorManager;
import com.borland.primetime.editor.EditorPane;
import com.borland.primetime.ide.*;
import com.borland.primetime.node.Node;
import com.borland.primetime.properties.PropertyDialog;
import com.borland.primetime.properties.PropertyManager;
import com.borland.primetime.viewer.TextNodeViewer;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.cpd.*;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.*;


public class PMDOpenTool {

    static MessageCategory msgCat = new MessageCategory("PMD Results");
    static MessageCategory cpdCat = new MessageCategory("CPD Results");
    public static ActionGroup GROUP_MENU_PMD = new ActionGroup("PMD", 'p', true);
    public static ActionGroup GROUP_PROJECT_PMD = new ActionGroup("PMD", 'p', true);
    public static ActionGroup GROUP_PACKAGE_PMD = new ActionGroup("PMD", 'p', true);
    public static ActionGroup GROUP_TOOLBAR_PMD = new ActionGroup("PMD", 'P', true);
    static Font fileNameMsgFont = new Font("Dialog", Font.BOLD, 12);
    static Font stdMsgFont = new Font("Dialog", Font.PLAIN, 12);
/*
    static ImageIcon IMAGE_CHECK_PROJECT;
    static ImageIcon IMAGE_CHECK_SELECTED_PACKAGE;
    static ImageIcon IMAGE_CHECK_ALL_OPEN_FILES;
    static ImageIcon IMAGE_CPD;
    static ImageIcon IMAGE_CHECK_FILE;
    static ImageIcon IMAGE_CHECK_SELECTED_FILE;
    static ImageIcon IMAGE_CONFIGURE_PMD;
    static ImageIcon IMAGE_CPD_SELECTED_PACKAGE;
*/

/*
    static {
        try {

            IMAGE_CHECK_PROJECT = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/checkProject.gif"));
            IMAGE_CHECK_SELECTED_PACKAGE = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/checkSelectedPackage.gif"));
            IMAGE_CHECK_ALL_OPEN_FILES = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/checkSelectedPackage.gif"));
            IMAGE_CPD = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/cpd.gif"));
            IMAGE_CHECK_FILE = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/checkFile.gif"));
            IMAGE_CHECK_SELECTED_FILE = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/checkSelectedFile.gif"));
            IMAGE_CONFIGURE_PMD = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/configurePMD.gif"));
            IMAGE_CPD_SELECTED_PACKAGE = new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/cpdSelectedPackage.gif"));
        } catch (Exception e) {
            MessageDialog md = new MessageDialog(null, "Error Loading PMD Images", e.toString());
            md.show();
        }

    }
*/

    public PMDOpenTool() {}


    /**
     * Required for JBuilder OpenTool support
     *
     * @param majorVersion major version id
     * @param minorVersion minor version id
     */
    public static void initOpenTool(byte majorVersion, byte minorVersion) {
        if (majorVersion == PrimeTime.CURRENT_MAJOR_VERSION) {
            try {
                GROUP_MENU_PMD.add(B_ACTION_PMDCheckCurrentFile);
                GROUP_MENU_PMD.add(B_ACTION_PMDProjectCheck);
                GROUP_MENU_PMD.add(B_ACTION_PMDAllOpenFilesCheck);
                GROUP_MENU_PMD.add(B_ACTION_CPDProjectCheck);
                GROUP_MENU_PMD.add(B_ACTION_PMDConfig);
                JBuilderMenu.GROUP_Tools.add(GROUP_MENU_PMD);
/*
                GROUP_TOOLBAR_PMD.add(B_ACTION_PMDCheckCurrentFile);
                GROUP_TOOLBAR_PMD.add(B_ACTION_PMDProjectCheck);
                GROUP_TOOLBAR_PMD.add(B_ACTION_CPDProjectCheck);
                Browser.addToolBarGroup(GROUP_TOOLBAR_PMD);
*/
                registerWithContentManager();
                registerWithProjectView();

                /**
                 * Unfortunately for now, the order in which these are instantiated is important
                 * The ActiveRuleSetPropertyGroup relies upon the ImportedRuleSetPropertyGroup already
                 * being construted before it builds itself.  It's ugly but it works.
                 */
                ImportedRuleSetPropertyGroup ipropGrp = new ImportedRuleSetPropertyGroup();
                ActiveRuleSetPropertyGroup apropGrp = new ActiveRuleSetPropertyGroup();
                ConfigureRuleSetPropertyGroup cpropGrp = new ConfigureRuleSetPropertyGroup();
                AcceleratorPropertyGroup accpropGrp = new AcceleratorPropertyGroup();
                CPDPropertyGroup cpdPropGrp = new CPDPropertyGroup();

                //register the Keymap shortcuts if they are enabled
                if (minorVersion > 1) {  //accelerators don't seem to work in OpenTools 4.1
                    if (AcceleratorPropertyGroup.PROP_KEYS_ENABLED.getBoolean()) {
                        registerShortCuts();
                    }
                }

                PropertyManager.registerPropertyGroup(apropGrp);
                PropertyManager.registerPropertyGroup(cpropGrp);
                PropertyManager.registerPropertyGroup(ipropGrp);
                if (minorVersion > 1) { //accelerators don't seem to work in OpenTools 4.1
                    PropertyManager.registerPropertyGroup(accpropGrp);
                }
                PropertyManager.registerPropertyGroup(cpdPropGrp);
            } catch (Exception e) {
                MessageDialog md = new MessageDialog(null, "PMDOpenTool Loading Error", e.toString());
                md.show();
            }
        }
    }


    static void clearShortCuts() {
        EditorManager.getKeymap().removeKeyStrokeBinding(KeyStroke.getKeyStroke(net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKFILE_KEY.getInteger(),
                net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKFILE_MOD.getInteger()));
        EditorManager.getKeymap().removeKeyStrokeBinding(KeyStroke.getKeyStroke(net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKPROJ_KEY.getInteger(),
                net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKPROJ_MOD.getInteger()));
    }

    static void registerShortCuts() {

        EditorManager.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKFILE_KEY.getInteger(),
                net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKFILE_MOD.getInteger()),
                E_ACTION_PMDCheckCurrentFile);

        EditorManager.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKPROJ_KEY.getInteger(),
                net.sourceforge.pmd.jbuilder.AcceleratorPropertyGroup.PROP_CHECKPROJ_MOD.getInteger()),
                E_ACTION_PMDCheckProject);
    }

    /**
     * Registers an "PMD Checker" action with the ContentManager (Tabs)
     * The action will not be visible if multiple nodes are selected
     */
    private static void registerWithContentManager() {
        ContextActionProvider cap = new ContextActionProvider() {
            public Action getContextAction(Browser browser, Node[] nodes) {
                return B_ACTION_PMDCheckCurrentFile;
            }
        };
        ContentManager.registerContextActionProvider(cap);
    }

    private static void registerWithProjectView() {
        GROUP_PROJECT_PMD.add(B_ACTION_PMDProjectCheck);
        GROUP_PROJECT_PMD.add(B_ACTION_PMDAllOpenFilesCheck);
        GROUP_PROJECT_PMD.add(B_ACTION_CPDProjectCheck);
        GROUP_PACKAGE_PMD.add(B_ACTION_PMDPackageCheck);
        GROUP_PACKAGE_PMD.add(B_ACTION_CPDPackageCheck);

        ContextActionProvider cap1 = new ContextActionProvider() {
            public Action getContextAction(Browser browser, Node[] nodes) {
                Node node = browser.getProjectView().getSelectedNode();
                if (node instanceof JBProject) {  //used to check across an entire project
                    return GROUP_PROJECT_PMD;
                } else if (node instanceof PackageNode) {   //used to check against a single package
                    return GROUP_PACKAGE_PMD;
                } else if (node instanceof JavaFileNode) {
                    return B_ACTION_PMDCheckSelectedFile;
                }
                return null;
            }
        };

        ProjectView.registerContextActionProvider(cap1);
    }

    /**
     * Create PMD Rule Sets based upon the configuration settings
     *
     * @return A Ruleset and any embedded rulesets
     */
    private static RuleSet constructRuleSets() {
        RuleSet masterRuleSet = new RuleSet();
        for (Iterator iter = net.sourceforge.pmd.jbuilder.ActiveRuleSetPropertyGroup.currentInstance.ruleSets.values().iterator(); iter.hasNext();) {
            net.sourceforge.pmd.jbuilder.RuleSetProperty rsp = (net.sourceforge.pmd.jbuilder.RuleSetProperty) iter.next();
            if (Boolean.valueOf(rsp.getGlobalProperty().getValue()).booleanValue()) {
                masterRuleSet.addRuleSet(rsp.getActiveRuleSet());
            }
        }
        return masterRuleSet;
    }

    /**
     * Run the PMD against some code
     *
     * @param text code to check
     * @return PMD Report object
     */
    public static Report instanceCheck(String text, RuleSet rules) {
        PMD pmd = new PMD();
        RuleContext ctx = new RuleContext();
        if (rules == null) {
            rules = constructRuleSets();
        }
        if (rules == null)
            return new Report();
        ctx.setReport(new Report());
        ctx.setSourceCodeFilename("this");
        try {
            pmd.processFile(new StringReader(text), rules, ctx);
            return ctx.getReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //create EditorAction for performing a PMD Check
    public static EditorAction E_ACTION_PMDCheckCurrentFile =
            new EditorAction("Check with PMD") {
                public void actionPerformed(ActionEvent e) {
                    pmdCheck();
                }
            };

    //create EditorAction for performing a PMD Check on a project
    public static EditorAction E_ACTION_PMDCheckProject =
            new EditorAction("Check with PMD") {
                public void actionPerformed(ActionEvent e) {
                    pmdCheckProject();
                }
            };

    //create the Menu action item for initiating the PMD check
    public static BrowserAction B_ACTION_PMDCheckCurrentFile =
            // A new action with short menu string, mnemonic, and long menu string
            new BrowserAction("PMD Check File", 'P', "Check with PMD"/*, IMAGE_CHECK_FILE*/) {
                // The function called when the menu is selected
                public void actionPerformed(Browser browser) {
                    pmdCheck();
                }
            };

    //create the Menu action item for initiating the PMD check
    public static BrowserAction B_ACTION_PMDCheckSelectedFile =
            // A new action with short menu string, mnemonic, and long menu string
            new BrowserAction("PMD Check File", 'P', "Check with PMD"/*, IMAGE_CHECK_SELECTED_FILE*/) {
                // The function called when the menu is selected
                public void actionPerformed(Browser browser) {
                    try {
                        browser.setActiveNode(browser.getProjectView().getSelectedNode(), true);
                        pmdCheck();
                    } catch (Exception e) {
                    }
                }
            };

    //Create the menu action item for configuring PMD
    public static BrowserAction B_ACTION_PMDConfig = new BrowserAction("Configure PMD",
            'C', "Configure the PMD Settings"/*, IMAGE_CONFIGURE_PMD*/) {
        public void actionPerformed(Browser browser) {
            PropertyManager.showPropertyDialog(browser, "PMD Options", Constants.RULESETS_TOPIC,
                    PropertyDialog.getLastSelectedPage());
        }
    };

    //create the project menu action for running a PMD check against all the java files within the active project
    public static BrowserAction B_ACTION_PMDProjectCheck = new BrowserAction("PMD Check Project", 'P', "Check all the java files in the project"/*, IMAGE_CHECK_PROJECT*/) {
        public void actionPerformed(Browser browser) {
            pmdCheckProject();
        }
    };

    //create the project menu action for running a PMD check against all the java files within the active project
    public static BrowserAction B_ACTION_PMDPackageCheck = new BrowserAction("PMD Check Package", 'P', "Check all the java files in the selected package"/*, IMAGE_CHECK_SELECTED_PACKAGE*/) {
        public void actionPerformed(Browser browser) {
            browser.waitMessage("PMD Status", "Please wait while PMD checks the files in this package.");
            Browser.getActiveBrowser().getMessageView().clearMessages(null);
            Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);
            RuleSet rules = constructRuleSets();
            PackageNode node = (PackageNode) browser.getProjectView().getSelectedNode();
            pmdCheckPackage(node, rules);
            browser.clearWaitMessages();
        }
    };

    //create the project menu action for running a PMD check against all the java files within the active project
    public static BrowserAction B_ACTION_PMDAllOpenFilesCheck = new BrowserAction("PMD Check All Open Files", 'P', "Check all the open java filese"/*, IMAGE_CHECK_ALL_OPEN_FILES*/) {
        public void actionPerformed(Browser browser) {
            browser.waitMessage("PMD Status", "Please wait while PMD checks all open files.");
            RuleSet rules = constructRuleSets();
            Node[] nodes = Browser.getAllOpenNodes(Browser.getActiveBrowser().getActiveProject());
            pmdCheckAllOpenFiles(nodes, rules);
            browser.clearWaitMessages();
        }
    };

    //create the project menu action for running a PMD check against all the java files within the active project
    public static BrowserAction B_ACTION_CPDPackageCheck = new BrowserAction("CPD Check Package", 'P', "Check all the java files in the selected package"/*, IMAGE_CPD_SELECTED_PACKAGE*/) {
        public void actionPerformed(final Browser browser) {
            Runnable r = new Runnable() {
                public void run() {
                    runCPDOnPackage((PackageNode) browser.getProjectView().getSelectedNode());
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    };


    //create the project menu action for running a PMD check against all the java files within the active project
    public static BrowserAction B_ACTION_CPDProjectCheck = new BrowserAction("CPD Check Project", 'P', "Run CPD on all the java files in the project"/*, IMAGE_CPD*/) {
        public void actionPerformed(Browser browser) {
            Runnable r = new Runnable() {
                public void run() {
                    runCPDOnPackage(null);
                }
            };
            Thread t = new Thread(r);
            t.start();
        }
    };

    static void checkCode(String srcCode, JavaFileNode node, RuleSet rules) {
        try {
            Report rpt = instanceCheck(srcCode, rules);
            if (rpt == null) {
                Message msg = new Message("Error Processing File");
                msg.setFont(stdMsgFont);
                Browser.getActiveBrowser().getMessageView().addMessage(msgCat, msg, false);
            } else {
                for (Iterator i = rpt.iterator(); i.hasNext();) {
                    addPMDWarningMessage((RuleViolation) i.next(), node);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void addPMDWarningMessage(RuleViolation rv, JavaFileNode node) {
        PMDMessage pmdMsg = new PMDMessage(node.getDisplayName() + ": " + rv.getRule().getName() + ": " + rv.getDescription() + " at line " + rv.getNode().getBeginLine(), rv.getNode().getBeginLine(), node);
        pmdMsg.setForeground(Color.red);
        pmdMsg.setFont(stdMsgFont);
        Browser.getActiveBrowser().getMessageView().addMessage(msgCat, pmdMsg, false);
    }

    private static void pmdCheck() {
        Browser.getActiveBrowser().getMessageView().clearMessages(null);
        Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);
        Node node = Browser.getActiveBrowser().getActiveNode();
        if (node instanceof JavaFileNode) {
            TextNodeViewer viewer = (TextNodeViewer) Browser.getActiveBrowser().getViewerOfType(node, TextNodeViewer.class);
            if (viewer != null) {
                Document doc = viewer.getEditor().getDocument();
                try {
                    checkCode(doc.getText(0, doc.getLength()), (JavaFileNode) node, null);
                } catch (Exception e) {
                    Browser.getActiveBrowser().getMessageView().addMessage(msgCat, "Error: " + e.toString());
                    e.printStackTrace();
                }
            } else {
                Browser.getActiveBrowser().getMessageView().addMessage(msgCat, "No active Browser.");
            }
        }
    }

    private static void pmdCheckPackage(PackageNode packageNode, RuleSet rules) {
        Node[] fileNodes = packageNode.getDisplayChildren();
        for (int j = 0; j < fileNodes.length; j++) {
            if (fileNodes[j] instanceof JavaFileNode) {
                JavaFileNode javaNode = (JavaFileNode) fileNodes[j];
                try {
                    Browser.getActiveBrowser().waitMessage("PMD Status", "PMD is checking " + javaNode.getDisplayName());
                    StringBuffer code = loadCodeToString(javaNode);
                    Report rpt = instanceCheck(code.toString(), rules);
                    if (rpt == null) {
                        Message msg = new Message("Error Processing File");
                        msg.setFont(stdMsgFont);
                        Browser.getActiveBrowser().getMessageView().addMessage(msgCat, msg, false);
                    } else {
                        for (Iterator i = rpt.iterator(); i.hasNext();) {
                            addPMDWarningMessage((RuleViolation) i.next(), javaNode);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (fileNodes[j] instanceof PackageNode) {
                pmdCheckPackage((PackageNode) fileNodes[j], rules);  //recursive call
            }
        }
    }

    private static void pmdCheckAllOpenFiles(Node[] candidates, RuleSet rules) {
        Browser.getActiveBrowser().getMessageView().clearMessages(null);
        Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);
        for (int i = 0; i < candidates.length; i++) {
            if (candidates[i] instanceof JavaFileNode) {
                JavaFileNode javaNode = (JavaFileNode) candidates[i];
                try {
                    StringBuffer code = loadCodeToString(javaNode);
                    Report rpt = instanceCheck(code.toString(), rules);
                    if (rpt == null) {
                        Message msg = new Message("Error Processing File");
                        msg.setFont(stdMsgFont);
                        Browser.getActiveBrowser().getMessageView().addMessage(msgCat, msg, false);
                    } else {
                        for (Iterator j = rpt.iterator(); j.hasNext();) {
                            addPMDWarningMessage((RuleViolation)j.next(), javaNode);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        Browser.getActiveBrowser().clearWaitMessages();
    }

    private static StringBuffer loadCodeToString(JavaFileNode javaNode) throws IOException {
        StringBuffer code = new StringBuffer();
        byte[] buffer = new byte[1024];
        InputStream is = javaNode.getInputStream();
        int charCount;
        while ((charCount = is.read(buffer)) != -1) {
            code.append(new String(buffer, 0, charCount));
        }
        return code;
    }

    private static void pmdCheckProject() {
        Browser.getActiveBrowser().waitMessage("PMD Status", "Please wait while PMD checks the files in your project.");
        Node[] nodes = Browser.getActiveBrowser().getActiveProject().getDisplayChildren();
        Browser.getActiveBrowser().getMessageView().clearMessages(null);
        Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);
        RuleSet rules = constructRuleSets();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof PackageNode) {
                PackageNode node = (PackageNode) nodes[i];
                if (node.getName() != null && !node.getName().trim().equals("")) {  //if there is no name then this is probably the <Project Source> package - so ignore it so we don't get duplicates
                    pmdCheckPackage(node, rules);
                }
            }
        }
        Browser.getActiveBrowser().clearWaitMessages();
    }

    private static void collectFiles(PackageNode packageNode, Set files) {
        Node[] fileNodes = packageNode.getDisplayChildren();
        for (int j = 0; j < fileNodes.length; j++) {
            if (fileNodes[j] instanceof JavaFileNode) {
                try {
                    files.add(new File(fileNodes[j].getLongDisplayName()));
                } catch (Exception e) {
                }
            } else if (fileNodes[j] instanceof PackageNode) {
                collectFiles((PackageNode) fileNodes[j], files);   //recursive call
            }
        }
    }

    private static void runCPDOnPackage(PackageNode startingNode) {
        try {
            Browser.getActiveBrowser().getMessageView().clearMessages(cpdCat);      //clear the message window
            CPD cpd = new CPD(CPDPropertyGroup.PROP_MIN_TOKEN_COUNT.getInteger(), new LanguageFactory().createLanguage(LanguageFactory.JAVA_KEY));
            CPDDialog cpdd = new CPDDialog(cpd);

            Set files = new HashSet();
            if (startingNode != null) {
                collectFiles(startingNode, files);
            } else {  //otherwise, traverse all the nodes looking for package nodes
                Node[] nodes = Browser.getActiveBrowser().getActiveProject().getDisplayChildren();
                for (int i = 0; i < nodes.length; i++) {
                    if (nodes[i] instanceof PackageNode) {
                        String packageName = ((PackageNode) nodes[i]).getName();
                        if (packageName != null && !packageName.trim().equals("")) {  //if there is no name then this is probably the <Project Source> package - so ignore it so we don't get duplicates
                            collectFiles((PackageNode)nodes[i], files);
                        }
                    }
                }
            }

            for (Iterator i = files.iterator(); i.hasNext();) {
                cpd.add((File)i.next());
            }

            cpd.go();

            if (cpdd.wasCancelled()) {  //if the dialog was cancelled by the user then let's get out of here
                cpdd.close();
                return;
            }


            SortedSet matches = new TreeSet(new MatchLengthComparator());
            for (Iterator i = cpd.getMatches(); i.hasNext();) {
                matches.add((Match) i.next());
            }

            for (Iterator i = matches.iterator(); i.hasNext();) {
                Match m = (Match) i.next();
                CPDMessage msg = CPDMessage.createMessage(getFileName(m.getFirstMark().getTokenSrcID()) + " contains a " + m.getLineCount() + " line block of duplicated code", m.getSourceCodeSlice());
                for (Iterator iter2 = m.iterator(); iter2.hasNext();) {
                    TokenEntry mark = (TokenEntry) iter2.next();
                    msg.addChildMessage(mark.getBeginLine(), m.getLineCount(), mark.getTokenSrcID());
                }
                Browser.getActiveBrowser().getMessageView().addMessage(cpdCat, msg, false);
            }

            cpdd.close();
        } catch (Exception e) {
            e.printStackTrace();
            Browser.getActiveBrowser().getMessageView().addMessage(cpdCat, new Message(e.toString()), false);
        }
    }

    public static String getFileName(String in) {
        String sepChar = System.getProperty("file.separator");
        int last = in.lastIndexOf(sepChar.charAt(0));
        if (last == -1) {
            return in;
        }
        return in.substring(last+1);
    }
}
