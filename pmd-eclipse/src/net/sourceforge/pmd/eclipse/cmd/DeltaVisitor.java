package net.sourceforge.pmd.eclipse.cmd;

import java.io.InputStreamReader;
import java.io.Reader;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * 
 * $Log$
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
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.DeltaVisitor");

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
        final IProgressMonitor monitor = getMonitor();
        boolean fProcessChildren = true;

        if ((monitor == null) || ((monitor != null) && (!monitor.isCanceled()))) {
            if (delta.getKind() == IResourceDelta.ADDED) {
                log.debug("Visiting added resource " + delta.getResource().getName());
                visitAdded(delta.getResource());
            } else if (delta.getKind() == IResourceDelta.CHANGED) {
                log.debug("Visiting changed resource " + delta.getResource().getName());
                visitChanged(delta.getResource());
            } else { // other kinds are not visited            
                log.debug("Resource " + delta.getResource().getName() + " not visited.");
            }
            
            if (monitor != null) {
                monitor.worked(1);
                log.debug("Monitor worked");
            }
        } else {
            fProcessChildren = false;
        }

        return fProcessChildren;
    }

    /**
     * Visit added resource
     * @param resource a new resource
     */
    private void visitAdded(final IResource resource) {
        processResource(resource);
    }

    /**
     * Visit changed resource
     * @param resource a changed resource
     */
    private void visitChanged(final IResource resource) {
        processResource(resource);
    }

    /**
     * Process a targeted resource
     * @param resource the resource to process
     */
    private void processResource(final IResource resource) {
        final IFile file = (IFile) resource.getAdapter(IFile.class);
        if ((file != null)
            && (file.getFileExtension() != null)
            && (file.getFileExtension().equals("java"))) {
                
            if (getMonitor() != null) {
                getMonitor().subTask(((IFile) resource).getName());
            }            
                
            if (isFileInWorkingSet(file)) {
                try {
                    final Reader input = new InputStreamReader(file.getContents());
                    final RuleContext context = new RuleContext();
                    context.setSourceCodeFilename(file.getName());
                    context.setReport(new Report());
                    this.getPmdEngine().processFile(input, this.getRuleSet(), context);

                    resource.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    updateMarkers(file, context, this.isUseTaskMarker(), this.getAccumulator());
                } catch (CoreException e) {
                    log.error("Core exception visiting " + resource.getName(), e); //TODO: complete message
                } catch (PMDException e) {
                    log.error("PMD exception visiting " + resource.getName(), e); // TODO: complete message
                }
            } else {
                log.debug("The file " + file.getName() + " is not in the working set");
            }
        }
    }

}