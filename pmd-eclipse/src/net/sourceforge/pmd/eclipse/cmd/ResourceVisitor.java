package net.sourceforge.pmd.eclipse.cmd;

import java.io.InputStreamReader;
import java.io.Reader;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
        final IProgressMonitor monitor = getMonitor();

        if ((monitor == null) || ((monitor != null) && (!monitor.isCanceled()))) {
            final IFile file = (IFile) resource.getAdapter(IFile.class);
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
                    try {
                        final Reader input = new InputStreamReader(file.getContents());
                        final RuleContext context = new RuleContext();
                        resource.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                        context.setSourceCodeFilename(file.getName());
                        context.setReport(new Report());
                        this.getPmdEngine().processFile(input, this.getRuleSet(), context);
                        updateMarkers(file, context, this.isUseTaskMarker(), this.getAccumulator());
                    } catch (CoreException e) {
                        log.error("Core exception visiting " + resource.getName(), e); //TODO: complete message
                    } catch (PMDException e) {
                        log.error("PMD exception visiting " + resource.getName(), e); // TODO: complete message
                    }
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
