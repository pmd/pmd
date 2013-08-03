package net.sourceforge.pmd.eclipse.runtime.builder;

import java.util.Map;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.cmd.ReviewCodeCmd;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Implements an incremental builder for PMD. Use ResourceVisitor and DeltaVisitor
 * to process each file of the project.
 *
 * @author Philippe Herlin
 *
 */
public class PMDBuilder extends IncrementalProjectBuilder {
	
    public static final Logger log = Logger.getLogger(PMDBuilder.class);
    public static final String PMD_BUILDER = "net.sourceforge.pmd.eclipse.plugin.pmdBuilder";

    public static final IProject[] EMPTY_PROJECT_ARRAY = new IProject[0];
    /**
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
        log.info("Incremental builder activated");

        try {
            if (kind == AUTO_BUILD) {
                log.debug("Auto build requested.");
                buildAuto(monitor);
            } else if (kind == FULL_BUILD) {
                log.debug("Full build requested.");
                buildFull(monitor);
            } else if (kind == INCREMENTAL_BUILD) {
                log.debug("Incremental build requested.");
                buildIncremental(monitor);
            } else {
                log.warn("This kind of build is not supported : " + kind);
            }
        } catch (CommandException e) {
            throw new CoreException(new Status(IStatus.ERROR, PMDPlugin.getDefault().getBundle().getSymbolicName(), 0, e.getMessage(), e));
        }

        log.info("Build done.");
        return EMPTY_PROJECT_ARRAY;
    }

    /**
     * Automatic build
     * @param monitor a progress monitor
     * @throws CommandException
     */
    private void buildAuto(IProgressMonitor monitor) throws CommandException {
        this.buildIncremental(monitor);
    }

    /**
     * Full build
     * @param monitor A progress monitor.
     * @throws CommandException
     */
    private void buildFull(IProgressMonitor monitor) throws CommandException {
        IProject currentProject = this.getProject();
        if (currentProject != null) {
            this.processProjectFiles(currentProject, monitor);
        }
    }

    /**
     * Incremental build
     * @param monitor a progress monitor.
     * @throws CommandException
     */
    private void buildIncremental(IProgressMonitor monitor) throws CommandException {
        IProject currentProject = getProject();
        if (currentProject != null) {
            IResourceDelta resourceDelta = this.getDelta(currentProject);
            if (resourceDelta != null && resourceDelta.getAffectedChildren().length != 0) {
                ReviewCodeCmd cmd = new ReviewCodeCmd();
                cmd.setResourceDelta(resourceDelta);
                cmd.setTaskMarker(false);
                cmd.setMonitor(monitor);
                cmd.clearExistingMarkersBeforeApplying(true);
                cmd.performExecute(); // a builder is always asynchronous; execute a command synchronously whatever its processor
            } else {
                log.info("No change reported. Performing no build");
            }
        }
    }

    /**
     * Process all files in the project
     * @param project the project
     * @param monitor a progress monitor
     * @throws CommandException
     */
    private void processProjectFiles(IProject project, IProgressMonitor monitor) throws CommandException {
        ReviewCodeCmd cmd = new ReviewCodeCmd();
        cmd.addResource(project);
        cmd.setTaskMarker(false);
        cmd.setMonitor(monitor);
        cmd.clearExistingMarkersBeforeApplying(true);
        cmd.performExecute(); // a builder is always asynchronous; execute a command synchronously whatever its processor
    }

}
