/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnACTION_PMDCheckt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

package  net.sourceforge.pmd.jbuilder;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import com.borland.jbuilder.*;
import com.borland.jbuilder.node.*;
import com.borland.primetime.*;
import com.borland.primetime.actions.*;
import com.borland.primetime.editor.*;
import com.borland.primetime.ide.*;
import com.borland.primetime.node.*;
import com.borland.primetime.properties.*;
import com.borland.primetime.viewer.*;
import net.sourceforge.pmd.*;



public class PMDOpenTool {
    static MessageCategory msgCat = new MessageCategory("PMD Results");
    public static ActionGroup GROUP_PMD = new ActionGroup("PMD", 'p', true);
    static Font fileNameMsgFont = new Font("Dialog", Font.BOLD, 12);
    static Font stdMsgFont = new Font("Dialog", Font.PLAIN, 12);


    /**
     * Default constructor
     */
    public PMDOpenTool () {
    }

    /**
     * Required for JBuilder OpenTool support
     * @param majorVersion major version id
     * @param minorVersion minor version id
     */
    public static void initOpenTool (byte majorVersion, byte minorVersion) {
        if (majorVersion == PrimeTime.CURRENT_MAJOR_VERSION) {
            GROUP_PMD.add(ACTION_PMDCheck);
            GROUP_PMD.add(ACTION_PMDConfig);
            JBuilderMenu.GROUP_Tools.add(GROUP_PMD);
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

            PropertyManager.registerPropertyGroup(apropGrp);
            PropertyManager.registerPropertyGroup(cpropGrp);
            PropertyManager.registerPropertyGroup(ipropGrp);

        }
    }

    /**
     * Registers an "PMD Checker" action with the ContentManager (Tabs)
     * The action will not be visible if multiple nodes are selected
     */
    private static void registerWithContentManager () {
        ContextActionProvider cap = new ContextActionProvider() {

            public Action getContextAction (Browser browser, Node[] nodes) {
                return  ACTION_PMDCheck;
            }
        };
        ContentManager.registerContextActionProvider(cap);
    }

    private static void registerWithProjectView() {
        ContextActionProvider cap = new ContextActionProvider() {
            public Action getContextAction (Browser browser, Node[] nodes) {
                //Browser.getActiveBrowser().getMessageView().addMessage(msgCat, browser.getProjectView().getSelectedNode().toString());
                Node node = browser.getProjectView().getSelectedNode();
                if (node instanceof JBProject || node instanceof PackageNode)
                    return  ACTION_PMDProjectCheck;
                return null;
            }
        };
        ProjectView.registerContextActionProvider(cap);
    }

    /**
     * Create PMD Rule Sets based upon the configuration settings
     * @param ruleSetFactory PMD RuleSetFactory
     * @param pmd PMD object
     * @return A Ruleset and any embedded rulesets
     */
    private static RuleSet constructRuleSets (RuleSetFactory ruleSetFactory,
            PMD pmd) {
        RuleSet masterRuleSet = null;
        for (Iterator iter = ActiveRuleSetPropertyGroup.currentInstance.ruleSets.values().iterator(); iter.hasNext(); ) {
            RuleSetProperty rsp = (RuleSetProperty)iter.next();
            if (Boolean.valueOf(rsp.getGlobalProperty().getValue()).booleanValue()) {
                RuleSet rules = rsp.getActiveRuleSet();
                if (masterRuleSet == null) {
                    masterRuleSet = rules;
                }
                else {
                    masterRuleSet.addRuleSet(rules);
                }
            }
        }
        return  masterRuleSet;
    }

