package net.sourceforge.pmd.eclipse.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Implements action on the "Check code with PMD" action menu on a file
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2003/03/30 20:48:19  phherlin
 * Adding logging
 * Displaying error dialog in a thread safe way
 * Adding support for folders and package
 *
 */
public class PMDCheckAction implements IObjectActionDelegate {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.actions.PMDCheckAction");
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        log.info("Check PMD action requested");
        ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        try {
            // selection must be acquired in this thread, otherwise an exception would be raised
            monitorDialog.run(true, true, new CheckPMDTask(targetPart.getSite().getSelectionProvider().getSelection()));
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().logError("Error when processing PMD action", e.getTargetException());
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Check PMD action interrupted", e);
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    // Inner class to run PMD in a thread
    private class CheckPMDTask implements IRunnableWithProgress {
        private ISelection sel;

        /**
         * Constructor
         */
        public CheckPMDTask(ISelection sel) {
            this.sel = sel;
        }

        /**
        * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
        */
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            PMDVisitor visitor = new PMDVisitor(monitor);
            visitor.setUseTaskMarker(true);

            if (sel instanceof IStructuredSelection) {
                IStructuredSelection structuredSel = (IStructuredSelection) sel;
                monitor.beginTask(
                    PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING),
                    structuredSel.toArray().length);

                for (Iterator i = structuredSel.iterator(); i.hasNext();) {
                    Object element = i.next();

                    try {
                        if (element instanceof IResource) {
                            ((IResource) element).accept(visitor);
                        } else if (element instanceof ICompilationUnit) {
                            IResource resource = ((ICompilationUnit) element).getResource();
                            resource.accept(visitor);
                        } else if (element instanceof IJavaProject) {
                            IResource resource = ((IJavaProject) element).getResource();
                            resource.accept(visitor);
                        } else if (element instanceof IPackageFragment) {
                            IResource resource = ((IPackageFragment) element).getResource();
                            resource.accept(visitor);
                        } else { // else no processing for other types
                            monitor.worked(1);
                        }
                    } catch (CoreException e) {
                        PMDPlugin.getDefault().showError(
                            PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION),
                            e);
                    }
                }

                monitor.done();
            }
        }
    }
}
