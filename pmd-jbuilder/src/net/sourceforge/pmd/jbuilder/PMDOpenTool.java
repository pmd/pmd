package net.sourceforge.pmd.jbuilder;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;

import com.borland.jbuilder.*;
import com.borland.jbuilder.node.*;
import com.borland.primetime.*;
import com.borland.primetime.editor.*;
import com.borland.primetime.ide.*;
import com.borland.primetime.node.*;
import com.borland.primetime.viewer.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.reports.*;
import com.borland.primetime.actions.ActionGroup;
import com.borland.primetime.properties.NodeProperty;
import com.borland.primetime.properties.GlobalProperty;
import com.borland.primetime.properties.PropertyManager;
import com.borland.primetime.properties.PropertyDialog;


/**
 * <p>Title: JBuilder OpenTool for PMD</p>
 * <p>Description: Provides an environemnACTION_PMDCheckt for using the PMD aplication from within JBuilder</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: InfoEther</p>
 * @author David Craine
 * @version 1.0
 */

public class PMDOpenTool  {

    static MessageCategory msgCat = new MessageCategory("PMD Results");
    public static ActionGroup GROUP_PMD = new ActionGroup("PMD", 'p', true);

    public PMDOpenTool() {
        int i;
        int a;
    }

    public static void initOpenTool(byte majorVersion, byte minorVersion) {
        if (majorVersion == PrimeTime.CURRENT_MAJOR_VERSION) {
            GROUP_PMD.add(ACTION_PMDCheck);
            GROUP_PMD.add(ACTION_PMDConfig);
            JBuilderMenu.GROUP_Tools.add(GROUP_PMD);
            registerWithContentManager();
            PropertyManager.registerPropertyGroup(new RuleSetPropertyGroup());
        }
    }


    /**
     * Registers an "PMD Checker" action with the ContentManager (Tabs)
     * The action will not be visible if multiple nodes are selected
     */
    private static void registerWithContentManager()
    {
        ContextActionProvider cap = new ContextActionProvider()
        {
            public Action getContextAction(Browser browser, Node[] nodes)
            {
                return ACTION_PMDCheck;
            }
        };

        ContentManager.registerContextActionProvider(cap);
    }


    private static RuleSet constructRuleSets(RuleSetFactory ruleSetFactory, PMD pmd) {
        RuleSet masterRuleSet = null;
        for (int i = 0; i<RuleSetPropertyGroup.PROPKEYS.length; i++) {
            if (Boolean.valueOf(RuleSetPropertyGroup.PROPKEYS[i].getValue()).booleanValue()){
                RuleSet rules= ruleSetFactory.createRuleSet(pmd.getClass().getClassLoader().getResourceAsStream("rulesets/"+RuleSetPropertyGroup.RULESET_NAMES[i]+".xml"));
                if (masterRuleSet == null) {
                    masterRuleSet = rules;
                }
                else {
                    masterRuleSet.addRuleSet(rules);
                }
            }
        }
        return masterRuleSet;
    }

    public static  Report instanceCheck(String text) {

        PMD pmd = new PMD();

        ReportFactory rf = new ReportFactory();
        RuleContext ctx = new RuleContext();
        RuleSetFactory ruleSetFactory = new RuleSetFactory();
        //RuleSet rules = ruleSetFactory.createRuleSet(pmd.getClass().getClassLoader().getResourceAsStream("rulesets/unusedcode.xml"));
        RuleSet rules = constructRuleSets(ruleSetFactory, pmd);
        if (rules == null) return null;

        ctx.setReport(rf.createReport("xml"));
        ctx.setSourceCodeFilename("this");
        try {
            // TODO switch to use StringReader once PMD 0.4 gets released
            pmd.processFile(new StringBufferInputStream(text), rules, ctx);
            return ctx.getReport();

        } catch (FileNotFoundException fnfe) {
                fnfe.printStackTrace();
            }
            return null;

    }

