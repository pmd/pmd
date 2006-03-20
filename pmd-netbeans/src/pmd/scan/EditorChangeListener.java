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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.StyledDocument;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Registry;

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
public class EditorChangeListener implements PropertyChangeListener, ChangeListener {

    private Scanner scanner;
    
    private BaseDocument active;
    
    public static void initialize() {
        EditorChangeListener ecl = new EditorChangeListener();
        PMDOptionsSettings settings = PMDOptionsSettings.getDefault();
        settings.addPropertyChangeListener(ecl);
        if (settings.isScanEnabled().booleanValue()) {
            Registry.addChangeListener(ecl);
        }
    }
    
	private EditorChangeListener() {
	}
 	
 	public void propertyChange(PropertyChangeEvent evt) {
            PMDOptionsSettings settings = PMDOptionsSettings.getDefault();
            if (settings.PROP_ENABLE_SCAN.equals(evt.getPropertyName())) {
                Registry.removeChangeListener(this);
                if (settings.isScanEnabled().booleanValue()) {
                    Registry.addChangeListener(this);
                }
                else {
                    if(scanner != null) {
                        tracelog("Stopping scanner " + scanner + " because of changed PMD settings");
                        scanner.cancel();
                        scanner = null;
                    }
                }
            }
        }
         
	private void startScan( BaseDocument doc ) {
            if (scanner == null) {
                scanner = new Scanner();
            }
            scanner.attachToDoc(doc);
            active = doc;
	}
	
	private void tracelog(String str) {
		if(RunPMDAction.TRACE_LOGGING) {
			ErrorManager.getDefault().log(ErrorManager.ERROR, str);
		}
	}

    public void stateChanged(ChangeEvent e) {
        BaseDocument doc = Registry.getMostActiveDocument();
        if (doc == null) {
            if (scanner != null) {
                scanner.cancel();
            }
        }
        else if (!doc.equals(active)) {
            startScan (doc);
        }
        // else it is the same document
    }
	
}