    /**
     * Run the PMD against some code
     * @param text code to check
     * @return PMD Report object
     */
    public static Report instanceCheck (String text) {
        PMD pmd = new PMD();

        RuleContext ctx = new RuleContext();
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        RuleSet rules = constructRuleSets(ruleSetFactory, pmd);
        if (rules == null)
            return  new Report();
        ctx.setReport(new Report());
        ctx.setSourceCodeFilename("this");
        try {
            // TODO switch to use StringReader once PMD 0.4 gets released
            pmd.processFile(new StringReader(text), rules, ctx);
            return  ctx.getReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    //create the Menu action item for initiating the PMD check
    public static BrowserAction ACTION_PMDCheck =
            // A new action with short menu string, mnemonic, and long menu string
    new BrowserAction("PMD Checker", 'P', "Displays PMD statistics about a Java File") {

        // The function called when the menu is selected
        public void actionPerformed (Browser browser) {
            Node node = Browser.getActiveBrowser().getActiveNode();
            if (node instanceof JavaFileNode) {
                Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);      //clear the message window
                TextNodeViewer viewer = (TextNodeViewer)Browser.getActiveBrowser().getViewerOfType(node,
                        TextNodeViewer.class);
                if (viewer != null) {
                    Document doc = viewer.getEditor().getDocument();
                    try {
                        checkCode(doc.getText(0, doc.getLength()), (JavaFileNode)node);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    };

    static void checkCode(String srcCode, JavaFileNode node) {
        try {
            Report rpt = instanceCheck(srcCode);

            if (rpt == null) {
                Message msg = new Message("Error Processing File");
                msg.setFont(stdMsgFont);
                Browser.getActiveBrowser().getMessageView().addMessage(msgCat,
                        msg);
            }
            else if (rpt.size() == 0) {
                Message msg = new Message("No violations detected.");
                msg.setFont(stdMsgFont);
                Browser.getActiveBrowser().getMessageView().addMessage(msgCat,
                        msg);
            }
            else {
                for (Iterator i = rpt.iterator(); i.hasNext();) {
                    RuleViolation rv = (RuleViolation)i.next();
                    PMDMessage pmdMsg = new PMDMessage(rv.getRule().getName() + ": " + rv.getDescription()
                            + " at line " + rv.getLine(), rv.getLine(),
                            node);
                    pmdMsg.setForeground(Color.red);
                    pmdMsg.setFont(stdMsgFont);
                    Browser.getActiveBrowser().getMessageView().addMessage(msgCat,
                            pmdMsg);                //add the result message
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Create the menu action item for configuring PMD
    public static BrowserAction ACTION_PMDConfig = new BrowserAction("Configure PMD",
            'C', "Configure the PMD Settings") {
        public void actionPerformed (Browser browser) {
            PropertyManager.showPropertyDialog(browser, "PMD Options", Constants.RULESETS_TOPIC,
                    PropertyDialog.getLastSelectedPage());
        }
    };

            //create the project menu action for running a PMD check against all the java files within the active project
            public static BrowserAction ACTION_PMDProjectCheck = new BrowserAction ("PMD Check Project", 'P', "Check all the java files in the project") {
                public void actionPerformed(Browser browser) {
                    Node[] nodes = browser.getActiveBrowser().getActiveProject().getDisplayChildren();
                    Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);      //clear the message window
                    for (int i=0; i<nodes.length; i++ ) {
                        if (nodes[i] instanceof PackageNode) {
                            PackageNode node = (PackageNode)nodes[i];
                            Node[] fileNodes = node.getDisplayChildren();
                            for (int j=0; j<fileNodes.length; j++) {
                                if (fileNodes[j] instanceof JavaFileNode) {
                                    Message fileNameMsg = new Message(fileNodes[j].getDisplayName());
                                    fileNameMsg.setFont(fileNameMsgFont);
                                    Browser.getActiveBrowser().getMessageView().addMessage(msgCat, fileNameMsg);
                                    JavaFileNode javaNode = (JavaFileNode)fileNodes[j];
                                    StringBuffer code = new StringBuffer();
                                    try {
                                        byte[] buffer = new byte[1024];
                                        InputStream is = javaNode.getInputStream();
                                        int charCount;
                                        while ((charCount = is.read(buffer)) != -1) {
                                            code.append(new String(buffer, 0, charCount));
                                        }
                                        checkCode(code.toString(), javaNode);
                                    }
                                    catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            };


            /**
            * Main method for testing purposes
            * @param args standard arguments
            */
            public static void main (String[] args) {
                Report ret = PMDOpenTool.instanceCheck("package abc; \npublic class foo {\npublic void bar() {int i;}\n}");
                System.out.println("PMD: " + ret);
            }
}


/**
 * Wrapper for the OpenTools message object
 */
class PMDMessage extends Message {
    final LineMark MARK = new HighlightMark();
    JavaFileNode javaNode;
    int line = 0;
    int column = 0;

    /**
     * Constructor
     * @param msg text message
     * @param line line of code to associate this message with
     * @param node the node that the code belongs to
     */
    public PMDMessage (String msg, int line, JavaFileNode node) {
        super(msg);
        this.line = line;
        this.javaNode = node;
    }

    /**
     * Called by JBuilder when user selects a message
     * @param browser JBuilder Browser
     */
    public void selectAction (Browser browser) {
        displayResult(browser, true);
    }

    /**
     * Called by JBuilder when the user double-clicks a message
     * @param browser JBuilder Browser
     */
    public void messageAction (Browser browser) {
        displayResult(browser, true);
    }

    /**
     * Position the code window to the line number that the message is associated with
     * @param browser JBuilder Browser
     * @param requestFocus whether or not the code window should receive focus
     */
    private void displayResult (Browser browser, boolean requestFocus) {
        try {
            if (requestFocus || browser.isOpenNode(javaNode)) {
                browser.setActiveNode(javaNode, requestFocus);
                TextNodeViewer viewer = (TextNodeViewer)browser.getViewerOfType(javaNode,
                        TextNodeViewer.class);
                browser.setActiveViewer(javaNode, viewer, requestFocus);
                EditorPane editor = viewer.getEditor();
                editor.gotoPosition(line, column, false, EditorPane.CENTER_IF_NEAR_EDGE);
                if (requestFocus) {
                    editor.requestFocus();
                }
                editor.setTemporaryMark(line, MARK);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


/**
 * Used to highlite a line of code within a source file
 */
class HighlightMark extends LineMark {
    static Style highlightStyle;
    static {
        StyleContext context = EditorManager.getStyleContext();
        highlightStyle = context.addStyle("line_highlight", null);
        highlightStyle.addAttribute(MasterStyleContext.DISPLAY_NAME, "Line highlight");
        StyleConstants.setBackground(highlightStyle, Color.yellow);
        StyleConstants.setForeground(highlightStyle, Color.black);
    }

    /**
     * Constructor
     */
    public HighlightMark () {
        super(highlightStyle);
    }
}