/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;

import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.queries.SourceLevelQuery;
import org.openide.ErrorManager;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.cookies.SourceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

import pmd.config.ConfigUtils;
import pmd.scan.EditorChangeListener;

/**
 * Action that runs PMD on the currently selected Java file or set of Java files.
 * This is called both by NetBeans when the action is manually invoked by the user
 * ({@link #performAction}),
 * and by the real-time scanner when a Java file needs to be scanned
 * ({@link #checkCookies}).
 */
public class RunPMDAction extends CookieAction {
	
	/** True means verbose trace logging should be performed. Trace logging goes out at ERROR level
	 * so that INFORMATIONAL level is not needed (would require more configuration and would mix output
	 * with a lot of verbose non-PMD-related output). There is probably a better-behaved way of doing this ...
	 * if you care, please tell me :)
	 **/
	public static final boolean TRACE_LOGGING = System.getProperty("pmd-netbeans.trace.logging") != null;


	/**
	 * Overridden to log that the action is being initialized, and to register an editor change listener for
	 * scanning.
	 */
	protected void initialize() {
		super.initialize();
		EditorChangeListener.initialize();
	}
	/**
	 * Gets the name of this action
	 *
	 * @return the name of this action
	 */
	public String getName() {
		return NbBundle.getMessage( RunPMDAction.class, "LBL_Action" );
	}


	/**
	 * Gets the filename of the icon associated with this action
	 *
	 * @return the name of the icon
	 */
	protected String iconResource() {
		return "pmd/resources/PMDOptionsSettingsIcon.gif";
	}


	/**
	 * Returns default help
	 *
	 * @return HelpCtx.DEFAULT_HELP
	 */
	public HelpCtx getHelpCtx() {
		return HelpCtx.DEFAULT_HELP;
	}


	/**
	 * Returns the cookies that can use this action
	 *
	 * @return an array of the two elements DataFolder.class and SourceCookie.class
	 */
	protected Class[] cookieClasses() {
		return new Class[]{DataFolder.class, SourceCookie.class};
	}


	/**
	 * Returns the mode of this action
	 *
	 * @return the mode of this action
	 * @see org.openide.util.actions.CookieAction#MODE_ALL
	 */
	protected int mode() {
		return MODE_ALL;
	}


	/**
	 * Runs PMD on the given list of DataObjects, with no callback.
	 * This just calls {@link #checkCookies(List, RunPMDCallback)} with a default callback that displays
	 * progress in the status bar.
	 *
	 * @param dataobjects the list of data objects to run PMD on, not null. Elements are instanceof
	 *                    {@link DataObject}.
	 * @return the list of rule violations found in the run, not null. Elements are instanceof {@link Fault}.
	 * @throws IOException on failure to read one of the files or to write to the output window.
	 */
	public static List checkCookies( List dataobjects ) throws IOException {
		return checkCookies(dataobjects, new DefaultCallback());
	}
	
