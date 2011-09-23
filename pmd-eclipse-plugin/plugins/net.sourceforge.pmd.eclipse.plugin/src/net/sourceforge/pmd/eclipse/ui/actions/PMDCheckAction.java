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
package net.sourceforge.pmd.eclipse.ui.actions;

import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.cmd.AbstractDefaultCommand;
import net.sourceforge.pmd.eclipse.runtime.cmd.ReviewCodeCmd;
import net.sourceforge.pmd.eclipse.ui.model.AbstractPMDRecord;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;
import org.eclipse.ui.IWorkingSet;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * Implements action on the "Check code with PMD" action menu on a file
 *
 * @author Philippe Herlin
 *
 */
public class PMDCheckAction extends AbstractUIAction {
	
    private static final Logger log = Logger.getLogger(PMDCheckAction.class);

    /**
     * @see org.eclipse.ui.IActionDelegate#run(IAction)
     */
    public void run(IAction action) {
        log.info("Check PMD action requested");

        try {

            // Execute PMD on a range of selected resource if action selected from a view part
            if (isViewPart()) {
                ISelection selection = targetSelection();
                if (selection instanceof IStructuredSelection) {
                    reviewSelectedResources((IStructuredSelection) selection);
                } else {
                    log.debug("The selection is not an instance of IStructuredSelection. This is not supported: " + selection.getClass().getName());
                }
            }

            // If action is selected from an editor, run PMD on the file currently edited
            else if (isEditorPart()) {
                IEditorInput editorInput = ((IEditorPart) targetPart()).getEditorInput();
                if (editorInput instanceof IFileEditorInput) {
                    reviewSingleResource(((IFileEditorInput) editorInput).getFile());
                } else {
                    log.debug("The kind of editor input is not supported. The editor input if of type: " + editorInput.getClass().getName());
                }
            }

            // Else, this is not supported for now
            else {
                log.debug("Running PMD from this kind of part is not supported. Part is of type " + targetPartClassName());
            }

        } catch (CommandException e) {
            showErrorById(StringKeys.ERROR_CORE_EXCEPTION, e);
        }

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Run the reviewCode command on a single resource
     *
     * @param resource
     * @throws CommandException
     */
    private void reviewSingleResource(IResource resource) throws CommandException {
        ReviewCodeCmd cmd = new ReviewCodeCmd();
        cmd.addResource(resource);

        setupAndExecute(cmd, 1);
    }

    private void setupAndExecute(ReviewCodeCmd cmd, int count) throws CommandException {
    	cmd.setStepCount(count);
    	cmd.setTaskMarker(true);
        cmd.setOpenPmdPerspective(PMDPlugin.getDefault().loadPreferences().isPmdPerspectiveEnabled());
        cmd.setUserInitiated(true);
        cmd.performExecute();
    }
    
    /**
     * Prepare and run the reviewCode command for all selected resources
     *
     * @param selection the selected resources
     */
	private void reviewSelectedResources(IStructuredSelection selection)
			throws CommandException {
		ReviewCodeCmd cmd = new ReviewCodeCmd();

		// Add selected resources to the list of resources to be reviewed
		for (Iterator<?> i = selection.iterator(); i.hasNext();) {
			Object element = i.next();
			if (element instanceof AbstractPMDRecord) {
				final IResource resource = ((AbstractPMDRecord) element)
						.getResource();
				if (resource != null) {
					cmd.addResource(resource);
				} else {
					log.warn("The selected object has no resource");
					log.debug("  -> selected object : " + element);
				}
			} else if (element instanceof IWorkingSet) {
				IWorkingSet set = (IWorkingSet) element;
				for (IAdaptable adaptable : set.getElements()) {
					addAdaptable(cmd, adaptable);
				}
			} else if (element instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) element;
				addAdaptable(cmd, adaptable);
			} else {
				log.warn("The selected object is not adaptable");
				log.debug("   -> selected object : " + element);
			}
		}

		// Run the command
		setupAndExecute(cmd, countElement(selection));
	}

	private void addAdaptable(ReviewCodeCmd cmd, IAdaptable adaptable) {
		IResource resource = (IResource) adaptable.getAdapter(IResource.class);
		if (resource != null) {
			cmd.addResource(resource);
		} else {
			log.warn("The selected object cannot adapt to a resource");
			log.debug("   -> selected object : " + adaptable);
		}
	}
	
    /**
     * Count the number of resources of a selection
     *
     * @param selection a selection
     * @return the element count
     */
    private int countElement(IStructuredSelection selection) {
        CountVisitor visitor = new CountVisitor();

        for (Iterator<?> i = selection.iterator(); i.hasNext();) {
            Object element = i.next();

            try {
                if (element instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable) element;
                    IResource resource = (IResource) adaptable.getAdapter(IResource.class);
                    if (resource != null) {
                        resource.accept(visitor);
                    } else {
                        log.warn("The selected object cannot adapt to a resource");
                        log.debug("   -> selected object : " + element);
                    }
                } else {
                    log.warn("The selected object is not adaptable");
                    log.debug("   -> selected object : " + element);
                }
            } catch (CoreException e) {
                // Ignore any exception
                logError("Exception when counting the number of impacted elements when running PMD from menu", e);
            }
        }

        return visitor.count;
    }

    // Inner visitor to count number of children of a resource
    private class CountVisitor implements IResourceVisitor {
        public int count = 0;

        public boolean visit(IResource resource) {
            boolean fVisitChildren = true;
            count++;

            if (resource instanceof IFile && AbstractDefaultCommand.isJavaFile((IFile) resource)) {

                fVisitChildren = false;
            }

            return fVisitChildren;
        }
    }
}
