package net.sourceforge.pmd.jdeveloper;

import java.awt.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;

import oracle.ide.AddinManager;
import oracle.ide.ContextMenu;
import oracle.ide.Ide;
import oracle.ide.IdeAction;
import oracle.ide.addin.Addin;
import oracle.ide.addin.Context;
import oracle.ide.addin.ContextMenuListener;
import oracle.ide.addin.Controller;
import oracle.ide.config.IdeSettings;
import oracle.ide.editor.EditorManager;
import oracle.ide.layout.ViewId;
import oracle.ide.log.AbstractLogPage;
import oracle.ide.log.LogManager;
import oracle.ide.log.LogPage;
import oracle.ide.log.LogWindow;
import oracle.ide.model.Element;
import oracle.ide.model.Node;
import oracle.ide.model.PackageFolder;
import oracle.ide.model.Project;
import oracle.ide.navigator.NavigatorManager;
import oracle.ide.panels.Navigable;

import oracle.jdeveloper.ceditor.CodeEditor;
import oracle.jdeveloper.compiler.IdeLog;
import oracle.jdeveloper.compiler.IdeStorage;
import oracle.jdeveloper.model.JavaSourceNode;

public class Plugin implements Addin, Controller, ContextMenuListener {

    public class CPDViolationPage extends AbstractLogPage implements TreeSelectionListener {

        private final transient JScrollPane scrollPane;
        private final transient JTree tree;
        private final transient DefaultMutableTreeNode top;

        public CPDViolationPage() {
            super(new ViewId("PMDPage", Plugin.CPD_TITLE), null, false);
            top = new DefaultMutableTreeNode("CPD");
            tree = new JTree(top);
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            tree.addTreeSelectionListener(this);
            scrollPane = new JScrollPane(tree);
        }

        public void valueChanged(final TreeSelectionEvent event) {
            final DefaultMutableTreeNode node = 
                (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
            if (node != null && node.isLeaf()) {
                final CPDViolationWrapper nodeInfo = 
                    (CPDViolationWrapper)node.getUserObject();
                EditorManager.getEditorManager().openDefaultEditorInFrame(nodeInfo.file.getURL());
                ((CodeEditor)EditorManager.getEditorManager().getCurrentEditor()).gotoLine(nodeInfo.mark.getBeginLine(), 
                                                                                           0, 
                                                                                           false);
            }
        }

        public void add(final Match match) {
            final Node file1 = 
                (Node)cpdFileToNodeMap.get(match.getFirstMark().getTokenSrcID());
            final DefaultMutableTreeNode matchNode = 
                new DefaultMutableTreeNode(file1.getShortLabel() + 
                                           " contains a " + 
                                           match.getLineCount() + 
                                           " line block of duplicated code", 
                                           true);
            top.add(matchNode);
            for (final Iterator i = match.iterator(); i.hasNext(); ) {
                final TokenEntry mark = (TokenEntry)i.next();
                final Node file = (Node)cpdFileToNodeMap.get(mark.getTokenSrcID());
                final DefaultMutableTreeNode markTreeNode = 
                    new DefaultMutableTreeNode(new CPDViolationWrapper(mark, 
                                                                       file, 
                                                                       file.getShortLabel() + 
                                                                       " has some at line " + 
                                                                       mark.getBeginLine()), 
                                               false);
                matchNode.add(markTreeNode);
            }
        }

        public Component getGUI() {
            return scrollPane;
        }

        public void clearAll() {
            top.removeAllChildren();
            tree.repaint();
            scrollPane.repaint();
            //tree.removeSelectionPath(new TreePath(new Object[] {top}));
        }
    }

    private static class CPDViolationWrapper {
        private final transient String label;
        public transient Node file;
        public transient TokenEntry mark;

        public CPDViolationWrapper(final TokenEntry mark, final Node file, final String label) {
            this.label = label;
            this.mark = mark;
            this.file = file;
        }

        public String toString() {
            return label;
        }
    }

    public static final String RUN_PMD_CMD = 
        "net.sourceforge.pmd.jdeveloper.Check";
    public static final int RUN_PMD_CMD_ID = 
        Ide.createCmdID("PMDJDeveloperPlugin.RUN_PMD_CMD_ID");

    public static final String RUN_CPD_CMD = 
        "net.sourceforge.pmd.jdeveloper.CheckCPD";
    public static final int RUN_CPD_CMD_ID = 
        Ide.createCmdID("PMDJDeveloperPlugin.RUN_CPD_CMD_ID");

