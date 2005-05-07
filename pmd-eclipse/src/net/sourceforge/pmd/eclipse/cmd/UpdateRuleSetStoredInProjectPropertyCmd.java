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

import name.herlin.command.CommandException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Update whether a project rule set is stored in a project file instead of
 * inside the plugin properties store.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.3  2004/12/03 00:22:42  phherlin
 * Continuing the refactoring experiment.
 * Implement the Command framework.
 * Refine the MVC pattern usage.
 *
 * Revision 1.2  2004/11/28 20:31:37  phherlin
 * Continuing the refactoring experiment
 *
 * Revision 1.1  2004/11/21 21:39:45  phherlin
 * Applying Command and CommandProcessor patterns
 *
 *
 */
public class UpdateRuleSetStoredInProjectPropertyCmd extends AbstractDefaultCommand {
    private IProject project;
    private boolean ruleSetStoredInProject;
    private boolean ruleSetFileExists;
    private boolean needRebuild;
    
    /**
     * Default constructor. Initializes command attributes
     *
     */
    public UpdateRuleSetStoredInProjectPropertyCmd() {
        super();
        setReadOnly(false);
        setOutputProperties(true);
        setName("UpdateRuleSetStoredInProjectProperty");
        setDescription("Update whether a project rule set is stored in a project file.");
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        
        // First query the current value
        final QueryRuleSetStoredInProjectPropertyCmd queryCmd = new QueryRuleSetStoredInProjectPropertyCmd();
        queryCmd.setProject(this.project);
        queryCmd.execute();
        
        // Then, update the property
        try {
            final Boolean property = Boolean.valueOf(this.ruleSetStoredInProject);
            this.project.setPersistentProperty(PERSISTENT_PROPERTY_STORE_RULESET_PROJECT, property.toString());
            this.project.setSessionProperty(SESSION_PROPERTY_STORE_RULESET_PROJECT, property);
            if (this.ruleSetStoredInProject) {
                this.project.setSessionProperty(SESSION_PROPERTY_RULESET_MODIFICATION_STAMP, null);
            }

            final IFile ruleSetFile = this.project.getFile(".ruleset");
            this.ruleSetFileExists = ruleSetFile.exists();
            
        } catch (CoreException e) {
            throw new CommandException(getMessage(MSGKEY_ERROR_CORE_EXCEPTION), e);
        }
        
        // Now, check if a rebuild is necessary
        this.needRebuild = queryCmd.isRuleSetStoredInProject() != this.ruleSetStoredInProject;
    }

    /**
     * @return Returns the ruleSetFileExists.
     */
    public boolean isRuleSetFileExists() {
        return ruleSetFileExists;
    }

    /**
     * @return Returns the needRebuild.
     */
    public boolean isNeedRebuild() {
        return needRebuild;
    }
    
    /**
     * @param project The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
        setReadyToExecute(project != null);
    }
    
    /**
     * @param ruleSetStoredInProject The ruleSetStoredInProject to set.
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) {
        this.ruleSetStoredInProject = ruleSetStoredInProject;
    }
    
    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.setProject(null);
        this.setRuleSetStoredInProject(false);
        setReadyToExecute(false);
    }
}
