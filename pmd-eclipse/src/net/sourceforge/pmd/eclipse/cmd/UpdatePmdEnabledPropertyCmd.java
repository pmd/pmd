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

import net.sourceforge.pmd.eclipse.builder.PMDNature;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Update whether PMD is enabled for a project or not.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2004/11/28 20:31:37  phherlin
 * Continuing the refactoring experiment
 *
 * Revision 1.1  2004/11/21 21:39:45  phherlin
 * Applying Command and CommandProcessor patterns
 *
 *
 */
public class UpdatePmdEnabledPropertyCmd extends DefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.UpdatePmdEnabledPropertyCmd");
    private IProject project;
    private boolean pmdEnabled;
    private boolean needRebuild;
    
    /**
     * Default constructor. Initializes command attributes
     *
     */
    public UpdatePmdEnabledPropertyCmd() {
        setReadOnly(false);
        setOutputData(true);
        setName("UpdatePmdEnabledProperty");
        setDescription("Update whether PMD is enabled for a project or not.");
    }

    /**
     * @see net.sourceforge.pmd.eclipse.cmd.DefaultCommand#execute()
     */
    protected void execute() throws CommandException {
        if (this.project == null) throw new CommandException("project");

        if (this.pmdEnabled) {
            addNature();
        } else {
            removeNature();
        }
    }

    /**
     * @param pmdEnabled The pmdEnabled to set.
     */
    public void setPmdEnabled(boolean pmdEnabled) {
        this.pmdEnabled = pmdEnabled;
    }
    
    /**
     * @param project The project to set.
     */
    public void setProject(IProject project) {
        this.project = project;
    }
    
    /**
     * @return Returns the needRebuild.
     */
    public boolean isNeedRebuild() {
        return this.needRebuild;
    }
    
    /**
     * Add a PMD nature to the project
     * @throws CommandException
     */
    private void addNature() throws CommandException {
        try {
            if (!this.project.hasNature(PMDNature.PMD_NATURE)) {
                log.info("Adding PMD nature to the project " + this.project.getName());

                IProjectDescription description = this.project.getDescription();
                String[] natureIds = description.getNatureIds();
                String[] newNatureIds = new String[natureIds.length + 1];
                System.arraycopy(natureIds, 0, newNatureIds, 0, natureIds.length);
                newNatureIds[natureIds.length] = PMDNature.PMD_NATURE;
                description.setNatureIds(newNatureIds);
                this.project.setDescription(description, null);
                this.needRebuild = true;
            }
        } catch (CoreException e) {
            throw new CommandException(getMessage(MSGKEY_ERROR_CORE_EXCEPTION), e);
        }
    }
    
    /**
     * Remove a PMD nature from the project
     * @throws CommandException
     */
    private void removeNature() throws CommandException {
        try {
            if (this.project.hasNature(PMDNature.PMD_NATURE)) {
                IProjectDescription description = this.project.getDescription();
                String[] natureIds = description.getNatureIds();
                String[] newNatureIds = new String[natureIds.length - 1];
                for (int i = 0, j = 0; i < natureIds.length; i++) {
                    if (!natureIds[i].equals(PMDNature.PMD_NATURE)) {
                        newNatureIds[j++] = natureIds[i];
                    }
                }
                description.setNatureIds(newNatureIds);
                this.project.setDescription(description, null);
                this.project.deleteMarkers(PMD_MARKER, true, IResource.DEPTH_INFINITE);
            }
        } catch (CoreException e) {
            throw new CommandException(getMessage(MSGKEY_ERROR_CORE_EXCEPTION), e);
        }
    }
}
