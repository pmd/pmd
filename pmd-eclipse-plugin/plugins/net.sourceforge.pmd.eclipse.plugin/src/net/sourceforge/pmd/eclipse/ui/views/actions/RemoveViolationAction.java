package net.sourceforge.pmd.eclipse.ui.views.actions;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TableViewer;

/**
 * Deletes selected Violations Adapted from Phillipe Herlin
 * 
 * @author SebastianRaffel ( 21.05.2005 )
 */
public class RemoveViolationAction extends AbstractViolationSelectionAction {

    /**
     * Constructor
     * 
     * @param viewer
     */
    public RemoveViolationAction(TableViewer viewer) {
        super(viewer);
    }

 	protected String textId() { return StringKeys.MSGKEY_VIEW_ACTION_REMOVE_VIOLATION; }
 	
 	protected String imageId() { return PMDUiConstants.ICON_BUTTON_REMVIO; }
    
    protected String tooltipMsgId() { return StringKeys.MSGKEY_VIEW_TOOLTIP_REMOVE_VIOLATION; } 
    
    /**
     * Executes the Action
     */
    public void run() {
        // simply: get all Markers
        final IMarker[] markers = getSelectedViolations();
        if (markers == null) return;
        
        try {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            workspace.run(new IWorkspaceRunnable() {
                public void run(IProgressMonitor monitor) throws CoreException {
                    for (IMarker marker : markers) {                        
                        marker.delete();	// ... and delete them
                    }
                }
            }, null);
        } catch (CoreException ce) {
        	logErrorByKey(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION, ce);
        }
    }

}
