package net.sourceforge.pmd.eclipse.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Mark;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.eclipse.CPDReportWindow;
import net.sourceforge.pmd.eclipse.CPDVisitor;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Process CPD action menu. Run CPD against the selected project.
 * 
 * @author David Craine
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.7  2003/11/30 22:57:37  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.6.2.1  2003/11/04 16:27:19  phherlin
 * Refactor to use the adaptable framework instead of downcasting
 *
 * Revision 1.6  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 * Revision 1.5  2003/05/19 22:26:07  phherlin
 * Updating PMD engine to v1.05
 * Fixing CPD usage to conform to new engine implementation
 *
 */
public class CPDCheckProjectAction implements IObjectActionDelegate, IRunnableWithProgress {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.CPDCheckProjectAction");
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
            int minTileSize = PMDPlugin.getDefault().getPreferenceStore().getInt(PMDPlugin.MIN_TILE_SIZE_PREFERENCE);

            log.debug("Instantiating CPD for Java language with tile size to " + minTileSize);
            CPD cpd = new CPD(minTileSize, new LanguageFactory().createLanguage(LanguageFactory.JAVA_KEY));

            PMDPlugin.getDefault().getPreferenceStore().setDefault(PMDPlugin.MIN_TILE_SIZE_PREFERENCE, 25);
            CPDVisitor visitor = new CPDVisitor(cpd);
            CPDReportWindow crw = new CPDReportWindow(targetPart.getSite().getShell());
            crw.create();

            Object sel = targetPart.getSite().getSelectionProvider().getSelection();
            if (sel instanceof IStructuredSelection) {
                monitor.beginTask("Searhing for files...", IProgressMonitor.UNKNOWN);
                StructuredSelection ss = (StructuredSelection) sel;
                for (Iterator iter = ss.iterator(); iter.hasNext();) {
                    Object obj = iter.next();
                    if (obj instanceof IAdaptable) {
                        IAdaptable adaptable = (IAdaptable) obj;
                        IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                        if (resource != null) {
                            resource.accept(visitor);
                        } else {
                            log.warn("The selected object cannot adapt to a resource");
                            log.debug("   -> selected object : " + obj);
                        }
                    } else {
                        log.warn("The selected object is not adaptable");
                        log.warn("   -> selected object : " + obj);
                    }
                }

                log.debug("CPD start");
                monitor.beginTask("Running Cut & Paste Detector...", IProgressMonitor.UNKNOWN);
                cpd.go();
                monitor.beginTask("Building result set...", IProgressMonitor.UNKNOWN);
                log.debug("CPD stop");

                Iterator iter = cpd.getMatches();
                while (iter.hasNext()) {
                    Match match = (Match) iter.next();
                    crw.addEntry("=====================================\n");
                    crw.addEntry(
                        "Found a "
                            + match.getLineCount()
                            + " line ("
                            + match.getTokenCount()
                            + " tokens) duplication in the following files :\n");
                    log.debug(
                        "Found a "
                            + match.getLineCount()
                            + " line ("
                            + match.getTokenCount()
                            + " tokens) duplication in the following files :");
                    for (Iterator iter2 = match.iterator(); iter2.hasNext();) {
                        Mark mark = (Mark) iter2.next();
                        crw.addEntry("\tStarting at line " + mark.getBeginLine() + " of " + mark.getTokenSrcID() + "\n");
                        log.debug("   Starting at line " + mark.getBeginLine() + " of " + mark.getTokenSrcID());
                    }
                    crw.addEntry("\n");
                    crw.addEntry("-------------------------------------\n");
                    crw.addEntry(match.getSourceCodeSlice() + "\n");
                    crw.addEntry("=====================================\n");
                }
                monitor.done();
                crw.open();
                crw.getShell().setSize(800, 600);
            }
        } catch (Exception e) {
            MessageDialog.openError(null, "Error running CPD", e.toString());
            monitor.done();
        }

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     * Find all the selected projects and add their respsective java files into the 	 
     * CPD utility via the CPDVisitor.  Then run the CPD utility and display
     * the results.
     */
    public void run(IAction action) {
        try {
            new ProgressMonitorDialog(targetPart.getSite().getShell()).run(false, false, this);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().logError("Error while executing CPD", e);
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("CPD interrupted", e);
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

}
