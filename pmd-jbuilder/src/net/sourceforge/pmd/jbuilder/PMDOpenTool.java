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
import java.awt.event.ActionEvent;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.Results;
import net.sourceforge.pmd.cpd.Tile;
import net.sourceforge.pmd.cpd.CPDNullListener;
import net.sourceforge.pmd.cpd.CPDListener;
import javax.swing.ProgressMonitor;
import net.sourceforge.pmd.cpd.TokenEntry;
import com.borland.primetime.vfs.Url;
import java.util.ArrayList;
import com.borland.jbcl.control.MessageDialog;



public class PMDOpenTool {
    static MessageCategory msgCat = new MessageCategory("PMD Results");
    static MessageCategory cpdCat = new MessageCategory("CPD Results");
    public static ActionGroup GROUP_PMD = new ActionGroup("PMD", 'p', true);
    public static ActionGroup GROUP_TOOLBAR_PMD = new ActionGroup("PMD", 'P', true);
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


            GROUP_PMD.add(B_ACTION_PMDCheck);
            GROUP_PMD.add(B_ACTION_PMDProjectCheck);
            GROUP_PMD.add(B_ACTION_CPDProjectCheck);
            GROUP_PMD.add(B_ACTION_PMDConfig);
            JBuilderMenu.GROUP_Tools.add(GROUP_PMD);
            GROUP_TOOLBAR_PMD.add(B_ACTION_PMDCheck);
            GROUP_TOOLBAR_PMD.add(B_ACTION_PMDProjectCheck);
            GROUP_TOOLBAR_PMD.add(B_ACTION_CPDProjectCheck);
            Browser.addToolBarGroup(GROUP_TOOLBAR_PMD);
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
            if (AcceleratorPropertyGroup.PROP_KEYS_ENABLED.getBoolean()) {
                registerShortCuts();
            }