    public static final String PMD_TITLE = "PMD";
    public static final String CPD_TITLE = "CPD";

    private transient JMenuItem pmdMenuItem;
    private transient JMenuItem cpdMenuItem;

    private transient RuleViolationPage ruleViolationPage;
    private transient CPDViolationPage cpdViolationPage;

    private transient boolean added;
    private final transient Map pmdFileToNodeMap = new HashMap(); // whew, this is kludgey
    private final transient Map cpdFileToNodeMap = new HashMap(); // whew, this is kludgey

    // Addin

    public void initialize() {
        final IdeAction pmdAction = 
            IdeAction.get(RUN_PMD_CMD_ID, AddinManager.getAddinManager().getCommand(RUN_PMD_CMD_ID, 
                                                                                    RUN_PMD_CMD), 
                          PMD_TITLE, PMD_TITLE, null, null, null, true);
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

        NavigatorManager.getWorkspaceNavigatorManager().addContextMenuListener(this, 
                                                                               null);
        EditorManager.getEditorManager().getContextMenu().addContextMenuListener(this, 
                                                                                 null);
        IdeSettings.registerUI(new Navigable(PMD_TITLE, SettingsPanel.class, 
                                             new Navigable[] { }));
        Ide.getVersionInfo().addComponent(PMD_TITLE, 
                                          "JDeveloper Extension " + Version.version());

        ruleViolationPage = new RuleViolationPage();
        //        cpdViolationPage = new CPDViolationPage();
    }

    public void shutdown() {
        NavigatorManager.getWorkspaceNavigatorManager().removeContextMenuListener(this);
        EditorManager.getEditorManager().getContextMenu().removeContextMenuListener(this);
    }