    public static /*final*/ BrowserAction ACTION_PMDCheck =
    // A new action with short menu string, mnemonic, and long menu string
    new BrowserAction("PMD Checker", 'P', "Displays PMD statistics about a Java File") {

        // The function called when the menu is selected
        public void actionPerformed(Browser browser) {
            Node node = Browser.getActiveBrowser().getActiveNode();
            if (node instanceof JavaFileNode) {
                TextNodeViewer viewer = (TextNodeViewer)Browser.getActiveBrowser().getViewerOfType(node, TextNodeViewer.class);
                if (viewer != null) {
                    Document doc = viewer.getEditor().getDocument();
                    try {
                        Report rpt = instanceCheck(doc.getText(0, doc.getLength()));
                        Browser.getActiveBrowser().getMessageView().clearMessages(msgCat);//clear the message window
                        if (rpt == null) {
                            Browser.getActiveBrowser().getMessageView().addMessage(msgCat, "Error Processing File");
                        }
                        else if (rpt.size() == 0) {
                            Browser.getActiveBrowser().getMessageView().addMessage(msgCat, "No violations detexted.");
                        }
                        else {
                            for (Iterator i = rpt.iterator(); i.hasNext(); ) {
                                RuleViolation rv = (RuleViolation)i.next();
                                PMDMessage pmdMsg = new PMDMessage(rv.getDescription() + " at line " + rv.getLine(), rv.getLine(), (JavaFileNode)node);
                                pmdMsg.setForeground(Color.red);
                                Browser.getActiveBrowser().getMessageView().addMessage(msgCat, pmdMsg);//add the result message
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    };

    public static BrowserAction ACTION_PMDConfig =
            new BrowserAction("Configure PMD", 'C', "Configure the PMD Settings") {
        public void actionPerformed(Browser browser) {
            PropertyManager.showPropertyDialog(browser,
              "PMD Options",
              RuleSetPropertyGroup.RULESETS_TOPIC,
              PropertyDialog.getLastSelectedPage());

        }
    };

    /**
    * Main method for testing purposes
    * @param args standard arguments
    */
    public static void main(String[] args) {
        Report ret = PMDOpenTool.instanceCheck("package abc; \npublic class foo {\npublic void bar() {int i;}\n}");
        System.out.println("PMD: " + ret);
    }

}
class PMDMessage extends Message {

    final LineMark MARK = new HighlightMark();
    JavaFileNode javaNode;
    int line=0;
    int column=0;

    public PMDMessage(String msg, int line, JavaFileNode node) {
        super(msg);
        this.line = line;
        this.javaNode = node;

    }
    public void selectAction(Browser browser) {
        displayResult(browser, false);
    }

    public void messageAction(Browser browser) {
        displayResult(browser, true);
    }

    private void displayResult(Browser browser, boolean requestFocus) {
        try {
            if (requestFocus || browser.isOpenNode(javaNode)) {
                browser.setActiveNode(javaNode, requestFocus);
                TextNodeViewer viewer = (TextNodeViewer)browser.getViewerOfType(javaNode, TextNodeViewer.class);
                browser.setActiveViewer(javaNode, viewer, requestFocus);
                EditorPane editor = viewer.getEditor();
                editor.gotoPosition(line, column, false, EditorPane.CENTER_IF_NEAR_EDGE);
                if (requestFocus)
                    editor.requestFocus();
                else
                    editor.setTemporaryMark(line, MARK);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class HighlightMark extends LineMark {
    static Style highlightStyle;
    static {
        StyleContext context = EditorManager.getStyleContext();
        highlightStyle = context.addStyle("line_highlight", null);
        highlightStyle.addAttribute(MasterStyleContext.DISPLAY_NAME, "Line highlight");
        StyleConstants.setBackground(highlightStyle, Color.yellow);
        StyleConstants.setForeground(highlightStyle, Color.black);
    }

    public HighlightMark() {
        super(highlightStyle);
    }
}
