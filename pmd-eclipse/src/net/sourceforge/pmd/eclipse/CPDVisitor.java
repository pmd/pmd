package net.sourceforge.pmd.eclipse;

import net.sourceforge.pmd.cpd.CPD;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import java.io.File;

/**
 * @author David Craine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
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
		if ((resource instanceof IFile) &&
			(((IFile) resource).getFileExtension() != null) &&
			((IFile) resource).getFileExtension().equals("java")) {	
				try {
					cpd.add(((IFile)resource).getLocation().toFile());
				}
				catch (Exception e){
					MessageDialog.openError(null, "CPD", e.toString());
				}
			return false;
		} 
		else {
			return true;
		}

	}

}
