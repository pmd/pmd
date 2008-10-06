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
package net.sourceforge.pmd.eclipse.runtime.properties;

import java.io.File;

import net.sourceforge.pmd.RuleSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkingSet;

/**
 * This interface specifies what is the model for the PMD related project
 * properties
 * 
 * @author Philippe Herlin
 *
 */
public interface IProjectProperties {
    /**
     * @return the related project
     */
    IProject getProject();
    
    /**
     * @return Returns whether PMD is enabled for that project.
     */
    boolean isPmdEnabled() throws PropertiesException;
    
    /**
     * @param pmdEnabled Enable or disable PMD for that project.
     */
    void setPmdEnabled(boolean pmdEnabled) throws PropertiesException;

    /**
     * @return Returns the project Rule Set.
     */
    RuleSet getProjectRuleSet() throws PropertiesException;

    /**
     * @param projectRuleSet The project Rule Set to set.
     */
    void setProjectRuleSet(RuleSet projectRuleSet) throws PropertiesException;

    /**
     * @return Returns the whether the project rule set is stored as a file
     * inside the project.
     */
    boolean isRuleSetStoredInProject() throws PropertiesException;

    /**
     * @param ruleSetStoredInProject Specify whether the rule set is stored in
     * the project.
     */
    void setRuleSetStoredInProject(boolean ruleSetStoredInProject) throws PropertiesException;

    /**
     * @return Returns the rule set file.
     */
    String getRuleSetFile() throws PropertiesException;

    /**
     * @param ruleSetFile The rule set file.
     */
    void setRuleSetFile(String ruleSetFile) throws PropertiesException;
    
    /**
     * @return Returns the resolved RuleSet File suitable for loading a rule set.
     */
    File getResolvedRuleSetFile() throws PropertiesException;

    /**
     * @return Returns the project Working Set.
     */
    IWorkingSet getProjectWorkingSet() throws PropertiesException;

    /**
     * @param projectWorkingSet The project Working Set to set.
     */
    void setProjectWorkingSet(IWorkingSet projectWorkingSet) throws PropertiesException;

    /**
     * @return whether the project needs to be rebuilt.
     */
    boolean isNeedRebuild() throws PropertiesException;

    /**
     * Let force the rebuild state of a project.
     */
    void setNeedRebuild(boolean needRebuild) throws PropertiesException;

    /**
     * @return in case the rule set is stored inside the project, whether
     * the ruleset file exists.
     */
    boolean isRuleSetFileExist() throws PropertiesException;
    
    /**
     * Create a project ruleset file from the current configured rules
     *
     */
    void createDefaultRuleSetFile() throws PropertiesException;
    
    /**
     * @return whether derived files should be checked
     */
    boolean isIncludeDerivedFiles() throws PropertiesException;
    
    /**
     * @param excludeDerivedFiles whether derived files should be checked
     */
    void setIncludeDerivedFiles(boolean excludeDerivedFiles) throws PropertiesException;
    
    /**
     * Synchronize the properties with the persistant store
     *
     */
    void sync() throws PropertiesException;
    
}
