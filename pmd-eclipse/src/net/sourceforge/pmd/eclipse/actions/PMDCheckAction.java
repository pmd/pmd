package net.sourceforge.pmd.eclipse.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import net.sourceforge.pmd.eclipse.PMDVisitorRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
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
                int elementCount = countElement(structuredSel);
                monitor.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), elementCount);
                log.debug("Monitor beginTask(" + elementCount + ")");

                for (Iterator i = structuredSel.iterator(); i.hasNext();) {
                    Object element = i.next();

                    try {
                        if (element instanceof IResource) {
                            new PMDVisitorRunner().run((IResource) element, visitor);
                        } else if (element instanceof ICompilationUnit) {
                            IResource resource = ((ICompilationUnit) element).getResource();
                            new PMDVisitorRunner().run(resource, visitor);
                        } else if (element instanceof IJavaProject) {
                            IResource resource = ((IJavaProject) element).getResource();
                            new PMDVisitorRunner().run(resource, visitor);
                        } else if (element instanceof IPackageFragment) {
                            IResource resource = ((IPackageFragment) element).getResource();
                            new PMDVisitorRunner().run(resource, visitor);
                        } else if (element instanceof PackageFragmentRoot) {
                            IResource resource = ((PackageFragmentRoot) element).getResource();
                            new PMDVisitorRunner().run(resource, visitor);
                        } else { // else no processing for other types
                            log.info(element.getClass().getName() + " : PMD check on this resource's type is not supported");
                        }
                    } catch (CoreException e) {
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
            final class CountVisitor implements IResourceVisitor {
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

            CountVisitor visitor = new CountVisitor();

            for (Iterator i = selection.iterator(); i.hasNext();) {
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
                    } else if (element instanceof PackageFragmentRoot) {
                        IResource resource = ((PackageFragmentRoot) element).getResource();
                        resource.accept(visitor);
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
