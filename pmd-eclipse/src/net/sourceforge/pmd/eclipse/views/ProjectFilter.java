package net.sourceforge.pmd.eclipse.views;

import java.util.ArrayList;

import net.sourceforge.pmd.eclipse.model.FileRecord;
import net.sourceforge.pmd.eclipse.model.PMDRecord;
import net.sourceforge.pmd.eclipse.model.PackageRecord;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * Allows to filter Projects in the Violation Overview
 * 
 * @author SebastianRaffel  ( 17.05.2005 )
 */
public class ProjectFilter extends ViewerFilter {
	
	private ArrayList projectFilterList;
	
	
	/**
	 * Constructor
	 */
	public ProjectFilter() {
		projectFilterList = new ArrayList();
	}
	
	
	/* @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object) */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		PMDRecord projectRec = null;
		if (element instanceof PackageRecord) {
			projectRec = ((PackageRecord) element).getParent();
		} else if (element instanceof FileRecord) {
			projectRec = ((FileRecord) element).getParent().getParent();
		}
		
		// if the Project to a File or Package is in the List
		// we don't want the Element to be shown
		if (projectFilterList.contains(projectRec))
			return false;
		
		return true;
	}
	
	/**
	 * Sets the List of projects to filter
	 * 
	 * @param newList
	 */
	public void setProjectFilterList(ArrayList newList) {
		projectFilterList = newList;
	}
	
	/**
	 * @return the List of filtered Projects
	 */
	public ArrayList getProjectFilterList() {
		return projectFilterList;
	}
	
	/**
	 * Adds a Project to the FilterList
	 * 
	 * @param project
	 */
	public void addProjectToList(PMDRecord project) {
		projectFilterList.add(project);
	}
	
	/**
	 * Removes a Project From the filterList
	 * 
	 * @param project
	 */
	public void removeProjectFromList(PMDRecord project) {
		projectFilterList.remove(project);
	}
}