	/**
	 * Runs PMD on the given list of DataObjects, interacting with the given callback.
	 *
	 * @param dataobjects the list of data objects to run PMD on, not null. Elements are instanceof
	 *                    {@link DataObject}.
	 * @param callback the callback to interact with. Receives notifications and can stop the run.
	 * @return the list of rule violations found in the run, not null. Elements are instanceof {@link Fault}.
	 * @throws IOException on failure to read one of the files or to write to the output window.
	 */
	public static List checkCookies( List dataobjects, RunPMDCallback callback ) throws IOException {
                SourceLevelQuery sourceLevelQuery =
                        (SourceLevelQuery) Lookup.getDefault().lookup(SourceLevelQuery.class);
		RuleSet set = constructRuleSets();
                PMD pmd_1_3 = null;
                PMD pmd_1_4 = null;
                PMD pmd_1_5 = null;
		ArrayList list = new ArrayList( 100 );
		callback.pmdStart( dataobjects.size() );
		for( int i = 0; i < dataobjects.size(); i++ ) {
			boolean keepGoing = callback.pmdProgress( i + 1 );
			if(!keepGoing) {
				break;
			}
			DataObject dataobject = ( DataObject )dataobjects.get( i );
			FileObject fobj = dataobject.getPrimaryFile();
			String name = ClassPath.getClassPath( fobj, ClassPath.SOURCE ).getResourceName( fobj, '.', false );
			
			//The file is not a java file
			if( !dataobject.getPrimaryFile().hasExt( "java" ) || dataobject.getCookie( LineCookie.class ) == null ) {
				continue;
			}
                        
                        String sourceLevel = sourceLevelQuery.getSourceLevel(fobj);
                        
                        // choose the correct PMD to use according to the source level
                        PMD pmd = null;
                        if (sourceLevel != null) {
                            if (sourceLevel.equals("1.5")) {
                                if (pmd_1_5 == null)
                                    pmd_1_5 = new PMD(new TargetJDK1_5());
                                pmd = pmd_1_5;
                            } else if (sourceLevel.equals("1.3")) {
                                if (pmd_1_3 == null)
                                    pmd_1_3 = new PMD(new TargetJDK1_3());
                                pmd = pmd_1_3;
                            }
                        }
                        // default to JDK 1.4 if we don't know any better...
                        if (pmd == null) {
                            if (pmd_1_4 == null)
                                pmd_1_4 = new PMD(new TargetJDK1_4());
                            pmd = pmd_1_4;
                        }

                        
			Reader reader;
			try {
				reader = getSourceReader( dataobject );
			}
			catch( IOException ioe) {
				Fault fault = new Fault( 1, name, "IOException reading file for class " + name + ": " + ioe.toString());
				ErrorManager.getDefault().notify( ioe );
				list.add( fault );
				FaultRegistry.getInstance().registerFault( fault, dataobject );
				continue;
			}
			
			RuleContext ctx = new RuleContext();
			Report report = new Report();
			ctx.setReport( report );
			ctx.setSourceCodeFilename( name );
			try {
				pmd.processFile( reader, set, ctx );
			}
			catch( PMDException e ) {
				Fault fault = new Fault( 1, name, e );
				ErrorManager.getDefault().log(ErrorManager.ERROR, "PMD threw exception " + e.toString());
				list.add( fault );
				FaultRegistry.getInstance().registerFault( fault, dataobject );
			}

			Iterator iterator = ctx.getReport().iterator();
			while( iterator.hasNext() ) {
				RuleViolation violation = ( RuleViolation )iterator.next();
				StringBuffer buffer = new StringBuffer();
				buffer.append( violation.getRule().getName() ).append( ", " );
				buffer.append( violation.getDescription() );
				Fault fault = new Fault( violation.getLine(),
					violation.getFilename(),
					buffer.toString() );
				list.add( fault );
				FaultRegistry.getInstance().registerFault( fault, dataobject );
			}
		}
		callback.pmdEnd();
		Collections.sort( list );
		return list;
	}


	/**
	 * Performs the action this action is set up to do on the specified nodes
	 *
	 * @param node the nodes that the action is involved on
	 */
	protected void performAction( Node[] node ) {
		PMDOutputListener listener = PMDOutputListener.getInstance();
		listener.detach();
		FaultRegistry.getInstance().clearRegistry();
		ProgressDialog progressDlg = null;
		try {
			StatusDisplayer.getDefault().setStatusText("PMD checking for rule violations");
			List list = getDataObjects(node);
			progressDlg = new ProgressDialog();
			List violations = checkCookies(list, progressDlg);
			progressDlg = null;
			IOProvider ioProvider = (IOProvider)Lookup.getDefault().lookup(IOProvider.class);
			InputOutput output = ioProvider.getIO("PMD output", false);
			if(violations.isEmpty()) {
				StatusDisplayer.getDefault().setStatusText("PMD found no rule violations");
				output.closeInputOutput();
			}
			else {
				output.select();
				output.getOut().reset();
				for(int i = 0; i < violations.size(); i++) {
					Fault fault = (Fault)violations.get(i);
					if(fault.getLine() == -1) {
						output.getOut().println(String.valueOf(fault));
					}
					else {
						output.getOut().println(String.valueOf(fault), listener);
					}
				}
				StatusDisplayer.getDefault().setStatusText("PMD found rule violations");
			}
		} catch(IOException e) {
			ErrorManager.getDefault().notify(e);
			if(progressDlg != null) {
				progressDlg.pmdEnd();
			}
		}
	}


