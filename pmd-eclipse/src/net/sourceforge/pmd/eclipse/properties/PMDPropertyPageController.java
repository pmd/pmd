/*
 * Created on 18 nov. 2004
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.eclipse.properties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDEclipseException;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.RuleSetWriter;
import net.sourceforge.pmd.eclipse.WriterAbstractFactory;
import net.sourceforge.pmd.eclipse.cmd.BuildProjectCommand;
import net.sourceforge.pmd.eclipse.cmd.CommandException;
import net.sourceforge.pmd.eclipse.cmd.QueryProjectPropertiesCmd;
import net.sourceforge.pmd.eclipse.cmd.UpdateProjectPropertiesCmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

/**
 * This class implements the controler of the Property page
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2004/11/21 21:38:43  phherlin
 * Continue applying MVC.
 * Revision 1.1 2004/11/18 23:54:27
 * phherlin Refactoring to apply MVC. The goal is to test the refactoring before
 * a complete refactoring for all GUI
 * 
 *  
 */
public class PMDPropertyPageController implements PMDConstants {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.properties.PMDPropertyPageController");
    private IProject project;
    private PMDPropertyPage propertyPage;
    private PMDPropertyPageModel propertyPageModel;

    /**
     * Contructor
     * 
     * @param propertyPage
     *            the page for which this class is a controller
     */
    public PMDPropertyPageController(PMDPropertyPage propertyPage) {
        this.propertyPage = propertyPage;
    }

    /**
     * @return Returns the project.
     */
    public IAdaptable getProject() {
        return this.project;
    }

    /**
     * @param element
     *            The project to set.
     */
    public void setProject(IProject project) {
        if (!project.isAccessible()) {
            log.warn("Couldn't accept project because it is not accessible.");
        } else {
            this.project = project;
        }
    }

    /**
     * @return Returns the propertyPageModel.
     */
    public PMDPropertyPageModel getPropertyPageModel() {
        // assert ((this.project != null) && (this.project.isAccessible()))

        if (propertyPageModel == null) {
            try {
                QueryProjectPropertiesCmd cmd = new QueryProjectPropertiesCmd();
                cmd.setProject(this.project);
                cmd.performExecute();

                propertyPageModel = new PMDPropertyPageModel();
                propertyPageModel.setPmdEnabled(cmd.isPMDEnabled());
                propertyPageModel.setProjectWorkingSet(cmd.getProjectWorkingSet());
                propertyPageModel.setProjectRuleSet(cmd.getProjectRuleSet());
                propertyPageModel.setRuleSetStoredInProject(cmd.isRuleSetStoredInProject());
            } catch (CommandException e) {
                PMDPlugin.getDefault().showError(e.getMessage(), e);
            }
        }

        return propertyPageModel;
    }

    /**
     * @return the configured ruleset for the entire workbench
     */
    public RuleSet getAvailableRules() {
        return PMDPlugin.getDefault().getRuleSet();
    }

    /**
     * Process the validation of the properties (OK button pressed)
     * 
     * @return always true
     */
    public boolean performOk() {
        // assert ((this.project != null) && (this.project.isAccessible()))
        
        try {
            // first check whether the project ruleset file exists if user has
            // choosen this option
            if ((this.propertyPageModel.isPmdEnabled()) && (this.propertyPageModel.isRuleSetStoredInProject())) {
                this.checkProjectRuleSetFile();
            }
            
            // Then updates the project properties
            UpdateProjectPropertiesCmd cmd = new UpdateProjectPropertiesCmd();
            cmd.setProject(this.project);
            cmd.setPmdEnabled(this.propertyPageModel.isPmdEnabled());
            cmd.setProjectWorkingSet(this.propertyPageModel.getProjectWorkingSet());
            cmd.setProjectRuleSet(this.propertyPageModel.getProjectRuleSet());
            cmd.setRuleSetStoredInProject(this.propertyPageModel.isRuleSetStoredInProject());
            cmd.performExecute();

            // If rebuild is needed, then rebuild the project
            log.debug("Updating command terminated, checking whether the project need to be rebuilt");
            if (cmd.isNeedRebuild()) {
                rebuildProject();
            }
        } catch (CommandException e) {
            PMDPlugin.getDefault().showError(e.getMessage(), e);
        }

        return true;
    }
    
    /**
     * Process a select workingset event
     * 
     * @param currentWorkingSet the working set currently selected of null if none
     * @return the newly selected working set or null if none.
     *
     */
    public IWorkingSet selectWorkingSet(IWorkingSet currentWorkingSet) {
        IWorkingSet selectedWorkingSet = null;
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkingSetManager workingSetManager = workbench.getWorkingSetManager();
        IWorkingSetSelectionDialog selectionDialog = workingSetManager.createWorkingSetSelectionDialog(this.propertyPage.getShell(), false);
        if (currentWorkingSet != null) {
            selectionDialog.setSelection(new IWorkingSet[] { currentWorkingSet });
        }

        if (selectionDialog.open() == Window.OK) {
            if (selectionDialog.getSelection().length != 0) {
                selectedWorkingSet = selectionDialog.getSelection()[0];
                log.info("Working set " + selectedWorkingSet.getName() + " selected");
            } else {
                selectedWorkingSet = null;
                log.info("Deselect working set");
            }
        }
        
        return selectedWorkingSet;
    }

    /**
     * Perform a full rebuild of the project
     *  
     */
    private void rebuildProject() {
        boolean rebuild = MessageDialog.openQuestion(this.propertyPage.getShell(),
                getMessage(MSGKEY_QUESTION_TITLE), this.getMessage(MSGKEY_QUESTION_REBUILD_PROJECT));
        if (rebuild) {
            log.info("Full rebuild of the project " + this.project.getName());
            try {
                BuildProjectCommand cmd = new BuildProjectCommand();
                cmd.setProject(this.project);
                cmd.performExecute();
            } catch (CommandException e) {
                PMDPlugin.getDefault().showError(e.getMessage(), e);
            }
        }
    }

    /**
     * Check if the project has a ruleset file. If not propose to create a
     * default one
     */
    private void checkProjectRuleSetFile() {
        IFile ruleSetFile = this.project.getFile(".ruleset");
        if (!ruleSetFile.exists()) {
            if (MessageDialog.openQuestion(this.propertyPage.getShell(), this.getMessage(MSGKEY_QUESTION_TITLE),
                    this.getMessage(MSGKEY_QUESTION_CREATE_RULESET_FILE))) {
                RuleSet ruleSet = this.propertyPageModel.getProjectRuleSet();
                ruleSet.setName("Project rulset");
                ruleSet.setDescription("Generated by PMD Plugin for Eclipse");
                try {
                    OutputStream out = new FileOutputStream(ruleSetFile.getLocation().toOSString());
                    RuleSetWriter writer = WriterAbstractFactory.getFactory().getRuleSetWriter();
                    writer.write(out, ruleSet);
                    out.flush();
                    out.close();
                    ruleSetFile.refreshLocal(IResource.DEPTH_INFINITE, null);
                } catch (IOException e) {
                    PMDPlugin.getDefault().showError(this.getMessage(MSGKEY_ERROR_EXPORTING_RULESET), e);
                } catch (PMDEclipseException e) {
                    PMDPlugin.getDefault().showError(this.getMessage(MSGKEY_ERROR_EXPORTING_RULESET), e);
                } catch (CoreException e) {
                    PMDPlugin.getDefault().showError(this.getMessage(MSGKEY_ERROR_EXPORTING_RULESET), e);
                }
            }
        }
    }

    /**
     * Helper method to shorten message access
     * 
     * @param key
     *            a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }
}