package net.sourceforge.pmd.eclipse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author David Dixon-Peugh
 * @author David Craine
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
	public PMDVisitor(String[] ruleSetFiles) 
		throws IOException
	{
		try {
			pmd = new PMD();
 			RuleSetFactory factory = new RuleSetFactory();
 			
			ruleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(ruleSetFiles[0]));
			for (int i=1; i<ruleSetFiles.length; i++) {
				RuleSet tmpRuleSet = factory.createRuleSet(getClass().getClassLoader().getResourceAsStream(ruleSetFiles[i]));
				ruleSet.addRuleSet(tmpRuleSet);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			MessageDialog.openError(null, "PMD Error", "RuleSet construction problem: " + e.toString());
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			MessageDialog.openInformation(null, "PMD Error", sw.toString());


		}
				
	}

	private void runPMD( IFile file ) throws CoreException
	{
		Reader input = 
			new InputStreamReader( file.getContents() );
		RuleContext context = new RuleContext();
		context.setSourceCodeFilename( file.getName() );
		context.setReport( new Report() );

		pmd.processFile( input, ruleSet, context);

		Iterator iter = context.getReport().iterator();
		file.deleteMarkers(null,false, IResource.DEPTH_ONE);
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
