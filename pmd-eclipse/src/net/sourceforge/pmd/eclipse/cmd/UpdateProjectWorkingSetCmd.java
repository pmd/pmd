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

import net.sourceforge.pmd.eclipse.PMDConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSet;

/**
 * Update a project working set.
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
public class UpdateProjectWorkingSetCmd extends DefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.UpdateProjectWorkingSetCmd");
    private IProject project;
    private IWorkingSet projectWorkingSet;
    private boolean needRebuild;

    /**
     * Default constructor. Initialize command attributes. 
     */
    public UpdateProjectWorkingSetCmd() {
        setReadOnly(false);
        setOutputData(true);
        setName("UpdateProjectWorkingSet");
        setDescription("Update a project working set.");
    }

    /**
     * @see net.sourceforge.pmd.eclipse.cmd.DefaultCommand#execute()
     */
    protected void execute() throws CommandException {
        if (this.project == null) throw new MandatoryInputParameterMissingException("project");
        log.debug("Set the working set " + this.projectWorkingSet + " for project " + this.project.getName());
        
        // First query the previous active working set
        QueryProjectWorkingSetCmd queryCmd = new QueryProjectWorkingSetCmd();
        queryCmd.setProject(this.project);
        queryCmd.execute();

        // Then, store the property
        try {
            this.project.setPersistentProperty(PERSISTENT_PROPERTY_WORKINGSET, this.projectWorkingSet == null ? null : this.projectWorkingSet.getName());
            this.project.setSessionProperty(SESSION_PROPERTY_WORKINGSET, this.projectWorkingSet);

        } catch (CoreException e) {
            throw new CommandException(getMessage(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION), e);
        }
        
        // Now, check whether the property has changed to know if rebuild is necessary
        boolean bothNotNull = (queryCmd.getProjectWorkingSet() != null) && (this.projectWorkingSet != null);
        log.debug("both working set are not null : " + bothNotNull);
        
        boolean fl1 = (queryCmd.getProjectWorkingSet() == null) && (this.projectWorkingSet !=null);
        log.debug("no previous working set, new one selected : " + fl1);
        
        boolean fl2 = (queryCmd.getProjectWorkingSet() != null) && (this.projectWorkingSet ==null);
        log.debug("previous working set selected, no working set selected now : " + fl2);
        
        this.needRebuild = fl1 | fl2 | (bothNotNull && (!queryCmd.getProjectWorkingSet().getName().equals(this.projectWorkingSet.getName())));
        log.debug("project need to be rebuild : " + this.needRebuild);
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
    public void setProject(IProject project) {
        this.project = project;
    }
    
    /**
     * @param projectWorkingSet The projectWorkingSet to set.
     */
    public void setProjectWorkingSet(IWorkingSet projectWorkingSet) {
        this.projectWorkingSet = projectWorkingSet;
    }
}
