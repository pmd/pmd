package net.sourceforge.pmd.eclipse.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.cmd.ReviewCodeCmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
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
 * Revision 1.11  2005/05/07 13:32:06  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.10  2003/11/30 22:57:37  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.8.2.2  2003/11/04 16:27:19  phherlin
 * Refactor to use the adaptable framework instead of downcasting
 *
 * Revision 1.8.2.1  2003/10/30 22:09:51  phherlin
 * Simplify the code : moving the deep nested CountVisitor class as a first level nested inner class.
 * This also correct a rule violation from PMD.
 *
 * Revision 1.8  2003/08/13 20:08:40  phherlin
 * Refactoring private->protected to remove warning about non accessible member access in enclosing types
 *
 * Revision 1.7  2003/07/01 20:21:37  phherlin
 * Correcting some PMD violations ! (empty if stmt)
 *
 * Revision 1.6  2003/06/19 20:58:13  phherlin
 * Improve progress indicator accuracy
 *
 * Revision 1.5  2003/05/19 22:27:33  phherlin
 * Refactoring to improve performance
 *
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

    // Inner visitor to count number of childs of a resource
    private class CountVisitor implements IResourceVisitor {
        public int count = 0;
        public boolean visit(IResource resource) {
            boolean fVisitChildren = true;
            count++;

            if ((resource instanceof IFile)
                && (((IFile) resource).getFileExtension() != null)
                && ((IFile) resource).getFileExtension().equals("java")) {

                fVisitChildren = false;
            }

            return fVisitChildren;
        }
    };

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

            if (sel instanceof IStructuredSelection) {
                IStructuredSelection structuredSel = (IStructuredSelection) sel;
                int elementCount = countElement(structuredSel);
                monitor.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), elementCount);
                log.debug("Monitor beginTask(" + elementCount + ")");

                for (Iterator i = structuredSel.iterator(); i.hasNext();) {
                    Object element = i.next();

                    try {
                        if (element instanceof IAdaptable) {
                            IAdaptable adaptable = (IAdaptable) element;
                            IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                            if (resource != null) {
                                ReviewCodeCmd cmd = new ReviewCodeCmd();
                                cmd.setResource(resource);
                                cmd.setTaskMarker(true);
                                cmd.setMonitor(monitor);
                                cmd.performExecute();
                                
                            } else {
                                log.warn("The selected object cannot adapt to a resource");
                                log.debug("   -> selected object : " + element);
                            }
                        } else {
                            log.warn("The selected object is not adaptable");
                            log.debug("   -> selected object : " + element);
                        }                        
                    } catch (CommandException e) {
                        PMDPlugin.getDefault().showError(
                            PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION),
                            e);
                    }
                }

                monitor.done();
                log.debug("Monitor done");
            }

        }

        /**
         * Count the number of resources of a selection
         * @param selection a selection
         * @return the element count
         */
        private int countElement(IStructuredSelection selection) {
            CountVisitor visitor = new CountVisitor();

            for (Iterator i = selection.iterator(); i.hasNext();) {
                Object element = i.next();

                try {
                    if (element instanceof IAdaptable) {
                        IAdaptable adaptable = (IAdaptable) element;
                        IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                        if (resource != null) {
                            resource.accept(visitor);
                        } else {
                            log.warn("The selected object cannot adapt to a resource");
                            log.debug("   -> selected object : " + element);
                        }
                    } else {
                        log.warn("The selected object is not adaptable");
                        log.debug("   -> selected object : " + element);
                    }
                } catch (CoreException e) {
                    // Ignore any exception
                    PMDPlugin.getDefault().logError(
                        "Exception when counting the number of impacted elements when running PMD from menu",
                        e);
                }
            }

            return visitor.count;
        }
    }
}
