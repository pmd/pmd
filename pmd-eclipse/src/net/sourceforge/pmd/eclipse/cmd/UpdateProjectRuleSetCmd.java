/*
 * Created on 21 nov. 2004
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

import java.util.Iterator;
import java.util.Set;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Update a project ruleset. The ruleset are stored in the plugin properties
 * store.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2004/12/03 00:22:42  phherlin
 * Continuing the refactoring experiment.
 * Implement the Command framework.
 * Refine the MVC pattern usage.
 *
 * Revision 1.1  2004/11/21 21:39:45  phherlin
 * Applying Command and CommandProcessor patterns
 *
 *
 */
public class UpdateProjectRuleSetCmd extends DefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.UpdateProjectRuleSetCmd");
    private IProject project;
    private RuleSet projectRuleSet;
    private boolean needRebuild;
    
    /**
     * Default constructor. Initializes command attributes
     *
     */
    public UpdateProjectRuleSetCmd() {
        setReadOnly(false);
        setOutputProperties(true);
        setName("UpdateProjectRuleSet");
        setDescription("Update a project ruleset.");
    }

    /**
     * @see name.herlin.command.ProcessableCommand#execute()
     */
    public void execute() throws CommandException {

        // Before updating, query the current ruleset
        QueryProjectRuleSetCmd queryCmd = new QueryProjectRuleSetCmd();
        queryCmd.setProject(this.project);
        queryCmd.setFromProperties(true);
        queryCmd.execute();
        
        // Now store the ruleset
        try {
            StringBuffer ruleSelectionList = new StringBuffer();
            Iterator i = this.projectRuleSet.getRules().iterator();
            while (i.hasNext()) {
                Rule rule = (Rule) i.next();
                ruleSelectionList.append(rule.getName()).append(LIST_DELIMITER);
            }
            
            log.debug("Storing ruleset for project " + this.project.getName());
            this.project.setPersistentProperty(PERSISTENT_PROPERTY_ACTIVE_RULESET, ruleSelectionList.toString());
            log.debug("   list : " + ruleSelectionList.toString());
            this.project.setSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET, this.projectRuleSet);

        } catch (CoreException e) {
            throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_STORING_PROPERTY), e);
        }
        
        // Finally, compare to the initial list whether the ruleset has really changed
        if (queryCmd.getProjectRuleSet() != null) {
            
            // 1-if a rule has been deselected
            Iterator i = queryCmd.getProjectRuleSet().getRules().iterator();
            Set selectedRules = this.projectRuleSet.getRules();
            while ((i.hasNext()) && (!this.needRebuild)) {
                Rule rule = (Rule) i.next();
                if (!selectedRules.contains(rule)) {
                    this.needRebuild = true;
                }
            }
            
            // 1-if a rule has been selected
            i = this.projectRuleSet.getRules().iterator();
            Set previousRules = queryCmd.getProjectRuleSet().getRules();
            while ((i.hasNext()) && (!this.needRebuild)) {
                Rule rule = (Rule) i.next();
                if (!previousRules.contains(rule)) {
                    this.needRebuild = true;
                }
            }
        } else {
            this.needRebuild = true;
        }
    }

    /**
     * @param project The project to set.
     */
    public void setProject(IProject project) {
        this.project = project;
    }
    
    /**
     * @param projectRuleSet The projectRuleSet to set.
     */
    public void setProjectRuleSet(RuleSet projectRuleSet) {
        this.projectRuleSet = projectRuleSet;
    }
    
    /**
     * @return Returns the needRebuild.
     */
    public boolean isNeedRebuild() {
        return needRebuild;
    }
    
    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return (this.project != null) && (this.projectRuleSet != null);
    }
    
    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.project = null;
        this.projectRuleSet = null;
    }
}
