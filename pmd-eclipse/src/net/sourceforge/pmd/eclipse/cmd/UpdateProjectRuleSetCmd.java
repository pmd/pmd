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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDEclipseException;
import net.sourceforge.pmd.eclipse.RuleSetWriter;
import net.sourceforge.pmd.eclipse.WriterAbstractFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Update a project ruleset. The ruleset are stored in the plugin properties
 * store.
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
public class UpdateProjectRuleSetCmd extends AbstractDefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.UpdateProjectRuleSetCmd");
    private IProject project;
    private RuleSet projectRuleSet;
    private boolean needRebuild;
    
    /**
     * Default constructor. Initializes command attributes
     *
     */
    public UpdateProjectRuleSetCmd() {
        super();
        setReadOnly(false);
        setOutputProperties(true);
        setName("UpdateProjectRuleSet");
        setDescription("Update a project ruleset.");
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {

        // Before updating, query the current ruleset
        final QueryProjectRuleSetCmd queryCmd = new QueryProjectRuleSetCmd();
        queryCmd.setProject(this.project);
        queryCmd.execute();
        
        // Now store the ruleset
        try {
            final IFile ruleSetFile = this.project.getFile(".ruleset");
            final OutputStream out = new FileOutputStream(ruleSetFile.getLocation().toOSString());
            final RuleSetWriter writer = WriterAbstractFactory.getFactory().getRuleSetWriter();
            writer.write(out, this.projectRuleSet);
            out.flush();
            out.close();
            ruleSetFile.refreshLocal(IResource.DEPTH_INFINITE, this.getMonitor());
            
            log.debug("Storing ruleset for project " + this.project.getName());
            this.project.setSessionProperty(SESSION_PROPERTY_ACTIVE_RULESET, this.projectRuleSet);
            
            this.needRebuild = this.checkIfRebuidNeeded(queryCmd.getProjectRuleSet(), this.projectRuleSet);

        } catch (CoreException e) {
            throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_STORING_PROPERTY), e);
        } catch (FileNotFoundException e) {
            throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_STORING_PROPERTY), e);
        } catch (PMDEclipseException e) {
            throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_STORING_PROPERTY), e);
        } catch (IOException e) {
            throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_STORING_PROPERTY), e);
        }
    }

    /**
     * @param project The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
    }
    
    /**
     * @param projectRuleSet The projectRuleSet to set.
     */
    public void setProjectRuleSet(final RuleSet projectRuleSet) {
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
        this.setProject(null);
        this.setProjectRuleSet(null);
    }
    
    /**
     * Check if a project really needs to be rebuilt
     * @param oldRuleSet the old project ruleset
     * @param newRuleSet the new project ruleset
     * @return if the project needs to be rebuilt
     */
    private boolean checkIfRebuidNeeded(final RuleSet oldRuleSet, final RuleSet newRuleSet) {
        boolean needRebuild = false;

        if (oldRuleSet == null) {
            needRebuild = true;
        } else {
            
            // 1-if a rule has been deselected
            final Iterator i = oldRuleSet.getRules().iterator();
            final Set selectedRules = newRuleSet.getRules();
            while ((i.hasNext()) && (!needRebuild)) {
                final Rule rule = (Rule) i.next();
                if (!selectedRules.contains(rule)) {
                    needRebuild = true;
                }
            }
            
            // 2-if a rule has been selected
            final Iterator j = newRuleSet.getRules().iterator();
            final Set previousRules = oldRuleSet.getRules();
            while ((j.hasNext()) && (!needRebuild)) {
                final Rule rule = (Rule) j.next();
                if (!previousRules.contains(rule)) {
                    needRebuild = true;
                }
            }
        }
        
        return needRebuild;
    }
}
