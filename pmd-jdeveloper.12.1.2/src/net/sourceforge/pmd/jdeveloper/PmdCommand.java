package net.sourceforge.pmd.jdeveloper;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceCodeProcessor;

import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.ceditor.CodeEditor;
import oracle.ide.controller.Command;
import oracle.ide.extension.RegisteredByExtension;
import oracle.ide.log.LogManager;
import oracle.ide.log.LogPage;
import oracle.ide.log.LogWindow;
import oracle.ide.model.DataContainer;
import oracle.ide.model.Element;
import oracle.ide.model.Node;
import oracle.ide.model.Project;
import oracle.ide.model.RelativeDirectoryContextFolder;
import oracle.ide.model.Workspace;

import oracle.jdeveloper.compiler.IdeLog;
import oracle.jdeveloper.compiler.IdeStorage;
import oracle.jdeveloper.model.JavaSourceNode;


@RegisteredByExtension("net.sourceforge.pmd.jdeveloper")
public class PmdCommand extends Command {

    public static final int RUN_PMD_CMD_ID = Ide.findCmdID("net.sourceforge.pmd.jdeveloper.CheckPmd");
    private transient Map pmdFileToNodeMap = new HashMap(); // whew, this is kludgey

    public PmdCommand() {
        super(RUN_PMD_CMD_ID);
    }

    @Override
    public int doit() {
        if (!PmdAddin.added) {
            addLogPage();
        }
        runPmd(context);
        return OK;
    }

    private void runPmd(final Context context) {
        try {
            pmdFileToNodeMap.clear();
            final PMD pmd = new PMD();
            // TODO ? Version.setJavaVersion(context, pmd);

            final PmdSelectedRules rules = new PmdSelectedRules(PmdSettingsPanel.createSettingsStorage());
            final RuleContext ctx = new RuleContext();
            ctx.setReport(new Report());
            // Util.logMessage(context.getElement().getLongLabel());
            if (context.getElement() instanceof RelativeDirectoryContextFolder) {
                final RelativeDirectoryContextFolder folder = (RelativeDirectoryContextFolder)context.getElement();
                checkTree(folder.getChildren(), pmd, rules, ctx);
                render(ctx);
            } else if (context.getElement() instanceof Workspace) {
                final Workspace workspace = (Workspace)context.getElement();
                checkTree(workspace.getChildren(), pmd, rules, ctx);
                render(ctx);
            } else if (context.getElement() instanceof Project) {
                final Project project = (Project)context.getElement();
                checkTree(project.getChildren(), pmd, rules, ctx);
                render(ctx);
            } else if (context.getElement() instanceof JavaSourceNode || context.getView() instanceof CodeEditor) {
                final Node candidate = context.getNode();
                processFile(rules, ctx, candidate);
                render(ctx);
            }
        } catch (IOException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, PmdAddin.PMD_TITLE);
        } catch (PMDException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, PmdAddin.PMD_TITLE);
        } catch (RuleSetNotFoundException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, PmdAddin.PMD_TITLE);
        }
    }

    private void processFile(final PmdSelectedRules rules, final RuleContext ctx,
                             final Node candidate) throws net.sourceforge.pmd.PMDException, java.io.IOException {
        pmdFileToNodeMap.put(candidate.getLongLabel(), candidate);
        ctx.setSourceCodeFilename(candidate.getLongLabel());
        SourceCodeProcessor scp = new SourceCodeProcessor(new PMDConfiguration());
        RuleSets rss = new RuleSets();
        rss.addRuleSet(rules.getSelectedRules());
        scp.processSourceCode(candidate.getInputStream(), rss, ctx);
    }

    private void addLogPage() {
        LogManager.getLogManager().addPage(PmdAddin.pmdViolationPage);
        LogManager.getLogManager().showLog();
        PmdAddin.added = true;
    }

    private void checkTree(final Iterator iter, final PMD pmd, final PmdSelectedRules rules,
                           final RuleContext ctx) throws IOException, PMDException {
        while (iter.hasNext()) {
            final Object obj = iter.next();
            final Element el = (Element)obj;
            DataContainer dataContainer;
            // Util.logMessage(el.getLongLabel() + ":: " + obj.getClass().getName());
            try {
                dataContainer = ((DataContainer)obj);
                Iterator children = dataContainer.getChildren();
                if (!children.equals(null)) {
                     checkTree(children, pmd, rules, ctx);
                } 
            } catch (ClassCastException e) {
                if ((obj instanceof JavaSourceNode)) {
                   final JavaSourceNode candidate = (JavaSourceNode)obj;
                   processFile(rules, ctx, candidate);
                } else {
                    continue;
                }
            }
        }
    }

    private void render(final RuleContext ctx) {
        PmdAddin.pmdViolationPage.show();
        PmdAddin.pmdViolationPage.clearAll();
        if (ctx.getReport().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No problems found", PmdAddin.PMD_TITLE,
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
                list.add(new IdeLog.Message(Ide.getActiveWorkspace(), Ide.getActiveProject(), new IdeStorage(node),
                                            viol.getDescription(), 2, viol.getBeginLine(), viol.getBeginColumn()));
            }
            PmdAddin.pmdViolationPage.add(list);
        }
    }
}
