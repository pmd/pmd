package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
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
public class QuickFixAction extends ViolationSelectionAction {

    /**
     * Constructor
     * 
     * @param viewer
     */
    public QuickFixAction(TableViewer viewer) {
        super(viewer);

        setImageDescriptor(PMDPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_QUICKFIX));
        setText(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_ACTION_QUICKFIX));
        setToolTipText(PMDPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_TOOLTIP_QUICKFIX));
    }

    /**
     * Checks, if the Markers support QuickFix
     * 
     * @return true, if the Marker(s) support QuickFix, false otherwise
     */
    public boolean hasQuickFix() {
        boolean hasQuickFix = false;
        IMarker[] selectedMarkers = getSelectedViolations();

        if ((selectedMarkers != null) && (selectedMarkers.length == 1)) {
            hasQuickFix = IDE.getMarkerHelpRegistry().hasResolutions(selectedMarkers[0]);
        }

        return hasQuickFix;
    }

    /**
     * Executes the Action
     */
    public void run() {
        IMarker[] selectedMarkers = getSelectedViolations();
        IWorkbench workbench = PlatformUI.getWorkbench();
        IMarkerResolution resolutions[] = IDE.getMarkerHelpRegistry().getResolutions(selectedMarkers[0]);
        if (resolutions.length != 0) {
            MarkerResolutionSelectionDialog dialog = new MarkerResolutionSelectionDialog(workbench.getActiveWorkbenchWindow()
                    .getShell(), resolutions);
            if (dialog.open() == Dialog.OK) {
                Object[] result = dialog.getResult();
                if ((result != null) && (result.length > 0)) {
                    IMarkerResolution selectedResolution = (IMarkerResolution) result[0];
                    selectedResolution.run(selectedMarkers[0]);
                }
            }
        }
    }
}
