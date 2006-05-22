package net.sourceforge.pmd.ui.actions;

import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.DataflowView;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;


/**
 * 
 * @author SebastianRaffel  ( 26.05.2005 )
 */
public class ShowDataflowAction implements IObjectActionDelegate {
	
	private IWorkbenchPage workbenchPage;
	private DataflowView dataflowView;
	
	private IResource resource;
	private IMethod method;
	
	
	
	/* @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart) */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		workbenchPage = targetPart.getSite().getPage();
	}

	/* @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction) */
	public void run(IAction action) {
		if (workbenchPage != null) {
			try {
				dataflowView = (DataflowView)
					workbenchPage.showView(PMDUiConstants.ID_DATAFLOWVIEW);
				
				if (method != null)
					dataflowView.showMethod(method);
				
			} catch (PartInitException pie) {
				PMDUiPlugin.getDefault().logError(
					StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION + 
					this.toString(), pie);
			}
		}
	}
	
	
	/* @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection) */
	public void selectionChanged(IAction action, ISelection selection) {
		if (!PMDRuntimePlugin.getDefault().loadPreferences().isDfaEnabled()) {
			action.setEnabled(false);
			return;
		} else {
			action.setEnabled(true);
		}
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object element = sel.getFirstElement();
			
			method = null;
			resource = null;
			
			if (element instanceof IMethod) {
				method = (IMethod) element;
				resource = method.getResource();
			}
		}
	}
}


