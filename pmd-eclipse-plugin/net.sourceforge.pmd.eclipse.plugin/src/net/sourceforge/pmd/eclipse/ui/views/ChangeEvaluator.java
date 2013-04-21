package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;

import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.model.PackageRecord;
import net.sourceforge.pmd.eclipse.ui.model.ProjectRecord;
import net.sourceforge.pmd.eclipse.ui.model.RootRecord;

/**
 * As chunks of code originally found in the ViolationOverviewContentProvider, the ChangeEvaluator
 * aggregates the same functionality while using formal ChangeRecord instances instead of List triplets.
 * 
 * @author Brian Remedios
 */
public class ChangeEvaluator {

	private final RootRecord root;

	public ChangeEvaluator(RootRecord theRoot) {
		root = theRoot;
	}


	public ChangeRecord<AbstractPMDRecord> changeRecordFor(IResourceChangeEvent event) {

		List<IMarkerDelta> markerDeltas = MarkerUtil.markerDeltasIn(event);

		// first we get a List of changes to Files and Projects so we won't be updating everything
		List<IResource> changedFiles = new ArrayList<IResource>();
		List<IProject> changedProjects = new ArrayList<IProject>();
		for (IMarkerDelta markerDelta : markerDeltas) {
			IResource resource = markerDelta.getResource();
			IProject project = resource.getProject();

			// the lists should not contain Projects or Resources twice
			if (!changedFiles.contains(resource)) {
				changedFiles.add(resource);
//                LOG.debug("Resource " + resource.getName() + " has changed");
			}

			if (!changedProjects.contains(project)) {
				changedProjects.add(project);
//                LOG.debug("Project " + project.getName() + " has changed");
			}
		}

		// we can add, change, or remove Resources
		// all the changes are given to the viewer later

		ChangeRecord<AbstractPMDRecord> changeRec = new ChangeRecord<AbstractPMDRecord>();
		
		// we go through the changed Projects
		for (IProject project : changedProjects) {
//           LOG.debug("Processing changes for project " + project.getName());
			ProjectRecord projectRec = (ProjectRecord) root.findResource(project);

			// if the Project is closed or deleted, we also delete it from the Model and go on
			if (!(project.isOpen() && project.isAccessible())) { // NOPMD by Sven on 09.11.06 22:17
//               LOG.debug("The project is not open or not accessible. Remove it");
				List<AbstractPMDRecord>[] array = updateFiles(project, changedFiles);
				changeRec.removed(array[1]);
				root.removeResource(project);
			}

			// if we couldn't find the Project then it has to be new
			else if (projectRec == null) {
//              LOG.debug("Cannot find a project record for it. Add it.");
				projectRec = (ProjectRecord) root.addResource(project);
			}

			// then we can update the Files for the new or updated Project
			List<AbstractPMDRecord>[] array = updateFiles(project, changedFiles);
			changeRec.added(array[0]);
			changeRec.removed(array[1]);
			changeRec.changed(array[2]);
		}

		// the additions, removals and changes are given to the viewer
		// so that it can update itself
		// updating the table MUST be in sync
		//        this.treeViewer.getControl().getDisplay().syncExec(new Runnable() {
		//            public void run() {
		//                updateViewer(additions, removals, changes);
		//            }
		//        });
		
		return changeRec;
	}

	/**
	 * Updates the Files for a given Project
	 *
	 * @param project
	 * @param changedFiles, a List of all changed Files
	 * @return an List of Lists containing additions [0], removals [1]
	 *         and changes [2] (Array-Position in Brackets)
	 */
	private List<AbstractPMDRecord>[] updateFiles(IProject project, List<IResource> changedFiles) {

		// TODO use ChangeRecord
		List<AbstractPMDRecord> additions = new ArrayList<AbstractPMDRecord>();
		List<AbstractPMDRecord> removals = new ArrayList<AbstractPMDRecord>();
		List<AbstractPMDRecord> changes = new ArrayList<AbstractPMDRecord>();
		List<AbstractPMDRecord>[] updatedFiles = new List[] { additions, removals, changes };

		// we search for the ProjectRecord to the Project
		// if it doesn't exist, we return nothing
		ProjectRecord projectRec = (ProjectRecord) root.findResource(project);

		// we got through all files
		if (projectRec != null && project.isAccessible()) {
			updatedFiles = searchProjectForModifications(projectRec, changedFiles);
		}

		// if the project is deleted or closed
		else if (projectRec != null) {
			List<AbstractPMDRecord> packages = projectRec.getChildrenAsList();
			// ... we add all Packages to the removals so they are not shown anymore
			removals.addAll(packages);
			for (int k = 0; k < packages.size(); k++) {
				PackageRecord packageRec = (PackageRecord) packages.get(k);
				removals.addAll(packageRec.getChildrenAsList());
			}
			updatedFiles = new List[] { additions, removals, changes };
		}

		return updatedFiles;
	}

	/**
	 * Analyzes the modification inside a single project and compute the list of additions, updates and removals.
	 *
	 * @param projectRec
	 * @param changedFiles
	 * @return
	 */
	private static List<AbstractPMDRecord>[] searchProjectForModifications(ProjectRecord projectRec, List<IResource> changedFiles) {

		// TODO use ChangeRecord
		List<AbstractPMDRecord> additions = new ArrayList<AbstractPMDRecord>();
		List<AbstractPMDRecord> removals = new ArrayList<AbstractPMDRecord>();
		List<AbstractPMDRecord> changes = new ArrayList<AbstractPMDRecord>();
		IProject project = (IProject) projectRec.getResource();

//        LOG.debug("Analyses project " + project.getName());

		for (IResource resource : changedFiles) {
			//            LOG.debug("Analyses resource " + resource.getName());

			// ... and first check, if the project is the right one
			if (project.equals(resource.getProject())) {
				AbstractPMDRecord rec = projectRec.findResource(resource);
				if (rec != null && rec.getResourceType() == IResource.FILE) {
					FileRecord fileRec = (FileRecord) rec;
					fileRec.updateChildren();
					if (fileRec.getResource().isAccessible() && fileRec.hasMarkers()) {
//                        LOG.debug("The file has changed");
						changes.add(fileRec);
					} else {
//                        LOG.debug("The file has been removed");
						projectRec.removeResource(fileRec.getResource());
						removals.add(fileRec);

						// remove parent if no more markers
						PackageRecord packageRec = (PackageRecord) fileRec.getParent();
						if (!packageRec.hasMarkers()) {
							projectRec.removeResource(fileRec.getParent().getResource());
							removals.add(packageRec);
						}
					}
				} else if (rec == null) {
					//                    LOG.debug("This is a new file.");
					AbstractPMDRecord fileRec = projectRec.addResource(resource);
					additions.add(fileRec);
				} else {
//                    LOG.debug("The resource found is not a file! type found : " + rec.getResourceType());
				}
			} else {
//                LOG.debug("The project resource is not the same! (" + resource.getProject().getName() + ')');
			}
		}

		return new List[] { additions, removals, changes };
	}
}
