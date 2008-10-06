package net.sourceforge.pmd.eclipse.runtime.cmd;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

/**
 * This class visits all of the resources in the Eclipse
 * Workspace, and runs PMD on them if they happen to be
 * Java files.
 * 
 * Any violations get tagged onto the file as problems in the tasks list.
 * @author Philippe Herlin
 *
 */
public class ResourceVisitor extends BaseVisitor implements IResourceVisitor {
    private static final Logger log = Logger.getLogger(ResourceVisitor.class);

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
     */
    public boolean visit(final IResource resource) {
        log.debug("Visiting resource " + resource.getName());
        boolean fVisitChildren = true;

        if (this.isCanceled()) {
            fVisitChildren = false;
        } else {
            this.reviewResource(resource);
        }

        return fVisitChildren;
    }

}
