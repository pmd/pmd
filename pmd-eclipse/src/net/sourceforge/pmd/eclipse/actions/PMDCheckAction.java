package net.sourceforge.pmd.eclipse.actions;

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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Implements action on the "Check code with PMD" action menu on a file
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.12  2005/10/24 22:39:00  phherlin
 * Integrating Sebastian Raffel's work
 * Refactor command processing
 * Revision 1.11 2005/05/07 13:32:06 phherlin
 * Continuing refactoring Fix some PMD violations Fix Bug 1144793 Fix Bug
 * 1190624 (at least try)
 * 
 * Revision 1.10 2003/11/30 22:57:37 phherlin Merging from eclipse-v2
 * development branch
 * 
 * Revision 1.8.2.2 2003/11/04 16:27:19 phherlin Refactor to use the adaptable
 * framework instead of downcasting
 * 
 * Revision 1.8.2.1 2003/10/30 22:09:51 phherlin Simplify the code : moving the
 * deep nested CountVisitor class as a first level nested inner class. This also
 * correct a rule violation from PMD.
 * 
 * Revision 1.8 2003/08/13 20:08:40 phherlin Refactoring private->protected to
 * remove warning about non accessible member access in enclosing types
 * 
 * Revision 1.7 2003/07/01 20:21:37 phherlin Correcting some PMD violations !
 * (empty if stmt)
 * 
 * Revision 1.6 2003/06/19 20:58:13 phherlin Improve progress indicator accuracy
 * 
 * Revision 1.5 2003/05/19 22:27:33 phherlin Refactoring to improve performance
 * 
 * Revision 1.4 2003/03/30 20:48:19 phherlin Adding logging Displaying error
 * dialog in a thread safe way Adding support for folders and package
 * 
 */
public class PMDCheckAction implements IObjectActionDelegate {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.actions.PMDCheckAction");
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction,
     *      IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        log.info("Check PMD action requested");

        try {
            ISelection selection = this.targetPart.getSite().getSelectionProvider().getSelection();
            if (selection instanceof IStructuredSelection) {
                reviewSelectedResources((IStructuredSelection) selection);
            }
        } catch (CommandException e) {
            PMDPlugin.getDefault().showError(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Prepare and run the reviewCode command for all selected resources
     * 
     * @param selection
     *            the selected resources
     */
    private void reviewSelectedResources(IStructuredSelection selection) throws CommandException {
        ReviewCodeCmd cmd = new ReviewCodeCmd();

        // Add selected resources to the list of resources to be reviewed
        for (Iterator i = selection.iterator(); i.hasNext();) {
            Object element = i.next();

            if (element instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) element;
                IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                if (resource != null) {
                    cmd.addResource(resource);
                } else {
                    log.warn("The selected object cannot adapt to a resource");
                    log.debug("   -> selected object : " + element);
                }
            } else {
                log.warn("The selected object is not adaptable");
                log.debug("   -> selected object : " + element);
            }
        }

        // Run the command
        cmd.setStepsCount(countElement(selection));
        cmd.setTaskMarker(true);
        cmd.setOpenPmdPerspective(PMDPlugin.getDefault().getPreferenceStore()
                .getInt(PMDPlugin.SHOW_PERSPECTIVE_ON_CHECK_PREFERENCE) == 1);
        cmd.setUserInitiated(true);
        cmd.performExecute();

    }

    /**
     * Count the number of resources of a selection
     * 
     * @param selection
     *            a selection
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
                        "Exception when counting the number of impacted elements when running PMD from menu", e);
            }
        }

        return visitor.count;
    }

    // Inner visitor to count number of childs of a resource
    private class CountVisitor implements IResourceVisitor {
        public int count = 0;

        public boolean visit(IResource resource) {
            boolean fVisitChildren = true;
            count++;

            if ((resource instanceof IFile) && (((IFile) resource).getFileExtension() != null)
                    && ((IFile) resource).getFileExtension().equals("java")) {

                fVisitChildren = false;
            }

            return fVisitChildren;
        }
    }
}
