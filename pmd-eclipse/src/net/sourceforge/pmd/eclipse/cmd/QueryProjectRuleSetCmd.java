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

import name.herlin.command.CommandException;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.eclipse.PMDConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * Query the rule set configured for a specific project.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
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
public class QueryProjectRuleSetCmd extends AbstractDefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.QueryProjectRuleSetCmd");
    private IProject project;
    private RuleSet projectRuleSet;
    
    /**
     * Default constructor
     *
     */
    public QueryProjectRuleSetCmd() {
        super();
        setReadOnly(true);
        setOutputProperties(true);
        setName("QueryProjectRuleSet");
        setDescription("Query a project rule set.");
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        this.getRuleSetFromProject();
    }

    /**
     * @param project The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
        setReadyToExecute(project != null);
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
        this.setProject(null);
    }

    /**
     * Retrieve a project ruleset from a ruleset file in the project instead of
     * the plugin properties/preferences
     */
    private void getRuleSetFromProject() throws CommandException {
        log.debug("Searching a ruleset for project " + this.project.getName() + " in the project file");
        final IFile ruleSetFile = this.project.getFile(".ruleset");
        if (ruleSetFile.exists()) {
            try {
                this.projectRuleSet = (RuleSet) this.project.getSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET);
                final Long oldModificationStamp = (Long) this.project.getSessionProperty(SESSION_PROPERTY_RULESET_MODIFICATION_STAMP);
                final long newModificationStamp = ruleSetFile.getModificationStamp();
                if ((oldModificationStamp == null) || (oldModificationStamp.longValue() != newModificationStamp)) {
                    final RuleSetFactory ruleSetFactory = new RuleSetFactory();
                    this.projectRuleSet = ruleSetFactory.createRuleSet(ruleSetFile.getContents());
                    this.project.setSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET, this.projectRuleSet);
                    this.project.setSessionProperty(SESSION_PROPERTY_RULESET_MODIFICATION_STAMP, new Long(newModificationStamp));
                }
            } catch (Exception e) {
                throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_LOADING_RULESET), e);
            }
        }
    }
}
