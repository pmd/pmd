package net.sourceforge.pmd.eclipse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.eclipse.preferences.PMDPreferencePage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author David Dixon-Peugh
 *
 * This class visits all of the resources in the Eclipse
 * Workspace, and runs PMD on them if they happen to be
 * Java files.
 * 
 * Any violations get tagged onto the file as notes.
 */
public class PMDVisitor implements IResourceVisitor {
	private PMD pmd = null;
	private RuleSet ruleSet = null;
		
	/**
	 * No Argument Constructor
	 */
	public PMDVisitor(String ruleSetFile) 
		throws IOException
	{
		try {
			pmd = new PMD();
 			RuleSetFactory factory = new RuleSetFactory();
			ruleSet = 
				factory.createRuleSet(new FileInputStream(ruleSetFile));
		} catch (Throwable e) {
			e.printStackTrace();
		}
				
	}

	private void runPMD( IFile file ) throws CoreException
	{
		Reader input = 
			new InputStreamReader( file.getContents() );
		RuleContext context = new RuleContext();
		context.setSourceCodeFilename( file.getName() );
		context.setReport( new Report() );

		try {
			pmd.processFile( input, ruleSet, context);
		} catch (FileNotFoundException e) {
			e.printStackTrace();	
		}	

		Iterator iter = context.getReport().iterator();
		while (iter.hasNext()) {
			RuleViolation violation = (RuleViolation) iter.next();
			
			IMarker marker = file.createMarker(IMarker.TASK);
			marker.setAttribute( IMarker.MESSAGE, 
								 violation.getDescription() );
			marker.setAttribute( IMarker.LINE_NUMBER,
								 violation.getLine() );
			
		}
	}
	
	/**
	 * @see org.eclipse.core.resources.IResourceVisitor#visit(IResource)
	 */
	public boolean visit(IResource resource) throws CoreException {
		if ((resource instanceof IFile) &&
			(((IFile) resource).getFileExtension() != null) &&
			((IFile) resource).getFileExtension().equals("java")) {	
				runPMD( (IFile) resource );
			return false;
		} else {
			return true;
		}
	}
}
