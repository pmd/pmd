/*
 * Created on 10.11.2006
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

import net.sourceforge.pmd.eclipse.ui.PMDUiConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.ui.model.FileRecord;
import net.sourceforge.pmd.eclipse.ui.model.FileToMarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.MarkerRecord;
import net.sourceforge.pmd.eclipse.ui.model.PackageRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

/**
 * DoubleClickListener for the violation-overview. 
 * 
 * @author Sven
 *
 */

public class ViolationOverviewDoubleClickListener implements IDoubleClickListener {
    private final ViolationOverview overview;
    
    public ViolationOverviewDoubleClickListener(ViolationOverview overview) {
        this.overview = overview;
    }

    /**
     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
     */
    public void doubleClick(DoubleClickEvent event) {
        final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        final Object object = selection.getFirstElement();

        if (object instanceof PackageRecord) {
            doubleClickToPackageRecord((PackageRecord) object);
        } else if (object instanceof FileRecord) {
            doubleClickToFileRecord((FileRecord) object);
        } else if (object instanceof MarkerRecord) {
            doubleClickToMarkerRecord((MarkerRecord) object);
        } else if (object instanceof FileToMarkerRecord) {
            doubleClickToFileToMarkerRecord((FileToMarkerRecord) object);
        }
    }

    /**
     * Opens the editor and select the markers of the record.
     * @param record
     */
    private void doubleClickToFileToMarkerRecord(FileToMarkerRecord record) {
        openEditor((IFile) record.getResource());
        final IMarker[] markers = record.findMarkers();
        
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                selectMarkerInOutline(markers);
            }
        });
    }

    /**
     * Opens the editor to the marker and select all according markers in the violation outline. 
     * @param markerRec
     */
    private void doubleClickToMarkerRecord(MarkerRecord markerRec) {
        switch (overview.getShowType()) {
        case ViolationOverview.SHOW_FILES_MARKERS:
        case ViolationOverview.SHOW_PACKAGES_FILES_MARKERS:
            openEditor((IFile) markerRec.getResource());
            final IMarker[] markers = markerRec.findMarkers();
            
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    selectMarkerInOutline(markers);
                }
            });
            break;
        default:
            // do nothing
        }
        
        final TreeViewer viewer = this.overview.getViewer();
        if (viewer.getExpandedState(markerRec)) {
            viewer.collapseToLevel(markerRec, 1);
        } else {
            if (overview.getShowType() == ViolationOverview.SHOW_MARKERS_FILES) {
                viewer.expandToLevel(markerRec, 1);
            }
        }
    }

    /**
     * Expand or collapse the tree and open the editor.
     * @param fileRec
     */
    private void doubleClickToFileRecord(FileRecord fileRec) {
        final TreeViewer viewer = this.overview.getViewer();
        if (viewer.getExpandedState(fileRec)) {
            viewer.collapseToLevel(fileRec, 1);
        } else {
            viewer.expandToLevel(fileRec, 1);
        }
        
        openEditor((IFile) fileRec.getResource());
    }

    /**
     * Just expand or collapse the tree.
     * @param packageRec
     */
    private void doubleClickToPackageRecord(PackageRecord packageRec) {
        final TreeViewer viewer = overview.getViewer();
        if (viewer.getExpandedState(packageRec)) {
            viewer.collapseToLevel(packageRec, TreeViewer.ALL_LEVELS);
        } else {
            viewer.expandToLevel(packageRec, 1);
        }
    }

    /**
     * Helper method to open the editor on a file
     * @param file
     */
    private void openEditor(IFile file) {
        try {
            // open the corresponding File            
            IDE.openEditor(this.overview.getSite().getPage(), file);
        } catch (PartInitException pie) {
            PMDPlugin.getDefault().logError(StringKeys.ERROR_VIEW_EXCEPTION + this.toString(), pie);
        }
    }
    
    /**
     * Select the markers in the violation outline.
     * @param markers
     */
    private void selectMarkerInOutline(IMarker[] markers) {
        try {
            final IWorkbenchPage workbenchPage = this.overview.getSite().getPage();
            ViolationOutline view = (ViolationOutline) workbenchPage.findView(PMDUiConstants.ID_OUTLINE);
            if (view == null) {
                view = (ViolationOutline) workbenchPage.showView(PMDUiConstants.ID_OUTLINE);
            }
            
            if (view.getCurrentPage() instanceof ViolationOutlinePageBR) {
                final ViolationOutlinePageBR vioPage = (ViolationOutlinePageBR)view.getCurrentPage();
                
                final TableItem[] items = vioPage.getTableViewer().getTable().getItems();
                vioPage.getTableViewer().getTable().deselectAll();
                for (int j=0; j< markers.length; j++) {                               
                    for (int i=0; i< items.length; i++) {
                        if (items[i].getData() instanceof IMarker 
                                && markers[j].equals(items[i].getData())) {
                            vioPage.getTableViewer().getTable().select(i);
                        }
                    }
                }
            }
        } catch (PartInitException pie) {
            PMDPlugin.getDefault().logError(StringKeys.ERROR_VIEW_EXCEPTION + toString(), pie);
        }            
    }
}
