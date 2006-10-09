/*
 * Created on 9 mai 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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

package net.sourceforge.pmd.ui.views;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.model.FileRecord;
import net.sourceforge.pmd.ui.model.PackageRecord;
import net.sourceforge.pmd.ui.model.ProjectRecord;
import net.sourceforge.pmd.ui.model.RootRecord;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * Provides the Violation Overview with Content Content Elements can be
 * PackageRecords or FileRecords
 * 
 * @author SebastianRaffel ( 09.05.2005 ), Philppe Herlin, Sven Jacob
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2006/10/09 13:26:40  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 */
public class ViolationOverviewContentProvider implements ITreeContentProvider, IStructuredContentProvider, IResourceChangeListener {
    protected static final Object[] NO_CHILDREN = new Object[0];
    protected boolean filterPackages;

    final private ViolationOverview violationView;
    private TreeViewer treeViewer;

    private RootRecord root;

    /**
     * Constructor
     * 
     * @param view
     */
    public ViolationOverviewContentProvider(ViolationOverview view) {
        super();

        this.violationView = view;
        this.treeViewer = view.getViewer();
    }

    /* @see org.eclipse.jface.viewers.IContentProvider#dispose() */
    public void dispose() {
        if (root != null) {
            IWorkspaceRoot workspaceRoot = (IWorkspaceRoot) root.getResource();
            workspaceRoot.getWorkspace().removeResourceChangeListener(this);
        }
    }

    /* @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object) */
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    /* @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object) */
    public Object[] getChildren(Object parentElement) {
        // if we got a WorkspaceRoot
        if ((parentElement instanceof IWorkspaceRoot) || (parentElement instanceof RootRecord)) {

            // ... we care about its Project's
            List projects = root.getChildrenAsList();
            ProjectRecord[] projectArray = new ProjectRecord[projects.size()];
            projects.toArray(projectArray);

            // we make a List of all Packages
            ArrayList packages = new ArrayList();
            for (int i = 0; i < projectArray.length; i++) {
                if (projectArray[i].isProjectOpen())
                    packages.addAll(projectArray[i].getChildrenAsList());
            }

            if (!violationView.isPackageFiltered()) {
                // we can show Packages
                return packages.toArray();
            } else {
                // ... or only Files
                ArrayList files = new ArrayList();
                for (int j = 0; j < packages.size(); j++) {
                    AbstractPMDRecord packageRec = (AbstractPMDRecord) packages.get(j);
                    files.addAll(packageRec.getChildrenAsList());
                }
                return files.toArray();
            }
        } else if (parentElement instanceof PackageRecord) {
            // here we show the Files contained in a Package
            return ((PackageRecord) parentElement).getChildren();
        }

        return NO_CHILDREN;
    }

    /* @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object) */
    public Object getParent(Object element) {
        if (element instanceof FileRecord) {
            // the Parent to a FileRecord is its PackageRecord
            if (!violationView.isPackageFiltered())
                return ((FileRecord) element).getParent();
            else
                return root;
        } else if (element instanceof PackageRecord) {
            // the Parent to a PackageRecord is the Root
            return root;
        }

        return null;
    }

    /* @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object) */
    public boolean hasChildren(Object element) {
        if (element instanceof IWorkspaceRoot) {
            if (root.getChildren().length > 0)
                return true;
        } else if (element instanceof PackageRecord) {
            if (((PackageRecord) element).hasMarkers())
                return true;
        }
        return false;
    }

    /*
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
     *      java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.treeViewer = (TreeViewer) viewer;

        // this is called, when the View is instantiated and gets Input
        // or if the Source of Input changes

        // we remove an existing ResourceChangeListener
        IWorkspaceRoot workspaceRoot = null;
        if (root != null) {
            workspaceRoot = (IWorkspaceRoot) root.getResource();
            workspaceRoot.getWorkspace().removeResourceChangeListener(this);
        }

        // ... to add a new one, so we can listen to Changes made
        // to Resources in the Workspace
        if (newInput instanceof IWorkspaceRoot) {
            // either we got a WorkspaceRoot
            workspaceRoot = (IWorkspaceRoot) newInput;
            root = new RootRecord(workspaceRoot);
            workspaceRoot.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
        } else if (newInput instanceof RootRecord) {
            // ... or already a Record for it
            root = (RootRecord) newInput;
            workspaceRoot = (IWorkspaceRoot) root.getResource();
            workspaceRoot.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
        }
    }

    /* @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent) */
    public void resourceChanged(IResourceChangeEvent event) {
        IMarkerDelta[] markerDeltas = event.findMarkerDeltas(PMDRuntimeConstants.PMD_MARKER, true);

        // first we get a List of changes to Files and Projects
        // so we won't need updating everything
        ArrayList changedFiles = new ArrayList();
        ArrayList changedProjects = new ArrayList();
        IResource resource = null;
        for (int i = 0; i < markerDeltas.length; i++) {
            resource = markerDeltas[i].getResource();
            IProject project = resource.getProject();

            // the lists should not contain
            // Projects or Resources twice

            if (!changedFiles.contains(resource))
                changedFiles.add(resource);

            if (!changedProjects.contains(project))
                changedProjects.add(project);
        }

        // we can add, change or remove Resources
        // all this changes are given to the viewer later
        final ArrayList additions = new ArrayList();
        final ArrayList removals = new ArrayList();
        final ArrayList changes = new ArrayList();

        // we got through the changed Projects
        for (int i = 0; i < changedProjects.size(); i++) {
            IProject project = (IProject) changedProjects.get(i);
            ProjectRecord projectRec = (ProjectRecord) root.findResource(project);

            // if the Project is closed or deleted,
            // we also delete it from the Model and go on
            if ((!project.isOpen()) || (!project.exists())) {
                ArrayList[] array = updateFiles(project, changedFiles);
                removals.addAll(array[1]);
                root.removeResource(project);
                continue;
            }

            // if we couldn't find the Project
            // then it has to be new
            if (projectRec == null) {
                projectRec = (ProjectRecord) root.addResource(project);
            }

            // then we can update the Files for the new or updated Project
            ArrayList[] array = updateFiles(project, changedFiles);
            additions.addAll(array[0]);
            removals.addAll(array[1]);
            changes.addAll(array[2]);
        }

        // the addtions, removals and changes are given to the viewer
        // so that it can update itself
        // updating the table MUST be in sync
        treeViewer.getControl().getDisplay().syncExec(new Runnable() {
            public void run() {
                updateViewer(additions, removals, changes);
            }
        });
    }

