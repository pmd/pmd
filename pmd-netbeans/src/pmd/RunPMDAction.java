/*
 *  Copyright (c) 2002, Ole-Martin Mørk
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
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;

import org.openide.TopManager;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.SourceCookie;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CookieAction;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

import pmd.config.PMDOptionsSettings;
import pmd.config.ui.SelectedListModel;

/**
 * Action that can always be invoked and work procedurally.
 *
 * @author Ole-Martin Mørk
 * @created 17. oktober 2002
 */
public class RunPMDAction extends CookieAction {
	
	/** Indicates if any violations has been printed */
	private boolean printed = false;


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
		return "/pmd/resources/MyActionIcon.gif";
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
	 * @return DataFolder.class and SourceCookie.class
	 */
	protected Class[] cookieClasses() {
		return new Class[]{DataFolder.class, SourceCookie.class};
	}


	/**
	 * Returns the mode of this action
	 *
	 * @return Description of the Return Value
	 * @see org.openide.util.actions.SystemAction#MODE_EXACTLY_ONE
	 */
	protected int mode() {
		return MODE_ALL;
	}


	/**
	 * Runs pmd on the specified cookie
	 *
	 * @param dataobject the dataobject to check
	 * @param listener the listener to be used as listener for the output window
	 * @param writer the writer to use to write to the output window
	 * @exception IOException If the method can't read the files it should check or
	 *      can't write to the output window
	 */
	private void checkCookie( DataObject dataobject, PMDOutputListener listener, OutputWriter writer )
		 throws IOException {
		SourceCookie cookie = ( SourceCookie )dataobject.getCookie( SourceCookie.class );

		//The file is not a java file
		if( cookie == null ) {
			return;
		}
		Reader reader = getSourceReader( dataobject );
		String name = cookie.getSource().getClasses()[0].getName().getFullName();
		PMD pmd = new PMD();
		RuleContext ctx = new RuleContext();
		Report report = new Report();
		ctx.setReport( report );
		ctx.setSourceCodeFilename( name );
		RuleSet set = constructRuleSets();
		pmd.processFile( reader, set, ctx );
		Iterator iterator = ctx.getReport().iterator();
		if( !ctx.getReport().isEmpty() ) {

			ArrayList list = new ArrayList( ctx.getReport().size() );
			while( iterator.hasNext() ) {
				RuleViolation violation = ( RuleViolation )iterator.next();
				StringBuffer buffer = new StringBuffer();
				buffer.append( violation.getRule().getName() ).append( ", " );
				buffer.append( violation.getDescription() );
				Fault fault = new Fault( violation.getLine(), name, buffer.toString() );
				list.add( fault );
				FaultRegistry.getInstance().registerFault( fault, dataobject );
			}
			Collections.sort( list );
			for( int i = 0; i < list.size(); i++ ) {
				writer.println( String.valueOf( list.get( i ) ), listener );
			}
			printed = true;
		}
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
		try {
			printed = false;
			InputOutput io = TopManager.getDefault().getIO( "PMD output", false );
			io.select();
			io.getOut().reset();
			for( int i = 0; i < node.length; i++ ) {
				SourceCookie cookie = ( SourceCookie )node[i].getCookie( SourceCookie.class );

				//Checks to see if it's a java source file
				if( cookie != null ) {
					checkCookie( ( DataObject )node[i].getCookie( DataObject.class ), listener, io.getOut() );
				}
				//Or if it's a folder
				else {
					DataFolder folder = ( DataFolder )node[i].getCookie( DataFolder.class );
					Enumeration enumeration = folder.children( true );
					while( enumeration.hasMoreElements() ) {
						DataObject dataobject = ( DataObject )enumeration.nextElement();
						cookie = ( SourceCookie )dataobject.getCookie( SourceCookie.class );
						if( cookie != null ) {
							checkCookie( dataobject, listener, io.getOut() );
						}
					}
				}
			}
			if( !printed ) {
				io.getOut().println( "Everything ok", null );
			}

		}
		catch( IOException e ) {
			TopManager.getDefault().getErrorManager().notify( e );
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
			
			
			
			SelectedListModel.getSelectedListModelInstance().setData( PMDOptionsSettings.getDefault().getRules() );
			List list = SelectedListModel.getSelectedListModelInstance().getData();
			Iterator iterator = list.iterator();
			while( iterator.hasNext() ) {
				rules.addRule( (Rule)iterator.next() );
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
	private Reader getSourceReader( DataObject dataobject ) throws IOException {
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
			reader = new InputStreamReader( file.getInputStream() );
		}
		return reader;
	}
}
