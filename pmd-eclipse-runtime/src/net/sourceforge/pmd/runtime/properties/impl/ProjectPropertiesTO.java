/*
 * Created on 28 mai 2005
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


/**
 * This class is a simple data bean to let simply serialize project properties
 * to an XML file (or any). 
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.2  2006/04/26 21:13:38  phherlin
 * Add the include derived files option
 *
 * Revision 1.1  2005/06/07 18:38:14  phherlin
 * Move classes to limit packages cycle dependencies
 *
 * Revision 1.1  2005/05/31 20:44:40  phherlin
 * Continuing refactoring
 *
 * Revision 1.1  2005/05/31 20:33:01  phherlin
 * Continuing refactoring
 *
 *
 */
public class ProjectPropertiesTO {
    private RuleSpecTO[] rules;
    private String workingSetName;
    private boolean ruleSetStoredInProject;
    private boolean includeDerivedFiles;
    
    /**
     * @return rules an array of RuleSpecTO objects that keep information of rules
     * selected for the current project
     */
    public RuleSpecTO[] getRules() {
        return rules;
    }
    
    /**
     * Set the rules selected for a project
     * @param rules an array of RuleSpecTO objects describing each select project
     * rules.
     */
    public void setRules(final RuleSpecTO[] rules) {
        this.rules = rules;
    }
    
    /**
     * @return ruleSetStoredInProject tells whether the project use a ruleset
     * stored in the project or the global plugin ruleset.
     */
    public boolean isRuleSetStoredInProject() {
        return ruleSetStoredInProject;
    }
    
    /**
     * Tells whether a project must use a ruleset stored in the project or the
     * global project ruleset.
     * @param ruleSetStoredInProject see above.
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) {
        this.ruleSetStoredInProject = ruleSetStoredInProject;
    }
    
    /**
     * @return workingSetName the name of the project workingSet
     */
    public String getWorkingSetName() {
        return workingSetName;
    }
    
    /**
     * Set the project working set name
     * @param workingSetName the name of the project working set
     */
    public void setWorkingSetName(final String workingSetName) {
        this.workingSetName = workingSetName;
    }

    /**
     * @return Returns the includeDerivedFiles.
     */
    public boolean isIncludeDerivedFiles() {
        return this.includeDerivedFiles;
    }

    /**
     * @param includeDerivedFiles The includeDerivedFiles to set.
     */
    public void setIncludeDerivedFiles(boolean includeDerivedFiles) {
        this.includeDerivedFiles = includeDerivedFiles;
    }

}
