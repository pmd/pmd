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
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDEclipseException;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.RuleSetWriter;
import net.sourceforge.pmd.eclipse.WriterAbstractFactory;
import net.sourceforge.pmd.eclipse.builder.PMDNature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;

/**
 * This class implements the controler of the Property page
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2004/11/18 23:54:27  phherlin
 * Refactoring to apply MVC.
 * The goal is to test the refactoring before a complete refactoring for all GUI
 *
 *
 */
public class PMDPropertyPageController {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.properties.PMDPropertyPageController");
    private IAdaptable element;
    private PMDPropertyPage propertyPage;
    
    /**
     * Contructor
     * @param propertyPage the page for which this class is a controller
     */
    public PMDPropertyPageController(PMDPropertyPage propertyPage) {
        this.propertyPage = propertyPage;
    }
    
    /**
     * @return Returns the element.
     */
    public IAdaptable getElement() {
        return element;
    }
    
    /**
     * @param element The element to set.
     */
    public void setElement(IAdaptable element) {
        this.element = element;
    }
    
    /**
     * Process the validation of the properties (OK button pressed)
     * @return always true
     */
    public boolean performOk() {
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if ((resource != null) && (resource.isAccessible())) {

            boolean forceRebuild = storeRuleSetStoredInProject();
            boolean flag = storeRuleSelection();
            if (!propertyPage.isRuleSetStoredInProject()) {
                forceRebuild |= flag;
            }

            storeSelectedWorkingSet();
            boolean fEnabled = propertyPage.isPMDEnabled();

            if (fEnabled) {
                forceRebuild |= addPMDNature();
                if (forceRebuild) {
                    rebuildProject();
                }
            } else {
                removePMDNature();
            }
        }

        return true;
    }
    
