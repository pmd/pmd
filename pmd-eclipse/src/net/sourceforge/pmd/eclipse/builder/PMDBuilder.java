package net.sourceforge.pmd.eclipse.builder;

import java.util.Map;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDDeltaVisitor;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDVisitor;
import net.sourceforge.pmd.eclipse.PMDVisitorRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
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
 * Revision 1.8  2003/07/01 20:22:16  phherlin
 * Make rules selectable from projects
 *
 * Revision 1.7  2003/06/30 22:05:07  phherlin
 * Improving incremental building
 *
 * Revision 1.6  2003/06/19 20:58:33  phherlin
 * Improve progress indicator accuracy
 *
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
                int elementCount = countDeltaElement(resourceDelta);
                monitor.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), elementCount);
                log.debug("Monitor beginTask(" + elementCount + ")");
                PMDDeltaVisitor visitor = new PMDDeltaVisitor(monitor);
                new PMDVisitorRunner().run(resourceDelta, visitor);
                monitor.done();
                log.debug("Monitor done");
            } else {
                log.info("No change reported. Performing no build");
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
        int elementCount = countProjectElement(project);
        monitor.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), elementCount);
        log.debug("Monitor beginTask(" + elementCount + ")");
        PMDVisitor visitor = new PMDVisitor(monitor);
        new PMDVisitorRunner().run(project, visitor);
        monitor.done();
        log.debug("Monitor done");
    }

    /**
     * Count the number of sub-resources of a project
     * @param project a project
     * @return the element count
     */
    private int countProjectElement(IProject project) {
        final class CountVisitor implements IResourceVisitor {
            public int count = 0;
            public boolean visit(IResource resource) {
                boolean fVisitChildren = true;
                count++;

                if ((resource instanceof IFile)
                    && (((IFile) resource).getFileExtension() != null)
                    && ((IFile) resource).getFileExtension().equals("java")) {

                    fVisitChildren = false;
                }

                return fVisitChildren;
            }
        };

        CountVisitor visitor = new CountVisitor();

        try {
            project.accept(visitor);
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError("Exception when counting elements of a project", e);
        }

        return visitor.count;
    }

    /**
     * Count the number of sub-resources of a delta
     * @param delta a resource delta
     * @return the element count
     */
    private int countDeltaElement(IResourceDelta delta) {
        final class CountVisitor implements IResourceDeltaVisitor {
            public int count = 0;
            public boolean visit(IResourceDelta delta) {
                count++;
                return true;
            }
        };

        CountVisitor visitor = new CountVisitor();

        try {
            delta.accept(visitor);
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError("Exception counting elemnts in a delta selection", e);
        }

        return visitor.count;
    }

}
