package net.sourceforge.pmd.eclipse.actions;

import java.util.Iterator;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
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
 * Revision 1.1  2003/03/17 23:30:30  phherlin
 * refactoring of pmd check action
 *
 */
public class PMDCheckAction implements IObjectActionDelegate {
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
        ISelection sel = targetPart.getSite().getSelectionProvider().getSelection();
        PMDVisitor visitor = new PMDVisitor(null);
        visitor.setUseTaskMarker(true);

        if (sel instanceof IStructuredSelection) {
            IStructuredSelection structuredSel = (IStructuredSelection) sel;
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
                    } // else no processing for other types
                } catch (CoreException e) {
                    MessageDialog.openError(
                        null,
                        PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_TITLE),
                        PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION) + e.toString());
                }
            }
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

}
