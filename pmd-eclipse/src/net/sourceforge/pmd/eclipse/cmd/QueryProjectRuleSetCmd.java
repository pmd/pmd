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

import java.util.Iterator;
import java.util.StringTokenizer;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Query the rule set configured for a specific project.
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
public class QueryProjectRuleSetCmd extends DefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.QueryProjectRuleSetCmd");
    private IProject project;
    private boolean fromProperties;
    private RuleSet projectRuleSet;
    
    /**
     * Default constructor
     *
     */
    public QueryProjectRuleSetCmd() {
        setReadOnly(true);
        setOutputProperties(true);
        setName("QueryProjectRuleSet");
        setDescription("Query a project rule set.");
    }

    /**
     * @see name.herlin.command.ProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        QueryRuleSetStoredInProjectPropertyCmd queryRuleSetStoredInProjectCmd = new QueryRuleSetStoredInProjectPropertyCmd();
        queryRuleSetStoredInProjectCmd.setProject(this.project);
        queryRuleSetStoredInProjectCmd.performExecute();
        boolean ruleSetStoredInProject = queryRuleSetStoredInProjectCmd.isRuleSetStoredInProject();

        if ((this.fromProperties) || (!ruleSetStoredInProject)) {
            getRuleSetFromProperties();
        } else {
            getRuleSetFromProject();
        }
    }

    /**
     * @param project The project to set.
     */
    public void setProject(IProject project) {
        this.project = project;
        setReadyToExecute(true);
    }
    
    /**
     * @param fromProperties The fromProperties to set.
     */
    public void setFromProperties(boolean fromProperties) {
        this.fromProperties = fromProperties;
    }

    /**
     * @return Returns the projectRuleSet.
     */
    public RuleSet getProjectRuleSet() {
        return this.projectRuleSet;
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.project = null;
        setReadyToExecute(false);
    }

    /**
     * Get the rulset configured for the project
     */
    private void getRuleSetFromProperties() throws CommandException {
        log.debug("Searching a ruleset for project " + this.project.getName() + " in properties");
        boolean flSaveInSession = false;
        RuleSet configuredRuleSet = PMDPlugin.getDefault().getRuleSet();

        try {
            // Try to load the ruleset from the session properties
            // If not available query the rules list from presistent store and 
            // ask the instantiation of a RuleSet object from that list
            this.projectRuleSet = (RuleSet) this.project.getSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET);
            if (this.projectRuleSet == null) {
                String activeRulesList = this.project.getPersistentProperty(PERSISTENT_PROPERTY_ACTIVE_RULESET);
                if (activeRulesList != null) {
                    this.projectRuleSet = getRuleSetFromRuleList(activeRulesList);
                    flSaveInSession = true;
                }
            }

            // If meanwhile, rules have been deleted from preferences
            // delete them also from the project ruleset
            if ((this.projectRuleSet != null) && (this.projectRuleSet != configuredRuleSet)) {
                Iterator i = this.projectRuleSet.getRules().iterator();
                while (i.hasNext()) {
                    Object rule = i.next();
                    if (!configuredRuleSet.getRules().contains(rule)) {
                        i.remove();
                        flSaveInSession = true;
                    }
                }
            }

            // If needed store modified ruleset in session properties
            if (flSaveInSession) {
                this.project.setSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET, this.projectRuleSet);
            }
            
        } catch (CoreException e) {
            throw new CommandException("Error when searching for project ruleset. Using the full ruleset.", e);
        }
    }

    /**
     * Retrieve a project ruleset from a ruleset file in the project instead of
     * the plugin properties/preferences
     */
    private void getRuleSetFromProject() throws CommandException {
        log.debug("Searching a ruleset for project " + this.project.getName() + " in the project file");
        IFile ruleSetFile = this.project.getFile(".ruleset");
        if (ruleSetFile.exists()) {
            try {
                this.projectRuleSet = (RuleSet) this.project.getSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET);
                Long oldModificationStamp = (Long) this.project.getSessionProperty(SESSION_PROPERTY_RULESET_MODIFICATION_STAMP);
                long newModificationStamp = ruleSetFile.getModificationStamp();
                if ((oldModificationStamp == null) || (oldModificationStamp.longValue() != newModificationStamp)) {
                    RuleSetFactory ruleSetFactory = new RuleSetFactory();
                    this.projectRuleSet = ruleSetFactory.createRuleSet(ruleSetFile.getContents());
                    this.project.setSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET, this.projectRuleSet);
                    this.project.setSessionProperty(SESSION_PROPERTY_RULESET_MODIFICATION_STAMP, new Long(newModificationStamp));
                }
            } catch (Exception e) {
                throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_LOADING_RULESET), e);
            }
        }
    }

    /**
     * Get a sub ruleset from a rule list
     * @param ruleList a list composed of rule names seperated by a delimiter
     * @return a rule set composed only of the rules imported in the plugin
     */
    private RuleSet getRuleSetFromRuleList(String ruleList) {
        RuleSet subRuleSet = new RuleSet();
        RuleSet ruleSet = PMDPlugin.getDefault().getRuleSet();

        StringTokenizer st = new StringTokenizer(ruleList, LIST_DELIMITER);
        while (st.hasMoreTokens()) {
            try {
                Rule rule = ruleSet.getRuleByName(st.nextToken());
                if (rule != null) {
                    subRuleSet.addRule(rule);
                }
            } catch (RuntimeException e) {
                PMDPlugin.getDefault().logError("Ignored runtime exception from PMD : ", e);
            }
        }

        return subRuleSet;
    }
}
