package net.sourceforge.pmd.eclipse;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A PMD visitor for processing resource deltas
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * $Log$
 * Revision 1.2  2003/03/18 23:28:36  phherlin
 * *** keyword substitution change ***
 *
 */
public class PMDDeltaVisitor implements IResourceDeltaVisitor {
    private IProgressMonitor monitor;
    private boolean useTaskMarker = false;

    /**
     * Default construtor
     */
    public PMDDeltaVisitor() {
    }

    /**
     * Constructor with monitor
     */
    public PMDDeltaVisitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
     */
    public boolean visit(IResourceDelta delta) throws CoreException {
        boolean fProcessChildren = true;

        if ((monitor == null) || ((monitor != null) && (!monitor.isCanceled()))) {
            if (delta.getKind() == IResourceDelta.ADDED) {
                visitAdded(delta.getResource());
            } else if (delta.getKind() == IResourceDelta.CHANGED) {
                visitChanged(delta.getResource());
            } // other kinds are not visited            
        } else {
            fProcessChildren = false;
        }

        return fProcessChildren;
    }

    /**
     * Visit added resource
     * @param resource a new resource
     */
    private void visitAdded(IResource resource) {
        processResource(resource);
    }

    /**
     * Visit changed resource
     * @param resource a changed resource
     */
    private void visitChanged(IResource resource) {
        processResource(resource);
    }

    /**
     * Process a targeted resource
     * @param resource the resource to process
     */
    private void processResource(IResource resource) {
        if ((resource instanceof IFile)
            && (((IFile) resource).getFileExtension() != null)
            && ((IFile) resource).getFileExtension().equals("java")) {
            if (monitor != null) monitor.subTask(((IFile) resource).getName());
            PMDProcessor.getInstance().run((IFile) resource, useTaskMarker);
            if (monitor != null) monitor.worked(1);
        }
    }

    /**
     * Returns the monitor.
     * @return IProgressMonitor
     */
    public IProgressMonitor getMonitor() {
        return monitor;
    }

    /**
     * Sets the monitor.
     * @param monitor The monitor to set
     */
    public void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
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