    /**
     * Check whether PMD is already enabled for this project
     * @return
     */
    public boolean isPMDEnabled() {
        boolean fEnabled = false;
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                fEnabled = project.hasNature(PMDNature.PMD_NATURE);
            }
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }

        return fEnabled;
    }
    
    /**
     * Check wether the ruleset is stored in the project
     * @return
     */
    public boolean isRuleSetStoredInProject() {
        boolean ruleSetStoredInProject = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            ruleSetStoredInProject = PMDPlugin.getDefault().isRuleSetStoredInProject(resource.getProject());
        }

        return ruleSetStoredInProject;
    }
    
    /**
     * Return the configured working set for this project
     * @return
     */
    public IWorkingSet getProjectWorkingSet() {
        return (getElement() instanceof IProject) ?
               PMDPlugin.getDefault().getProjectWorkingSet((IProject) getElement()) : null;
    }
    
    /**
     * @return the configured ruleset for the entire workbench
     */
    public RuleSet getAvailableRules() {
        return PMDPlugin.getDefault().getRuleSet();
    }
    
    /**
     * @return only the selected rules for this project
     */
    public RuleSet getProjectRules() {
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        return resource != null ? PMDPlugin.getDefault().getRuleSetForResourceFromProperties(resource, true) : null;
    }

    /**
     * Store the store_ruleset_project property
     *
     */
    private boolean storeRuleSetStoredInProject() {
        boolean needRebuild = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            IProject project = resource.getProject();
            boolean configuredProperty = PMDPlugin.getDefault().isRuleSetStoredInProject(project);

            if (configuredProperty != this.propertyPage.isRuleSetStoredInProject()) {
                PMDPlugin.getDefault().setRuleSetStoredInProject(
                    project,
                    new Boolean(this.propertyPage.isRuleSetStoredInProject()));
                needRebuild = true;
            }

            if (this.propertyPage.isRuleSetStoredInProject()) {
                checkProjectRuleSetFile(project);
                try {
                    project.setSessionProperty(PMDPlugin.SESSION_PROPERTY_RULESET_MODIFICATION_STAMP, null);
                } catch (CoreException e) {
                    log.error("Core exception when nullify the ruleset timestamp");
                    log.debug("", e);
                }
            }
        }

        return needRebuild;
    }

    /**
     * Store the rules selection in project property
     */
    private boolean storeRuleSelection() {
        log.debug("Storing the rule selection");
        boolean flNeedRebuild = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            RuleSet activeRuleSet = PMDPlugin.getDefault().getRuleSetForResourceFromProperties(resource, true);
            Set activeRules = null;
            activeRules = activeRuleSet.getRules();

            RuleSet selectedRuleSet = new RuleSet();
            TableItem[] rulesList = this.propertyPage.getRulesList();
            for (int i = 0; i < rulesList.length; i++) {
                if (rulesList[i].getChecked()) {
                    Rule rule = (Rule) rulesList[i].getData();
                    selectedRuleSet.addRule(rule);
                    log.debug("Adding rule " + rule.getName() + " in the active ruleset");
                    if (!flNeedRebuild && (!activeRules.contains(rule))) {
                        flNeedRebuild = true;
                    }
                }
            }

            log.debug("Ask to store the rule selection for resource " + resource.getName());
            PMDPlugin.getDefault().storeRuleSetForResource(resource, selectedRuleSet);

            if (!flNeedRebuild) {
                Iterator i = activeRules.iterator();
                Set selectedRules = selectedRuleSet.getRules();
                while ((i.hasNext()) && (!flNeedRebuild)) {
                    Rule rule = (Rule) i.next();
                    if (!selectedRules.contains(rule)) {
                        flNeedRebuild = true;
                    }
                }
            }
        }

        return flNeedRebuild;
    }

    /**
     * Store the selected workingset
     *
     */
    private void storeSelectedWorkingSet() {
        boolean flStore = false;
        IResource resource = (IResource) getElement().getAdapter(IResource.class);
        if (resource != null) {
            IProject project = resource.getProject();
            IWorkingSet configuredWorkingSet = PMDPlugin.getDefault().getProjectWorkingSet(project);
            IWorkingSet selectedWorkingSet = this.propertyPage.getSelectedWorkingSet();

            if ((configuredWorkingSet == null) && (selectedWorkingSet != null)) {
                flStore = true;
            } else if ((configuredWorkingSet != null) && (selectedWorkingSet == null)) {
                flStore = true;
            } else if (
                (configuredWorkingSet != null)
                    && (selectedWorkingSet != null)
                    && (!configuredWorkingSet.getName().equals(selectedWorkingSet.getName()))) {
                flStore = true;
            }

            if (flStore) {
                PMDPlugin.getDefault().setProjectWorkingSet(project, selectedWorkingSet);
            }
        }
    }

    /**
     * Add the PMD Nature to the project
     * @return false if nature cannot be added
     */
    private boolean addPMDNature() {
        boolean natureAdded = false;
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                if (!project.hasNature(PMDNature.PMD_NATURE)) {
                    log.info("Adding PMD nature to the project " + project.getName());
                    ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(this.propertyPage.getShell());
                    AddNatureTask task = new AddNatureTask(project);
                    progressDialog.run(true, true, task);
                    natureAdded = task.natureAdded;
                    log.debug("Nature added = " + natureAdded);
                }
            }
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Adding PMD nature interrupted", e);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
        } catch (CoreException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }

        return natureAdded;
    }

    /**
     * Perform a full rebuild of the project
     *
     */
    private void rebuildProject() {
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                log.info("Adding PMD nature to the project " + project.getName());
                boolean rebuild =
                    MessageDialog.openQuestion(
                        this.propertyPage.getShell(),
                        getMessage(PMDConstants.MSGKEY_QUESTION_TITLE),
                        getMessage(PMDConstants.MSGKEY_QUESTION_REBUILD_PROJECT));
                if (rebuild) {
                    log.info("Full rebuild of the project " + project.getName());
                    ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(this.propertyPage.getShell());
                    progressDialog.run(true, true, new RebuildTask(project));
                }
            }
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_INVOCATIONTARGET_EXCEPTION), e);
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Rebuilding project interrupted", e);
        }
    }

    /**
     * Remove a PMD Nature from the project
     */
    private void removePMDNature() {
        try {
            IResource resource = (IResource) getElement().getAdapter(IResource.class);
            if (resource != null) {
                IProject project = resource.getProject();
                log.info("Removing PMD nature from the project " + project.getName());
                ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(this.propertyPage.getShell());
                progressDialog.run(true, true, new RemoveNatureTask(project));
            }
        } catch (InterruptedException e) {
            PMDPlugin.getDefault().logError("Removing PMD nature interrupted", e);
        } catch (InvocationTargetException e) {
            PMDPlugin.getDefault().showError("Error removing PMD nature", e);
        }
    }

    // For debug purpose
    //    private void traceProjectNaturesAndCommands(IProject project) {
    //        try {
    //            IProjectDescription description = project.getDescription();
    //            String[] natureIds = description.getNatureIds();
    //            System.out.println("Natures : ");
    //            for (int i = 0; i < natureIds.length; i++) {
    //                System.out.println("   " + natureIds[i]);
    //            }
    //            
    //            ICommand[] commands = description.getBuildSpec();
    //            System.out.println("Commands : ");
    //            for (int i = 0; i < commands.length; i++) {
    //                System.out.println("   " + commands[i].getBuilderName());
    //            }
    //        } catch (CoreException e) {
    //            e.printStackTrace();
    //        }
    //
    //    }

    /**
     * Check if the project has a ruleset file. If not propose to create a default one
     * @param project
     */
    private void checkProjectRuleSetFile(IProject project) {
        IFile ruleSetFile = project.getFile(".ruleset");
        if (!ruleSetFile.exists()) {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            if (MessageDialog
                .openQuestion(
                    shell,
                    getMessage(PMDConstants.MSGKEY_QUESTION_TITLE),
                    getMessage(PMDConstants.MSGKEY_QUESTION_CREATE_RULESET_FILE))) {
                storeRuleSelection();
                RuleSet ruleSet = PMDPlugin.getDefault().getRuleSetForResourceFromProperties(project, false);
                ruleSet.setName("project rulset");
                ruleSet.setDescription("Generated by PMD Plugin for Eclipse");
                try {
                    OutputStream out = new FileOutputStream(ruleSetFile.getLocation().toOSString());
                    RuleSetWriter writer = WriterAbstractFactory.getFactory().getRuleSetWriter();
                    writer.write(out, ruleSet);
                    out.flush();
                    out.close();
                    ruleSetFile.refreshLocal(IResource.DEPTH_INFINITE, null);
                } catch (IOException e) {
                    PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_EXPORTING_RULESET), e);
                } catch (PMDEclipseException e) {
                    PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_EXPORTING_RULESET), e);
                } catch (CoreException e) {
                    PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_EXPORTING_RULESET), e);
                }
            }
        }
    }

    /**
     * Private inner class to add nature to project
     */
    private class AddNatureTask implements IRunnableWithProgress {
        private IProject project;
        public boolean natureAdded = false;

        public AddNatureTask(IProject project) {
            this.project = project;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                IProjectDescription description = project.getDescription();
                String[] natureIds = description.getNatureIds();
                String[] newNatureIds = new String[natureIds.length + 1];
                System.arraycopy(natureIds, 0, newNatureIds, 0, natureIds.length);
                newNatureIds[natureIds.length] = PMDNature.PMD_NATURE;
                description.setNatureIds(newNatureIds);
                this.project.setDescription(description, monitor);
                this.natureAdded = true;
            } catch (CoreException e) {
                PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
            }
        }
    };

    /**
     * Inner class to rebuild the project
     */
    private class RebuildTask implements IRunnableWithProgress {
        private IProject project;

        public RebuildTask(IProject project) {
            this.project = project;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
            } catch (CoreException e) {
                PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
            }
        }
    };

    /**
     * Private inner class to remove nature from project
     */
    private class RemoveNatureTask implements IRunnableWithProgress {
        private IProject project;

        public RemoveNatureTask(IProject project) {
            this.project = project;
        }

        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            try {
                if (project.hasNature(PMDNature.PMD_NATURE)) {
                    IProjectDescription description = project.getDescription();
                    String[] natureIds = description.getNatureIds();
                    String[] newNatureIds = new String[natureIds.length - 1];
                    for (int i = 0, j = 0; i < natureIds.length; i++) {
                        if (!natureIds[i].equals(PMDNature.PMD_NATURE)) {
                            newNatureIds[j++] = natureIds[i];
                        }
                    }
                    description.setNatureIds(newNatureIds);
                    project.setDescription(description, monitor);
                    project.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                }
            } catch (CoreException e) {
                PMDPlugin.getDefault().showError(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
            }
        }
    };

    /**
     * Helper method to shorten message access
     * @param key a message key
     * @return requested message
     */
    protected String getMessage(String key) {
        return PMDPlugin.getDefault().getMessage(key);
    }
}
