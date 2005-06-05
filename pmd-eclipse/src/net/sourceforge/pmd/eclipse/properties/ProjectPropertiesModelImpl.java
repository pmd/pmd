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
package net.sourceforge.pmd.eclipse.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;
import net.sourceforge.pmd.eclipse.builder.PMDNature;
import net.sourceforge.pmd.eclipse.dao.DAOException;
import net.sourceforge.pmd.eclipse.dao.DAOFactory;
import net.sourceforge.pmd.eclipse.model.AbstractModel;
import net.sourceforge.pmd.eclipse.model.ModelException;

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
            this.loadProperties();
        } catch (Exception e) {
            log.warn("Cannot load properties for project " + this.project.getName(), e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#isPmdEnabled()
     */
    public boolean isPmdEnabled() throws ModelException {
        return this.pmdEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#setPmdEnabled(boolean)
     */
    public void setPmdEnabled(final boolean pmdEnabled) throws ModelException {
        log.info("Enable PMD for project " + this.project.getName() + " : " + pmdEnabled);
        if (this.pmdEnabled != pmdEnabled) {
            this.pmdEnabled = pmdEnabled;
            this.needRebuild |= pmdEnabled;
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#getProjectRuleSet()
     */
    public RuleSet getProjectRuleSet() throws ModelException {
        if (this.synchronizeRuleSet()) {
            this.sync();
        }

        return this.projectRuleSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#setProjectRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setProjectRuleSet(final RuleSet projectRuleSet) throws ModelException {
        log.info("Set a rule set for project " + this.project.getName());
        if (projectRuleSet == null) {
            throw new ModelException("Setting a project rule set to null"); // TODO NLS
        }

        this.projectRuleSet = projectRuleSet;
        if (this.synchronizeRuleSet()) {
            this.sync();
            this.needRebuild = true;
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#isRuleSetStoredInProject()
     */
    public boolean isRuleSetStoredInProject() throws ModelException {
        return this.ruleSetStoredInProject;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#setRuleSetStoredInProject(boolean)
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) throws ModelException {
        log.info("Set rule set stored in project for project " + this.project.getName() + " : " + ruleSetStoredInProject);
        this.ruleSetStoredInProject = ruleSetStoredInProject;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#getProjectWorkingSet()
     */
    public IWorkingSet getProjectWorkingSet() throws ModelException {
        return this.projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#setProjectWorkingSet(org.eclipse.ui.IWorkingSet)
     */
    public void setProjectWorkingSet(final IWorkingSet projectWorkingSet) throws ModelException {
        log.info("Set working set for project " + this.project.getName() + " : "
                + (projectWorkingSet == null ? "none" : projectWorkingSet.getName()));

        this.projectWorkingSet = projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#isNeedRebuild()
     */
    public boolean isNeedRebuild() {
        log.debug("Query if project " + this.project.getName() + " need rebuild : " + (this.pmdEnabled && this.needRebuild));
        log.debug("   PMD Enabled = " + this.pmdEnabled);
        log.debug("   Project need rebuild = " + this.needRebuild);
        return this.pmdEnabled && this.needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#setNeedRebuild()
     */
    public void setNeedRebuild(final boolean needRebuild) {
        this.needRebuild = needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.properties.ProjectPropertiesModel#isRuleSetFileExist()
     */
    public boolean isRuleSetFileExist() {
        final IFile file = this.project.getFile(PMDPluginConstants.PROJECT_RULESET_FILE);
        return file.exists() && file.isAccessible();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PMDPluginModel#sync()
     */
    public void sync() throws ModelException {
        try {
            if (this.pmdEnabled) {
                PMDNature.addPMDNature(this.project, this.getMonitor());
            } else {
                PMDNature.removePMDNature(this.project, this.getMonitor());
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
        final RuleSet pluginRuleSet = PMDPlugin.getDefault().getRuleSet();
        boolean flChanged = false;

        if (this.isRuleSetEqual(this.projectRuleSet, pluginRuleSet)) {
            this.projectRuleSet = pluginRuleSet;
        } else {
            // 1-If rules have been deleted from preferences
            // delete them also from the project ruleset
            final Iterator i = this.projectRuleSet.getRules().iterator();
            while (i.hasNext()) {
                final Rule projectRule = (Rule) i.next();
                try {
                    // @PMD:REVIEWED:UnusedLocalVariable: by Herlin on 15/05/05
                    // 19:37
                    // a if (pluginRule == null) would be better but this is how
                    // PMD works !
                    final Rule pluginRule = pluginRuleSet.getRuleByName(projectRule.getName());
                } catch (RuntimeException e) {
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
                ruleSet.addRule(pluginRule);
            }
            this.projectRuleSet = ruleSet;

            flChanged = true;
        }

        return flChanged;
    }

    /**
     * Test if 2 rule sets are equals. RuleSets are equals if they are the same
     * instance (trivial) or if they contain the same rules with the same
     * properties.
     * 
     * @param ruleSet1
     * @param ruleSet2
     * @return true if rulesets are equals:
     */
    private boolean isRuleSetEqual(final RuleSet ruleSet1, final RuleSet ruleSet2) {
        boolean equal = true;
        try {
            if (ruleSet1 != ruleSet2) {
                if (ruleSet1.getRules().size() == ruleSet2.getRules().size()) {
                    equal = deepRuleSetEqual(ruleSet1, ruleSet2);
                } else {
                    equal = false;
                }
            }
        } catch (RuntimeException e) {
            equal = false;
        }

        return equal;
    }

    /**
     * Deeply test if rules sets are equal. (called by isRuleSetEqual);
     * @param ruleSet1
     * @param ruleSet2
     * @return equal equality
     */
    private boolean deepRuleSetEqual(final RuleSet ruleSet1, final RuleSet ruleSet2) {
        final Iterator i = ruleSet1.getRules().iterator();
        boolean equal = true;
        while (i.hasNext() && equal) {
            final Rule rule1 = (Rule) i.next();
            final Rule rule2 = ruleSet2.getRuleByName(rule1.getName());
            equal = rule1.getPriority() == rule2.getPriority();
            if (equal) {
                final Properties p1 = rule1.getProperties();
                final Properties p2 = rule2.getProperties();
                final Iterator j = p1.keySet().iterator();
                while (j.hasNext() && equal) {
                    final String key = (String) j.next();
                    final String v1 = p1.getProperty(key).trim();
                    final String v2 = p2.getProperty(key).trim();
                    equal = v1.equals(v2);
                }
            }
        }
        
        return equal;
    }

    /**
     * Load project properties from a properties file in the project. If the
     * file is not found or if an exception occurs, default values are used and
     * the project is considered not to have any properties set.
     *  
     */
    private void loadProperties() throws DAOException, CoreException {
        log.debug("Loading project properties");
        final ProjectPropertiesTO projectProperties = this.propertiesDao.readProjectProperties(this.project);

        if (projectProperties == null) {
            log.info("Project properties not found. Use default.");
        } else {
            this.setRuleSetFromProperties(projectProperties.getRules());
            this.setWorkingSetFromProperties(projectProperties.getWorkingSetName());
            this.ruleSetStoredInProject = projectProperties.isRuleSetStoredInProject();
            this.pmdEnabled = this.project.hasNature(PMDNature.PMD_NATURE);
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
     * Store all properties in a project file
     * 
     * @throws DAOException
     */
    private void saveProperties() throws DAOException {
            final ProjectPropertiesTO bean = new ProjectPropertiesTO();
            bean.setRuleSetStoredInProject(this.ruleSetStoredInProject);
            bean.setWorkingSetName(this.projectWorkingSet == null ? null : this.projectWorkingSet.getName());

            final List rules = new ArrayList();
            final Iterator i = this.projectRuleSet.getRules().iterator();
            while (i.hasNext()) {
                final Rule rule = (Rule) i.next();
                rules.add(new RuleSpecTO(rule.getName(), rule.getRuleSetName())); // NOPMD:AvoidInstantiatingObjectInLoop
            }
            bean.setRules((RuleSpecTO[]) rules.toArray(new RuleSpecTO[rules.size()]));

            this.propertiesDao.writeProjectProperties(this.project, bean, this.getMonitor());
    }
}