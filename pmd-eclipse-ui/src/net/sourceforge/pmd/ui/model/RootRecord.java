/*
 * Created on 7 mai 2005
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;


/**
 * AbstractPMDRecord for the WorkspaceRoot
 * creates ProjectRecords when instantiated
 * 
 * @author SebastianRaffel  ( 16.05.2005 ), Philippe Herlin, Sven Jacob
 * @version $$Revision$$
 * 
 * $$Log$
 * $Revision 1.3  2006/10/07 16:01:21  phherlin
 * $Integrate Sven updates
 * $$
 * 
 */
public class RootRecord extends AbstractPMDRecord {

	private IWorkspaceRoot workspaceRoot;
	private AbstractPMDRecord[] children;
	
	
	/**
	 * Constructor
	 * 
	 * @param root, the WorkspaceRoot
	 */
	public RootRecord(IWorkspaceRoot root) {
		workspaceRoot = root;
		children = createChildren();
	}
	
	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getParent() */
	public AbstractPMDRecord getParent() {
		return this;
	}
	
	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getChildren() */
	public AbstractPMDRecord[] getChildren() {
		return children;
	}

	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResource() */
	public IResource getResource() {
		return (IResource) workspaceRoot;
	}
	
	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#createChildren() */
	protected final AbstractPMDRecord[] createChildren() {
		// get the projects
		IProject[] projects = workspaceRoot.getProjects();
		ArrayList projectList = new ArrayList();
		
		// ... and create Records for them
		for (int i=0; i<projects.length; i++) {
			ProjectRecord projRec = new ProjectRecord(projects[i], this);
			projectList.add( projRec );
		}
		
		// return the Array of children
		AbstractPMDRecord[] projectRecords = 
			new AbstractPMDRecord[projectList.size()];
		projectList.toArray(projectRecords);
		return projectRecords;
	}
	
	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#addResource(org.eclipse.core.resources.IResource) */
	public AbstractPMDRecord addResource(IResource resource) {
		// we only care about Projects
		if (resource instanceof IProject)
			return addProject((IProject) resource);
		return null;
	}
	
	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#removeResource(org.eclipse.core.resources.IResource) */
	public AbstractPMDRecord removeResource(IResource resource) {
		// we only care about Projects
		if (resource instanceof IProject)
			return removeProject((IProject) resource);
		return null;
	}
	
	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getName() */
	public String getName() {
		return workspaceRoot.getName();
	}
	
	/* @see net.sourceforge.pmd.ui.model.AbstractPMDRecord#getResourceType() */
	public int getResourceType() {
		return AbstractPMDRecord.TYPE_ROOT;
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
			List projects = getChildrenAsList();
			ProjectRecord projectRec = new ProjectRecord(project, this);
			projects.add(projectRec);
			
			children = new AbstractPMDRecord[projects.size()];
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
		List projects = getChildrenAsList();
		for (int k=0; k<projects.size(); k++) {
			ProjectRecord projectRec = (ProjectRecord) projects.get(k);
			IProject proj = (IProject) projectRec.getResource();
			
			// get the Project-Resource from the List and compare
			if (proj.equals(project)) {
				projects.remove(projectRec);
				
				children = new AbstractPMDRecord[projects.size()];
				projects.toArray(children);
				return projectRec;
			}
		}
		return null;
	}
}
