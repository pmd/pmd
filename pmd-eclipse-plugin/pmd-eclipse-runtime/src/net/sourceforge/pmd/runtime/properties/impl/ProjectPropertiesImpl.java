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
package net.sourceforge.pmd.runtime.properties.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.runtime.PMDRuntimePlugin;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.runtime.properties.PropertiesException;
import net.sourceforge.pmd.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.runtime.writer.WriterException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSet;

/**
 * Implementation of a project properties information structure
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2007/06/24 16:42:13  phherlin
 * Fix 1703589 ConcurrentModificationException in RuleSet.apply
 *
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.14  2006/04/26 21:13:14  phherlin
 * Add the include derived files option
 *
 * Revision 1.13  2006/04/10 20:55:58  phherlin
 * Update to PMD 3.6
 *
 * Revision 1.12  2005/10/25 00:02:44  phherlin
 * Fix the update of the project rule set file.
 *
 * Revision 1.11  2005/10/24 23:53:51  phherlin
 * Fix "when enabling PMD, does not ask to rebuild or not the project".
 *
 * Revision 1.10  2005/10/24 23:19:58  phherlin
 * Fix never ending loop issue (finally..)
 *
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
public class ProjectPropertiesImpl implements IProjectProperties {
    private static final Logger log = Logger.getLogger(ProjectPropertiesImpl.class);

    private final IProjectPropertiesManager projectPropertiesManager;
    private final IProject project;
    private boolean needRebuild;
    private boolean pmdEnabled;
    private boolean ruleSetStoredInProject;
    private RuleSet projectRuleSet;
    private IWorkingSet projectWorkingSet;
    private boolean includeDerivedFiles;

    /**
     * The default constructor takes a project as an argument
     */
    public ProjectPropertiesImpl(final IProject project, IProjectPropertiesManager projectPropertiesManager) {
        super();
        this.project = project;
        this.projectPropertiesManager = projectPropertiesManager;
        this.projectRuleSet = PMDRuntimePlugin.getDefault().getPreferencesManager().getRuleSet();
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#isPmdEnabled()
     */
    public boolean isPmdEnabled()  {
        return this.pmdEnabled;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#setPmdEnabled(boolean)
     */
    public void setPmdEnabled(final boolean pmdEnabled) {
        log.debug("Enable PMD for project " + this.project.getName() + ": " + this.pmdEnabled);
        if (this.pmdEnabled != pmdEnabled) {
            this.pmdEnabled = pmdEnabled;
            this.needRebuild |= pmdEnabled;
        }
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#getProjectRuleSet()
     */
    public RuleSet getProjectRuleSet() throws PropertiesException {
        return cloneRuleSet();
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#setProjectRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setProjectRuleSet(final RuleSet projectRuleSet) throws PropertiesException {
        log.debug("Set a rule set for project " + this.project.getName());
        if (projectRuleSet == null) {
            throw new PropertiesException("Setting a project rule set to null"); // TODO NLS
        }

        this.needRebuild |= !this.projectRuleSet.getRules().equals(projectRuleSet.getRules());
        this.projectRuleSet = projectRuleSet;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#isRuleSetStoredInProject()
     */
    public boolean isRuleSetStoredInProject() {
        return this.ruleSetStoredInProject;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#setRuleSetStoredInProject(boolean)
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) throws PropertiesException {
        log.debug("Set rule set stored in project for project " + this.project.getName() + ": " + ruleSetStoredInProject);
        this.needRebuild |= this.ruleSetStoredInProject != ruleSetStoredInProject;
        this.ruleSetStoredInProject = ruleSetStoredInProject;
        if ((this.ruleSetStoredInProject) && (!isRuleSetFileExist())) {
            throw new PropertiesException("The project ruleset file cannot be found for project " + this.project.getName()); // TODO NLS
        }
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#getProjectWorkingSet()
     */
    public IWorkingSet getProjectWorkingSet() {
        return this.projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#setProjectWorkingSet(org.eclipse.ui.IWorkingSet)
     */
    public void setProjectWorkingSet(final IWorkingSet projectWorkingSet) {
        log.debug("Set working set for project " + this.project.getName() + ": "
                + (projectWorkingSet == null ? "none" : projectWorkingSet.getName()));

        this.needRebuild |= (this.projectWorkingSet == null)?(projectWorkingSet != null):!this.projectWorkingSet.equals(projectWorkingSet);
        this.projectWorkingSet = projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#isNeedRebuild()
     */
    public boolean isNeedRebuild() {
        log.debug("Query if project " + this.project.getName() + " need rebuild : " + (this.pmdEnabled && this.needRebuild));
        log.debug("   PMD Enabled = " + this.pmdEnabled);
        log.debug("   Project need rebuild = " + this.needRebuild);
        return this.pmdEnabled && this.needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#setNeedRebuild()
     */
    public void setNeedRebuild(final boolean needRebuild) {
        log.debug("Set if rebuild is needed for project " + this.project.getName() + ": " + needRebuild);
        this.needRebuild = needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#isRuleSetFileExist()
     */
    public final boolean isRuleSetFileExist() {
        final IFile file = this.project.getFile(ProjectPropertiesManagerImpl.PROJECT_RULESET_FILE);
        return file.exists() && file.isAccessible();
    }
    
    /**
     * Create a project ruleset file from the current configured rules
     *
     */
    public void createDefaultRuleSetFile() throws PropertiesException {
        log.info("Create a default rule set file for project " + this.project.getName());
        try {
            final IRuleSetWriter writer = PMDRuntimePlugin.getDefault().getRuleSetWriter();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writer.write(baos, this.projectRuleSet);
            baos.close();
            
            final IFile file = this.project.getFile(ProjectPropertiesManagerImpl.PROJECT_RULESET_FILE);
            if (file.exists() && file.isAccessible()) {
                throw new PropertiesException("Project ruleset file already exists");
            } else {
                final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());            
                file.create(bais, true, null);
                bais.close();
            }
        } catch (WriterException e) {
            throw new PropertiesException(e);
        } catch (IOException e) {
            throw new PropertiesException(e);
        } catch (CoreException e) {
            throw new PropertiesException(e);
        }
        
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#isIncludeDerivedFiles()
     */
    public boolean isIncludeDerivedFiles() {
        return this.includeDerivedFiles;
    }

    /**
     * @see net.sourceforge.pmd.runtime.properties.IProjectProperties#setIncludeDerivedFiles(boolean)
     */
    public void setIncludeDerivedFiles(boolean includeDerivedFiles) {
        log.debug("Set if derived files should be included: " + includeDerivedFiles);
        this.needRebuild |= this.includeDerivedFiles != includeDerivedFiles;
        this.includeDerivedFiles = includeDerivedFiles;        
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PMDPluginModel#sync()
     */
    public void sync() throws PropertiesException {
        log.info("Commit properties for project " + this.project.getName());
        this.projectPropertiesManager.storeProjectProperties(this);
    }
    
    /**
     * Clone the PMD ruleset.
     * @return a pmd ruleSetClone.
     */
    private RuleSet cloneRuleSet() {
        final RuleSet clonedRuleSet = new RuleSet();
        
        for (final Iterator i = this.projectRuleSet.getRules().iterator(); i.hasNext();) {
            final Rule rule = (Rule) i.next();
            clonedRuleSet.addRule(rule);
        }
        
        return clonedRuleSet;
    }

}