package net.sourceforge.pmd.eclipse.runtime.cmd;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A PMD visitor for processing resource deltas
 * 
 * @author Philippe Herlin
 *
 */
public class DeltaVisitor extends BaseVisitor implements IResourceDeltaVisitor {
    private static final Logger log = Logger.getLogger(DeltaVisitor.class);

    /**
     * Default constructor
     */
    public DeltaVisitor() {
        super();
    }

    /**
     * Constructor with monitor
     */
    public DeltaVisitor(final IProgressMonitor monitor) {
        super();
        this.setMonitor(monitor);
    }

    /**
     * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(IResourceDelta)
     */
    public boolean visit(final IResourceDelta delta) throws CoreException {
        boolean fProcessChildren = true;

        if (this.isCanceled()) {
            fProcessChildren = false;
        } else {
            if (delta.getKind() == IResourceDelta.ADDED) {
                log.debug("Visiting added resource " + delta.getResource().getName());
                visitAdded(delta.getResource());
            } else if (delta.getKind() == IResourceDelta.CHANGED) {
                log.debug("Visiting changed resource " + delta.getResource().getName());
                visitChanged(delta.getResource());
            } else { // other kinds are not visited            
                log.debug("Resource " + delta.getResource().getName() + " not visited.");
            }
        }

        return fProcessChildren;
    }

    /**
     * Visit added resource
     * @param resource a new resource
     */
    private void visitAdded(final IResource resource) {
        this.reviewResource(resource);
    }

    /**
     * Visit changed resource
     * @param resource a changed resource
     */
    private void visitChanged(final IResource resource) {
        this.reviewResource(resource);
    }

}