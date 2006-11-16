package net.sourceforge.pmd.ui.views;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.model.MarkerRecord;
import net.sourceforge.pmd.ui.model.PackageRecord;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Allows to filter Projects in the Violation Overview
 * 
 * @author SebastianRaffel  ( 17.05.2005 )
 */
public class ProjectFilter extends ViewerFilter {
	private List projectFilterList;
	
	
	/**
	 * Constructor
	 */
	public ProjectFilter() {
        super();
		projectFilterList = new ArrayList();
	}
	
	
	/* @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object) */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean select = true;
        
        AbstractPMDRecord projectRec = null;
		if (element instanceof PackageRecord) {
			projectRec = ((PackageRecord) element).getParent();
		} else if (element instanceof FileRecord) {
			projectRec = ((FileRecord) element).getParent().getParent();
		} else if (element instanceof MarkerRecord) {
            projectRec = ((MarkerRecord) element).getParent().getParent().getParent();
        }
		
		// if the Project to a File or Package is in the List
		// we don't want the Element to be shown
		if (projectFilterList.contains(projectRec)) {
			select = false;
        }
        
		return select;
	}
	
	/**
	 * Sets the List of projects to filter
	 * 
	 * @param newList
	 */
	public void setProjectFilterList(List newList) {
		this.projectFilterList = newList;
	}
	
	/**
	 * @return the List of filtered Projects
	 */
	public List getProjectFilterList() {
		return this.projectFilterList;
	}
	
	/**
	 * Adds a Project to the FilterList
	 * 
	 * @param project
	 */
	public void addProjectToList(AbstractPMDRecord project) {
		projectFilterList.add(project);
	}
	
	/**
	 * Removes a Project From the filterList
	 * 
	 * @param project
	 */
	public void removeProjectFromList(AbstractPMDRecord project) {
		projectFilterList.remove(project);
	}
}
