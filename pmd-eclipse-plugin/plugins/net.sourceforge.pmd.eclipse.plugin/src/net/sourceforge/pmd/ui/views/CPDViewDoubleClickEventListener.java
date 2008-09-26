/*
 * Created on 01.11.2006
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

package net.sourceforge.pmd.ui.views;

import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 * 
 * @author Sven
 * @version $Revision$
 * 
 * $Log: CPDViewDoubleClickEventListener.java,v $
 * Revision 1.2  2007/06/24 16:36:24  phherlin
 * Fix 1737975 CPD view double-click selection&jump bug
 *
 * Revision 1.1  2006/11/16 17:11:08  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 *
 */

public class CPDViewDoubleClickEventListener implements IDoubleClickListener {
    private final CPDView view;
    
    public CPDViewDoubleClickEventListener(CPDView view) {
        this.view = view;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     */
    public void doubleClick(DoubleClickEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        final Object object = selection.getFirstElement();

        final TreeNode node = (TreeNode) object;
        final Object value = node.getValue();
        final TreeViewer treeViewer = view.getTreeViewer();
        
        if (value instanceof Match) {
            if (treeViewer.getExpandedState(node)) {
                // the node is expanded, so collapse
                treeViewer.collapseToLevel(node, TreeViewer.ALL_LEVELS);
            } else {
                // the node is collapsed, so expand
                treeViewer.expandToLevel(node, 1);
            }
        } else if (value instanceof TokenEntry) {
            // open file and jump to the startline
            final TokenEntry entry = (TokenEntry) value;
            final Match match = (Match) node.getParent().getValue();
            final IPath path = Path.fromOSString(entry.getTokenSrcID());
            final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);

            if (file != null) {
                try {
                    // open editor
                    final IWorkbenchPage page = this.view.getSite().getPage();
                    final IEditorPart part = IDE.openEditor(page, file);
                    if (part instanceof ITextEditor) {
                        // select text
                        final ITextEditor textEditor = (ITextEditor) part;
                        final IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
                        final int offset = document.getLineOffset(entry.getBeginLine()-1);
                        final int length = document.getLineOffset(entry.getBeginLine()-1 + match.getLineCount()) - offset -1;
                        textEditor.selectAndReveal(offset, length); 
                    }                    
                } catch (PartInitException pie) {
                    PMDPlugin.getDefault().logError(
                            getString(StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION), pie);
                } catch (BadLocationException ble) {
                    PMDPlugin.getDefault().logError(
                            getString(StringKeys.MSGKEY_ERROR_VIEW_EXCEPTION), ble);
                }
            }
        }
    }
    
    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

}
