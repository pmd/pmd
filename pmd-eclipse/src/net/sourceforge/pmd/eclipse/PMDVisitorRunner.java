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
import org.eclipse.core.runtime.CoreException;

/**
 * This class is intended to run visitor objects. It's purpose is to
 * factor the code arround a call to a visitor.
 * 
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2003/05/19 22:27:33  phherlin
 * Refactoring to improve performance
 *
 * 
 */
public class PMDVisitorRunner {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.PMDVisitorRunner");

    /**
     * Visit a resource
     * @param resource the resource to visit
     * @param visitor the visitor
     */
    public void run(IResource resource, PMDVisitor visitor) throws CoreException {
        Map markerDirectives = new HashMap();
        visitor.setAccumulator(markerDirectives);
        resource.accept(visitor);
        applyDirectives(markerDirectives);
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
        visitor.setAccumulator(markerDirectives);
        project.accept(visitor);
        applyDirectives(markerDirectives);
        markerDirectives.clear();
    }

    /**
     * Visit a resource delta
     * @param resourceDelta a set of resources to visit
     * @param visitor the visitor
     */
    public void run(IResourceDelta resourceDelta, PMDDeltaVisitor visitor) throws CoreException {
        Map markerDirectives = new HashMap();
        visitor.setAccumulator(markerDirectives);
        resourceDelta.accept(visitor);
        applyDirectives(markerDirectives);
        markerDirectives.clear();
    }

    /**
     * Apply new markers to selected files
     * @param markerDirectives map of new marker informations
     */
    private void applyDirectives(Map markerDirectives) throws CoreException {
        try {
            log.info("Processing marker directives");
            Set filesSet = markerDirectives.keySet();
            Iterator i = filesSet.iterator();

            if (i.hasNext()) {
                IFile file = (IFile) i.next();
                Workspace workspace = (Workspace) file.getWorkspace();
                MarkerManager markerManager = workspace.getMarkerManager();

                try {
                    workspace.prepareOperation();
                    workspace.beginOperation(true);

                    boolean fLoop = false;
                    do {
                        Set markerInfoSet = (Set) markerDirectives.get(file);
                        markerManager.removeMarkers(file, PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                        markerManager.add(file, (MarkerInfo[]) markerInfoSet.toArray(new MarkerInfo[markerInfoSet.size()]));

                        if (i.hasNext()) {
                            file = (IFile) i.next();
                            fLoop = true;
                        } else {
                            fLoop = false;
                        }

                    } while (fLoop);

                } catch (CoreException e) {
                    log.warn("CoreException when setting marker info for file " + file.getName() + " : " + e.getMessage());
                } finally {
                    workspace.endOperation(false, null);
                }
            }

        } finally {
            log.info("End of marker info directives processing");
        }
    }

}
