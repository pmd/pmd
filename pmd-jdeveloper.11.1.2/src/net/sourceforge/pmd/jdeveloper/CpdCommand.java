package net.sourceforge.pmd.jdeveloper;

import java.io.File;
import java.io.IOException;

import java.util.Iterator;

import javax.swing.JOptionPane;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Match;

import oracle.ide.Context;
import oracle.ide.Ide;
import oracle.ide.controller.Command;
import oracle.ide.extension.RegisteredByExtension;
import oracle.ide.log.LogManager;
import oracle.ide.log.LogPage;
import oracle.ide.log.LogWindow;
import oracle.ide.model.Project;
import oracle.ide.model.RelativeDirectoryContextFolder;

import oracle.jdeveloper.model.JavaSourceNode;


@RegisteredByExtension("net.sourceforge.pmd.jdeveloper")
public class CpdCommand extends Command {
    public static final int RUN_CPD_CMD_ID = Ide.findCmdID("net.sourceforge.pmd.jdeveloper.CheckCpd");
    private transient final CpdViolationPage cpdViolationPage = new CpdViolationPage();
    private boolean added = false;

    public CpdCommand() {
        super(RUN_CPD_CMD_ID);
    }

    @Override
    public int doit() {
        if (!added) {
            addLogPage();
        }
        runCpd(context);
        return OK;
    }

    private void runCpd(final Context context) {
        try {
            cpdViolationPage.cpdFileToNodeMap.clear();
            // TODO get minimum tokens from prefs panel
            final CPD cpd = new CPD(100, new LanguageFactory().createLanguage("java"));

            // add all files to CPD
            if (context.getElement() instanceof RelativeDirectoryContextFolder) {
                final RelativeDirectoryContextFolder folder = (RelativeDirectoryContextFolder)context.getElement();
                glomToCPD(folder.getChildren(), cpd);
            } else if (context.getElement() instanceof Project) {
                final Project project = (Project)context.getElement();
                glomToCPD(project.getChildren(), cpd);
            } else if (context.getElement() instanceof JavaSourceNode) {
                cpd.add(new File(context.getNode().getLongLabel()));
                cpdViolationPage.cpdFileToNodeMap.put(context.getNode().getLongLabel(), context.getNode());
            }

            cpd.go();

            cpdViolationPage.show();
            cpdViolationPage.clearAll();
            if (cpd.getMatches().hasNext()) {
                for (final Iterator i = cpd.getMatches(); i.hasNext(); ) {
                    cpdViolationPage.add((Match)i.next());
                }
            } else {
                JOptionPane.showMessageDialog(null, "No problems found", CpdAddin.CPD_TITLE, JOptionPane.INFORMATION_MESSAGE);
                final LogPage page = LogManager.getLogManager().getMsgPage();
                if (page instanceof LogWindow) {
                    ((LogWindow)page).show();
                }
            }

        } catch (IOException e) {
            Util.logMessage(e.getStackTrace());
            Util.showError(e, CpdAddin.CPD_TITLE);
        }
    }

    private void addLogPage() {
        LogManager.getLogManager().addPage(cpdViolationPage);
        LogManager.getLogManager().showLog();
        added = true;
    }

    private void glomToCPD(final Iterator iter, final CPD cpd) throws IOException {
        while (iter.hasNext()) {
            final Object obj = iter.next();
            if (!(obj instanceof JavaSourceNode)) {
                continue;
            }
            final JavaSourceNode candidate = (JavaSourceNode)obj;
            if (candidate.getLongLabel().endsWith(".java") && new File(candidate.getLongLabel()).exists()) {
                cpdViolationPage.cpdFileToNodeMap.put(candidate.getLongLabel(), candidate);
                cpd.add(new File(candidate.getLongLabel()));
            }
        }
    }
}