    public float version() {
          return 4.2f;
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


    public boolean handleEvent(final IdeAction ideAction, final Context context) {
        if (!added) {
            LogManager.getLogManager().addPage(ruleViolationPage);
            LogManager.getLogManager().showLog();
            added = true;
        }
        if (ideAction.getCommandId() == RUN_PMD_CMD_ID) {
            try {
                pmdFileToNodeMap.clear();
                final PMD pmd = new PMD();
                Version.setJavaVersion(context, pmd);

                final SelectedRules rules = 
                    new SelectedRules(SettingsPanel.createSettingsStorage());
                final RuleContext ctx = new RuleContext();
                ctx.setReport(new Report());
                if (context.getElement() instanceof 
                    PackageFolder) {
                    final PackageFolder folder = 
                        (PackageFolder)context.getElement();
                    checkTree(folder.getChildren(), pmd, rules, ctx);
                } else if (context.getElement() instanceof Project) {
                    final Project project = (Project)context.getElement();
                    checkTree(project.getChildren(), pmd, rules, ctx);
                } else if (context.getElement() instanceof JavaSourceNode) {
                    ctx.setSourceCodeFilename(context.getDocument().getLongLabel());
                    pmd.processFile(context.getDocument().getInputStream(), rules.getSelectedRules(), ctx);
                    render(ctx);
                }
                return true;
            } catch (PMDException e) {
                // TODO reroute the whole printStackTrace to the IDE log window
                e.printStackTrace();
                e.getCause().printStackTrace();
                JOptionPane.showMessageDialog(null, 
                                              "Error while running PMD: " + 
                                              "\n" + e.getMessage() + "\n" + 
                                              e.getCause().getMessage(), 
                                              PMD_TITLE, 
                                              JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logMessage(e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                                              "Error while running PMD: " + 
                                              "\n" + e.getMessage(), PMD_TITLE, 
                                              JOptionPane.ERROR_MESSAGE);
            }
        } else if (ideAction.getCommandId() == RUN_CPD_CMD_ID) {
            try {
                cpdFileToNodeMap.clear();

                // TODO get minimum tokens from prefs panel
                final CPD cpd = 
                    new CPD(100, new LanguageFactory().createLanguage("java"));

                // add all files to CPD
                if (context.getElement() instanceof 
                    PackageFolder) {
                    final PackageFolder folder = 
                        (PackageFolder)context.getElement();
                    glomToCPD(folder.getChildren(), cpd);
                } else if (context.getElement() instanceof Project) {
                    final Project project = (Project)context.getElement();
                    glomToCPD(project.getChildren(), cpd);
                } else if (context.getElement() instanceof JavaSourceNode) {
                    cpd.add(new File(context.getDocument().getLongLabel()));
                    cpdFileToNodeMap.put(context.getDocument().getLongLabel(), 
                                         (JavaSourceNode)context.getElement());
                }

                cpd.go();

                cpdViolationPage.show();
                cpdViolationPage.clearAll();
                if (!cpd.getMatches().hasNext()) {
                    JOptionPane.showMessageDialog(null, "No problems found", 
                                                  CPD_TITLE, 
                                                  JOptionPane.INFORMATION_MESSAGE);
                    final LogPage page = LogManager.getLogManager().getMsgPage();
                    if (page instanceof LogWindow) {
                        ((LogWindow)page).show();
                    }
                } else {
                    for (final Iterator i = cpd.getMatches(); i.hasNext(); ) {
                        cpdViolationPage.add((Match)i.next());
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                                              "Error while running CPD: " + 
                                              e.getMessage(), CPD_TITLE, 
                                              JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }

    public boolean update(final IdeAction ideAction, final Context context) {
        return false;
    }

    // Controller

    // ContextMenuListener

    public void poppingDown(final ContextMenu contextMenu) {
      // Nothing to do
    }

    public void poppingUp(final ContextMenu contextMenu) {
        final Element doc = contextMenu.getContext().getElement();
        // RelativeDirectoryContextFolder -> a package
        if (doc instanceof Project || doc instanceof JavaSourceNode || 
            doc instanceof PackageFolder) {
            contextMenu.add(pmdMenuItem);
            contextMenu.add(cpdMenuItem);
        }
    }

    public boolean handleDefaultAction(final Context context) {
        return false;
    }
    // ContextMenuListener

    public static String getVersion() {
        return Package.getPackage("net.sourceforge.pmd.jdeveloper").getImplementationVersion();
    }


    private void render(final RuleContext ctx) {
        ruleViolationPage.show();
        ruleViolationPage.clearAll();
        if (ctx.getReport().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No problems found", PMD_TITLE, 
                                          JOptionPane.INFORMATION_MESSAGE);
            final LogPage page = LogManager.getLogManager().getMsgPage();
            if (page instanceof LogWindow) {
                ((LogWindow)page).show();
            }
        } else {
            final List list = new ArrayList();
            for (final Iterator i = ctx.getReport().iterator(); i.hasNext(); ) {
                final RuleViolation viol = (RuleViolation)i.next();
                final Node node = (Node)pmdFileToNodeMap.get(viol.getFilename());
                list.add(new IdeLog.Message(Ide.getActiveWorkspace(), 
                                            Ide.getActiveProject(), 
                                            new IdeStorage(node), 
                                            viol.getDescription(), 2, 
                                            viol.getBeginLine(), 
                                            viol.getBeginColumn()));
            }
            ruleViolationPage.add(list);
        }
    }

    private void glomToCPD(final Iterator iter, final CPD cpd) throws IOException {
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (!(obj instanceof JavaSourceNode)) {
                continue;
            }
            final JavaSourceNode candidate = (JavaSourceNode)obj;
            if (candidate.getLongLabel().endsWith(".java") && 
                new File(candidate.getLongLabel()).exists()) {
                cpdFileToNodeMap.put(candidate.getLongLabel(), candidate);
                cpd.add(new File(candidate.getLongLabel()));
            }
        }
    }

    private void checkTree(final Iterator iter, final PMD pmd, final SelectedRules rules, 
                           final RuleContext ctx) throws IOException, PMDException {
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (!(obj instanceof JavaSourceNode)) {
                continue;
            }
            final JavaSourceNode candidate = (JavaSourceNode)obj;
            if (candidate.getLongLabel().endsWith(".java") && 
                new File(candidate.getLongLabel()).exists()) {
                pmdFileToNodeMap.put(candidate.getLongLabel(), candidate);
                ctx.setSourceCodeFilename(candidate.getLongLabel());
                final FileInputStream fis = 
                    new FileInputStream(new File(candidate.getLongLabel()));
                pmd.processFile(fis, rules.getSelectedRules(), ctx);
                fis.close();
            }
        }
        render(ctx);
    }

    private static final void logMessage(final String msg) {
        LogManager.getLogManager().showLog();
        LogManager.getLogManager().getMsgPage().log(msg + "\n");
    }
}
