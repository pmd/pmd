package net.sourceforge.pmd.eclipse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.internal.resources.MarkerInfo;
import org.eclipse.core.internal.resources.MarkerManager;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class is intended to run visitor objects. It's purpose is to
 * factor the code arround a call to a visitor.
 * 
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/10/30 16:29:16  phherlin
 * Adapting to Eclipse v3 M4
 *
 * Revision 1.1  2003/05/19 22:27:33  phherlin
 * Refactoring to improve performance
 *
 * 
 */
public class PMDVisitorRunner {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.PMDVisitorRunner");
    private IProgressMonitor monitor;

    /**
     * Visit a resource
     * @param resource the resource to visit
     * @param visitor the visitor
     */
    public void run(IResource resource, PMDVisitor visitor) throws CoreException {
        Map markerDirectives = new HashMap();
        setMonitor(visitor.getMonitor());
        visitor.setAccumulator(markerDirectives);
        resource.accept(visitor);
        runApplyDirectives(markerDirectives);
        markerDirectives.clear();
    }

    /**
     * Visit a project
     * Note: A project is not resource, ie. doesn't extends IResource but
     * accept resource visitors such as PMDVisitor
     * @param project the project to visit
     * @param visitor the visitor
     */
    public void run(IProject project, PMDVisitor visitor) throws CoreException {
        Map markerDirectives = new HashMap();
		setMonitor(visitor.getMonitor());
        visitor.setAccumulator(markerDirectives);
        project.accept(visitor);
        runApplyDirectives(markerDirectives);
        markerDirectives.clear();
    }

    /**
     * Visit a resource delta
     * @param resourceDelta a set of resources to visit
     * @param visitor the visitor
     */
    public void run(IResourceDelta resourceDelta, PMDDeltaVisitor visitor) throws CoreException {
        Map markerDirectives = new HashMap();
        setMonitor(visitor.getMonitor());
        visitor.setAccumulator(markerDirectives);
        resourceDelta.accept(visitor);
        runApplyDirectives(markerDirectives);
        markerDirectives.clear();
    }
    
    /**
     * Apply the marker changes in a bacth workspace job
     * @param markerDirectives map of new marker informations
     */
    private void runApplyDirectives(final Map markerDirectives) throws CoreException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.run(new IWorkspaceRunnable() {
            public void run(IProgressMonitor monitor) throws CoreException {
                applyDirectives(markerDirectives);
            }
        }, getMonitor());
    }

    /**
     * Apply new markers to selected files
     * @param markerDirectives map of new marker informations
     */
    protected void applyDirectives(Map markerDirectives) throws CoreException {
        try {
            log.info("Processing marker directives");
            Set filesSet = markerDirectives.keySet();
            Iterator i = filesSet.iterator();
            Workspace workspace = (Workspace) ResourcesPlugin.getWorkspace();
			MarkerManager markerManager = workspace.getMarkerManager();

            while (i.hasNext()) {
				IFile file = (IFile) i.next();
                try {
                    Set markerInfoSet = (Set) markerDirectives.get(file);
                    file.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    markerManager.add(file, (MarkerInfo[]) markerInfoSet.toArray(new MarkerInfo[markerInfoSet.size()]));
				} catch (CoreException e) {
					log.warn("CoreException when setting marker info for file " + file.getName() + " : " + e.getMessage());
                }
            }

        } finally {
            log.info("End of marker info directives processing");
        }
    }

    /**
     * @return Returns the monitor.
     */
    public IProgressMonitor getMonitor() {
        return monitor;
    }

    /**
     * @param monitor The monitor to set.
     */
    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

}
