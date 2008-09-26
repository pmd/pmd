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

package net.sourceforge.pmd.eclipse.ui.views;

import java.util.StringTokenizer;

import net.sourceforge.pmd.cpd.Match;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * This class is created for showing the source code of a tablerow via a tooltip. 
 * There need to be some listener for the mouse to get the position. 
 * 
 * @author Sven
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2007/01/18 21:03:56  phherlin
 * Fix several problems on memento usage
 *
 * Revision 1.1  2006/11/16 17:11:08  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 *
 */
public class CPDViewTooltipListener implements Listener {
    private final CPDView view;
    private Listener labelListener;
    private Shell tip;
    private TreeItem shownItem;
    /**
     * Constructor.
     * @param view Tree of the CPD View.
     */
    public CPDViewTooltipListener(final CPDView view) {
        this.view = view;
    }
    
    /**
     * Initialization needs to be called after the treeViewer was created.
     */
    public void initialize() {
        this.labelListener = new Listener () {
            public void handleEvent (Event event) {
                final Label label = (Label)event.widget;
                final Shell shell = label.getShell ();
                final Tree tree = view.getTreeViewer().getTree();
                
                switch (event.type) {
                    case SWT.MouseDown:
                        final Event e = new Event ();
                        e.item = (TreeItem)label.getData("_TREEITEM");
                        // Assuming table is single select, set the selection as if
                        // the mouse down event went through to the table
                        tree.setSelection(new TreeItem [] {(TreeItem) e.item});
                        tree.notifyListeners(SWT.Selection, e);
                        shell.dispose();
                        view.setFocus();
                        break;
                    case SWT.MouseExit:
                        shell.dispose ();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    /* 
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    public void handleEvent(Event event) {
        final Tree tree = view.getTreeViewer().getTree();
        final TreeItem item = tree.getItem(new Point(event.x, event.y));

        switch (event.type) {
            case SWT.Dispose:
            case SWT.KeyDown:
                disposeTooltip();
                break;
            case SWT.MouseMove:
                if (this.shownItem != null && !shownItem.equals(item)) {
                    disposeTooltip();
                }
                break;
            
            case SWT.MouseHover:
                if (item != null) {
                    final TreeNode node = (TreeNode) item.getData();
                    if (node.getValue() instanceof Match) {
                        final Match match = (Match) node.getValue();
                        final String text = createText(match);
                        showTooltip(item, text);
                    }
                }
                break;
                
            default:
                break;
        }
    }

    private String createText(Match match) {
        final String text = match.getSourceCodeSlice().replaceAll("\t", "    ");
        final StringBuffer outputString = new StringBuffer();
        final StringTokenizer lines = new StringTokenizer(text, "\n");
        for (int i=0; lines.hasMoreTokens(); i++) {
            if (i < 6) {
                outputString.append(lines.nextToken()).append('\n');
            } else {
                outputString.append("\n...\n");
                break;
            }
        }

        return outputString.toString();
    }

    private void disposeTooltip() {
        if (tip != null && !tip.isDisposed()) {
            tip.dispose ();
        }
    }

    /**
     * Shows the tooltip window.
     * @param item The TreeItem to show.
     * @param text The text to show.
     */
    private void showTooltip(final TreeItem item, final String text) {
        final Tree tree = view.getTreeViewer().getTree();
        final Display display = tree.getDisplay();
        if (tip != null && !tip.isDisposed()) {
            tip.dispose();
        }
        tip = new Shell(SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
        tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        final FillLayout layout = new FillLayout();
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        tip.setLayout(layout);
        final Label label = new Label(tip, SWT.NONE);
        label.setForeground(display.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
        label.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
        label.setData("_TREEITEM", item);

        label.setText(text);
        label.addListener(SWT.MouseExit, this.labelListener);
        label.addListener(SWT.MouseDown, this.labelListener);
        final Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        final Rectangle rect = item.getBounds(2);
        final Point pt = tree.toDisplay(rect.x, rect.y);
        final Rectangle treeSize = tree.getClientArea();
        final Point treePt = tree.toDisplay(treeSize.width,treeSize.height);
        if (pt.x + size.x + 50 > treePt.x) {
            size.x = treePt.x - pt.x - 50;
        }
        
        if (pt.y + size.y + 3 > treePt.y) {
            size.y = treePt.y - pt.y - 3;
        }
        
        tip.setBounds(pt.x+50, pt.y+3, size.x, size.y);
        tip.setVisible(true);
        this.shownItem = item;
    }
}
