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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.text.Document;
import org.netbeans.api.editor.settings.SimpleValueNames;
import org.netbeans.api.java.source.CancellableTask;
import org.netbeans.api.java.source.CompilationInfo;
import org.netbeans.modules.editor.NbEditorUtilities;
import org.openide.loaders.DataObject;
import org.openide.cookies.LineCookie;
import org.openide.filesystems.FileObject;
import org.openide.text.Annotatable;
import org.openide.text.Line;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import pmd.Fault;
import pmd.RunPMDAction;
import pmd.config.PMDOptionsSettings;

/**
 * PMD background scanner.
 */
public class Scanner implements CancellableTask<CompilationInfo> {
    
    private static final Logger LOG = Logger.getLogger("pmd");

    private boolean scanEnabled;
    
    private PropertyChangeListener optionLsnr;
    
    private FileObject fo;
    
    private volatile boolean cancelled = false;

    /** Creates a new instance of Scanner.
     *  This is created from EditorChangeListener only.
     */
    Scanner(FileObject fo) {
        this.fo = fo;
        optionLsnr = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                boolean current = scanEnabled;
                scanEnabled = PMDOptionsSettings.getDefault().isScanEnabled();
                if (current && !scanEnabled) {
                    PMDScanAnnotation.clearAll();
                }
            }
        };
        PMDOptionsSettings option = PMDOptionsSettings.getDefault();
        option.addPropertyChangeListener(WeakListeners.propertyChange(optionLsnr, option));
        scanEnabled = option.isScanEnabled();
    }
	
    public void run(CompilationInfo info) throws Exception {
        if (!scanEnabled) 
            return;
        try {
            LOG.fine(toString() + "started");
            cancelled = false;
            Document doc = info.getDocument();
            if (doc == null) {
                return;
            }
            
            int tabSize = 8;
            Integer foo = (Integer) doc.getProperty(SimpleValueNames.TAB_SIZE);
            if (foo != null)
                tabSize = foo.intValue();
            
            DataObject dobj = NbEditorUtilities.getDataObject(doc);
            if (dobj == null) {
                return;
            }
            if (!dobj.getPrimaryFile().canWrite()) {
                return;
            }
            
            LineCookie cookie = dobj.getCookie(LineCookie.class);
            Line.Set lineset = cookie.getLineSet();
            List<DataObject> list = Collections.singletonList(dobj);
            // TODO try to avoid duplicate work in this method
            // TODO want to make this cancellable too
            List<Fault> faults = RunPMDAction.performScan(list ); 
            PMDScanAnnotation.clearAll();
            for(Fault fault: faults) {
                if (cancelled) 
                    break;
                int lineNum = fault.getLine();
                Line line = lineset.getCurrent( lineNum - 1 );
                if(line == null) {
                    LOG.fine(toString() + "no original line found for line " + lineNum + " in lineset; probably document closed" );
                } else {
                    LOG.finest(toString() + "Line class : " + line.getClass().getName() + ", count: " + line.getAnnotationCount() );
                    
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
            Exceptions.printStackTrace(e);
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
        cancelled = true;
    }
	
    @Override public String toString() {
        return "PMDScanner[" + fo + "]";
    }
}
