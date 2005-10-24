package net.sourceforge.pmd.eclipse.views.actions;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.views.ViolationOverview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableTreeViewer;


/**
 * Collapses the Violation Overview Tree
 * 
 * @author SebastianRaffel  ( 22.05.2005 )
 */
public class CollapseAllAction extends Action {
	
	private TableTreeViewer treeViewer;
	
	
	/**
	 * Constructor
	 * 
	 * @param view, the Violation Overview
	 */
	public CollapseAllAction(ViolationOverview view) {
		treeViewer = view.getViewer();
		
		// we set Image and ToolTip for this
		setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(
			PMDPlugin.ICON_BUTTON_COLLAPSE));
		setToolTipText(PMDPlugin.getDefault().getMessage(
			PMDConstants.MSGKEY_VIEW_TOOLTIP_COLLAPSE_ALL));
	}
	
	/**
	 * Performs the Action
	 */
	public void run() {
		// we just delegate to the TreeViewers Action
		treeViewer.collapseAll();
	}
}


