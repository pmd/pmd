package net.sourceforge.pmd.eclipse.actions;

import java.io.IOException;

import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import net.sourceforge.pmd.eclipse.preferences.PMDPreferencePage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Insert the type's description here.
 * @see IWorkbenchWindowActionDelegate
 */
public class PMDAction implements IWorkbenchWindowActionDelegate {
	/**
	 * The constructor.
	 */
	public PMDAction() {
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
			PMDPlugin.getWorkspace().getRoot().accept( visitor );
			
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
