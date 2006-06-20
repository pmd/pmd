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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.sourceforge.pmd.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.ui.PMDUiPlugin;
import net.sourceforge.pmd.ui.nls.StringKeys;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Implements the clear reviews action
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/06/20 21:01:23  phherlin
 * Enable PMD and fix error level violations
 *
 * Revision 1.1  2006/05/22 21:23:56  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.5  2006/05/07 12:03:08  phherlin
 * Add the possibility to use the PMD violation review style
 *
 * Revision 1.4  2006/01/27 00:03:11  phherlin
 * Fix BUG#1365407 Problems with PMD in Eclipse/Issue 3
 * Revision 1.3 2005/10/24 22:39:00 phherlin Integrating Sebastian Raffel's work Refactor command
 * processing Revision 1.2 2003/11/30 22:57:37 phherlin Merging from eclipse-v2 development branch
 * 
 * Revision 1.1.2.1 2003/11/04 16:27:19 phherlin Refactor to use the adaptable framework instead of downcasting
 * 
 * Revision 1.1 2003/08/14 16:10:41 phherlin Implementing Review feature (RFE#787086)
 * 
 */
public class ClearReviewsAction implements IObjectActionDelegate, IResourceVisitor, IViewActionDelegate {
    private static final Logger log = Logger.getLogger(ClearReviewsAction.class);
    private IWorkbenchPart targetPart;
    private IProgressMonitor monitor;

    /**
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
        this.targetPart = view.getSite().getPage().getActivePart();
    }

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        log.info("Remove violation reviews requested.");
        ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        try {
            monitorDialog.run(false, false, new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    setMonitor(monitor);
                    clearReviews();
                    monitor.done();
                }
            });
        } catch (InvocationTargetException e) {
            PMDUiPlugin.getDefault().logError("Invocation Target Exception when removing violations reviews", e.getTargetException());
        } catch (InterruptedException e) {
            PMDUiPlugin.getDefault().logError("Interrupted Exception when removing violations reviews", e);
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Get the monitor
     * 
     * @return
     */
    protected IProgressMonitor getMonitor() {
        return monitor;
    }

    /**
     * Set the monitor
     * 
     * @param monitor
     */
    protected void setMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Progress monitor
     */
    protected void monitorWorked() {
        if (getMonitor() != null) {
            getMonitor().worked(1);
        }
    }

    /**
     * Set a substask
     * 
     * @param message
     */
    protected void monitorSubTask(String message) {
        if (getMonitor() != null) {
            getMonitor().subTask(message);
        }
    }

    /**
     * Process the clear review action
     */
    protected void clearReviews() {

        try {
            // If action is started from a view, the process all selected resource
            if (this.targetPart instanceof IViewPart) {
                ISelection selection = targetPart.getSite().getSelectionProvider().getSelection();

                if ((selection != null) && (selection instanceof IStructuredSelection)) {
                    IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                    if (getMonitor() != null) {
                        getMonitor().beginTask(getString(StringKeys.MSGKEY_MONITOR_REMOVE_REVIEWS),
                                IProgressMonitor.UNKNOWN);

                        Iterator i = structuredSelection.iterator();
                        while (i.hasNext()) {
                            Object object = (Object) i.next();
                            IResource resource = null;

                            if (object instanceof IMarker) {
                                resource = ((IMarker) object).getResource();
                            } else if (object instanceof IAdaptable) {
                                IAdaptable adaptable = (IAdaptable) object;
                                resource = (IResource) adaptable.getAdapter(IResource.class);
                            } else {
                                log.warn("The selected object is not adaptable");
                                log.debug("   -> selected object = " + object);
                            }

                            if (resource != null) {
                                resource.accept(this);
                            } else {
                                log.warn("The selected object cannot adapt to a resource.");
                                log.debug("   -> selected object" + object);
                            }
                        }
                    }
                }
            }

            // If action is started from an editor, process the file currently edited
            if (this.targetPart instanceof IEditorPart) {
                IEditorInput editorInput = ((IEditorPart) this.targetPart).getEditorInput();
                if (editorInput instanceof IFileEditorInput) {
                    ((IFileEditorInput) editorInput).getFile().accept(this);
                } else {
                    log.debug("The kind of editor input is not supported. The editor input if of type: "
                            + editorInput.getClass().getName());
                }
            }

            // else this is not supported
            else {
                log.debug("This action is not supported on this kind of part. This part type is: "
                        + this.targetPart.getClass().getName());
            }
        } catch (CoreException e) {
            PMDUiPlugin.getDefault().logError("Core Exception when clearing violations reviews", e);
        }
    }

    /**
     * Clear reviews for a file
     * 
     * @param file
     */
    private void clearReviews(IFile file) {
        monitorSubTask(file.getName());

        String updatedFileContent = removeReviews(file);
        if (updatedFileContent != null) {
            saveNewContent(file, updatedFileContent);
        }

        monitorWorked();
    }

    /**
     * remove reviews from file content
     * 
     * @param file
     * @return
     */
    private String removeReviews(IFile file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean noChange = true;
        try {
            boolean comment = false;
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
            PrintWriter out = new PrintWriter(baos);

            while (reader.ready()) {
                String origLine = reader.readLine();
                String line = origLine.trim();
                int index = origLine.indexOf(PMDRuntimeConstants.PMD_STYLE_REVIEW_COMMENT);
                
                if (line.startsWith("/*")) {
                    if (line.indexOf("*/") == -1) {
                        comment = true;
                    }
                    out.println(origLine);
                } else if (comment && (line.indexOf("*/") != -1)) {
                    comment = false;
                    out.println(origLine);
                } else if (!comment && line.startsWith(PMDRuntimeConstants.PLUGIN_STYLE_REVIEW_COMMENT)) {
                    noChange = false;
                } else if (!comment && (index != -1)) {
                    noChange = false;
                    out.println(origLine.substring(0, index));
                } else {
                    out.println(origLine);
                }
            }

            out.flush();

        } catch (CoreException e) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION, e);
        } catch (IOException e) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_IO_EXCEPTION, e);
        }

        return noChange ? null : baos.toString();
    }

    /**
     * Save the file
     * 
     * @param file
     * @param newContent
     */
    private void saveNewContent(IFile file, String newContent) {
        try {
            file.setContents(new ByteArrayInputStream(newContent.getBytes()), false, true, getMonitor());
        } catch (CoreException e) {
            PMDUiPlugin.getDefault().logError(StringKeys.MSGKEY_ERROR_CORE_EXCEPTION, e);
        }
    }

    /**
     * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
     */
    public boolean visit(IResource resource) throws CoreException {
        if (resource instanceof IFile) {
            clearReviews((IFile) resource);
        }

        return (resource instanceof IProject) || (resource instanceof IFolder);
    }
    
    /**
     * Helper method to return an NLS string from its key
     */
    private String getString(String key) {
        return PMDUiPlugin.getDefault().getStringTable().getString(key);
    }

}
