package net.sourceforge.pmd.eclipse;

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
 * Revision 1.10  2003/03/17 23:34:53  phherlin
 * refactoring
 *
 */
public class PMDVisitor implements IResourceVisitor {
    private IProgressMonitor monitor;
    private boolean useTaskMarker = false;

    /**
     * Construct with a progress monitor
     * @param monitor a progress indicator
     */
    public PMDVisitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
     */
    public boolean visit(IResource resource) {
        boolean fVisitChildren = true;

        if ((monitor == null) || ((monitor != null) && (!monitor.isCanceled()))) {
            if ((resource instanceof IFile)
                && (((IFile) resource).getFileExtension() != null)
                && ((IFile) resource).getFileExtension().equals("java")) {
                if (monitor != null) monitor.subTask(((IFile) resource).getName());
                PMDProcessor.getInstance().run((IFile) resource, useTaskMarker);
                if (monitor != null) monitor.worked(1);
                fVisitChildren = false;
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

}
