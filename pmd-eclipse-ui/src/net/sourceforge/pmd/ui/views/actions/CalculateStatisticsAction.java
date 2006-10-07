package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.model.PackageRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.ViolationOverview;

import org.eclipse.jface.action.Action;

public class CalculateStatisticsAction extends Action {
    public ViolationOverview violationView;
    
    public CalculateStatisticsAction(ViolationOverview view) {
        violationView = view;

        // se set Image and Tooltip-Text
        setImageDescriptor(PMDUiPlugin.getImageDescriptor(PMDUiConstants.ICON_BUTTON_CALCULATE));
        setToolTipText(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_TOOLTIP_CALCULATE_STATS));
    }
    
    /**
     * Executes the Action
     */
    public void run() {
        Object[] objects = violationView.getViewer().getVisibleExpandedElements();
        for (int i=0; i<objects.length; i++) {
            if (objects[i] instanceof PackageRecord) {
                PackageRecord record = (PackageRecord) objects[i];
                AbstractPMDRecord[] children = record.getChildren();
                for (int j=0; j<children.length; j++) {
                    if (children[j] instanceof FileRecord) {
                        FileRecord fileRecord = (FileRecord) children[j];
                        fileRecord.calculateLinesOfCode();
                        fileRecord.calculateNumberOfMethods();
                    }
                }
            }
        }
        violationView.refresh();
    }

}
