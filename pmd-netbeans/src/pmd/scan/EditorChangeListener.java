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
import javax.swing.text.StyledDocument;

import org.openide.ErrorManager;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent;

import pmd.RunPMDAction;
import pmd.config.PMDOptionsSettings;

/**
 * Listener for changes in active document or PMD settings.
 * When either of these changes, this object makes PMD background scanning react accordingly.
 */
public class EditorChangeListener implements PropertyChangeListener {

	private Scanner scanner;
	private Node currentlyScannedNode;
	
	public static void initialize() {
		EditorChangeListener ecl = new EditorChangeListener();
		TopComponent.getRegistry().addPropertyChangeListener(ecl);
		PMDOptionsSettings.getDefault().addPropertyChangeListener(ecl);
	}
	
	private EditorChangeListener() {
	}
 	
 	public void propertyChange(PropertyChangeEvent evt) {
		tracelog("Got propchange in editorchangelistener: " + evt);
		if(evt.getSource() instanceof PMDOptionsSettings) {
			// settings changed, so chuck out current scanner if any.
			if(scanner != null) {
				tracelog("Stopping scanner " + scanner + " because of changed PMD settings");
				scanner.stopThread();
				scanner = null;
				currentlyScannedNode = null;
			}
		}
		if( PMDOptionsSettings.getDefault().isScanEnabled().equals( Boolean.TRUE ) ) {
			Node[] nodes = TopComponent.getRegistry().getActivatedNodes();
			Node foundNode = null;
			for(int i = 0; i < nodes.length; i++) {
				if(nodes[i].getCookie(EditorCookie.class) != null) {
					foundNode = nodes[i];
					tracelog("  Found scannable node " + foundNode);
					break;
				} else {
					tracelog("  Non-scannable node " + nodes[i] + " of class " + nodes[i].getClass().getName());
				}
			}
			if(foundNode != null && (currentlyScannedNode == null || !foundNode.equals(currentlyScannedNode))) {
				startScan(foundNode);
			}
		}
	}
	
	private void startScan( Node node ) {
		if( scanner != null ) {
			// check whether it's the same one (if so, replacing the scanner is unnecessary work)
			EditorCookie edtCookie = (EditorCookie)node.getCookie(EditorCookie.class);
			if(edtCookie != null) {
				StyledDocument doc = edtCookie.getDocument();
				if(doc != null && doc == scanner.getSubscribedDocument()) {
					// Same document, so we don't need to replace the scanner.
					tracelog("  Cancelling scanner replacement, same document!");
					return;
				}
			}
			// not the same one, so we replace the scanner.
			tracelog("  Stopping scanner " + scanner);
			scanner.stopThread();
		}
		scanner = new Scanner( node );
		tracelog("  Starting scanner " + scanner);
		Thread thread = new Thread( scanner );
		thread.setPriority( Thread.MIN_PRIORITY );
		this.currentlyScannedNode = node;
		thread.start();
	}
	
	private void tracelog(String str) {
		if(RunPMDAction.TRACE_LOGGING) {
			ErrorManager.getDefault().log(ErrorManager.ERROR, str);
		}
	}
	
}
