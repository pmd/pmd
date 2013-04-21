/*
 * Created on 14.10.2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.ui.views.cpd2;

import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 *
 */
public class CPDViewTooltipListener2 implements Listener {
	
    private final CPDView2 view;
    private Cursor normalCursor;
    private Cursor handCursor;
    
    public CPDViewTooltipListener2(CPDView2 view) {
        this.view = view;
        initialize();
    }

    private void initialize() {
    	Display disp = Display.getCurrent();
    	normalCursor = disp.getSystemCursor(SWT.CURSOR_ARROW);
    	handCursor = disp.getSystemCursor(SWT.CURSOR_HAND);
    }

    // open file and jump to the startline
	private void highlight(Match match, TokenEntry entry) {
		
		IPath path = Path.fromOSString(entry.getTokenSrcID());
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		if (file == null) return;
		
	    try {
	        // open editor
	        IWorkbenchPage page = view.getSite().getPage();
	        IEditorPart part = IDE.openEditor(page, file);
	        if (part instanceof ITextEditor) {
	            // select text
	            ITextEditor textEditor = (ITextEditor) part;
	            IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	            int offset = document.getLineOffset(entry.getBeginLine()-1);
	            int length = document.getLineOffset(entry.getBeginLine()-1 + match.getLineCount()) - offset -1;
	            textEditor.selectAndReveal(offset, length); 
	        }                    
	    } catch (PartInitException pie) {
	        PMDPlugin.getDefault().logError(getString(StringKeys.ERROR_VIEW_EXCEPTION), pie);
	    } catch (BadLocationException ble) {
	        PMDPlugin.getDefault().logError(getString(StringKeys.ERROR_VIEW_EXCEPTION), ble);
	    }
	}
    
    private static Match matchAt(TreeItem treeItem) {

		Object item = ((TreeNode) treeItem.getData()).getValue();
		return item instanceof Match ? (Match)item : null;
    }
    
	private TokenEntry itemAt(TreeItem treeItem, Point location, GC gc) {

		if (treeItem == null) return null;
		
		Object item = ((TreeNode) treeItem.getData()).getValue();

		String[] names = null;
		if (item instanceof Match) {
			names = CPDViewLabelProvider2.sourcesFor((Match) item);
		} else {
			return null;
		}

		location.x -= view.widthOf(0);	// subtract width of preceeding columns
		
		int colWidth = view.widthOf(CPDView2.SourceColumnIdx);
		int cellWidth = colWidth / names.length;

  		for (int i=0; i<names.length; i++) {
  			int rightEdge = colWidth - (cellWidth * i);  			
  			int[] widths = view.widthsFor(names[i]);
  			if (widths == null) continue;
  			int classWidth = widths[1];
  			if (location.x > rightEdge-classWidth && 	// right of the start?
  				location.x < rightEdge)	{				// left of the end?
  				return CPDViewLabelProvider2.entriesFor((Match)item)[i];				
  				}
  			}
  			
		return null;
	}
	
    /* 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event) {
        
    	Tree tree = view.getTreeViewer().getTree();
        Point location = new Point(event.x, event.y);
        Shell shell = tree.getShell();
   	 	
        if (view.inColumn(location) != CPDView2.SourceColumnIdx) {
   			shell.setCursor(normalCursor);
   			return;
   	 	}
   	 	
        TreeItem item = tree.getItem(location);
        TokenEntry entry = itemAt(item, location, event.gc);
        if (entry == null) {
        	shell.setCursor(normalCursor);
        	return;
        }
        
        switch (event.type) {
            case SWT.MouseDown:
                 highlight(matchAt(item), entry);               	
                 break;
            case SWT.MouseMove:          
            case SWT.MouseHover:
                 shell.setCursor(handCursor);
                 break;                
            default:
                break;
        }        

    }
	
    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }
}
