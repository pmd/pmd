package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.ViolationOverview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TableTreeViewer;

/**
 * Collapses the Violation Overview Tree
 * 
 * @author SebastianRaffel ( 22.05.2005 )
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
        setImageDescriptor(PMDUiPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_COLLAPSE));
        setToolTipText(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_TOOLTIP_COLLAPSE_ALL));
    }

    /**
     * Performs the Action
     */
    public void run() {
        // we just delegate to the TreeViewers Action
        treeViewer.collapseAll();
    }
}
