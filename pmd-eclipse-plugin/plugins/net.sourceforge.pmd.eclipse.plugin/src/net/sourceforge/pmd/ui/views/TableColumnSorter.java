/*
 * Created on 22 mai 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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

import net.sourceforge.pmd.ui.PMDUiConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;

import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * Displays an Arrow-Image in a TableColumn, that shows in which Direction the Column is sorted
 * 
 * @author SebastianRaffel ( 22.05.2005 ), Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/09 13:26:40  phherlin
 * Review Sebastian code... and fix most PMD warnings
 *
 */
public class TableColumnSorter extends ViewerSorter {

    /**
     * Constructor
     * 
     * @param column, the column to sort
     * @param order, the Direction to sort by, -1 (desc) or 1 (asc)
     */
    public TableColumnSorter(TreeColumn column, int order) {
        super();

        // we delete all other Images
        // and set the current one
        final TreeColumn[] columns = column.getParent().getColumns();
        for (int i = 0; i < columns.length; i++) {
            columns[i].setImage(null);
        }

        column.setImage(getImage(order));
    }

    /**
     * Constructor
     * 
     * @param column, the column to sort
     * @param order, the Direction to sort by, -1 (desc) or 1 (asc)
     */
    public TableColumnSorter(TableColumn column, int order) {
        super();

        // we delete all other Images
        // and set the current one
        final TableColumn[] columns = column.getParent().getColumns();
        for (int i = 0; i < columns.length; i++) {
            columns[i].setImage(null);
        }

        column.setImage(getImage(order));
    }
    
    /**
     * Retreive an image for the corresponding sort order.
     * 
     * @param order
     * @return
     */
    private Image getImage(int order) {
        Image image = null;

        if (order == 1) {
            image = PMDUiPlugin.getDefault().getImage("arrow_up", PMDUiConstants.ICON_LABEL_ARRUP);
        } else if (order == -1) {
            image = PMDUiPlugin.getDefault().getImage("arrow_dn", PMDUiConstants.ICON_LABEL_ARRDN);
        }
        
        return image;
    }
}