            PropertyManager.registerPropertyGroup(apropGrp);
            PropertyManager.registerPropertyGroup(cpropGrp);
            PropertyManager.registerPropertyGroup(ipropGrp);
            PropertyManager.registerPropertyGroup(accpropGrp);
            PropertyManager.registerPropertyGroup(cpdPropGrp);

        }
    }


    static void clearShortCuts() {
        EditorManager.getKeymap().removeKeyStrokeBinding(KeyStroke.getKeyStroke(AcceleratorPropertyGroup.PROP_CHECKFILE_KEY.getInteger(),
                AcceleratorPropertyGroup.PROP_CHECKFILE_MOD.getInteger()));
        EditorManager.getKeymap().removeKeyStrokeBinding(KeyStroke.getKeyStroke(AcceleratorPropertyGroup.PROP_CHECKPROJ_KEY.getInteger(),
                AcceleratorPropertyGroup.PROP_CHECKPROJ_MOD.getInteger()));
    }

    static void registerShortCuts() {

        EditorManager.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(AcceleratorPropertyGroup.PROP_CHECKFILE_KEY.getInteger(),
                AcceleratorPropertyGroup.PROP_CHECKFILE_MOD.getInteger()),
                E_ACTION_PMDCheck);

        EditorManager.getKeymap().addActionForKeyStroke(KeyStroke.getKeyStroke(AcceleratorPropertyGroup.PROP_CHECKPROJ_KEY.getInteger(),
                AcceleratorPropertyGroup.PROP_CHECKPROJ_MOD.getInteger()),
                E_ACTION_PMDCheckProject);
    }

    /**
     * Registers an "PMD Checker" action with the ContentManager (Tabs)
     * The action will not be visible if multiple nodes are selected
     */
    private static void registerWithContentManager () {
        ContextActionProvider cap = new ContextActionProvider() {

            public Action getContextAction (Browser browser, Node[] nodes) {
                return  B_ACTION_PMDCheck;
            }
        };
        ContentManager.registerContextActionProvider(cap);
    }

    private static void registerWithProjectView() {
        ContextActionProvider cap = new ContextActionProvider() {
            public Action getContextAction (Browser browser, Node[] nodes) {
                Node node = browser.getProjectView().getSelectedNode();
                if (node instanceof JBProject || node instanceof PackageNode)
                    return  B_ACTION_PMDProjectCheck;
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
    private static RuleSet constructRuleSets () {
        RuleSet masterRuleSet = new RuleSet();
        for (Iterator iter = ActiveRuleSetPropertyGroup.currentInstance.ruleSets.values().iterator(); iter.hasNext(); ) {
            RuleSetProperty rsp = (RuleSetProperty)iter.next();
            if (Boolean.valueOf(rsp.getGlobalProperty().getValue()).booleanValue()) {
                RuleSet rules = rsp.getActiveRuleSet();
                masterRuleSet.addRuleSet(rules);
            }
        }
        return  masterRuleSet;
    }

    /**
     * Run the PMD against some code
     * @param text code to check
     * @return PMD Report object
     */
    public static Report instanceCheck (String text, RuleSet rules) {
        PMD pmd = new PMD();

        RuleContext ctx = new RuleContext();
        if (rules == null) {
            rules = constructRuleSets();
        }
        if (rules == null)
            return  new Report();
        ctx.setReport(new Report());
        ctx.setSourceCodeFilename("this");
        try {
            pmd.processFile(new StringReader(text), rules, ctx);
            return  ctx.getReport();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    //create EditorAction for performing a PMD Check
    public static EditorAction E_ACTION_PMDCheck =
            new EditorAction("Displays PMD statistics about a Java File") {
        public void actionPerformed(ActionEvent e) {
            pmdCheck();
        }
    };

    //create EditorAction for performing a PMD Check on a project
    public static EditorAction E_ACTION_PMDCheckProject =
            new EditorAction("Displays PMD statistics about a Java File") {
        public void actionPerformed(ActionEvent e) {
            pmdCheckProject();
        }
    };

    //create the Menu action item for initiating the PMD check
    public static BrowserAction B_ACTION_PMDCheck =
            // A new action with short menu string, mnemonic, and long menu string
    new BrowserAction("PMD Checker", 'P', "Displays PMD statistics about a Java File", new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/checkFile.gif"))) {

        // The function called when the menu is selected
        public void actionPerformed (Browser browser) {
            pmdCheck();
        }
    };

    //Create the menu action item for configuring PMD
    public static BrowserAction B_ACTION_PMDConfig = new BrowserAction("Configure PMD",
            'C', "Configure the PMD Settings") {
        public void actionPerformed (Browser browser) {
            PropertyManager.showPropertyDialog(browser, "PMD Options", Constants.RULESETS_TOPIC,
                    PropertyDialog.getLastSelectedPage());
        }
    };

    //create the project menu action for running a PMD check against all the java files within the active project
    public static BrowserAction B_ACTION_PMDProjectCheck = new BrowserAction ("PMD Check Project", 'P', "Check all the java files in the project",
            new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/checkProject.gif"))) {
        public void actionPerformed(Browser browser) {
            pmdCheckProject();
        }

    };

    //create the project menu action for running a PMD check against all the java files within the active project
    public static BrowserAction B_ACTION_CPDProjectCheck = new BrowserAction ("CPD Check Project", 'P', "Run CPD on all the java files in the project",
            new ImageIcon(PMDOpenTool.class.getClassLoader().getSystemResource("images/cpd.gif"))) {
        public void actionPerformed(Browser browser) {
            Runnable r = new Runnable() {
                public void run() {
                    pmdCPD();
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

    private static void pmdCheck() {
        Node node = Browser.getActiveBrowser().getActiveNode();
        if (node instanceof JavaFileNode) {
            Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);      //clear the message window
            TextNodeViewer viewer = (TextNodeViewer)Browser.getActiveBrowser().getViewerOfType(node,
                    TextNodeViewer.class);
            if (viewer != null) {
                Document doc = viewer.getEditor().getDocument();
                try {
                    checkCode(doc.getText(0, doc.getLength()), (JavaFileNode)node, null);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static void pmdCheckPackage(PackageNode packageNode, RuleSet rules) {
        Node[] fileNodes = packageNode.getDisplayChildren();
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
                    checkCode(code.toString(), javaNode, rules);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else if (fileNodes[j] instanceof PackageNode) {
                pmdCheckPackage((PackageNode)fileNodes[j], rules);  //recursive call
            }
        }

    }

    private static void pmdCheckProject() {
        Node[] nodes = Browser.getActiveBrowser().getActiveProject().getDisplayChildren();
        Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);      //clear the message window
        RuleSet rules = constructRuleSets();
        for (int i=0; i<nodes.length; i++ ) {
            if (nodes[i] instanceof PackageNode) {
                PackageNode node = (PackageNode)nodes[i];
                String packageName = node.getName();
                if (packageName != null && !packageName.trim().equals("")) {  //if there is no name then this is probably the <Project Source> package - so ignore it so we don't get duplicates
                    pmdCheckPackage(node, rules);
                }
            }
        }
    }

    private static void pmdCPDPackage(PackageNode packageNode, CPD cpd) {
        Node[] fileNodes = packageNode.getDisplayChildren();
        for (int j=0; j<fileNodes.length; j++) {
            if (fileNodes[j] instanceof JavaFileNode) {
                try {
                    cpd.add(new File(fileNodes[j].getLongDisplayName()));
                }
                catch (Exception e){
                }
            }
            else if (fileNodes[j] instanceof PackageNode) {
                pmdCPDPackage((PackageNode)fileNodes[j], cpd);   //recursive call
            }
        }
    }

    private static void pmdCPD() {
        try {
            Browser.getActiveBrowser().getMessageView().clearMessages(cpdCat);      //clear the message window
            final CPD cpd = new CPD();
            cpd.setMinimumTileSize(CPDPropertyGroup.PROP_MIN_TOKEN_COUNT.getInteger());
            Node[] nodes = Browser.getActiveBrowser().getActiveProject().getDisplayChildren();
            CPDDialog cpdd = new CPDDialog(cpd);
            for (int i=0; i<nodes.length; i++ ) {
                if (nodes[i] instanceof PackageNode) {
                    PackageNode node = (PackageNode)nodes[i];
                    String packageName = node.getName();
                    if (packageName != null && !packageName.trim().equals("")) {  //if there is no name then this is probably the <Project Source> package - so ignore it so we don't get duplicates
                        pmdCPDPackage(node, cpd);
                    }
                }
            }
            cpd.go();
            if (cpdd.wasCancelled()) {  //if the dialog was cancelled by the user then let's get out of here
                cpdd.close();
                return;
            }
            Results results = cpd.getResults();
            int resultCount = 0;
            if (results != null) {
                for (Iterator iter = results.getTiles(); iter.hasNext(); ) {
                    Tile t = (Tile)iter.next();
                    resultCount++;
                    int tileLineCount = cpd.getLineCountFor(t);
                    int dupCount = results.getOccurrenceCountFor(t);
                    CPDMessage msg = CPDMessage.createMessage(String.valueOf(dupCount)+" duplicates in code set: " + resultCount, cpd.getImage(t));
                    for (Iterator iter2 = results.getOccurrences(t); iter2.hasNext(); ) {
                        TokenEntry te = (TokenEntry)iter2.next();
                        msg.addChildMessage(te.getBeginLine(), tileLineCount, te.getTokenSrcID());
                    }
                    Browser.getActiveBrowser().getMessageView().addMessage(cpdCat, msg);
                }
            }
            cpdd.close();
        }
        catch (Exception e) {
            Browser.getActiveBrowser().getMessageView().addMessage(cpdCat, e.toString());
        }
    }

    /**
     * Main method for testing purposes
     * @param args standard arguments
     */
    public static void main (String[] args) {
        //Report ret = PMDOpenTool.instanceCheck("package abc; \npublic class foo {\npublic void bar() {int i;}\n}");
        //System.out.println("PMD: " + ret);
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
 * Wrapper for the OpenTools message object
 */
class CPDMessage extends Message {
    final static LineMark MARK = new HighlightMark(true);
    static Font PARENT_FONT = new Font("SansSerif", Font.BOLD, 12);
    static Font CHILD_FONT = new Font("SansSerif", Font.PLAIN, 12);
    String filename;
    FileNode javaNode = null;
    int startline = 0;
    int lineCount = 0;
    int column = 0;
    boolean isParent = true;
    ArrayList childMessages = new ArrayList();
    String codeBlock = null;

    /**
     * Constructor
     * @param msg text message
     * @param line line of code to associate this message with
     * @param node the node that the code belongs to
     */

    private CPDMessage(String msg, String codeBlock) {
        super(msg);
        this.codeBlock = codeBlock;
        this.setLazyFetchChildren(true);
    }

    private CPDMessage (String msg, int startline, int lineCount, String fileName) {
        super(msg);
        this.startline = startline;
        this.lineCount = lineCount;
        this.filename = fileName;
        try {
            File javaFile = new File(fileName);
            javaNode = Browser.getActiveBrowser().getActiveProject().findNode(new Url(javaFile));
        }
        catch (Exception e){
            Browser.getActiveBrowser().getMessageView().addMessage(Constants.MSGCAT_TEST, e.toString());
        }
    }

    public static CPDMessage createMessage(String msg, String codeBlock) {
        CPDMessage cpdm = new CPDMessage(msg, codeBlock);
        cpdm.isParent = true;
        cpdm.setFont(PARENT_FONT);
        return cpdm;
    }


    public void addChildMessage (int startline, int endline, String fileName) {
        this.lazyFetchChildren = true;
        String sep = System.getProperty("file.separator");
        String msg = fileName.substring(fileName.lastIndexOf(sep.charAt(0))+1)+": line: " + String.valueOf(startline);
        CPDMessage cpdmsg =  new CPDMessage(msg, startline, endline, fileName);
        cpdmsg.isParent = false;
        cpdmsg.setFont(CHILD_FONT);
        childMessages.add(cpdmsg);

    }

    public void fetchChildren(Browser browser) {
        CodeFragmentMessage cfm = new CodeFragmentMessage(this.codeBlock);
        browser.getMessageView().addMessage(PMDOpenTool.cpdCat, this, cfm);
        for (Iterator iter = childMessages.iterator(); iter.hasNext(); ) {
            browser.getMessageView().addMessage(PMDOpenTool.cpdCat, this, (CPDMessage)iter.next());
        }
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
        MARK.removeEditor();
        if (!isParent) {
            try {
                if (requestFocus || browser.isOpenNode(javaNode)) {
                    browser.setActiveNode(javaNode, requestFocus);
                    TextNodeViewer viewer = (TextNodeViewer)browser.getViewerOfType(javaNode,
                            TextNodeViewer.class);
                    browser.setActiveViewer(javaNode, viewer, requestFocus);
                    EditorPane editor = viewer.getEditor();
                    editor.gotoPosition(startline, 0, false, EditorPane.CENTER_IF_NEAR_EDGE);
                    if (requestFocus) {
                        editor.requestFocus();
                    }
                    /*EditorDocument ed = (EditorDocument)editor.getDocument();
                    int[] lines = new int[lineCount];
                    for (int i=0; i<lineCount; i++)
                        lines[i] = startline+i-1;
                    ed.setLightweightLineMarks(lines, MARK);*/
                    editor.setTemporaryMark(startline, MARK);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

class CodeFragmentMessage extends Message {
    String codeFragment = null;
    static Font CODE_FONT = new Font("Monospaced", Font.ITALIC, 12);
    public CodeFragmentMessage(String codeFragment) {
        super("View Code");
        this.setLazyFetchChildren(true);
        this.codeFragment = codeFragment;

    }
    public void fetchChildren(Browser browser) {
        BufferedReader reader = new BufferedReader(new StringReader(codeFragment));
        try {
            String line = reader.readLine();
            while (line != null) {
                Message msg = new Message(line);
                msg.setFont(CODE_FONT);
                browser.getMessageView().addMessage(PMDOpenTool.cpdCat, this, msg);
                line = reader.readLine();
            }
        }
        catch (Exception e){}

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

    public HighlightMark(boolean isLightWeight) {
        super(isLightWeight, highlightStyle);
    }
}