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
package pmd.scan;

import java.beans.PropertyChangeListener;
import org.openide.ErrorManager;
import org.openide.cookies.SourceCookie;
import org.openide.nodes.Node;
import org.openide.windows.TopComponent.Registry;

/**
 *
 * @author  ole martin mørk
 */
public class EditorChangeListener implements PropertyChangeListener {

	private final Registry registry;
	private Scanner scanner;
	public EditorChangeListener(Registry registry ) {
		this.registry = registry;
	
	}
 	
 	public void propertyChange(java.beans.PropertyChangeEvent propertyChangeEvent) {
		Node node[] = registry.getActivatedNodes();
		SourceCookie cookie = null;
		int i = 0;
		for( i = 0; i < node.length; i++ ) {
			ErrorManager.getDefault().log(ErrorManager.ERROR, "checking cookie " + node[i]);			
			cookie = (SourceCookie)node[i].getCookie( SourceCookie.class );
			if( cookie != null ) {
				break;
			}
		}
		if( cookie != null ) {
			ErrorManager.getDefault().log(ErrorManager.ERROR, "starting scan");
			startScan( node[i] );
		}
	}
	
	private void startScan( Node node ) 
	{
		if( scanner != null ) {
			scanner.stopThread();
		}
		scanner = new Scanner( node );
		Thread thread = new Thread( scanner );
		thread.setPriority( Thread.MIN_PRIORITY );
		thread.start();
		int i;
	}
	
}
