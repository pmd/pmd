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
import net.sourceforge.pmd.eclipse.PMDPluginConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This class is a default implementation for job commands. Job commands are
 * standard Eclipse Jobs and, therefore are executed under the control of the
 * Eclipse Job Processor which acts as a Command Processor.
 * Job commands are used for long running commands such as a buildin a project,
 * runing PMD against a project, or commands that perfoms ressource updates. 
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
public abstract class JobCommand extends Job implements Command, PMDConstants, PMDPluginConstants {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.JobCommand");
    private IProgressMonitor monitor;
    private boolean readOnly;
    private String description;
    private String name;
    private boolean outputData;
    
    /**
     * Default constructor for a job
     * @param name
     */
    public JobCommand(String name) {
        super(name);
    }

    /**
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }
    
    /**
     * @param readOnly The readOnly to set.
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return Returns the outputData.
     */
    public boolean hasOutputData() {
        return outputData;
    }
    
    /**
     * @param outputData The outputData to set.
     */
    public void setOutputData(boolean hasOutputData) {
        this.outputData = hasOutputData;
    }
    
    /**
     * @return the command default priority. The default is Job.SHORT.
     * Any concrete command may override this method to set a different priority.
     */
    public int getDefaultPriority() {
       return Job.SHORT;    
    }
    
    /**
     * @see net.sourceforge.pmd.eclipse.cmd.Command#performExecute()
     */
    public void performExecute() throws CommandException {
        log.debug("Begining a job command");
        try {
            this.setPriority(this.getDefaultPriority());
            this.schedule();
            this.join();
        } catch (InterruptedException e) {
            throw new CommandException(e);
        } finally {
            log.debug("Ending a job command");
        }
    }
    
    /**
     * Execute the command in the specified delay.
     */
    public void performExecuteWithDelay(long delay) throws CommandException {
        this.setPriority(this.getDefaultPriority());
        this.schedule(delay);
    }
    
    /**
     * @return Returns the monitor.
     */
    protected IProgressMonitor getMonitor() {
        return this.monitor;
    }
    
    /**
     * @see org.eclipse.core.internal.jobs.InternalJob#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IStatus run(IProgressMonitor monitor) {
        this.monitor = monitor;
        IStatus executionStatus = null;
        
        try {
            executionStatus = this.execute();
        } catch (CommandException e) {
            executionStatus = new Status(Status.ERROR, PLUGIN_ID, 0, e.getMessage(), e);
        }
        
        return executionStatus;
    }
    
    /**
     * Method that implements the actual command logic. Each concrete command
     * must implement this method.
     * @return
     */
    protected abstract IStatus execute() throws CommandException;
}
