package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import oracle.ide.AddinManager;
import oracle.ide.ContextMenu;
import oracle.ide.Ide;
import oracle.ide.IdeAction;
import oracle.ide.log.LogWindow;
import oracle.ide.addin.Addin;
import oracle.ide.addin.Context;
import oracle.ide.addin.ContextMenuListener;
import oracle.ide.addin.Controller;
import oracle.ide.config.IdeSettings;
import oracle.ide.model.DirectoryFolder;
import oracle.ide.model.Element;
import oracle.ide.model.PackageFolder;
import oracle.ide.model.Workspace;
import oracle.ide.model.Workspaces;
import oracle.ide.panels.Navigable;
import oracle.jdeveloper.model.BusinessComponents;
import oracle.jdeveloper.model.EnterpriseJavaBeans;
import oracle.jdeveloper.model.JProject;
import oracle.jdeveloper.model.JavaSourceNode;
import oracle.jdeveloper.model.JavaSources;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.io.InputStream;
import java.util.Iterator;

public class Plugin implements Addin, Controller, ContextMenuListener {

    public static final String CHECK_CMD = "net.sourceforge.pmd.jdeveloper.Check";
    public static final int CHECK_CMD_ID = Ide.newCmd("PMDJDeveloperPlugin.CHECK_CMD_ID");

    private static final int INVALID = -1;
    private static final int SOURCE = 0;
    private static final int SOURCES = 1;
    private static final int WORKSPACE = 2;
    private static final int WORKSPACES = 3;
    private static final int PACKAGE = 4;
    private static final int DIRECTORY = 5;
    private static final int PROJECT = 6;
    private static final int EJB = 7;
    private static final int BUSINESS = 8;

    private JMenuItem checkItem;

    public Plugin() {
        super();
    }

    // Addin
    public void initialize() {
        AddinManager addinManager = Ide.getAddinManager();
        String command = addinManager.getCommand(CHECK_CMD_ID, CHECK_CMD);
        String category = "PMD";
        IdeAction action = IdeAction.get(CHECK_CMD_ID, command, "PMD", category, null, null, null, true);
        action.setController(this);
        checkItem = Ide.getMenubar().createMenuItem(action);
        checkItem.setText("PMD");
        checkItem.setMnemonic('P');
        Ide.getNavigatorManager().addContextMenuListener(this, null);
        Ide.getEditorManager().getContextMenu().addContextMenuListener(this, null);
        System.out.println("PMD JDeveloper Extension " + getVersion());
        IdeSettings.registerUI(new Navigable("PMD", SettingsPanel.class, new Navigable[] {}));
        Ide.getVersionInfo().addComponent("PMD", " JDeveloper Extension " + getVersion());
    }

    public void shutdown() {
        Ide.getNavigatorManager().removeContextMenuListener(this);
        Ide.getEditorManager().getContextMenu().removeContextMenuListener(this);
    }

    public float version() {
        return 0.1f;
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
        if (ideAction.getCommandId() == CHECK_CMD_ID) {
            try {
                LogWindow window = Ide.getLogWindow();

                if (!window.isVisible()) {
                    window.show();
                }

                PMD pmd = new PMD();
                RuleContext ctx = new RuleContext();
                ctx.setReport(new Report());
                ctx.setSourceCodeFilename(context.getElement().toString());
                SelectedRules rs = new SelectedRules();
                pmd.processFile(context.getDocument().getInputStream(), rs.getSelectedRules(), ctx);
                if (ctx.getReport().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No problems found", "PMD", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                        RuleViolation rv = (RuleViolation)i.next();
                        window.log(rv.getFilename() + ":" + rv.getLine() +":"+ rv.getDescription());
                        System.out.println("rv = " + rv.getDescription());
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
       }
        return true;
    }

    public boolean update(IdeAction ideAction, Context context) {
        return false;
    }

    public void checkCommands(Context context, Controller controller) {
    }
    // Controller

    // Controller
    public void poppingUp(ContextMenu contextMenu) {
        if (contextMenu != null) {
            Context context = contextMenu.getContext();
            if (context != null) {
                Element doc = context.getDocument();
                if (resolveType(doc) == PROJECT || resolveType(doc) == SOURCE) {
                    contextMenu.add(checkItem);
                }
            }
        }
    }

    public void poppingDown(ContextMenu contextMenu) {
    }

    public boolean handleDefaultAction(Context context) {
        return false;
    }
    // Controller

    public static String getVersion() {
        return Package.getPackage("net.sourceforge.pmd.jdeveloper").getImplementationVersion();
    }

    private int resolveType(Element element)
    {
        if (element instanceof JavaSourceNode) {
            return SOURCE;
        }
        if (element instanceof JavaSources) {
            return SOURCES;
        }
        if (element instanceof JProject) {
            return PROJECT;
        }
        if (element instanceof PackageFolder) {
            return PACKAGE;
        }
        if (element instanceof DirectoryFolder) {
            return DIRECTORY;
        }
        if (element instanceof BusinessComponents) {
            return BUSINESS;
        }
        if (element instanceof EnterpriseJavaBeans) {
            return EJB;
        }
        if (element instanceof Workspace) {
            return WORKSPACE;
        }
        if (element instanceof Workspaces) {
            return WORKSPACES;
        }
        return INVALID;
    }
}
