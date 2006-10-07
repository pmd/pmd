package net.sourceforge.pmd.ui.views.actions;

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.ProjectFilter;
import net.sourceforge.pmd.ui.views.ViolationOverview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filters Projects in the Violation Overview
 * 
 * @author SebastianRaffel ( 22.05.2005 )
 */
public class ProjectFilterAction extends Action {

    private ViolationOverview violationView;
    private ProjectFilter projectFilter;
    private AbstractPMDRecord project;

    /**
     * Constructor
     * 
     * @param projectRecord, the project to show or hide
     * @param view, the Violation Overview
     */
    public ProjectFilterAction(AbstractPMDRecord projectRecord, ViolationOverview view) {
        violationView = view;
        project = projectRecord;

        // we need to get the viewes Filter
        ViewerFilter[] filters = view.getViewer().getFilters();
        for (int i = 0; i < filters.length; i++) {
            if (filters[i] instanceof ProjectFilter)
                projectFilter = (ProjectFilter) filters[i];
        }

        // we set Image and Text for the Action
        setImageDescriptor(PMDUiPlugin.getImageDescriptor(PMDUiConstants.ICON_PROJECT));
        setText(PMDUiPlugin.getDefault().getStringTable().getString(StringKeys.MSGKEY_VIEW_FILTER_PROJECT_PREFIX) + " "
                + projectRecord.getName());
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
        // the Filter contains a List of Projects to show
        // we add or remove our project to/from this List

        if (isChecked()) {
            projectFilter.removeProjectFromList(project);
        } else {
            projectFilter.addProjectToList(project);
        }

        violationView.refresh();
    }
}
