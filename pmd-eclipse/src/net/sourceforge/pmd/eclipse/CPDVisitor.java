package net.sourceforge.pmd.eclipse;

import net.sourceforge.pmd.cpd.CPD;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * A visitor to process IFile resource against CPD
 * 
 * @author David Craine
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2003/05/19 22:26:07  phherlin
 * Updating PMD engine to v1.05
 * Fixing CPD usage to conform to new engine implementation
 *
 */
public class CPDVisitor implements IResourceVisitor {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.CPDVisitor");
    private CPD cpd;

    /**
     * Constructor for CPDVisitor.
     */
    public CPDVisitor(CPD cpd) {
        super();
        this.cpd = cpd;
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
     * Add java files into the CPD object
     */
    public boolean visit(IResource resource) throws CoreException {
        log.debug("CPD Visiting " + resource);
        boolean result = true;
        if ((resource instanceof IFile)
            && (((IFile) resource).getFileExtension() != null)
            && ((IFile) resource).getFileExtension().equals("java")) {
            try {
                log.debug("CPD adding file " + resource.getName());
                cpd.add(((IFile) resource).getLocation().toFile());
            } catch (Exception e) {
                MessageDialog.openError(null, "CPD", e.toString());
            }
            result = false;
        }

        return result;

    }

}
