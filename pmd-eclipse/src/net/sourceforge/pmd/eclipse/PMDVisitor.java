package net.sourceforge.pmd.eclipse;

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
 * Revision 1.16  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.14.2.2  2003/11/04 13:26:38  phherlin
 * Implement the working set feature (working set filtering)
 *
 * Revision 1.14.2.1  2003/11/03 14:40:16  phherlin
 * Refactoring to remove usage of Eclipse internal APIs
 *
 * Revision 1.14  2003/06/30 22:00:53  phherlin
 * Adding clearer monitor message when visiting files
 *
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
public class PMDVisitor extends PMDAbstractVisitor implements IResourceVisitor {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.PMDVisitor");
    
    /**
     * Construct with a progress monitor
     * @param monitor a progress indicator
     */
    public PMDVisitor(IProgressMonitor monitor) {
        log.debug("Instanciating a new visitor");
        setMonitor(monitor);
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
     */
    public boolean visit(IResource resource) {
        log.debug("Visiting resource " + resource.getName());
        boolean fVisitChildren = true;
        IProgressMonitor monitor = getMonitor();

        if ((monitor == null) || ((monitor != null) && (!monitor.isCanceled()))) {
            IFile file = (IFile) resource.getAdapter(IFile.class);
            if ((file != null)
                && (file.getFileExtension() != null)
                && (file.getFileExtension().equals("java"))) {

                if (monitor != null) {
                    monitor.subTask(
                        PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_MONITOR_CHECKING_FILE)
                            + " "
                            + file.getName());
                }
                
                if (isFileInWorkingSet(file)) {
                    PMDProcessor.getInstance().run(file, isUseTaskMarker(), getAccumulator());
                } else {
                    log.debug("The file " + file.getName() + " is not in the working set");
                }

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

}
