package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import oracle.ide.Addin;
import oracle.ide.AddinManager;
import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.ceditor.CodeEditor;
import oracle.ide.config.IdeSettings;
import oracle.ide.controller.ContextMenu;
import oracle.ide.controller.ContextMenuListener;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.editor.EditorManager;
import oracle.ide.layout.ViewId;
import oracle.ide.log.AbstractLogPage;
import oracle.ide.log.LogManager;
import oracle.ide.log.LogPage;
import oracle.ide.log.LogWindow;
import oracle.ide.model.Element;
import oracle.ide.model.Node;
import oracle.ide.model.Project;
import oracle.ide.model.RelativeDirectoryContextFolder;
import oracle.ide.navigator.NavigatorManager;
import oracle.ide.panels.Navigable;
import oracle.jdeveloper.compiler.IdeLog;
import oracle.jdeveloper.compiler.IdeStorage;
import oracle.jdeveloper.model.JavaSourceNode;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Plugin implements Addin, Controller, ContextMenuListener {

    public class CPDViolationPage extends AbstractLogPage implements TreeSelectionListener {

        private JScrollPane scrollPane;
        private JTree tree;
        private DefaultMutableTreeNode top;

        public CPDViolationPage() {
            super(new ViewId("PMDPage", Plugin.CPD_TITLE), null, false);
            top = new DefaultMutableTreeNode("CPD");
            tree = new JTree(top);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.addTreeSelectionListener(this);
            scrollPane = new JScrollPane(tree);
        }

        public void valueChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                CPDViolationWrapper nodeInfo = (CPDViolationWrapper)node.getUserObject();
                EditorManager.getEditorManager().openDefaultEditorInFrame(nodeInfo.file.getURL());
                ((CodeEditor)EditorManager.getEditorManager().getCurrentEditor()).gotoLine(nodeInfo.mark.getBeginLine(), 0, false);
            }
        }

        public void add(Match match) {
            Node file1 = (Node) cpdFileToNodeMap.get(match.getFirstMark().getTokenSrcID());
            DefaultMutableTreeNode matchNode = new DefaultMutableTreeNode(file1.getShortLabel() + " contains a " + match.getLineCount() + " line block of duplicated code", true);
            top.add(matchNode);
            for (Iterator i = match.iterator(); i.hasNext();) {
                TokenEntry mark = (TokenEntry) i.next();
                Node file = (Node) cpdFileToNodeMap.get(mark.getTokenSrcID());
                DefaultMutableTreeNode markTreeNode = new DefaultMutableTreeNode(new CPDViolationWrapper(mark, file, file.getShortLabel() + " has some at line " + mark.getBeginLine()), false);
                matchNode.add(markTreeNode);
            }
        }

        public Component getGUI() {
            return scrollPane;
        }

        public void clearAll() {
            System.out.println("clearing nodes");
            System.out.println("before: top now has " + top.getChildCount());
            top.removeAllChildren();
            System.out.println("after: top now has " + top.getChildCount());
            tree.repaint();
            scrollPane.repaint();
            //tree.removeSelectionPath(new TreePath(new Object[] {top}));
        }
    }

    private static class CPDViolationWrapper {
        private String label;
        public Node file;
        public TokenEntry mark;
        public CPDViolationWrapper(TokenEntry mark, Node file, String label) {
            this.label = label;
            this.mark = mark;
            this.file = file;
        }
        public String toString() {
            return label;
        }
    }

    public static final String RUN_PMD_CMD = "net.sourceforge.pmd.jdeveloper.Check";
    public static final int RUN_PMD_CMD_ID = Ide.createCmdID("PMDJDeveloperPlugin.RUN_PMD_CMD_ID");

    public static final String RUN_CPD_CMD = "net.sourceforge.pmd.jdeveloper.CheckCPD";
    public static final int RUN_CPD_CMD_ID = Ide.createCmdID("PMDJDeveloperPlugin.RUN_CPD_CMD_ID");

    public static final String PMD_TITLE = "PMD";
    public static final String CPD_TITLE = "CPD";

    private JMenuItem pmdMenuItem;
    private JMenuItem cpdMenuItem;

    private RuleViolationPage ruleViolationPage;
    private CPDViolationPage cpdViolationPage;

    private boolean added;
    private Map pmdFileToNodeMap = new HashMap(); // whew, this is kludgey
    private Map cpdFileToNodeMap = new HashMap(); // whew, this is kludgey

    // Addin
    public void initialize() {
        IdeAction pmdAction = IdeAction.get(RUN_PMD_CMD_ID, AddinManager.getAddinManager().getCommand(RUN_PMD_CMD_ID, RUN_PMD_CMD), PMD_TITLE, PMD_TITLE, null, null, null, true);
        pmdAction.addController(this);
        pmdMenuItem = Ide.getMenubar().createMenuItem(pmdAction);
        pmdMenuItem.setText(PMD_TITLE);
        pmdMenuItem.setMnemonic('P');

/*
        IdeAction cpdAction = IdeAction.get(RUN_CPD_CMD_ID, AddinManager.getAddinManager().getCommand(RUN_CPD_CMD_ID, RUN_CPD_CMD), CPD_TITLE, CPD_TITLE, null, null, null, true);
        cpdAction.addController(this);
        cpdMenuItem = Ide.getMenubar().createMenuItem(cpdAction);
        cpdMenuItem.setText(CPD_TITLE);
        cpdMenuItem.setMnemonic('C');
*/

        NavigatorManager.getWorkspaceNavigatorManager().addContextMenuListener(this, null);
        EditorManager.getEditorManager().getContextMenu().addContextMenuListener(this, null);
        IdeSettings.registerUI(new Navigable(PMD_TITLE, SettingsPanel.class, new Navigable[]{}));
        Ide.getVersionInfo().addComponent(PMD_TITLE, " JDeveloper Extension " + version());

        ruleViolationPage = new RuleViolationPage();
//        cpdViolationPage = new CPDViolationPage();
    }

    public void shutdown() {
        NavigatorManager.getWorkspaceNavigatorManager().removeContextMenuListener(this);
        EditorManager.getEditorManager().getContextMenu().removeContextMenuListener(this);
    }

    public float version() {
        return 1.8f;
    }

    public float ideVersion() {
        return 0.1f;
    }

    public boolean canShutdown() {
        return true;
    }
    // Addin

    // Controller
    public Controller supervisor() {
        return null;
    }


    public boolean handleEvent(IdeAction ideAction, Context context) {
        if (!added) {
            LogManager.getLogManager().addPage(ruleViolationPage);
            LogManager.getLogManager().showLog();
            added = true;
        }
        if (ideAction.getCommandId() == RUN_PMD_CMD_ID) {
            try {
                pmdFileToNodeMap.clear();
                PMD pmd = new PMD();
                SelectedRules rules = new SelectedRules(SettingsPanel.createSettingsStorage());
                RuleContext ctx = new RuleContext();
                ctx.setReport(new Report());
                if (context.getElement() instanceof RelativeDirectoryContextFolder) {
                    RelativeDirectoryContextFolder folder = (RelativeDirectoryContextFolder) context.getElement();
                    checkTree(folder.getChildren(), pmd, rules, ctx);
                } else if (context.getElement() instanceof Project) {
                    Project project = (Project) context.getElement();
                    checkTree(project.getChildren(), pmd, rules, ctx);
                } else if (context.getElement() instanceof JavaSourceNode) {
                    pmdFileToNodeMap.put(context.getNode().getLongLabel(), context.getNode());
                    ctx.setSourceCodeFilename(context.getNode().getLongLabel());
                    pmd.processFile(context.getNode().getInputStream(), rules.getSelectedRules(), ctx);
                    render(ctx);
                }
                return true;
            } catch (PMDException e) {
                e.printStackTrace();
                e.getReason().printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while running PMD: " + e.getMessage(), PMD_TITLE, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while running PMD: " + e.getMessage(), PMD_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        } else if (ideAction.getCommandId() == RUN_CPD_CMD_ID) {
            try {
                cpdFileToNodeMap.clear();

                // TODO get minimum tokens from prefs panel
                CPD cpd = new CPD(100, new LanguageFactory().createLanguage("java"));

                // add all files to CPD
                if (context.getElement() instanceof RelativeDirectoryContextFolder) {
                    RelativeDirectoryContextFolder folder = (RelativeDirectoryContextFolder) context.getElement();
                    glomToCPD(folder.getChildren(), cpd);
                } else if (context.getElement() instanceof Project) {
                    Project project = (Project) context.getElement();
                    glomToCPD(project.getChildren(), cpd);
                } else if (context.getElement() instanceof JavaSourceNode) {
                    cpd.add(new File(context.getNode().getLongLabel()));
                    cpdFileToNodeMap.put(context.getNode().getLongLabel(), context.getNode());
                }

                cpd.go();

                cpdViolationPage.show();
                cpdViolationPage.clearAll();
                if (!cpd.getMatches().hasNext()) {
                    JOptionPane.showMessageDialog(null, "No problems found", CPD_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    LogPage page = LogManager.getLogManager().getMsgPage();
                    if (page instanceof LogWindow) {
                        ((LogWindow) page).show();
                    }
                } else {
                    for (Iterator i = cpd.getMatches(); i.hasNext();) {
                        cpdViolationPage.add((Match) i.next());
                    }
                }



            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while running CPD: " + e.getMessage(), CPD_TITLE, JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }

    public boolean update(IdeAction ideAction, Context context) {
        return false;
    }

    public void checkCommands(Context context, Controller controller) {}
    // Controller

    // ContextMenuListener
    public void menuWillHide(ContextMenu contextMenu) {}

    public void menuWillShow(ContextMenu contextMenu) {
        Element doc = contextMenu.getContext().getElement();
        // RelativeDirectoryContextFolder -> a package
        if (doc instanceof Project || doc instanceof JavaSourceNode || doc instanceof RelativeDirectoryContextFolder) {
            contextMenu.add(pmdMenuItem);
            contextMenu.add(cpdMenuItem);
        }
    }

    public boolean handleDefaultAction(Context context) {
        return false;
    }
    // ContextMenuListener

    public static String getVersion() {
        return Package.getPackage("net.sourceforge.pmd.jdeveloper").getImplementationVersion();
    }


    private void render(RuleContext ctx) {
        ruleViolationPage.show();
        ruleViolationPage.clearAll();
        if (ctx.getReport().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No problems found", PMD_TITLE, JOptionPane.INFORMATION_MESSAGE);
            LogPage page = LogManager.getLogManager().getMsgPage();
            if (page instanceof LogWindow) {
                ((LogWindow) page).show();
            }
        } else {
            List list = new ArrayList();
            for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                RuleViolation rv = (RuleViolation) i.next();
                Node node = (Node) pmdFileToNodeMap.get(rv.getFilename());
                list.add(new IdeLog.Message(Ide.getActiveWorkspace(), Ide.getActiveProject(), new IdeStorage(node), rv.getDescription(), 2, rv.getNode().getBeginLine(), rv.getNode().getBeginColumn()));
            }
            ruleViolationPage.add(list);
        }
    }

    private void glomToCPD(Iterator i, CPD cpd) throws IOException {
        while (i.hasNext()) {
            Object obj = i.next();
            if (!(obj instanceof JavaSourceNode)) {
                continue;
            }
            JavaSourceNode candidate = (JavaSourceNode) obj;
            if (candidate.getLongLabel().endsWith(".java") && new File(candidate.getLongLabel()).exists()) {
                cpdFileToNodeMap.put(candidate.getLongLabel(), candidate);
                cpd.add(new File(candidate.getLongLabel()));
            }
        }
    }

    private void checkTree(Iterator i, PMD pmd, SelectedRules rules, RuleContext ctx) throws IOException, PMDException {
        while (i.hasNext()) {
            Object obj = i.next();
            if (!(obj instanceof JavaSourceNode)) {
                continue;
            }
            JavaSourceNode candidate = (JavaSourceNode) obj;
            if (candidate.getLongLabel().endsWith(".java") && new File(candidate.getLongLabel()).exists()) {
                pmdFileToNodeMap.put(candidate.getLongLabel(), candidate);
                ctx.setSourceCodeFilename(candidate.getLongLabel());
                FileInputStream fis = new FileInputStream(new File(candidate.getLongLabel()));
                pmd.processFile(fis, rules.getSelectedRules(), ctx);
                fis.close();
            }
        }
        render(ctx);
    }

}
