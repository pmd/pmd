/*
 *  Copyright (c) 2002-2006, the pmd-netbeans team
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
import java.util.Collections;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsNames;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.netbeans.modules.editor.java.JavaKit;
import org.openide.ErrorManager;
import org.openide.loaders.DataObject;
import org.openide.cookies.LineCookie;
import org.openide.text.Annotatable;
import org.openide.text.Line;
import org.openide.text.Line.Part;
import org.openide.util.RequestProcessor;
import pmd.Fault;
import pmd.RunPMDAction;
import pmd.config.PMDOptionsSettings;

/**
 * PMD background scanner.
 */
public class Scanner implements Runnable, DocumentListener {

    private static RequestProcessor PMD_RP = new RequestProcessor("PMD scanner", 1);
    
    private RequestProcessor.Task task;

    private BaseDocument doc;
    
    /** Creates a new instance of Scanner.
     *  This is created from EditorChangeListener only.
     */
    Scanner() {
    }
	
    /** Attaches listeners to a node.
     */
    void attachToDoc (BaseDocument doc) {
        if (doc == null) {
            return;
        }
        if (doc.equals (this.doc)) {
            tracelog("the same node detected");
            return;
        }
        detachFromDoc();
        
        this.doc = doc;
        doc.addDocumentListener(this);
        
        task = PMD_RP.post(this, PMDOptionsSettings.getDefault().getScanInterval().intValue() * 1000, Thread.MIN_PRIORITY);
    }
	
    private void detachFromDoc() {
        if (doc != null) {
            doc.removeDocumentListener(this);
	}
    }
    
        public void run() {
            if (doc == null) {
                return;
            }
            
            try {
                tracelog("started");
                    
                int tabSize = 8;
                Integer foo = (Integer) Settings.getValue(JavaKit.class, SettingsNames.TAB_SIZE);
                if (foo != null)
                    tabSize = foo.intValue();

                DataObject dobj = NbEditorUtilities.getDataObject(doc);
                if (dobj == null) {
                    return;
                }
                if (!dobj.getPrimaryFile().canWrite()) {
                    return;
                }

                LineCookie cookie = ( LineCookie )dobj.getCookie( LineCookie.class );
                Line.Set lineset = cookie.getLineSet();
                List list = Collections.singletonList(dobj);
                List faults = RunPMDAction.performScan(list );
                PMDScanAnnotation.clearAll();
                for( int i = 0; i < faults.size(); i++ ) {
                    Fault fault = (Fault)faults.get( i );
                    int lineNum = fault.getLine();
                    Line line = lineset.getCurrent( lineNum - 1 );
                    if(line == null) {
                        tracelog("no original line found for line " + lineNum + " in lineset; probably document closed" );
                    } else {
                        tracelog("Line class : " + line.getClass().getName() + ", count: " + line.getAnnotationCount() );

                        String text = line.getText();
                        if (text != null) {
                            Annotatable anno = line;
                            try {
                                int firstNonWhiteSpaceCharIndex = findFirstNonWhiteSpaceCharIndex(text);
                                if (firstNonWhiteSpaceCharIndex == -1)
                                    continue;
                                int lastNonWhiteSpaceCharIndex = findLastNonWhiteSpaceCharIndex(text);
                                String initialWhiteSpace = text.substring(0, firstNonWhiteSpaceCharIndex);
                                String content = text.substring(firstNonWhiteSpaceCharIndex, lastNonWhiteSpaceCharIndex + 1);
                                int start = expandedLength(0, initialWhiteSpace, tabSize);
                                int length = expandedLength(start, content, tabSize);

                                anno = line.createPart(start, length);
                            }
                            catch (Exception ex) {
                                // SIOOBE sometimes, wrong counting with tabs?, attach to whole line
                            }

                            PMDScanAnnotation annotation = PMDScanAnnotation.getNewInstance();
                            String msg = fault.getMessage();
                            annotation.setErrorMessage( msg );
                            annotation.attach( anno );
                            anno.addPropertyChangeListener( annotation );
                        }
                    }
                }
            } catch( IOException e ) {
                ErrorManager.getDefault().notify(e);
            }
        }

        private int findFirstNonWhiteSpaceCharIndex(String text) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (c != ' ')
                    return i;
            }
            
            return -1;
        }
        
        private int findLastNonWhiteSpaceCharIndex(String text) {
            for (int i = text.length() - 1; i >= 0; i--) {
                char c = text.charAt(i);
                if (c != ' ' && c != '\t' && c != '\n' && c != '\r')
                    return i;
            }
            
            return -1;
        }
        
        private int expandedLength(int start, String text, int tabSize) {
            int position = start;
            int length = 0;
            for (int i = 0; i < text.length(); i++) {
                position++;
                length++;
                char c = text.charAt(i);
                if (c == '\t') {
                    while (position % tabSize != 0) {
                        position++;
                        length++;
                    }
                }
            }
            
            return length;
        }
        
	public void cancel() {
            if (task != null) {
                task.cancel();
                task = null;
            }
            detachFromDoc();
	}
	
	public String toString() {
		return "PMDScanner[" + doc + "]";
	}
	
	public void changedUpdate(DocumentEvent evt) {
		// Ignore these; they are just attribute changes. Actual typing appears as remove and insert updates.
	}
	
	public void insertUpdate(DocumentEvent evt) {
            if (task != null) {
                task.schedule(PMDOptionsSettings.getDefault().getScanInterval().intValue() * 1000);
            }
	}
	
	public void removeUpdate(DocumentEvent evt) {
            if (task != null) {
                task.schedule(PMDOptionsSettings.getDefault().getScanInterval().intValue() * 1000);
            }
	}
	
	private void tracelog(String str) {
		if(RunPMDAction.TRACE_LOGGING) {
			ErrorManager.getDefault().log(ErrorManager.ERROR, this.toString() + ": " + str);		
		}
	}
}
