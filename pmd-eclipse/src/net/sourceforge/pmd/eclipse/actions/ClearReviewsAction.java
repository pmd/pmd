/*  
 * <copyright>  
 *  Copyright 1997-2003 PMD for Eclipse Development team
 *  under sponsorship of the Defense Advanced Research Projects  
 *  Agency (DARPA).  
 *   
 *  This program is free software; you can redistribute it and/or modify  
 *  it under the terms of the Cougaar Open Source License as published by  
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).   
 *   
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS   
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR   
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF   
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT   
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT   
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL   
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,   
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR   
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.   
 *   
 * </copyright>
 */
package net.sourceforge.pmd.eclipse.actions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Implements the clear reviews action
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2003/11/30 22:57:37  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/11/04 16:27:19  phherlin
 * Refactor to use the adaptable framework instead of downcasting
 *
 * Revision 1.1  2003/08/14 16:10:41  phherlin
 * Implementing Review feature (RFE#787086)
 *
 */
public class ClearReviewsAction implements IObjectActionDelegate, IResourceVisitor {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.actions.ClearReviewsAction");
    private IWorkbenchPart activePart;
    private IProgressMonitor monitor;

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        activePart = targetPart;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
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
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION, e.getTargetException());
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_INTERRUPTED_EXCEPTION, e);
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Get the monitor
     * @return
     */
    protected IProgressMonitor getMonitor() {
        return monitor;
    }

    /**
     * Set the monitor
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
        ISelection selection = activePart.getSite().getSelectionProvider().getSelection();
        if ((selection != null) && (selection instanceof IStructuredSelection)) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
            if (getMonitor() != null) {
                getMonitor().beginTask(
                    PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_MONITOR_REMOVE_REVIEWS),
                    IProgressMonitor.UNKNOWN);

                Iterator i = structuredSelection.iterator();
                try {
                    while (i.hasNext()) {
                        Object object = (Object) i.next();
                        
                        if (object instanceof IAdaptable) {
                            IAdaptable adaptable = (IAdaptable) object;
                            IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                            if (resource != null) {
                                resource.accept(this);
                            } else {
                                log.warn("The selected object cannot adapt to a resource.");
                                log.debug("   -> selected object" + object);
                            }
                        } else {
                            log.warn("The selected object is not adaptable");
                            log.debug("   -> selected object = " + object);
                        }
                    }
                } catch (CoreException e) {
                    PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION, e);
                }
            }
        }
    }

    /**
     * Clear reviews for a file
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
                if (line.startsWith("/*")) {
                    if (line.indexOf("*/") == -1) {
                        comment = true;
                    }
                    out.println(origLine);
                } else if (comment && (line.indexOf("*/") != -1)) {
                    comment = false;
                    out.println(origLine);
                } else if (!comment && line.startsWith(PMDPlugin.REVIEW_MARKER)) {
                    noChange = false;
                } else {
                    out.println(origLine);
                }
            }

            out.flush();

        } catch (CoreException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION, e);
        } catch (IOException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_IO_EXCEPTION, e);
        }

        return noChange ? null : baos.toString();
    }

    /**
     * Save the file
     * @param file
     * @param newContent
     */
    private void saveNewContent(IFile file, String newContent) {
        try {
            file.setContents(new ByteArrayInputStream(newContent.getBytes()), false, true, getMonitor());
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION, e);
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

}
