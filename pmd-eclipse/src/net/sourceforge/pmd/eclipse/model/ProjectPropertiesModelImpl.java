/*
 * Created on 24 nov. 2004
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
package net.sourceforge.pmd.eclipse.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.PMDEclipseException;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;
import net.sourceforge.pmd.eclipse.RuleSetWriter;
import net.sourceforge.pmd.eclipse.RuleSetWriterImpl;
import net.sourceforge.pmd.eclipse.builder.PMDNature;
import net.sourceforge.pmd.eclipse.dao.DAOException;
import net.sourceforge.pmd.eclipse.dao.DAOFactory;
import net.sourceforge.pmd.eclipse.dao.ProjectPropertiesDAO;
import net.sourceforge.pmd.eclipse.dao.ProjectPropertiesTO;
import net.sourceforge.pmd.eclipse.dao.RuleSpecTO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * Default implementation of a ProjectPropertiesModel
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.9  2005/10/24 22:42:22  phherlin
 * Fix never ending loop issue
 *
 * Revision 1.8  2005/07/01 00:04:11  phherlin
 * Fix the bug of the rules that cannot be unselected
 *
 * Revision 1.7  2005/06/30 23:25:03  phherlin
 * Fix project rule set synchronization with the plugin rule set
 *
 * Revision 1.6  2005/06/11 22:11:31  phherlin
 * Fixing the project ruleset management
 *
 * Revision 1.5  2005/06/07 18:38:13  phherlin
 * Move classes to limit packages cycle dependencies
 *
 * Revision 1.4  2005/06/05 19:28:13  phherlin
 * Decrease the complexity of isRuleSetEqual
 *
 * Revision 1.3  2005/05/31 23:02:20  phherlin
 * Refactor behaviour when project properties file does not exists.
 *
 * Revision 1.2  2005/05/31 20:44:40  phherlin
 * Continuing refactoring
 *
 * Revision 1.1  2005/05/31 20:33:01  phherlin
 * Continuing refactoring
 * Revision 1.3 2005/05/07 13:32:04
 * phherlin Continuing refactoring Fix some PMD violations Fix Bug 1144793 Fix
 * Bug 1190624 (at least try)
 * 
 * Revision 1.2 2004/12/03 00:22:43 phherlin Continuing the refactoring
 * experiment. Implement the Command framework. Refine the MVC pattern usage.
 * 
 * Revision 1.1 2004/11/28 20:31:38 phherlin Continuing the refactoring
 * experiment
 * 
 *  
 */
public class ProjectPropertiesModelImpl extends AbstractModel implements ProjectPropertiesModel {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.model.ProjectPropertiesModelImpl");
    private final ProjectPropertiesDAO propertiesDao;
    private final IProject project;
    private boolean needRebuild;
    private boolean pmdEnabled;
    private boolean ruleSetStoredInProject;
    private RuleSet projectRuleSet;
    private IWorkingSet projectWorkingSet;

