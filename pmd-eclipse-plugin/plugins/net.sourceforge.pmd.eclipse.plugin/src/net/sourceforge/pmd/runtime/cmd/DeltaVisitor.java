package net.sourceforge.pmd.runtime.cmd;

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
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:37:34  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.2  2005/05/31 20:44:41  phherlin
 * Continuing refactoring
 *
 * Revision 1.1  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.6  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.5.2.1  2003/11/04 13:26:38  phherlin
 * Implement the working set feature (working set filtering)
 *
 * Revision 1.5  2003/06/19 20:56:59  phherlin
 * Improve progress indicator accuracy
 *
 * Revision 1.4  2003/05/19 22:27:32  phherlin
 * Refactoring to improve performance
 *
 * Revision 1.3  2003/03/30 20:44:27  phherlin
 * Adding logging
 *
 */
public class DeltaVisitor extends BaseVisitor implements IResourceDeltaVisitor {
    private static final Logger log = Logger.getLogger(DeltaVisitor.class);

    /**
     * Default construtor
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