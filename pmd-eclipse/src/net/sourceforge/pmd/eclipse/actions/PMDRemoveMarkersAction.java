package net.sourceforge.pmd.eclipse.actions;

import java.util.Iterator;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Process "Delete PMD Markers" action menu
 * 
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2003/05/19 22:27:33  phherlin
 * Refactoring to improve performance
 *
 * Revision 1.3  2003/03/30 20:49:37  phherlin
 * Adding logging
 * Displaying error dialog in a thread safe way
 * Adding support for folders and package
 *
 */
public class PMDRemoveMarkersAction implements IViewActionDelegate, IObjectActionDelegate {
    private static final String VIEW_ACTION = "net.sourceforge.pmd.eclipse.pmdRemoveAllMarkersAction";
    private static final String OBJECT_ACTION = "net.sourceforge.pmd.eclipse.pmdRemoveMarkersAction";
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.actions.PMDRemoveMarkersAction");
    private IViewPart viewPart;
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IViewActionDelegate#init(IViewPart)
     */
    public void init(IViewPart view) {
        this.viewPart = view;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        log.info("Remove Markers action requested");
        try {
            if (action.getId().equals(VIEW_ACTION)) {
                ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
            } else if (action.getId().equals(OBJECT_ACTION)) {
                processResource();
            } // else action id not supported
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * Process removing of makers on a resource selection (project or file)
     */
    private void processResource() {
        ISelection sel = targetPart.getSite().getSelectionProvider().getSelection();

        if (sel instanceof IStructuredSelection) {
            IStructuredSelection structuredSel = (IStructuredSelection) sel;
            for (Iterator i = structuredSel.iterator(); i.hasNext();) {
                Object element = i.next();

                try {
                    if (element instanceof IResource) {
                        ((IResource) element).deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    } else if (element instanceof ICompilationUnit) {
                        IResource resource = ((ICompilationUnit) element).getResource();
                        resource.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    } else if (element instanceof IJavaProject) {
                        IResource resource = ((IJavaProject) element).getResource();
                        resource.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    } else if (element instanceof IPackageFragment) {
                        IResource resource = ((IPackageFragment) element).getResource();
                        resource.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    } else if (element instanceof IPackageFragmentRoot) {
                        IResource resource = ((IPackageFragmentRoot) element).getResource();
                        resource.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    } else {// else no processing for other types
                        log.info(element.getClass().getName() + " : Removing markers on this resource's type is not supported");
                    }
                } catch (CoreException e) {
                    PMDPlugin.getDefault().showError(
                        PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION),
                        e);
                }
            }
        }
    }

}
