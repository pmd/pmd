/*
 *  Copyright (c) 2002-2003, Ole-Martin Mørk
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

import org.openide.awt.StatusDisplayer;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.text.Line;
import org.openide.text.Line.Set;
import org.openide.windows.OutputEvent;
import org.openide.windows.OutputListener;

/**
 * Listens for user actions on the output pane
 *
 * @author Ole-Martin Mørk
 * @created 24. oktober 2002
 */
public class PMDOutputListener implements OutputListener {
	

	/** The instance of this class */
	private final static PMDOutputListener instance = new PMDOutputListener();


	/**
	 * Private constructor
	 *
	 * @see #getInstance()
	 */
	private PMDOutputListener() { }


	/**
	 * Returns the instance of this class
	 *
	 * @return the instance
	 */
	public static PMDOutputListener getInstance() {
		return instance;
	}


	/** Removes the marking of the line. */
	public void detach() {
		PMDAnnotation.clearAll();
	}


	/**
	 * Fired when the user doubleclicks on a line in the outputpane
	 *
	 * @param outputEvent the event that was fired
	 */
	public void outputLineAction( OutputEvent outputEvent ) {
		PMDAnnotation.clearAll();
		DataObject object = FaultRegistry.getInstance().getDataObject( outputEvent.getLine() );
		LineCookie cookie = ( LineCookie )object.getCookie( LineCookie.class );
		Set lineset = cookie.getLineSet();
		int lineNum = Fault.getLineNum( outputEvent.getLine() );
		Line line = lineset.getOriginal( lineNum - 1 );
		String msg = Fault.getErrorMessage( outputEvent.getLine() );
		PMDAnnotation annotation = PMDAnnotation.getNewInstance();
		annotation.setErrorMessage( msg );
		annotation.attach( line );
		line.addPropertyChangeListener( annotation );
		line.show( Line.SHOW_GOTO );
		StatusDisplayer.getDefault().setStatusText( msg );
	}
	
	public void addAnnotation() {
	
	
	}


	/**
	 * Not implemented
	 *
	 * @param outputEvent Description of the Parameter
	 */
	public void outputLineCleared( OutputEvent outputEvent ) { }


	/**
	 * Not implemented
	 *
	 * @param outputEvent Description of the Parameter
	 */
	public void outputLineSelected( OutputEvent outputEvent ) { }
} 
