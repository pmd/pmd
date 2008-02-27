/*
 * Created on 29 mai 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.runtime.properties.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.builder.PMDNature;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.runtime.properties.PropertiesException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * This class manages the persistances of the ProjectProperies information structure
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/06 16:41:57  phherlin
 * Continue refactoring of rullesets management
 *
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 * Revision 1.1 2005/06/07 18:38:14 phherlin Move classes to limit packages cycle
 * dependencies
 * 
 * Revision 1.2 2005/05/31 20:44:40 phherlin Continuing refactoring
 * 
 * Revision 1.1 2005/05/31 20:33:01 phherlin Continuing refactoring
 * 
 * 
 */
public class ProjectPropertiesManagerImpl implements IProjectPropertiesManager {
    private static final Logger log = Logger.getLogger(ProjectPropertiesManagerImpl.class);

    private static final String PROPERTIES_FILE = ".pmd";
    private static final String PROPERTIES_MAPPING = "/net/sourceforge/pmd/runtime/properties/impl/mapping.xml";

    private final Map projectsProperties = new HashMap();

    /**
     * Load a project properties
     * 
     * @param project a project
     */
    public IProjectProperties loadProjectProperties(final IProject project) throws PropertiesException {
        log.debug("Loading project properties for project " + project.getName());
        try {
            IProjectProperties projectProperties = (IProjectProperties) this.projectsProperties.get(project);
            if (projectProperties == null) {
                projectProperties = new PropertiesFactoryImpl().newProjectProperties(project, this);
                final ProjectPropertiesTO to = readProjectProperties(project);
                fillProjectProperties(projectProperties, to);
                this.projectsProperties.put(project, projectProperties);
            }
            
            // if the ruleset is stored in the project always reload it
            if (projectProperties.isRuleSetStoredInProject()) {
                loadRuleSetFromProject(projectProperties);
            }
            
            // else resynchronize the ruleset
            else {
                final boolean needRebuild = synchronizeRuleSet(projectProperties);
                projectProperties.setNeedRebuild(projectProperties.isNeedRebuild() || needRebuild);
            }

            return projectProperties;

        } catch (CoreException e) {
            throw new PropertiesException("Core Exception when loading project properties for project " + project.getName(), e);
        }
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectPropertiesManager#storeProjectProperties(net.sourceforge.pmd.runtime.properties.IProjectProperties)
     */
    public void storeProjectProperties(IProjectProperties projectProperties) throws PropertiesException {
        log.debug("Storing project properties for project " + projectProperties.getProject().getName());
        try {
            if (projectProperties.isPmdEnabled()) {
                PMDNature.addPMDNature(projectProperties.getProject(), null);
            } else {
                PMDNature.removePMDNature(projectProperties.getProject(), null);
            }

            writeProjectProperties(projectProperties.getProject(), fillTransferObject(projectProperties));
            this.projectsProperties.put(projectProperties.getProject(), projectProperties);
            
        } catch (CoreException e) {
            throw new PropertiesException("Core Exception when storing project properties for project " + projectProperties.getProject().getName(), e);
        }

    }

    /**
     * Load the project rule set from the project ruleset
     * 
     */
    private void loadRuleSetFromProject(IProjectProperties projectProperties) throws PropertiesException {
        if (projectProperties.isRuleSetFileExist()) {
            log.debug("Loading ruleset from project ruleset file: " + projectProperties.getRuleSetFile());
            try {
                final RuleSetFactory factory = new RuleSetFactory();
                final File ruleSetFile = projectProperties.getResolvedRuleSetFile();
                projectProperties.setProjectRuleSet(factory.createRuleSets(ruleSetFile.getPath()).getAllRuleSets()[0]);
            } catch (RuleSetNotFoundException e) {
                PMDRuntimePlugin.getDefault().logError(
                        "Project RuleSet cannot be loaded for project " + projectProperties.getProject().getName()
                                + " using RuleSet file name " + projectProperties.getRuleSetFile() + ". Using the rules from properties.", e);
            }
        }
    }

    /**
     * Read a project properties from properties file
     * 
     * @param project a project
     */
    private ProjectPropertiesTO readProjectProperties(final IProject project) throws PropertiesException {
        ProjectPropertiesTO projectProperties = null;

        try {
            final Mapping mapping = new Mapping(this.getClass().getClassLoader());
            final URL mappingSpecUrl = this.getClass().getResource(PROPERTIES_MAPPING);
            mapping.loadMapping(mappingSpecUrl);

            final IFile propertiesFile = project.getFile(PROPERTIES_FILE);
            if (propertiesFile.exists() && propertiesFile.isAccessible()) {
                final Reader reader = new InputStreamReader(propertiesFile.getContents());
                final Unmarshaller unmarshaller = new Unmarshaller(mapping);
                projectProperties = (ProjectPropertiesTO) unmarshaller.unmarshal(reader);
                reader.close();
            }
        } catch (MarshalException e) {
            throw new PropertiesException(e);
        } catch (ValidationException e) {
            throw new PropertiesException(e);
        } catch (IOException e) {
            throw new PropertiesException(e);
        } catch (MappingException e) {
            throw new PropertiesException(e);
        } catch (CoreException e) {
            throw new PropertiesException(e);
        }

        return projectProperties;
    }

    /**
     * Fill a properties information structure from a transfer object
     * 
     * @param projectProperties a project properties data structure
     * @param to a project properties transfer object
     */
    private void fillProjectProperties(IProjectProperties projectProperties, ProjectPropertiesTO to) throws PropertiesException,
            CoreException {
        if (to == null) {
            log.info("Project properties not found. Use default.");
        } else {
            final IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
            projectProperties.setProjectWorkingSet(workingSetManager.getWorkingSet(to.getWorkingSetName()));

            projectProperties.setRuleSetStoredInProject(to.isRuleSetStoredInProject());
            projectProperties.setRuleSetFile(to.getRuleSetFile());
            projectProperties.setPmdEnabled(projectProperties.getProject().hasNature(PMDNature.PMD_NATURE));
            projectProperties.setIncludeDerivedFiles(to.isIncludeDerivedFiles());

            if (to.isRuleSetStoredInProject()) {
                loadRuleSetFromProject(projectProperties);
            } else {
                setRuleSetFromProperties(projectProperties, to.getRules());
            }

            log.debug("Project properties loaded");
        }
    }

    /**
     * Set the rule set from rule specs found in properties file
     * 
     * @param rules array of selected rules
     */
    private void setRuleSetFromProperties(IProjectProperties projectProperties, RuleSpecTO[] rules) throws PropertiesException {
        final RuleSet ruleSet = new RuleSet();
        final RuleSet pluginRuleSet = PMDRuntimePlugin.getDefault().getPreferencesManager().getRuleSet();
        int n = rules==null?0:rules.length;
        for (int i = 0; i < n; i++) {
            try {
                final Rule rule = pluginRuleSet.getRuleByName(rules[i].getName());
                ruleSet.addRule(rule);
            } catch (RuntimeException e) {
                log.debug("The rule " + rules[i].getName() + " cannot be found. ignore.");
            }
        }
        ruleSet.addExcludePatterns(pluginRuleSet.getExcludePatterns());
        ruleSet.addIncludePatterns(pluginRuleSet.getIncludePatterns());

        projectProperties.setProjectRuleSet(ruleSet);
    }

    /**
     * Save project properties
     * 
     * @param project a project
     * @param projectProperties the project properties to save
     * @param monitor a progress monitor
     * @throws DAOException
     */
    private void writeProjectProperties(final IProject project, final ProjectPropertiesTO projectProperties)
            throws PropertiesException {
        try {
            LocalConfiguration.getInstance().getProperties().setProperty("org.exolab.castor.indent", "true");

            final Mapping mapping = new Mapping(this.getClass().getClassLoader());
            final URL mappingSpecUrl = this.getClass().getResource(PROPERTIES_MAPPING);
            mapping.loadMapping(mappingSpecUrl);

            final StringWriter writer = new StringWriter();
            final Marshaller marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
            marshaller.marshal(projectProperties);
            writer.flush();
            writer.close();

            final IFile propertiesFile = project.getFile(PROPERTIES_FILE);
            if (propertiesFile.exists() && propertiesFile.isAccessible()) {
                propertiesFile.setContents(new ByteArrayInputStream(writer.toString().getBytes()), false, false, null);
            } else {
                propertiesFile.create(new ByteArrayInputStream(writer.toString().getBytes()), false, null);
            }
        } catch (MarshalException e) {
            throw new PropertiesException(e);
        } catch (ValidationException e) {
            throw new PropertiesException(e);
        } catch (IOException e) {
            throw new PropertiesException(e);
        } catch (MappingException e) {
            throw new PropertiesException(e);
        } catch (CoreException e) {
            throw new PropertiesException(e);
        }
    }

    /**
     * Fill in a transfer object from a project properties information structure
     * 
     * @throws DAOException
     */
    private ProjectPropertiesTO fillTransferObject(IProjectProperties projectProperties) throws PropertiesException {
        final ProjectPropertiesTO bean = new ProjectPropertiesTO();
        bean.setRuleSetStoredInProject(projectProperties.isRuleSetStoredInProject());
        bean.setRuleSetFile(projectProperties.getRuleSetFile());
        bean.setWorkingSetName(projectProperties.getProjectWorkingSet() == null ? null : projectProperties.getProjectWorkingSet().getName());
        bean.setIncludeDerivedFiles(projectProperties.isIncludeDerivedFiles());

        if (!projectProperties.isRuleSetStoredInProject()) {
        	final RuleSet ruleSet = projectProperties.getProjectRuleSet();
            final List rules = new ArrayList();
            final Iterator i = ruleSet.getRules().iterator();
            while (i.hasNext()) {
                final Rule rule = (Rule) i.next();
                rules.add(new RuleSpecTO(rule.getName(), rule.getRuleSetName())); // NOPMD:AvoidInstantiatingObjectInLoop
            }
            bean.setRules((RuleSpecTO[]) rules.toArray(new RuleSpecTO[rules.size()]));
            bean.setExcludePatterns((String[])ruleSet.getExcludePatterns().toArray(new String[ruleSet.getExcludePatterns().size()]));
            bean.setIncludePatterns((String[])ruleSet.getIncludePatterns().toArray(new String[ruleSet.getIncludePatterns().size()]));
        }

        return bean;
    }

    /**
     * Check the project ruleset against the plugin ruleset and synchronize if
     * necessary
     * 
     * @return true if the project ruleset has changed.
     *  
     */
    private boolean synchronizeRuleSet(IProjectProperties projectProperties) throws PropertiesException {
        log.debug("Synchronizing the project ruleset with the plugin ruleset");
        final RuleSet pluginRuleSet = PMDRuntimePlugin.getDefault().getPreferencesManager().getRuleSet();
        final RuleSet projectRuleSet = projectProperties.getProjectRuleSet();
        boolean flChanged = false;

        if (!projectRuleSet.getRules().equals(pluginRuleSet.getRules())) {
            log.debug("The project ruleset is different from the plugin ruleset ; synchronizing.");
            
            // 1-If rules have been deleted from preferences
            // delete them also from the project ruleset
            final Iterator i = projectRuleSet.getRules().iterator();
            while (i.hasNext()) {
                final Rule projectRule = (Rule) i.next();
                final Rule pluginRule = pluginRuleSet.getRuleByName(projectRule.getName());
                if (pluginRule == null) {
                    log.debug("The rule " + projectRule.getName() + " is no more defined in the plugin ruleset. Remove it.");
                    i.remove();
                }
            }

            // 2-For all other rules, replace the current one by the plugin one
            final Iterator k = projectRuleSet.getRules().iterator();
            final RuleSet ruleSet = new RuleSet();
            ruleSet.setDescription(projectRuleSet.getDescription());
            ruleSet.setName(projectRuleSet.getName());
            while (k.hasNext()) {
                final Rule projectRule = (Rule) k.next();
                final Rule pluginRule = pluginRuleSet.getRuleByName(projectRule.getName());
                if (pluginRule != null) {
                    // log.debug("Keeping rule " + projectRule.getName());
                    ruleSet.addRule(pluginRule);
                }
            }

            if (!(ruleSet.getRules().equals(projectRuleSet.getRules()))) {
                log.info("Set the projet ruleset according to preferences.");
                projectProperties.setProjectRuleSet(ruleSet);
                flChanged = true;
            }

            log.debug("Ruleset for project " + projectProperties.getProject().getName() + " is now synchronized. " + (flChanged ? "Ruleset has changed" : "Ruleset has not changed"));
        }

        return flChanged;
    }
}
