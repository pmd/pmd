package net.sourceforge.pmd.eclipse;

import net.sourceforge.pmd.cpd.CPD;
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
 * Revision 1.2  2003/03/17 23:32:05  phherlin
 * minor cleaning
 *
 */
public class CPDVisitor implements IResourceVisitor {
    CPD cpd;

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
        boolean result = true;
        if ((resource instanceof IFile)
            && (((IFile) resource).getFileExtension() != null)
            && ((IFile) resource).getFileExtension().equals("java")) {
            try {
                cpd.add(((IFile) resource).getLocation().toFile());
            } catch (Exception e) {
                MessageDialog.openError(null, "CPD", e.toString());
            }
            result = false;
        }

        return result;

    }

}
