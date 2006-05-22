/*
 * Created on 20 nov. 2004
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
package net.sourceforge.pmd.ui.properties;

import net.sourceforge.pmd.RuleSet;

import org.eclipse.ui.IWorkingSet;

/**
 * This class is a bean that hold the property page data.
 * It acts as the model in the MVC paradigm. 
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:23:58  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.3  2006/04/26 21:16:06  phherlin
 * Add the include derived files option
 *
 * Revision 1.2  2005/05/07 13:32:05  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.1  2004/11/28 20:31:38  phherlin
 * Continuing the refactoring experiment
 *
 * Revision 1.1  2004/11/21 21:38:42  phherlin
 * Continue applying MVC.
 *
 *
 */
public class PMDPropertyPageBean {
    private boolean pmdEnabled;
    private IWorkingSet projectWorkingSet;
    private RuleSet projectRuleSet;
    private boolean ruleSetStoredInProject;
    private boolean includeDerivedFiles;
    
    /**
     * @return Returns the pmdEnabled.
     */
    public boolean isPmdEnabled() {
        return pmdEnabled;
    }
    
    /**
     * @param pmdEnabled The pmdEnabled to set.
     */
    public void setPmdEnabled(final boolean pmdEnabled) {
        this.pmdEnabled = pmdEnabled;
    }
    
    /**
     * @return Returns the projectRuleSet.
     */
    public RuleSet getProjectRuleSet() {
        return projectRuleSet;
    }
    
    /**
     * @param projectRuleSet The projectRuleSet to set.
     */
    public void setProjectRuleSet(final RuleSet projectRuleSet) {
        this.projectRuleSet = projectRuleSet;
    }
    
    /**
     * @return Returns the ruleSetStoredInProject.
     */
    public boolean isRuleSetStoredInProject() {
        return ruleSetStoredInProject;
    }
    
    /**
     * @param ruleSetStoredInProject The ruleSetStoredInProject to set.
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) {
        this.ruleSetStoredInProject = ruleSetStoredInProject;
    }
    
    /**
     * @return Returns the projectWorkingSet.
     */
    public IWorkingSet getProjectWorkingSet() {
        return projectWorkingSet;
    }
    
    /**
     * @param projectWorkingSet The projectWorkingSet to set.
     */
    public void setProjectWorkingSet(final IWorkingSet selectedWorkingSet) {
        this.projectWorkingSet = selectedWorkingSet;
    }
    
    /**
     * @return Returns the includeDerivedFiles.
     */
    public boolean isIncludeDerivedFiles() {
        return includeDerivedFiles;
    }
    
    /**
     * @param includeDerivedFiles The includeDerivedFiles to set.
     */
    public void setIncludeDerivedFiles(boolean includeDerivedFiles) {
        this.includeDerivedFiles = includeDerivedFiles;
    }
}
