package net.sourceforge.pmd.eclipse;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This class visits all of the resources in the Eclipse
 * Workspace, and runs PMD on them if they happen to be
 * Java files.
 * 
 * Any violations get tagged onto the file as problems in the tasks list.

 * @author David Dixon-Peugh
 * @author David Craine
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.13  2003/06/19 20:56:59  phherlin
 * Improve progress indicator accuracy
 *
 * Revision 1.12  2003/05/19 22:27:32  phherlin
 * Refactoring to improve performance
 *
 * Revision 1.11  2003/03/30 20:47:03  phherlin
 * Adding logging
 *
 * Revision 1.10  2003/03/17 23:34:53  phherlin
 * refactoring
 *
 */
public class PMDVisitor implements IResourceVisitor {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.PMDVisitor");
    private IProgressMonitor monitor;
    private boolean useTaskMarker = false;
    private Map accumulator;

    /**
     * Construct with a progress monitor
     * @param monitor a progress indicator
     */
    public PMDVisitor(IProgressMonitor monitor) {
        log.debug("Instanciating a new visitor");
        this.monitor = monitor;
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
     */
    public boolean visit(IResource resource) {
        log.debug("Visiting resource " + resource.getName());
        boolean fVisitChildren = true;

        if ((monitor == null) || ((monitor != null) && (!monitor.isCanceled()))) {
            if ((resource instanceof IFile)
                && (((IFile) resource).getFileExtension() != null)
                && ((IFile) resource).getFileExtension().equals("java")) {

                if (monitor != null) {
                    monitor.subTask(((IFile) resource).getName());
                }

                PMDProcessor.getInstance().run((IFile) resource, useTaskMarker, getAccumulator());

                fVisitChildren = false;
            }

            if (monitor != null) {
                monitor.worked(1);
                log.debug("Monitor worked");
            }

        } else {
            fVisitChildren = false;
        }

        return fVisitChildren;
    }
    /**
     * Returns the useTaskMarker.
     * @return boolean
     */
    public boolean isUseTaskMarker() {
        return useTaskMarker;
    }

    /**
     * Sets the useTaskMarker.
     * @param useTaskMarker The useTaskMarker to set
     */
    public void setUseTaskMarker(boolean useTaskMarker) {
        this.useTaskMarker = useTaskMarker;
    }

    /**
     * Returns the accumulator.
     * @return Map
     */
    public Map getAccumulator() {
        log.debug("Returning the accumulator " + accumulator);
        return accumulator;
    }

    /**
     * Sets the accumulator.
     * @param accumulator The accumulator to set
     */
    public void setAccumulator(Map accumulator) {
        this.accumulator = accumulator;
        log.debug("Setting accumulator " + accumulator);
    }

}
