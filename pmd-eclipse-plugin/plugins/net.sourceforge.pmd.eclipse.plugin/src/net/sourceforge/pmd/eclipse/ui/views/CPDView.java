/*
 * Created on 13.10.2006
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

package net.sourceforge.pmd.eclipse.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * A class for showing the Copy / Paste Detection View.
 * 
 * @author Sven
 *
 */

public class CPDView extends ViewPart implements IPropertyListener {
    private TreeViewer treeViewer;
    private TreeNodeContentProvider contentProvider;
    private CPDViewLabelProvider labelProvider;
    private CPDViewDoubleClickEventListener doubleClickListener;
    private CPDViewTooltipListener tooltipListener;
    private static final int MAX_MATCHES = 100;
    
    /*
     * @see org.eclipse.ui.ViewPart#init(org.eclipse.ui.IViewSite)
     */
    public void init(IViewSite site) throws PartInitException {
        super.init(site);
        this.contentProvider = new TreeNodeContentProvider();
        this.labelProvider = new CPDViewLabelProvider();
        this.doubleClickListener = new CPDViewDoubleClickEventListener(this);
        this.tooltipListener = new CPDViewTooltipListener(this);
    }
    
    /*
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        final int treeStyle = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
        this.treeViewer = new TreeViewer(parent, treeStyle);
        this.treeViewer.setUseHashlookup(true);
        this.treeViewer.getTree().setHeaderVisible(true);
        this.treeViewer.getTree().setLinesVisible(true);
        
        this.treeViewer.setContentProvider(contentProvider);
        this.treeViewer.setLabelProvider(labelProvider);
        this.treeViewer.addDoubleClickListener(this.doubleClickListener);

        this.tooltipListener.initialize();
        this.treeViewer.getTree().addListener(SWT.Dispose, this.tooltipListener);
        this.treeViewer.getTree().addListener(SWT.KeyDown, this.tooltipListener);
        this.treeViewer.getTree().addListener(SWT.MouseMove, this.tooltipListener);
        this.treeViewer.getTree().addListener(SWT.MouseHover, this.tooltipListener);        
        createColumns(treeViewer.getTree());
    }

    /**
     * Creates the columns of the tree. 
     * @param tree Tree from the treeViewer
     */
    private void createColumns(Tree tree) {
        // the "+"-sign for expanding packages
        final TreeColumn plusColumn = new TreeColumn(tree, SWT.RIGHT);
        plusColumn.setWidth(20);
        plusColumn.setResizable(false);

        // shows the image
        final TreeColumn imageColumn = new TreeColumn(tree, SWT.CENTER);
        imageColumn.setWidth(20);
        imageColumn.setResizable(false);

        // shows the message
        final TreeColumn messageColumn = new TreeColumn(tree, SWT.LEFT);
        messageColumn.setText(getString(StringKeys.MSGKEY_VIEW_COLUMN_MESSAGE));
        messageColumn.setWidth(300);
        
        // shows the class
        final TreeColumn classColumn = new TreeColumn(tree, SWT.LEFT);
        classColumn.setText(getString(StringKeys.MSGKEY_VIEW_COLUMN_CLASS));
        classColumn.setWidth(300);      

    }

    /**
     * @return the tree viewer.
     */
    public TreeViewer getTreeViewer() {
        return this.treeViewer;
    }
    
    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

    /*
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        this.treeViewer.getTree().setFocus();
    }

    /**
     * Sets input for the table.
     * @param matches CPD Command that contain the matches from the CPD
     */
    public void setData(Iterator matches) {
        final List elements = new ArrayList();
        if (matches != null) {
            // iterate the matches
            for (int count = 0; matches.hasNext() && count < MAX_MATCHES; count++) {
                final Match match = (Match) matches.next();
                
                // create a treenode for the match and add to the list
                final TreeNode matchNode = new TreeNode(match); // NOPMD by Sven on 02.11.06 11:27
                elements.add(matchNode);
                
                // create the children of the match
                final TreeNode[] children = new TreeNode[match.getMarkCount()]; // NOPMD by Sven on 02.11.06 11:28
                final Iterator entryIterator = match.getMarkSet().iterator();
                for (int j=0; entryIterator.hasNext(); j++) {
                    final TokenEntry entry = (TokenEntry) entryIterator.next();
                    children[j] = new TreeNode(entry); // NOPMD by Sven on 02.11.06 11:28
                    children[j].setParent(matchNode);
                }
                matchNode.setChildren(children);
            }
        }
        
        // set the children of the rootnode: the matches    
        this.treeViewer.setInput(elements.toArray(new TreeNode[elements.size()]));
    }

    /**
     * After the CPD command is executed, it will trigger an propertyChanged event.
     */
    public void propertyChanged(Object source, int propId) {
        if (propId == PMDRuntimeConstants.PROPERTY_CPD
                && source instanceof Iterator) {
            final Iterator iter = (Iterator) source;
            // after setdata(iter) iter.hasNext will always return false
            final boolean hasResults = iter.hasNext();  
            setData(iter);
            if (!hasResults) {
                // no entries
                final MessageBox box = new MessageBox(this.treeViewer.getControl().getShell());
                box.setText(getString(StringKeys.MSGKEY_DIALOG_CPD_NORESULTS_HEADER));
                box.setMessage(getString(StringKeys.MSGKEY_DIALOG_CPD_NORESULTS_BODY));
                box.open();
            }
        }
    }
}
