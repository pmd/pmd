/*
 * Copyright (c) 2005,2006 PMD for Eclipse Development Team
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
package net.sourceforge.pmd.ui.actions;

import java.util.Iterator;

import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.ui.nls.StringKeys;
import net.sourceforge.pmd.ui.views.ViolationOverview;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Process "Delete PMD Markers" action menu
 * 
 * @author phherlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.5  2006/11/16 17:09:40  holobender
 * Some major changes:
 * - new CPD View
 * - changed and refactored ViolationOverview
 * - some minor changes to dataflowview to work with PMD
 *
 * Revision 1.4  2006/10/10 22:31:01  phherlin
 * Fix other PMD warnings
 *
 * Revision 1.3  2006/10/06 18:42:30  phherlin
 * Fix 1554639 Clear markers in Project JAVA-files and other bugs related to dataflows
 *
 * Revision 1.2  2006/06/08 22:01:30  phherlin
 * Fix marker deletion issue following the architecture refactoring
 *
 * Revision 1.1  2006/05/22 21:23:56  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.6  2006/01/27 00:03:11  phherlin
 * Fix BUG#1365407 Problems with PMD in Eclipse/Issue 3
 * Revision 1.5 2003/11/30 22:57:37 phherlin Merging from eclipse-v2 development branch
 * 
 * Revision 1.4.2.1 2003/11/04 16:27:19 phherlin Refactor to use the adaptable framework instead of downcasting
 * 
 * Revision 1.4 2003/05/19 22:27:33 phherlin Refactoring to improve performance
 * 
 * Revision 1.3 2003/03/30 20:49:37 phherlin Adding logging Displaying error dialog in a thread safe way Adding support for folders
 * and package
 * 
 */
public class PMDRemoveMarkersAction implements IViewActionDelegate, IObjectActionDelegate {
    private static final String VIEW_ACTION = "net.sourceforge.pmd.ui.pmdRemoveAllMarkersAction";
    private static final String OBJECT_ACTION = "net.sourceforge.pmd.ui.pmdRemoveMarkersAction";
    private static final Logger log = Logger.getLogger(PMDRemoveMarkersAction.class);
    private IWorkbenchPart targetPart;

    /**
     * @see org.eclipse.ui.IViewActionDelegate#init(IViewPart)
     */
    public void init(IViewPart view) {
        // no initialization for now
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        log.info("Remove Markers action requested");
        try {
            if (action.getId().equals(VIEW_ACTION)) {
                ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                ResourcesPlugin.getWorkspace().getRoot().deleteMarkers(PMDRuntimeConstants.PMD_DFA_MARKER, true, IResource.DEPTH_INFINITE);
                log.debug("Remove markers on the entire workspace");
            } else if (action.getId().equals(OBJECT_ACTION)) {
                processResource();
            } else { // else action id not supported
                log.warn("Cannot remove markers, action ID is not supported");
            }
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getString(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        // nothing to do
    }

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * Process removing of makers on a resource selection (project or file)
     */
    private void processResource() {
        log.debug("Processing a resource");
        try {
            if (this.targetPart instanceof IViewPart) {
                // if action is run from a view, process the selected resources                
                final ISelection sel = targetPart.getSite().getSelectionProvider().getSelection();

                if (sel instanceof IStructuredSelection) {
                    final IStructuredSelection structuredSel = (IStructuredSelection) sel;
                    for (final Iterator i = structuredSel.iterator(); i.hasNext();) {
                        final Object element = i.next();
                        processElement(element);
                    }
                } else {
                    log.warn("The view part selection is not a structured selection !");
                }
            }

            // if action is run from an editor, process the file currently edited
            else if (this.targetPart instanceof IEditorPart) {
                final IEditorInput editorInput = ((IEditorPart) this.targetPart).getEditorInput();
                if (editorInput instanceof IFileEditorInput) {
                    ((IFileEditorInput) editorInput).getFile().deleteMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    log.debug("Remove markers " + PMDRuntimeConstants.PMD_MARKER + " on currently edited file " + ((IFileEditorInput) editorInput).getFile().getName());
                } else {
                    log.debug("The kind of editor input is not supported. The editor input if of type: "
                            + editorInput.getClass().getName());
                }
            }

            // else, this is not supported
            else {
                log.debug("This action is not supported on that part. This part type is: " + this.targetPart.getClass().getName());
            }
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getString(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }
    }
    
    private void processElement(Object element) throws CoreException {
        if (element instanceof AbstractPMDRecord) {
            final AbstractPMDRecord record = (AbstractPMDRecord) element;
            final IResource resource = record.getResource();
            if (this.targetPart instanceof ViolationOverview) {
                ((ViolationOverview)targetPart).deleteMarkers(record);
            }
            
            log.debug("Remove markers on resource " + resource.getName());
        } else if (element instanceof IAdaptable) {
            final IAdaptable adaptable = (IAdaptable) element;
            final IResource resource = (IResource) adaptable.getAdapter(IResource.class);
            if (resource == null) {
                log.warn("The selected object cannot adapt to a resource");
                log.debug("   -> selected object : " + element);
            } else {
                resource.deleteMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                log.debug("Remove markers on resrouce " + resource.getName());
            }
        } else {
            log.warn("The selected object is not adaptable");
            log.debug("   -> selected object : " + element);
        }
    }

    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    }

}
