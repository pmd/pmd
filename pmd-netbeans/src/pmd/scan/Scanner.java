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
package pmd.scan;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.StyledDocument;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.cookies.EditorCookie;
import org.openide.cookies.LineCookie;
import org.openide.nodes.Node;
import org.openide.text.Line;
import pmd.Fault;
import pmd.RunPMDAction;
import pmd.config.PMDOptionsSettings;

/**
 * PMD background scanner.
 */
public class Scanner implements Runnable, DocumentListener, PropertyChangeListener {

	private final Node node;
	private boolean running = true;
	private StyledDocument subscribedDoc = null;
	private EditorCookie.Observable subscribedObservable = null;
	
	/**
	 * If this is -1, scan constantly. Else, scan only when this has changed.
	 */
	private int modCount;
	
	/** Creates a new instance of Scanner */
	public Scanner( Node node ) {
		this.node = node;
		EditorCookie edtCookie = (EditorCookie)node.getCookie(EditorCookie.class);
		if(edtCookie != null) {
			StyledDocument doc = edtCookie.getDocument();
			if(doc != null) {
				doc.removeDocumentListener(this); // prevent duplicate listener registration
				doc.addDocumentListener(this);
				subscribedDoc = doc;
			} // else document has probably been unloaded because the editor window was closed.
			EditorCookie.Observable obs = (EditorCookie.Observable)node.getCookie(EditorCookie.Observable.class);
			if(obs != null) {
				obs.removePropertyChangeListener(this); // prevent duplicate listener registration
				obs.addPropertyChangeListener(this);
				subscribedObservable = obs;
			}
			modCount = 0;
		} else {
			modCount = -1;
		}
		
	}
	
	public StyledDocument getSubscribedDocument() {
		return subscribedDoc;
	}
	
	public void run() {
		try {
			tracelog("started");
			while( running ) {
				int lastModCount = modCount;
				tracelog("run starting at modcount: " + lastModCount);
				DataObject object = ( DataObject )node.getCookie( DataObject.class ) ;
				LineCookie cookie = ( LineCookie )object.getCookie( LineCookie.class );
				Line.Set lineset = cookie.getLineSet();
				List list = Collections.singletonList(object);
				List faults = RunPMDAction.checkCookies(list );
				PMDScanAnnotation.clearAll();
				for( int i = 0; i < faults.size(); i++ ) {
					Fault fault = (Fault)faults.get( i );
					int lineNum = fault.getLine();
					Line line = lineset.getCurrent( lineNum - 1 );
					if(line == null)
					{
						tracelog("no original line found for line " + lineNum + " in lineset; probably document closed" );
					}
					else
					{
						tracelog("Line class : " + line.getClass().getName());
						tracelog("Node: " + node + ", count: " + line.getAnnotationCount() );
						PMDScanAnnotation annotation = PMDScanAnnotation.getNewInstance();
						String msg = fault.getMessage();
						annotation.setErrorMessage( msg );
						annotation.attach( line );
						line.addPropertyChangeListener( annotation );
					}
				}
				tracelog("run finished at modcount: " + lastModCount);
				do {
					try {
						Thread.sleep( PMDOptionsSettings.getDefault().getScanInterval().intValue() * 1000 );
					}
					catch( InterruptedException e ) {
						ErrorManager.getDefault().notify(e);
					}
				} while(running && modCount != -1 && modCount == lastModCount);
			}
		}
		catch( IOException e ) {
			ErrorManager.getDefault().notify(e);
		}
		finally {
			tracelog("stopped");
		}
	}
	
	public void stopThread() {
		running = false;
	}
	
	public String toString() {
		return "PMDScanner[" + node + "]";
	}
	
	public void changedUpdate(DocumentEvent ev) {
		// Ignore these; they are just attribute changes. Actual typing appears as remove and insert updates.
	}
	
	public void insertUpdate(DocumentEvent ev) {
		incrementModCount();
	}
	
	public void removeUpdate(DocumentEvent ev) {
		incrementModCount();
	}
	
	public void propertyChange(PropertyChangeEvent ev) {
		if(ev.getSource() instanceof EditorCookie) {
			EditorCookie cookie = (EditorCookie)ev.getSource();
			if(subscribedDoc != null) {
				subscribedDoc.removeDocumentListener(this);
				subscribedDoc = null;
			}
			StyledDocument doc = cookie.getDocument();
			if(doc == null) {
				// stop the scanner thread -- this document has ben closed.
				running = false;
				incrementModCount();
			} else {
				doc.removeDocumentListener(this);
				doc.addDocumentListener(this);
				subscribedDoc = doc;
				if(subscribedObservable != null) {
					subscribedObservable.removePropertyChangeListener(this);
					subscribedObservable = null;
				}
				EditorCookie.Observable obs = (EditorCookie.Observable)node.getCookie(EditorCookie.Observable.class);
				if(obs != null) {
					obs.removePropertyChangeListener(this); // prevent duplicate listener registration
					obs.addPropertyChangeListener(this);
					subscribedObservable = obs;
				}
			}
		} else {
			tracelog("Expected PropertyChangeEvent to come from EditorCookie, but it came from " + ev.getSource().getClass().getName());
		}
	}
	
	private synchronized void incrementModCount() {
		modCount++;
	}
	
	private void tracelog(String str) {
		if(RunPMDAction.TRACE_LOGGING) {
			ErrorManager.getDefault().log(ErrorManager.ERROR, this.toString() + ": " + str);		
		}
	}
}
