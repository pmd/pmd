package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.ViolationOverview;

import org.eclipse.jface.action.Action;

/**
 * Allows to Switch betwwen Package and Files or Files only view in the Violation Ovberview
 * 
 * @author SebastianRaffel ( 22.05.2005 )
 */
public class PackageSwitchAction extends Action {

    private ViolationOverview violationView;

    /**
     * Constructor
     * 
     * @param view
     */
    public PackageSwitchAction(ViolationOverview view) {
        violationView = view;

        // se set Image and Tooltip-Text
        setImageDescriptor(PMDUiPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_FILES));
        setToolTipText(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_TOOLTIP_PACKAGES_FILES));
    }

    /**
     * @return the Style, in which the Button is displayed
     */
    public int getStyle() {
        return AS_CHECK_BOX;
    }

    /**
     * Executes the Action
     */
    public void run() {
        // we simply use Functions declared in the Violations Overview
        // the View itself does the Rest, when refreshed
        /*if (isChecked()) {
            violationView.setPackageFiltered(true);
        } else {
            violationView.setPackageFiltered(false);
        }*/
        violationView.refresh();
    }

    /**
     * Sets an Action-Button as checked or unchecked
     * 
     * @param checked, true, if the Action should be checked, false otherwise
     */
    public void setChecked(boolean checked) {
        // we use this Function to change the Images of the Action's Button
        if (checked == true) {
            setImageDescriptor(PMDUiPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_PACKFILES));
        } else {
            setImageDescriptor(PMDUiPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_FILES));
        }
        super.setChecked(checked);
    }
}