    /**
     * The default constructor takes a project as an argument
     */
    public ProjectPropertiesModelImpl(final IProject project) {
        super();
        this.project = project;
        this.propertiesDao = DAOFactory.getFactory().getProjectPropertiesDAO();
        this.projectRuleSet = PMDPlugin.getDefault().getRuleSet();

        try {
            loadProperties();
        } catch (Exception e) {
            log.warn("Cannot load properties for project " + this.project.getName(), e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isPmdEnabled()
     */
    public boolean isPmdEnabled() throws ModelException {
        log.info("Query if PMD is enabled for project " + this.project.getName() + ": " + this.pmdEnabled);
        return this.pmdEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setPmdEnabled(boolean)
     */
    public void setPmdEnabled(final boolean pmdEnabled) throws ModelException {
        log.info("Enable PMD for project " + this.project.getName() + ": " + this.pmdEnabled);
        if (this.pmdEnabled != pmdEnabled) {
            this.pmdEnabled = pmdEnabled;
            this.needRebuild |= pmdEnabled;
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#getProjectRuleSet()
     */
    public RuleSet getProjectRuleSet() throws ModelException {
        log.info("Query the rule set for project " + this.project.getName());
        if (!isRuleSetStoredInProject()) {
            if (synchronizeRuleSet()) {
                sync();
            }
        }

        return this.projectRuleSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setProjectRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setProjectRuleSet(final RuleSet projectRuleSet) throws ModelException {
        log.info("Set a rule set for project " + this.project.getName());
        if (projectRuleSet == null) {
            throw new ModelException("Setting a project rule set to null"); // TODO NLS
        }

        this.projectRuleSet = projectRuleSet;
        if (synchronizeRuleSet()) {
            sync();
            this.needRebuild = true;
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isRuleSetStoredInProject()
     */
    public boolean isRuleSetStoredInProject() throws ModelException {
        log.info("Query if the rule set is stored in project " + this.project.getName() + ": " + this.ruleSetStoredInProject);
        return this.ruleSetStoredInProject;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setRuleSetStoredInProject(boolean)
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) throws ModelException {
        log.info("Set rule set stored in project for project " + this.project.getName() + ": " + ruleSetStoredInProject);
        this.ruleSetStoredInProject = ruleSetStoredInProject;
        if (this.ruleSetStoredInProject) {
            if (!isRuleSetFileExist()) {
                throw new ModelException("The project ruleset file cannot be found for project " + this.project.getName()); // TODO NLS
            }
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#getProjectWorkingSet()
     */
    public IWorkingSet getProjectWorkingSet() throws ModelException {
        log.info("Query working set for project " + this.project.getName() + ": "
                + (this.projectWorkingSet == null ? "none" : this.projectWorkingSet.getName()));
        return this.projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setProjectWorkingSet(org.eclipse.ui.IWorkingSet)
     */
    public void setProjectWorkingSet(final IWorkingSet projectWorkingSet) throws ModelException {
        log.info("Set working set for project " + this.project.getName() + ": "
                + (projectWorkingSet == null ? "none" : projectWorkingSet.getName()));

        this.projectWorkingSet = projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isNeedRebuild()
     */
    public boolean isNeedRebuild() {
        log.info("Query if project " + this.project.getName() + " need rebuild : " + (this.pmdEnabled && this.needRebuild));
        log.debug("   PMD Enabled = " + this.pmdEnabled);
        log.debug("   Project need rebuild = " + this.needRebuild);
        return this.pmdEnabled && this.needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#setNeedRebuild()
     */
    public void setNeedRebuild(final boolean needRebuild) {
        log.info("Set if rebuild is needed for project " + this.project.getName() + ": " + needRebuild);
        this.needRebuild = needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel#isRuleSetFileExist()
     */
    public final boolean isRuleSetFileExist() {
        log.info("Query if rule set file exists for project " + this.project.getName());
        final IFile file = this.project.getFile(PMDPluginConstants.PROJECT_RULESET_FILE);
        return file.exists() && file.isAccessible();
    }
    
    /**
     * Create a project ruleset file from the current configured rules
     *
     */
    public void createDefaultRuleSetFile() throws ModelException {
        log.info("Create a default rule set file for project " + this.project.getName());
        try {
            final RuleSetWriter writer = new RuleSetWriterImpl();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writer.write(baos, this.projectRuleSet);
            baos.close();
            
            final IFile file = this.project.getFile(PMDPluginConstants.PROJECT_RULESET_FILE);
            if (file.exists() && file.isAccessible()) {
                throw new ModelException("Project ruleset file already exists");
            } else {
                final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());            
                file.create(bais, true, null);
                bais.close();
            }
        } catch (PMDEclipseException e) {
            throw new ModelException(e);
        } catch (IOException e) {
            throw new ModelException(e);
        } catch (CoreException e) {
            throw new ModelException(e);
        }
        
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PMDPluginModel#sync()
     */
    public void sync() throws ModelException {
        log.info("Commit properties for project " + this.project.getName());
        try {
            if (this.pmdEnabled) {
                PMDNature.addPMDNature(this.project, getMonitor());
            } else {
                PMDNature.removePMDNature(this.project, getMonitor());
            }
            
            saveProperties();
        } catch (CoreException e) {
            throw new ModelException(e.getMessage(), e);
        } catch (DAOException e) {
            throw new ModelException(e.getMessage(), e);
        }
    }

    /**
     * Check the project ruleset against the plugin ruleset and synchronize if
     * necessary
     * 
     * @return true if the project ruleset has changed.
     *  
     */
    private boolean synchronizeRuleSet() {
        log.debug("Synchronizing the project ruleset with the plugin ruleset");
        final RuleSet pluginRuleSet = PMDPlugin.getDefault().getRuleSet();
        boolean flChanged = false;

        if (!this.projectRuleSet.getRules().equals(pluginRuleSet.getRules())) {
            log.debug("The project ruleset is different from the plugin ruleset");
            
            // 1-If rules have been deleted from preferences
            // delete them also from the project ruleset
            final Iterator i = this.projectRuleSet.getRules().iterator();
            while (i.hasNext()) {
                final Rule projectRule = (Rule) i.next();
                final Rule pluginRule = pluginRuleSet.getRuleByName(projectRule.getName());
                if (pluginRule == null) {
                    log.debug("The rule " + projectRule.getName() + " is no more defined in the plugin ruleset. Remove it.");
                    i.remove();
                }
            }

            // 2-For all other rules, replace the current one by the plugin one
            final Iterator k = this.projectRuleSet.getRules().iterator();
            final RuleSet ruleSet = new RuleSet();
            ruleSet.setDescription(this.projectRuleSet.getDescription());
            ruleSet.setName(this.projectRuleSet.getName());
            while (k.hasNext()) {
                final Rule projectRule = (Rule) k.next();
                final Rule pluginRule = pluginRuleSet.getRuleByName(projectRule.getName());
                if (pluginRule != null) {
                    log.debug("Keeping rule " + projectRule.getName());
                    ruleSet.addRule(pluginRule);
                }
            }

            flChanged = !ruleSet.getRules().equals(this.projectRuleSet.getRules());
            if (flChanged) {
                this.projectRuleSet = ruleSet;
                log.info("Ruleset for project " + this.project.getName() + " is now synchronized.");
            }
        }

        return flChanged;
    }

    /**
     * Load project properties from a properties file in the project. If the
     * file is not found or if an exception occurs, default values are used and
     * the project is considered not to have any properties set.
     *  
     */
    private void loadProperties() throws DAOException, CoreException, ModelException {
        log.debug("Loading project properties");
        final ProjectPropertiesTO projectProperties = this.propertiesDao.readProjectProperties(this.project);

        if (projectProperties == null) {
            log.info("Project properties not found. Use default.");
        } else {
            setWorkingSetFromProperties(projectProperties.getWorkingSetName());
            this.ruleSetStoredInProject = projectProperties.isRuleSetStoredInProject();
            this.pmdEnabled = this.project.hasNature(PMDNature.PMD_NATURE);
            if (this.ruleSetStoredInProject) {
                loadRuleSetFromProject();
            } else {
                setRuleSetFromProperties(projectProperties.getRules());
            }
            
            log.debug("Project properties loaded");
        }
    }

    /**
     * Set the rule set from rule specs found in properties file
     * 
     * @param rules
     *            array of selected rules
     */
    private void setRuleSetFromProperties(final RuleSpecTO[] rules) {
        this.projectRuleSet = new RuleSet();
        final RuleSet pluginRuleSet = PMDPlugin.getDefault().getRuleSet();
        for (int i = 0; i < rules.length; i++) {
            try {
                final Rule rule = pluginRuleSet.getRuleByName(rules[i].getName());
                this.projectRuleSet.addRule(rule);
            } catch (RuntimeException e) {
                log.debug("The rule " + rules[i].getName() + " cannot be found. ignore.");
            }
        }
    }

    /**
     * Set the project working set from a name
     * 
     * @param workingSetName
     */
    private void setWorkingSetFromProperties(final String workingSetName) {
        final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
        this.projectWorkingSet = workingSetManager.getWorkingSet(workingSetName);
    }
    
    /**
     * Load the project rule set from the project ruleset
     *
     */
    private void loadRuleSetFromProject() {
        if (!isRuleSetFileExist()) {
            try {
                final RuleSetFactory factory = new RuleSetFactory();
                final IFile ruleSetFile = this.project.getFile(PMDPluginConstants.PROJECT_RULESET_FILE);
                this.projectRuleSet = factory.createRuleSet(ruleSetFile.getLocation().toOSString());
            } catch (RuleSetNotFoundException e) {
                PMDPlugin.getDefault().logError("Project RuleSet cannot be loaded for project " + this.project.getName() + ". Using the rules from properties.", e);
            }
        }
    }

    /**
     * Store all properties in a project file
     * 
     * @throws DAOException
     */
    private void saveProperties() throws DAOException {
            final ProjectPropertiesTO bean = new ProjectPropertiesTO();
            bean.setRuleSetStoredInProject(this.ruleSetStoredInProject);
            bean.setWorkingSetName(this.projectWorkingSet == null ? null : this.projectWorkingSet.getName());

            if (this.ruleSetStoredInProject) {
                loadRuleSetFromProject();
            } else {
                final List rules = new ArrayList();
                final Iterator i = this.projectRuleSet.getRules().iterator();
                while (i.hasNext()) {
                    final Rule rule = (Rule) i.next();
                    rules.add(new RuleSpecTO(rule.getName(), rule.getRuleSetName())); // NOPMD:AvoidInstantiatingObjectInLoop
                }
                bean.setRules((RuleSpecTO[]) rules.toArray(new RuleSpecTO[rules.size()]));
            }

            this.propertiesDao.writeProjectProperties(this.project, bean, getMonitor());
    }
}