package net.sourceforge.pmd.eclipse.builder;

import java.util.Map;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDDeltaVisitor;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import net.sourceforge.pmd.eclipse.PMDVisitorRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
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
 * Revision 1.5  2003/05/19 22:27:33  phherlin
 * Refactoring to improve performance
 *
 * Revision 1.4  2003/03/30 20:51:08  phherlin
 * Adding logging
 *
 */
public class PMDBuilder extends IncrementalProjectBuilder {
    public static final String PMD_BUILDER = "net.sourceforge.pmd.eclipse.pmdBuilder";
    public static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.builder.PMDBuilder");

    /**
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int, Map, IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        log.info("Incremental builder activated");
        IProject[] result = null;

        if (kind == AUTO_BUILD) {
            log.debug("Auto build requested.");
            result = buildAuto(args, monitor);
        } else if (kind == FULL_BUILD) {
            log.debug("Full build requested.");
            result = buildFull(args, monitor);
        } else if (kind == INCREMENTAL_BUILD) {
            log.debug("Incremental build requested.");
            result = buildIncremental(args, monitor);
        } else {
            log.warn("This kind of build is not supported : " + kind);
        }

        log.info("Build done.");
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
                new PMDVisitorRunner().run(resourceDelta, visitor);
                monitor.done();
            } else {
                log.info("No change reported. Performing a full build");
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
        PMDVisitor visitor = new PMDVisitor(monitor);
        new PMDVisitorRunner().run(project, visitor);
        monitor.done();
    }

}
