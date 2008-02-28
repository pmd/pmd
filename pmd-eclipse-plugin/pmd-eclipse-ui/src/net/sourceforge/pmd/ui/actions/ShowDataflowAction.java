package net.sourceforge.pmd.ui.actions;

import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
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
	
	/* @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart) */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.workbenchPage = targetPart.getSite().getPage();
	}

	/* @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction) */
	public void run(IAction action) {
		if (this.workbenchPage != null) {
			try {
			    this.workbenchPage.showView(PMDUiConstants.ID_DATAFLOWVIEW);
				
			} catch (PartInitException pie) {
				PMDUiPlugin.getDefault().logError(
					StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION + 
					this.toString(), pie);
			}
		}
	}
	
	
	/* @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection) */
	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(true);
	}
}


