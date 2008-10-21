package net.sourceforge.pmd.jdeveloper;

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

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Match;

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
import oracle.ide.log.LogManager;
import oracle.ide.log.LogPage;
import oracle.ide.log.LogWindow;
import oracle.ide.model.Element;
import oracle.ide.model.Node;
import oracle.ide.model.Project;
import oracle.ide.model.RelativeDirectoryContextFolder;
import oracle.ide.navigator.NavigatorManager;
import oracle.ide.panels.Navigable;
import oracle.ide.view.View;

import oracle.jdeveloper.compiler.IdeLog;
import oracle.jdeveloper.compiler.IdeStorage;
import oracle.jdeveloper.model.JavaSourceNode;


public class Plugin implements Addin, Controller, ContextMenuListener {


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
    private final transient Map pmdFileToNodeMap = 
        new HashMap(); // whew, this is kludgey

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

        /* TODO CPD plugin
        IdeAction cpdAction = 
            IdeAction.get(RUN_CPD_CMD_ID, AddinManager.getAddinManager().getCommand(RUN_CPD_CMD_ID, 
                                                                                    RUN_CPD_CMD), 
                          CPD_TITLE, CPD_TITLE, null, null, null, true);
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


    public boolean handleEvent(final IdeAction ideAction, 
                               final Context context) {
        if (!added) {
            addLogPage();
        }
        if (ideAction.getCommandId() == RUN_PMD_CMD_ID) {
            runPmd(context);
        } else if (ideAction.getCommandId() == RUN_CPD_CMD_ID) {
            runCpd(context);
        }
        return true;
    }

    private void runCpd(final Context context) {
        try {
            cpdViolationPage.cpdFileToNodeMap.clear();
            // TODO get minimum tokens from prefs panel
            final CPD cpd = 
                new CPD(100, new LanguageFactory().createLanguage("java"));

            // add all files to CPD
            if (context.getElement() instanceof 
                RelativeDirectoryContextFolder) {
                final RelativeDirectoryContextFolder folder = 
                    (RelativeDirectoryContextFolder)context.getElement();
                glomToCPD(folder.getChildren(), cpd);
            } else if (context.getElement() instanceof Project) {
                final Project project = (Project)context.getElement();
                glomToCPD(project.getChildren(), cpd);
            } else if (context.getElement() instanceof JavaSourceNode) {
                cpd.add(new File(context.getNode().getLongLabel()));
                cpdViolationPage.cpdFileToNodeMap.put(context.getNode().getLongLabel(), 
                                                      context.getNode());
            }

            cpd.go();

            cpdViolationPage.show();
            cpdViolationPage.clearAll();
            if (cpd.getMatches().hasNext()) {
                for (final Iterator i = cpd.getMatches(); i.hasNext(); ) {
                    cpdViolationPage.add((Match)i.next());
                }
            } else {
                JOptionPane.showMessageDialog(null, "No problems found", 
                                              CPD_TITLE, 
                                              JOptionPane.INFORMATION_MESSAGE);
                final LogPage page = LogManager.getLogManager().getMsgPage();
                if (page instanceof LogWindow) {
                    ((LogWindow)page).show();
                }
            }

        } catch (IOException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, CPD_TITLE);
        }
    }

    private void runPmd(final Context context) {
        try {
            pmdFileToNodeMap.clear();
            final PMD pmd = new PMD();
            Version.setJavaVersion(context, pmd);

            final SelectedRules rules = 
                new SelectedRules(SettingsPanel.createSettingsStorage());
            final RuleContext ctx = new RuleContext();
            ctx.setReport(new Report());
            if (context.getElement() instanceof 
                RelativeDirectoryContextFolder) {
                final RelativeDirectoryContextFolder folder = 
                    (RelativeDirectoryContextFolder)context.getElement();
                checkTree(folder.getChildren(), pmd, rules, ctx);
            } else if (context.getElement() instanceof Project) {
                final Project project = (Project)context.getElement();
                checkTree(project.getChildren(), pmd, rules, ctx);
            } else if (context.getElement() instanceof JavaSourceNode || 
                       context.getView() instanceof CodeEditor) {
                pmdFileToNodeMap.put(context.getNode().getLongLabel(), 
                                     context.getNode());
                ctx.setSourceCodeFilename(context.getNode().getLongLabel());
                pmd.processFile(context.getNode().getInputStream(), 
                                rules.getSelectedRules(), ctx);
                render(ctx);
            }
        } catch (IOException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, PMD_TITLE);
        } catch (PMDException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, PMD_TITLE);
        } catch (RuleSetNotFoundException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, PMD_TITLE);
        }
    }

    private void addLogPage() {
        LogManager.getLogManager().addPage(ruleViolationPage);
        LogManager.getLogManager().showLog();
        added = true;
    }

    public boolean update(final IdeAction ideAction, final Context context) {
        return false;
    }

    public void checkCommands(final Context context, 
                              final Controller controller) {
        // TODO don't know why plugin does not work after remove
    }
    // Controller

    // ContextMenuListener

    public void menuWillHide(final ContextMenu contextMenu) {
        // Nothing to do
    }

    public void menuWillShow(final ContextMenu contextMenu) {
        final Element doc = contextMenu.getContext().getElement();
        final View view = contextMenu.getContext().getView();
        // RelativeDirectoryContextFolder -> a package
        if (doc instanceof Project || doc instanceof JavaSourceNode || 
            doc instanceof RelativeDirectoryContextFolder || 
            view instanceof CodeEditor) {
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
            for (Iterator i = ctx.getReport().iterator(); i.hasNext(); ) {
                final RuleViolation viol = (RuleViolation)i.next();
                final Node node = 
                    (Node)pmdFileToNodeMap.get(viol.getFilename());
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

    private void glomToCPD(final Iterator iter, 
                           final CPD cpd) throws IOException {
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (!(obj instanceof JavaSourceNode)) {
                continue;
            }
            final JavaSourceNode candidate = (JavaSourceNode)obj;
            if (candidate.getLongLabel().endsWith(".java") && 
                new File(candidate.getLongLabel()).exists()) {
                cpdViolationPage.cpdFileToNodeMap.put(candidate.getLongLabel(), 
                                                      candidate);
                cpd.add(new File(candidate.getLongLabel()));
            }
        }
    }

    private void checkTree(final Iterator iter, final PMD pmd, 
                           final SelectedRules rules, 
                           final RuleContext ctx) throws IOException, 
                                                         PMDException {
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

}