	/**
	 * Constructs the ruleset.
	 *
	 * @return the constructed ruleset
	 * @see pmd.config.PMDOptionsSettings#getRulesets()
	 */
	private static RuleSet constructRuleSets() {
		RuleSet rules = new RuleSet();
		List list = ConfigUtils.getRuleList();
		Iterator iterator = list.iterator();
		while( iterator.hasNext() ) {
			rules.addRule( ( Rule )iterator.next() );
		}
		return rules;
	}


	/**
	 * Get the reader for the specified dataobject
	 *
	 * @param dataobject the dataobject to read
	 * @return a reader for the dataobject
	 * @exception IOException if the object can't be read
	 */
	private static Reader getSourceReader( DataObject dataobject ) throws IOException {
		Reader reader;
		EditorCookie editor = ( EditorCookie )dataobject.getCookie( EditorCookie.class );

		//If it's the currently open document that's being checked
		if( editor != null && editor.getOpenedPanes() != null ) {
			String text = editor.getOpenedPanes()[0].getText();
			reader = new StringReader( text );
		}
		else {
			Iterator iterator = dataobject.files().iterator();
			FileObject file = ( FileObject )iterator.next();
			reader = new BufferedReader( new InputStreamReader( file.getInputStream() ) );
		}
		return reader;
	}


	/**
	 * Gets the data objects associated with the given nodes.
	 *
	 * @param node the nodes to get data objects for
	 * @return a list of the data objects. Each element is instanceof DataObject.
	 */
	private List getDataObjects( Node[] node ) {
		ArrayList list = new ArrayList();
		for( int i = 0; i < node.length; i++ ) {
			DataObject data = (DataObject)node[i].getCookie( DataObject.class );
			
			//Checks to see if it's a java source file
			if( data.getPrimaryFile().hasExt( "java" ) ) {
				list.add( data );
			}
			//Or if it's a folder
			else {
				DataFolder folder = ( DataFolder )node[i].getCookie( DataFolder.class );
				Enumeration enumeration = folder.children( true );
				while( enumeration.hasMoreElements() ) {
					DataObject dataobject = ( DataObject )enumeration.nextElement();
					if( dataobject.getPrimaryFile().hasExt( "java" ) ) {
						list.add( dataobject );
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * Default callback implementation, to use when no callback is provided to <code>checkCookies</code>.
	 * Writes progress information into the StatusDisplayer (generally the status bar). Use a separate
	 * instance of this for each run, as it stores state (the total number of files).
	 */
	private static class DefaultCallback implements RunPMDCallback {
		
		/**
		 * This implementation is a no-op.
		 */
		public void pmdEnd() {
			// NO-OP
		}
		
		/**
		 * This implementation reports progress in the status bar and returns true.
		 *
		 * @param index index of the file on which PMD execution is starting. Greater than 0,
		 *              less than or equal to the number of files reported in {@link #pmdStart}.
		 * @return true
		 */
		public boolean pmdProgress(int index) {
			StatusDisplayer.getDefault().setStatusText(
				"PMD checking for rule violations in file " + index + "/" + numFiles );
			return true;
		}
		
		/**
		 * This implementation stores the number of files.
		 *
		 * @param numFiles the number of files to be scanned, greater than 0.
		 */
		public void pmdStart(int numFiles) {
			this.numFiles = numFiles;
		}
		
		private int numFiles;
		
	}
}
