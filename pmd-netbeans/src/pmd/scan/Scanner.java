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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.ErrorManager;
import org.openide.cookies.LineCookie;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.text.Line.Set;
import pmd.Fault;
import pmd.PMDAnnotation;
import pmd.RunPMDAction;
import pmd.config.PMDOptionsSettings;
import pmd.scan.PMDScanAnnotation;

/**
 *
 * @author  ole martin mørk
 */
public class Scanner implements Runnable {
	private final Node node;
	
	private boolean running = true;
	/** Creates a new instance of Scanner */
	public Scanner( Node node ) {
		this.node = node;
	}
	
	public void run() {
		try {
			while( running ) {
				DataObject object = ( DataObject )node.getCookie( DataObject.class ) ;
				List list = new ArrayList();
				list.add( object );
				List faults = RunPMDAction.checkCookies(list );
				ErrorManager.getDefault().log(ErrorManager.ERROR, ""+faults);
				PMDScanAnnotation.clearAll();
				LineCookie cookie = ( LineCookie )object.getCookie( LineCookie.class );
				Set lineset = cookie.getLineSet();
				for( int i = 0; i < faults.size(); i++ ) {
					Fault fault = (Fault)faults.get( i );
					int lineNum = fault.getLine();
					Line line = lineset.getOriginal( lineNum - 1 );
					ErrorManager.getDefault().log( ErrorManager.ERROR, "count: " + line.getAnnotationCount() );
					if( line.getAnnotationCount() <= 0 ) {
						PMDScanAnnotation annotation = PMDScanAnnotation.getNewInstance();
						String msg = fault.getMessage();
						annotation.setErrorMessage( msg );
						annotation.attach( line );

						line.addPropertyChangeListener( annotation );
					}
				}
				Thread.sleep( PMDOptionsSettings.getDefault().getScanInterval().intValue() * 1000 );
			}
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
		catch( InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
	public void stopThread() {
		running = false;
	}
	
}
