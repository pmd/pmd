package net.sourceforge.pmd.eclipse.ui.views.actions;

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IMarkerHelpRegistry;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.MarkerResolutionSelectionDialog;
import org.eclipse.ui.ide.IDE;

/**
 * Enables the QuickFix Action for a Marker Adapted from Phillipe Herlin
 * 
 * @author SebastianRaffel ( 21.05.2005 )
 */
public class QuickFixAction extends AbstractViolationSelectionAction {

    /**
     * Constructor
     * 
     * @param viewer
     */
    public QuickFixAction(TableViewer viewer) {
        super(viewer);
    }

 	protected String textId() { return StringKeys.VIEW_ACTION_QUICKFIX; }
 	
 	protected String imageId() { return PMDUiConstants.ICON_BUTTON_QUICKFIX; }
    
    protected String tooltipMsgId() { return StringKeys.VIEW_TOOLTIP_QUICKFIX; }    
    
    /**
     * Checks, if the Markers support QuickFix
     * 
     * @return true, if the Marker(s) support QuickFix, false otherwise
     */
    public boolean hasQuickFix() {
    	
    	if (!hasSelections()) return false;
    	        
        IMarkerHelpRegistry registry = IDE.getMarkerHelpRegistry();
        
        // must have resolutions for all of them to be valid
        for (IMarker marker : getSelectedViolations()) {
            if (!registry.hasResolutions(marker)) return false;
        }

        return true;
    }

    /**
     * Executes the Action
     */
    public void run() {
        IMarker[] selectedMarkers = getSelectedViolations();
        IWorkbench workbench = PlatformUI.getWorkbench();
        // TODO handle multiple selections
        IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry().getResolutions(selectedMarkers[0]);
        if (resolutions.length != 0) {
            MarkerResolutionSelectionDialog dialog = new MarkerResolutionSelectionDialog(workbench.getActiveWorkbenchWindow().getShell(), resolutions);
            if (dialog.open() == Window.OK) {
                Object[] result = dialog.getResult();
                if ((result != null) && (result.length > 0)) {
                    IMarkerResolution selectedResolution = (IMarkerResolution) result[0];
                    selectedResolution.run(selectedMarkers[0]);
                }
            }
        }
    }
}
