package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import oracle.ide.ContextMenu;
import oracle.ide.Ide;
import oracle.ide.IdeAction;
import oracle.ide.log.LogPage;
import oracle.ide.addin.Addin;
import oracle.ide.addin.Context;
import oracle.ide.addin.ContextMenuListener;
import oracle.ide.addin.Controller;
import oracle.ide.config.IdeSettings;
import oracle.ide.model.Document;
import oracle.ide.model.Element;
import oracle.ide.model.Project;
import oracle.ide.panels.Navigable;
import oracle.jdeveloper.model.JProject;
import oracle.jdeveloper.model.JavaSourceNode;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

public class Plugin implements Addin, Controller, ContextMenuListener {

    public static final String CHECK_CMD = "net.sourceforge.pmd.jdeveloper.Check";
    public static final int CHECK_CMD_ID = Ide.newCmd("PMDJDeveloperPlugin.CHECK_CMD_ID");
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
        IdeAction action = IdeAction.get(CHECK_CMD_ID, Ide.getAddinManager().getCommand(CHECK_CMD_ID, CHECK_CMD), TITLE, TITLE, null, null, null, true);
        action.setController(this);
        checkItem = Ide.getMenubar().createMenuItem(action);
        checkItem.setText(TITLE);
        checkItem.setMnemonic('P');
        Ide.getNavigatorManager().addContextMenuListener(this, null);
        Ide.getEditorManager().getContextMenu().addContextMenuListener(this, null);
        IdeSettings.registerUI(new Navigable(TITLE, SettingsPanel.class, new Navigable[] {}));
        Ide.getVersionInfo().addComponent(TITLE, " JDeveloper Extension " + getVersion());
        rvPage = new RuleViolationPage();
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

    private boolean added;

    public boolean handleEvent(IdeAction ideAction, Context context) {
        if (!added) {
            Ide.getLogManager().addPage(rvPage);
            Ide.getLogManager().showLog();
            added = true;
        }
        if (ideAction.getCommandId() == CHECK_CMD_ID) {
            try {
                PMD pmd = new PMD();
                SelectedRules rs = new SelectedRules(SettingsPanel.createSettingsStorage());
                RuleContext ctx = new RuleContext();
                ctx.setReport(new Report());
                if (resolveType(context.getDocument()) == PROJECT) {
                    for (Iterator i = ((Project)context.getDocument()).getListOfChildren().iterator(); i.hasNext();) {
                        Document candidate = (Document)i.next();
                        if (candidate.getLongLabel().endsWith(".java")) {
                            ctx.setSourceCodeFilename(candidate.getLongLabel());
                            FileInputStream fis = new FileInputStream(new File(candidate.getLongLabel()));
                            pmd.processFile(fis, rs.getSelectedRules(), ctx);
                            fis.close();
                        }
                    }
                    render(ctx);
                } else if (resolveType(context.getDocument()) == SOURCE) {
                    ctx.setSourceCodeFilename(context.getDocument().getLongLabel());
                    pmd.processFile(context.getDocument().getInputStream(), rs.getSelectedRules(), ctx);
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

    /**
     * TODO investigate CompilerPage as a replacement for RuleViolationPage; or could perhaps subclass it instead.
     */
    private void render(RuleContext ctx) {
        rvPage.show();
        rvPage.clearAll();
        if (ctx.getReport().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No problems found", TITLE, JOptionPane.INFORMATION_MESSAGE);
            Ide.getLogManager().getMsgPage().show();
        } else {
            for (Iterator i = ctx.getReport().iterator(); i.hasNext();) {
                rvPage.add((RuleViolation)i.next());
            }
        }
    }

    public boolean update(IdeAction ideAction, Context context) {
        return false;
    }

    public void checkCommands(Context context, Controller controller) {}
    // Controller

    // ContextMenuListener
    public void poppingUp(ContextMenu contextMenu) {
        Element doc = contextMenu.getContext().getDocument();
        if (resolveType(doc) == PROJECT || resolveType(doc) == SOURCE) {
            contextMenu.add(checkItem);
        }
    }

    public void poppingDown(ContextMenu contextMenu) {}

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
        } else if (element instanceof JProject) {
            return PROJECT;
        }
        return UNUSED;
    }
}
