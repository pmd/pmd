package net.sourceforge.pmd.eclipse.builder;

import java.util.Map;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDDeltaVisitor;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Implements an incremental builder for PMD. Use PMDVisitor and PMDDeltaVisitor
 * to process each file of the project.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2003/03/18 23:28:37  phherlin
 * *** keyword substitution change ***
 *
 */
public class PMDBuilder extends IncrementalProjectBuilder {
    public static final String PMD_BUILDER = "net.sourceforge.pmd.eclipse.pmdBuilder";

    /**
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int, Map, IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        IProject[] result = null;

        if (kind == AUTO_BUILD) {
            result = buildAuto(args, monitor);
        } else if (kind == FULL_BUILD) {
            result = buildFull(args, monitor);
        } else if (kind == INCREMENTAL_BUILD) {
            result = buildIncremental(args, monitor);
        }

        return result;
    }

    /**
     * Automatic build
     * @param args build parameters
     * @param monitor progress indicator
     * @return IProject[] related projects list
     * @throws CoreException
     */
    private IProject[] buildAuto(Map args, IProgressMonitor monitor) throws CoreException {
        return buildIncremental(args, monitor);
    }

    /**
     * Full build
     * @param args build parameters
     * @param monitor progress indicator
     * @return IProject[] related projects list
     * @throws CoreException
     */
    private IProject[] buildFull(Map args, IProgressMonitor monitor) throws CoreException {
        IProject currentProject = getProject();
        if (currentProject != null) {
            processProjectFiles(currentProject, monitor);
        }

        return null;
    }

    /**
     * Incremental build
     * @param args build parameters
     * @param monitor progress indicator
     * @return IProject[] related projects list
     * @throws CoreException
     */
    private IProject[] buildIncremental(Map args, IProgressMonitor monitor) throws CoreException {
        IProject result[] = null;

        IProject currentProject = getProject();
        if (currentProject != null) {
            IResourceDelta resourceDelta = getDelta(currentProject);
            if ((resourceDelta != null) && (resourceDelta.getAffectedChildren().length != 0)) {
                monitor.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), IProgressMonitor.UNKNOWN);
                PMDDeltaVisitor visitor = new PMDDeltaVisitor(monitor);
                resourceDelta.accept(visitor);
                monitor.done();
            } else {
                result = buildFull(args, monitor);
            }
        }

        return result;
    }

    /**
     * Process all files in the project
     * @param project the project
     * @param monitor a progress indicator
     */
    private void processProjectFiles(IProject project, IProgressMonitor monitor) throws CoreException {
        monitor.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), IProgressMonitor.UNKNOWN);
        IResourceVisitor visitor = new PMDVisitor(monitor);
        project.accept(visitor);
        monitor.done();
    }

}
