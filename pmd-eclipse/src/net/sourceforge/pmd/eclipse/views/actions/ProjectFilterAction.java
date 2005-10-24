package net.sourceforge.pmd.eclipse.views.actions;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.model.PMDRecord;
import net.sourceforge.pmd.eclipse.views.ProjectFilter;
import net.sourceforge.pmd.eclipse.views.ViolationOverview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Filters Projects in the Violation Overview
 * 
 * @author SebastianRaffel  ( 22.05.2005 )
 */
public class ProjectFilterAction extends Action {
	
	private ViolationOverview violationView;
	private ProjectFilter projectFilter;
	private PMDRecord project;
	
	
	/**
	 * Constructor
	 * 
	 * @param projectRecord, the project to show or hide
	 * @param view, the Violation Overview
	 */
	public ProjectFilterAction(PMDRecord projectRecord, ViolationOverview view) {
		violationView = view;
		project = projectRecord;
		
		// we need to get the viewes Filter
		ViewerFilter[] filters = view.getViewer().getFilters();
		for (int i=0; i<filters.length; i++) {
			if (filters[i] instanceof ProjectFilter)
				projectFilter = (ProjectFilter) filters[i];
		}
		
		// we set Image and Text for the Action
        setImageDescriptor(PMDPlugin.getDefault().getImageDescriptor(
        	PMDPlugin.ICON_PROJECT));
        setText(PMDPlugin.getDefault().getMessage(
        	PMDConstants.MSGKEY_VIEW_FILTER_PROJECT_PREFIX)
			+ " " + projectRecord.getName()); 
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


