package net.sourceforge.pmd.eclipse.actions;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import net.sourceforge.pmd.eclipse.*;


public class PMDCheckFileAction implements IObjectActionDelegate {
	IWorkbenchPart targetPart;
	/**
	 * Constructor for Action1.
	 */
	public PMDCheckFileAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		String[] rulesetFiles = PMDPlugin.getDefault().getRuleSetsPreference();
		PMDVisitor visitor = null;
		try {
			visitor = new PMDVisitor(rulesetFiles);
			Object sel = targetPart.getSite().getSelectionProvider().getSelection();
			if (sel instanceof StructuredSelection) {
				StructuredSelection ss = (StructuredSelection)sel;
				for (Iterator iter = ss.iterator(); iter.hasNext(); ) {
					Object obj = iter.next();
					if (obj instanceof IFile) {
						((IFile)obj).accept(visitor);
					} 
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
