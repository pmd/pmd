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
package net.sourceforge.pmd.eclipse.cmd;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkingSet;

/**
 * This command retrieves the PMD specific properties of a particular
 * workbench project. This is a composite basic command.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2004/11/21 21:39:45  phherlin
 * Applying Command and CommandProcessor patterns
 *
 *
 */
public class QueryProjectPropertiesCmd extends DefaultCommand {
    private IProject project;
    private boolean pmdEnabled;
    private IWorkingSet projectWorkingSet;
    private RuleSet projectRuleSet;
    private boolean ruleSetStoredInProject;
    
    /**
     * Default constructor. Initializes command attributes
     *
     */
    public QueryProjectPropertiesCmd() {
        setReadOnly(true);
        setOutputData(true);
        setName("QueryProjectProperties");
        setDescription("Query the PMD specific properties of a particular workbench project.");
    }

    /**
     * @see net.sourceforge.pmd.eclipse.cmd.Command#performExecute()
     */
    protected void execute() throws CommandException {
        if (this.project == null) throw new MandatoryInputParameterMissingException("project");
        
        QueryPmdEnabledPropertyCmd queryPmdEnabledCmd = new QueryPmdEnabledPropertyCmd();
        queryPmdEnabledCmd.setProject(this.project);
        queryPmdEnabledCmd.performExecute();
        this.pmdEnabled = queryPmdEnabledCmd.isPmdEnabled();
        
        QueryProjectWorkingSetCmd queryProjectWorkingSetCmd = new QueryProjectWorkingSetCmd();
        queryProjectWorkingSetCmd.setProject(this.project);
        queryProjectWorkingSetCmd.performExecute();
        this.projectWorkingSet = queryProjectWorkingSetCmd.getProjectWorkingSet();
        
        QueryProjectRuleSetCmd queryProjectRuleSetCmd = new QueryProjectRuleSetCmd();
        queryProjectRuleSetCmd.setProject(this.project);
        queryProjectRuleSetCmd.setFromProperties(true);
        queryProjectRuleSetCmd.performExecute();
        this.projectRuleSet = queryProjectRuleSetCmd.getProjectRuleSet();
        if (this.projectRuleSet == null) {
            this.projectRuleSet = PMDPlugin.getDefault().getRuleSet();
        }
        
        QueryRuleSetStoredInProjectPropertyCmd queryRuleSetStoredInProjectCmd = new QueryRuleSetStoredInProjectPropertyCmd();
        queryRuleSetStoredInProjectCmd.setProject(this.project);
        queryRuleSetStoredInProjectCmd.performExecute();
        this.ruleSetStoredInProject = queryRuleSetStoredInProjectCmd.isRuleSetStoredInProject();
    }
    
    /**
     * Mandatory input parameter.
     * Specify the project to query.
     * @param project
     */
    public void setProject(IProject project) {
        this.project = project;
    }
    
    /**
     * @return whether PMD is activated for that project
     */
    public boolean isPMDEnabled() {
        return this.pmdEnabled;
    }
    
    /**
     * @return the project working set
     */
    public IWorkingSet getProjectWorkingSet() {
        return this.projectWorkingSet;
    }
    
    /**
     * @return the project rule set
     */
    public RuleSet getProjectRuleSet() {
        return this.projectRuleSet;
    }
    
    /**
     * @return whether the project rule set is stored inside the project
     * instead of the plugin properties store
     */
    public boolean isRuleSetStoredInProject() {
        return this.ruleSetStoredInProject;
    }
}
