package net.sourceforge.pmd.eclipse.cmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

/**
 * This class visits all of the resources in the Eclipse
 * Workspace, and runs PMD on them if they happen to be
 * Java files.
 * 
 * Any violations get tagged onto the file as problems in the tasks list.
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2005/05/31 20:44:41  phherlin
 * Continuing refactoring
 *
 * Revision 1.1  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 */
public class ResourceVisitor extends BaseVisitor implements IResourceVisitor {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.ResourceVisitor");

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
