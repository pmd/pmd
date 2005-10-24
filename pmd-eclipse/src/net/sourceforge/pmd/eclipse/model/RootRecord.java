package net.sourceforge.pmd.eclipse.model;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;


/**
 * PMDRecord for the WorkspaceRoot
 * creates ProjectRecords when instantiated
 * 
 * @author SebastianRaffel  ( 16.05.2005 )
 */
public class RootRecord extends PMDRecord {

	private IWorkspaceRoot workspaceRoot;
	private PMDRecord[] children;
	
	
	/**
	 * Constructor
	 * 
	 * @param root, the WorkspaceRoot
	 */
	public RootRecord(IWorkspaceRoot root) {
		workspaceRoot = root;
		children = createChildren();
	}
	
	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#getParent() */
	public PMDRecord getParent() {
		return this;
	}
	
	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#getChildren() */
	public PMDRecord[] getChildren() {
		return children;
	}

	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#getResource() */
	public IResource getResource() {
		return (IResource) workspaceRoot;
	}
	
	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#createChildren() */
	protected PMDRecord[] createChildren() {
		// get the projects
		IProject[] projects = workspaceRoot.getProjects();
		ArrayList projectList = new ArrayList();
		
		// ... and create Records for them
		for (int i=0; i<projects.length; i++) {
			ProjectRecord projRec = new ProjectRecord(projects[i], this);
			projectList.add( projRec );
		}
		
		// return the Array of children
		PMDRecord[] projectRecords = 
			new PMDRecord[projectList.size()];
		projectList.toArray(projectRecords);
		return projectRecords;
	}
	
	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#addResource(org.eclipse.core.resources.IResource) */
	public PMDRecord addResource(IResource resource) {
		// we only care about Projects
		if (resource instanceof IProject)
			return addProject((IProject) resource);
		return null;
	}
	
	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#removeResource(org.eclipse.core.resources.IResource) */
	public PMDRecord removeResource(IResource resource) {
		// we only care about Projects
		if (resource instanceof IProject)
			return removeProject((IProject) resource);
		return null;
	}
	
	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#getName() */
	public String getName() {
		return workspaceRoot.getName();
	}
	
	/* @see net.sourceforge.pmd.eclipse.model.PMDRecord#getResourceType() */
	public int getResourceType() {
		return PMDRecord.TYPE_ROOT;
	}
	
	/**
	 * Creates a new ProjectRecord and adds it to the List of ProjectRecords 
	 * 
	 * @param project
	 * @return the ProjectRecord created for the Project or null,
	 * if the Project is not open
	 */
	private ProjectRecord addProject(IProject project) {
		if (project.isOpen()) {
			ArrayList projects = getChildrenAsList();
			ProjectRecord projectRec = new ProjectRecord(project, this);
			projects.add(projectRec);
			
			children = new PMDRecord[projects.size()];
			projects.toArray(children);
			return projectRec;
		}
		return null;
	}
	
	/**
	 * Searches with a given Project for a Record containing this Project;
	 * removes and returns this ProjectRecord
	 * 
	 * @param project
	 * @return the removed ProjectRecord
	 */
	private ProjectRecord removeProject(IProject project) {
		ArrayList projects = getChildrenAsList();
		for (int k=0; k<projects.size(); k++) {
			ProjectRecord projectRec = (ProjectRecord) projects.get(k);
			IProject proj = (IProject) projectRec.getResource();
			
			// get the Project-Resource from the List and compare
			if (proj.equals(project)) {
				projects.remove(projectRec);
				
				children = new PMDRecord[projects.size()];
				projects.toArray(children);
				return projectRec;
			}
		}
		return null;
	}
}
