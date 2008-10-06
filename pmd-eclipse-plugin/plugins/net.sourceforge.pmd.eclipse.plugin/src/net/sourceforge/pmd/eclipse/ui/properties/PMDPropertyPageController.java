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
package net.sourceforge.pmd.eclipse.ui.properties;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.cmd.BuildProjectCommand;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;
import net.sourceforge.pmd.eclipse.ui.nls.StringKeys;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;

/**
 * This class implements the controler of the Property page
 *
 * @author Philippe Herlin
 *
 */
public class PMDPropertyPageController {
    private static final Logger log = Logger.getLogger(PMDPropertyPageController.class);
    private final Shell shell;
    private IProject project;
    private PMDPropertyPageBean propertyPageBean;
    private boolean pmdAlreadyActivated;

    /**
     * Contructor
     *
     * @param shell
     *            the shell from the view the controller is associated
     */
    public PMDPropertyPageController(final Shell shell) {
        super();
        this.shell = shell;
    }

    /**
     * @return Returns the project.
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * @param element
     *            The project to set.
     */
    public void setProject(final IProject project) {
        if (project.isAccessible()) {
            this.project = project;
        } else {
            log.warn("Couldn't accept project because it is not accessible.");
        }
    }

    /**
     * @return Returns the propertyPageBean.
     */
    public PMDPropertyPageBean getPropertyPageBean() {
        // assert ((this.project != null) && (this.project.isAccessible()))

        if (this.propertyPageBean == null) {
            log.debug("Building a property page bean");
            try {
                final IProjectProperties properties = PMDPlugin.getDefault().loadProjectProperties(this.project);

                this.propertyPageBean = new PMDPropertyPageBean();
                this.propertyPageBean.setPmdEnabled(properties.isPmdEnabled());
                this.propertyPageBean.setProjectWorkingSet(properties.getProjectWorkingSet());
                this.propertyPageBean.setProjectRuleSet(properties.getProjectRuleSet());
                this.propertyPageBean.setRuleSetStoredInProject(properties.isRuleSetStoredInProject());
                this.propertyPageBean.setRuleSetFile(properties.getRuleSetFile());
                this.propertyPageBean.setIncludeDerivedFiles(properties.isIncludeDerivedFiles());
                this.pmdAlreadyActivated = properties.isPmdEnabled();

            } catch (PropertiesException e) {
                PMDPlugin.getDefault().showError(e.getMessage(), e);
            }
        }

        return this.propertyPageBean;
    }

    /**
     * @return the configured ruleset for the entire workbench
     */
    public RuleSet getAvailableRules() {
        return PMDPlugin.getDefault().getPreferencesManager().getRuleSet();
    }

    /**
     * Process the validation of the properties (OK button pressed)
     *
     * @return always true
     */
    public boolean performOk() {
        // assert ((this.project != null) && (this.project.isAccessible()))

        try {
            checkProjectRuleSetFile();

            // Updates the project properties
            final UpdateProjectPropertiesCmd cmd = new UpdateProjectPropertiesCmd();
            cmd.setProject(this.project);
            cmd.setPmdEnabled(this.propertyPageBean.isPmdEnabled());
            cmd.setProjectWorkingSet(this.propertyPageBean.getProjectWorkingSet());
            cmd.setProjectRuleSet(this.propertyPageBean.getProjectRuleSet());
            cmd.setRuleSetStoredInProject(this.propertyPageBean.isRuleSetStoredInProject());
            cmd.setRuleSetFile(this.propertyPageBean.getRuleSetFile());
            cmd.setIncludeDerivedFiles(this.propertyPageBean.isIncludeDerivedFiles());
            cmd.setUserInitiated(true);
            cmd.performExecute();

            // If rebuild is needed, then rebuild the project
            log.debug("Updating command terminated, checking whether the project need to be rebuilt");
            if (this.pmdAlreadyActivated && cmd.isNeedRebuild()) {
                rebuildProject();
            }
        } catch (PropertiesException e) {
            PMDPlugin.getDefault().showError(e.getMessage(), e);
        } catch (CommandException e) {
            PMDPlugin.getDefault().showError(e.getMessage(), e);
        }

        return true;
    }

    /**
     * Process a select workingset event
     *
     * @param currentWorkingSet
     *            the working set currently selected of null if none
     * @return the newly selected working set or null if none.
     *
     */
    public IWorkingSet selectWorkingSet(final IWorkingSet currentWorkingSet) {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkingSetManager workingSetManager = workbench.getWorkingSetManager();
        final IWorkingSetSelectionDialog selectionDialog = workingSetManager.createWorkingSetSelectionDialog(this.shell, false);
        IWorkingSet selectedWorkingSet = null;

        if (currentWorkingSet != null) {
            selectionDialog.setSelection(new IWorkingSet[] { currentWorkingSet });
        }

        if (selectionDialog.open() == Window.OK) {
            if (selectionDialog.getSelection().length == 0) {
                log.info("Deselect working set");
            } else {
                selectedWorkingSet = selectionDialog.getSelection()[0];
                log.info("Working set " + selectedWorkingSet.getName() + " selected");
            }
        }

        return selectedWorkingSet;
    }

    /**
     * Perform a full rebuild of the project
     *
     * @param monitor
     *            a progress monitor
     *
     */
    private void rebuildProject() {
        final boolean rebuild = MessageDialog.openQuestion(shell, getMessage(StringKeys.MSGKEY_QUESTION_TITLE),
                getMessage(StringKeys.MSGKEY_QUESTION_REBUILD_PROJECT));

        if (rebuild) {
            log.info("Full rebuild of the project " + this.project.getName());
            try {
                final BuildProjectCommand cmd = new BuildProjectCommand();
                cmd.setProject(this.project);
                cmd.setUserInitiated(true);
                cmd.performExecute();
            } catch (CommandException e) {
                PMDPlugin.getDefault().showError(e.getMessage(), e);
            }
        }
    }

    /**
     * If the user asks to use a project ruleset file, check if it exists.
     * Otherwise, asks the user to create a default one
     *
     */
    private void checkProjectRuleSetFile() throws PropertiesException {
        if (this.propertyPageBean.isRuleSetStoredInProject()) {
            final IProjectProperties properties = PMDPlugin.getDefault().loadProjectProperties(this.project);
            if (!properties.isRuleSetFileExist()) {
                createDefaultRuleSetFile();
            }
        }
    }

    /**
     * Create a default ruleset file from the current project ruleset
     *
     */
    private void createDefaultRuleSetFile() throws PropertiesException {
        final boolean create = MessageDialog.openQuestion(shell, getMessage(StringKeys.MSGKEY_QUESTION_TITLE),
                getMessage(StringKeys.MSGKEY_QUESTION_CREATE_RULESET_FILE));
        if (create) {
            final IProjectProperties properties = PMDPlugin.getDefault().loadProjectProperties(this.project);
            properties.createDefaultRuleSetFile();
        } else {
            this.propertyPageBean.setRuleSetStoredInProject(false);
        }
    }

    /**
     * Helper method to shorten message access
     *
     * @param key
     *            a message key
     * @return requested message
     */
    protected String getMessage(final String key) {
        return PMDPlugin.getDefault().getStringTable().getString(key);
    };
}