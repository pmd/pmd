package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import oracle.ide.Addin;
import oracle.ide.AddinManager;
import oracle.ide.Context;
import oracle.ide.Ide;
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
import oracle.ide.navigator.NavigatorManager;
import oracle.ide.panels.Navigable;
import oracle.jdeveloper.compiler.IdeLog;
import oracle.jdeveloper.compiler.IdeStorage;
import oracle.jdeveloper.model.JavaSourceNode;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Plugin implements Addin, Controller, ContextMenuListener {

    public static final String CHECK_CMD = "net.sourceforge.pmd.jdeveloper.Check";
    public static final int CHECK_CMD_ID = Ide.createCmdID("PMDJDeveloperPlugin.CHECK_CMD_ID");
    public static final String TITLE = "PMD";

    private static final int UNUSED = -1;
    private static final int SOURCE = 0;
    private static final int PROJECT = 6;

    private JMenuItem checkItem;
    private RuleViolationPage rvPage;

    public Plugin() {
        super();
    }

    // Addin
    public void initialize() {
        IdeAction action = IdeAction.get(CHECK_CMD_ID, AddinManager.getAddinManager().getCommand(CHECK_CMD_ID, CHECK_CMD), TITLE, TITLE, null, null, null, true);
        action.addController(this);
        checkItem = Ide.getMenubar().createMenuItem(action);
        checkItem.setText(TITLE);
        checkItem.setMnemonic('P');
        NavigatorManager.getWorkspaceNavigatorManager().addContextMenuListener(this, null);
        EditorManager.getEditorManager().getContextMenu().addContextMenuListener(this, null);
        IdeSettings.registerUI(new Navigable(TITLE, SettingsPanel.class, new Navigable[] {}));
        Ide.getVersionInfo().addComponent(TITLE, " JDeveloper Extension " + version());
        rvPage = new RuleViolationPage();
    }

    public void shutdown() {
        NavigatorManager.getWorkspaceNavigatorManager().removeContextMenuListener(this);
        EditorManager.getEditorManager().getContextMenu().removeContextMenuListener(this);
    }

    public float version() {
        return 1.7f;
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
    private boolean added;

    // whew, this is kludgey
    private Map fileToNodeMap = new HashMap();

    public boolean handleEvent(IdeAction ideAction, Context context) {
        if (!added) {
            LogManager.getLogManager().addPage(rvPage);
            LogManager.getLogManager().showLog();
            added = true;
        }
        if (ideAction.getCommandId() == CHECK_CMD_ID) {
            try {
                fileToNodeMap.clear();
                PMD pmd = new PMD();
                SelectedRules rs = new SelectedRules(SettingsPanel.createSettingsStorage());
                RuleContext ctx = new RuleContext();
                ctx.setReport(new Report());
                if (context.getElement() instanceof Project) {
                    Project project = (Project)context.getElement();
                    for (Iterator i = project.getChildren(); i.hasNext();) {
                        Object obj = i.next();
                        if (!(obj instanceof JavaSourceNode)) {
                            System.out.println("PMD plugin expected a JavaSourceNode, found a " + obj.getClass() + " instead.  Odd.");
                            continue;
                        }
                        JavaSourceNode candidate = (JavaSourceNode)obj;
                        if (candidate.getLongLabel().endsWith(".java") && new File(candidate.getLongLabel()).exists()) {
                            fileToNodeMap.put(candidate.getLongLabel(), candidate);
                            ctx.setSourceCodeFilename(candidate.getLongLabel());
                            FileInputStream fis = new FileInputStream(new File(candidate.getLongLabel()));
                            pmd.processFile(fis, rs.getSelectedRules(), ctx);
                            fis.close();
                        }
                    }
                    render(ctx);
                } else if (resolveType(context.getElement()) == SOURCE) {
                    fileToNodeMap.put(context.getNode().getLongLabel(), context.getNode());
                    ctx.setSourceCodeFilename(context.getNode().getLongLabel());
                    pmd.processFile(context.getNode().getInputStream(), rs.getSelectedRules(), ctx);
                    render(ctx);
                }
                return true;
            } catch (PMDException e) {
                e.printStackTrace();
                e.getReason().printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while running PMD: " + e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while running PMD: " + e.getMessage(), TITLE, JOptionPane.ERROR_MESSAGE);
            }
       }
        return true;
    }

    private void render(RuleContext ctx) {
        rvPage.show();
        rvPage.clearAll();
        if (ctx.getReport().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No problems found", TITLE, JOptionPane.INFORMATION_MESSAGE);
            LogPage page = LogManager.getLogManager().getMsgPage();
            if (page instanceof LogWindow) {
              ((LogWindow)page).show();
            }
        } else {
            List list = new ArrayList();
            for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                RuleViolation rv = (RuleViolation)i.next();
                Node node = (Node)fileToNodeMap.get(rv.getFilename());
                list.add(new IdeLog.Message(Ide.getActiveWorkspace(), Ide.getActiveProject(), new IdeStorage(node), rv.getDescription(), 2, rv.getNode().getBeginLine()+1, rv.getNode().getBeginColumn()));
            }
            rvPage.add(list);
        }
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
     if (resolveType(doc) == PROJECT || resolveType(doc) == SOURCE) {
         contextMenu.add(checkItem);
     }
   }

    public boolean handleDefaultAction(Context context) {
        return false;
    }
    // ContextMenuListener

    public static String getVersion() {
        return Package.getPackage("net.sourceforge.pmd.jdeveloper").getImplementationVersion();
    }

    private int resolveType(Element element) {
        if (element instanceof JavaSourceNode) {
            return SOURCE;
        } else if (element instanceof Project) {
            return PROJECT;
        }
        return UNUSED;
    }
}
