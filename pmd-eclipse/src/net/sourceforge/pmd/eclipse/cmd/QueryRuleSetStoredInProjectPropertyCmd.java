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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Query whether a project ruleset is stored in the project instead of the
 * plugin properties store 
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
public class QueryRuleSetStoredInProjectPropertyCmd extends DefaultCommand {
    private IProject project;
    private boolean ruleSetStoredInProject;
    
    /**
     * Default constructor. Initializes command attributes
     *
     */
    public QueryRuleSetStoredInProjectPropertyCmd() {
        setReadOnly(true);
        setOutputProperties(true);
        setName("QueryRuleSetStoredInProjectProperty");
        setDescription("Query whether a project rule set is stored in a project instead of the plugin properties store.");
    }

    /**
     * @see name.herlin.command.ProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        Boolean booleanProperty = Boolean.FALSE;
        boolean flSaveInSession = false;

        try {
            booleanProperty = (Boolean) this.project.getSessionProperty(SESSION_PROPERTY_STORE_RULESET_PROJECT);
            if (booleanProperty == null) {
                String stringProperty = this.project.getPersistentProperty(PERSISTENT_PROPERTY_STORE_RULESET_PROJECT);
                if (stringProperty != null) {
                    booleanProperty = new Boolean(stringProperty);
                    flSaveInSession = true;
                }
            }

            // If needed store modified ruleset
            if (flSaveInSession) {
                this.project.setSessionProperty(SESSION_PROPERTY_STORE_RULESET_PROJECT, booleanProperty);
            }

            this.ruleSetStoredInProject = booleanProperty == null ? false : booleanProperty.booleanValue();

        } catch (CoreException e) {
            throw new CommandException("Error when searching for the store_ruleset_project property. Assuming the project doesn't store it's own ruleset", e);
        }
    }

    /**
     * @return Returns the ruleSetStoredInProject.
     */
    public boolean isRuleSetStoredInProject() {
        return ruleSetStoredInProject;
    }
    
    /**
     * @param project The project to set.
     */
    public void setProject(IProject project) {
        this.project = project;
        setReadyToExecute(true);
    }
    
    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.project = null;
        setReadyToExecute(false);
    }
}