    /**
     * Updates the Files for a given Project
     * 
     * @param project
     * @param changedFiles, a List of all changed Files
     * @return an ArrayList of ArrayLists containing additons [0], removals [1]
     *         and changes [2] (Array-Position in Brackets)
     */
    protected ArrayList[] updateFiles(IProject project, ArrayList changedFiles) {
        ArrayList additions = new ArrayList();
        ArrayList removals = new ArrayList();
        ArrayList changes = new ArrayList();

        // we search for the ProjectRecord to the Project
        // if it doesn't exist, we return nothing
        ProjectRecord projectRec = (ProjectRecord) root.findResource(project);
        if (projectRec == null)
            return new ArrayList[] { additions, removals, changes };

        // if the project is deleted or closed
        if (!project.exists() || !project.isOpen()) {
            List packages = projectRec.getChildrenAsList();
            // ... we add all Packages to the removals
            // so they are not shown anymore
            removals.addAll(packages);
            for (int k = 0; k < packages.size(); k++) {
                PackageRecord packageRec = (PackageRecord) packages.get(k);
                removals.addAll(packageRec.getChildrenAsList());
            }
            return new ArrayList[] { additions, removals, changes };
        }

        // we got through all files
        IResource resource = null;
        for (int i = 0; i < changedFiles.size(); i++) {
            resource = (IResource) changedFiles.get(i);

            // ... and first check, if the project is the right one
            if (!project.equals(resource.getProject()))
                continue;

            // ... if so, we search for the corresponding Record to a File
            AbstractPMDRecord rec = projectRec.findResource(resource);
            FileRecord fileRec = null;
            if ((rec != null) && (rec.getResourceType() == IResource.FILE))
                fileRec = (FileRecord) rec;

            // ... if we couldn't find one, we add it
            if (fileRec == null) {
                fileRec = (FileRecord) projectRec.addResource(resource);
                if (fileRec.hasMarkers())
                    additions.add(fileRec);
                continue;
            }

            // if there is a FileRecord, but no File anymore, we delete it
            if (!fileRec.getResource().exists()) {
                projectRec.removeResource(fileRec.getResource());
                removals.add(fileRec);
                continue;
            }

            if (fileRec.hasMarkers()) {
                // if there are still Markers, it is a change
                changes.add(fileRec);
            } else {
                // ... if not, it can be removed from the View
                removals.add(fileRec);
                AbstractPMDRecord packageRec = fileRec.getParent();
                if (!packageRec.hasMarkers()) {
                    removals.add(packageRec);
                }
            }
        }

        return new ArrayList[] { additions, removals, changes };
    }

    /**
     * Applies found updates on the table, adapted from Philippe Herlin
     * 
     * @param additions
     * @param removals
     * @param changes
     */
    protected void updateViewer(ArrayList additions, ArrayList removals, ArrayList changes) {

        // perform removals
        if (removals.size() > 0) {
            treeViewer.cancelEditing();
            treeViewer.remove(removals.toArray());
        }

        // perform additions
        if (additions.size() > 0) {
            for (int i = 0; i < additions.size(); i++) {
                AbstractPMDRecord addedRec = (AbstractPMDRecord) additions.get(i);
                if (addedRec instanceof FileRecord)
                    treeViewer.add(addedRec.getParent(), addedRec);
                else
                    treeViewer.add(root, addedRec);
            }
        }

        // perform changes
        if (changes.size() > 0) {
            treeViewer.update(changes.toArray(), null);
        }

        violationView.refresh();
    }
}
