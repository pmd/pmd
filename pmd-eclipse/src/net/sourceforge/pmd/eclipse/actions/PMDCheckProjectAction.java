package net.sourceforge.pmd.eclipse.actions;

import java.io.IOException;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import net.sourceforge.pmd.eclipse.preferences.PMDPreferencePage;
import net.sourceforge.pmd.eclipse.util.Common;
import net.sourceforge.pmd.eclipse.util.ProgressDialog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import sun.security.krb5.internal.crypto.c;
import org.eclipse.ui.PlatformUI;

/**
 * Insert the type's description here.
 * @see IWorkbenchWindowActionDelegate
 */
public class PMDCheckProjectAction implements IObjectActionDelegate {
	IWorkbenchPart targetPart;
	/**
	 * The constructor.
	 */
	public PMDCheckProjectAction() {
		super();
	}
	
	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	/**
	 * Insert the method's description here.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action)  {
		String[] rulesetFiles = PMDPlugin.getDefault().getRuleSetsPreference();

		PMDVisitor visitor = null;
		try {
			visitor = new PMDVisitor(rulesetFiles);
			Common.PMD_DIALOG = new ProgressDialog(targetPart.getSite().getShell(), "PMD Status",  ProgressDialog.UNKNOWN);
			Common.PMD_DIALOG.open();
			PMDPlugin.getWorkspace().getRoot().accept( visitor );
			Common.PMD_DIALOG.close();
			Common.PMD_DIALOG = null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insert the method's description here.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection)  {
	}

	/**
	 * Insert the method's description here.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose()  {
	}

	/**
	 * Insert the method's description here.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window)  {
	}
}
