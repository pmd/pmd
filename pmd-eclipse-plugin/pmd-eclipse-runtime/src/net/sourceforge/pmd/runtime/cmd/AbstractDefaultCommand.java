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
package net.sourceforge.pmd.runtime.cmd;

import name.herlin.command.AbstractProcessableCommand;
import name.herlin.command.CommandException;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.SourceType;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * This is a base implementation for a command inside the PMD plugin.
 * This must be used as a root implementation for all the plugin commands.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.3  2005/10/24 22:40:54  phherlin
 * Refactor command processing
 *
 * Revision 1.2  2005/05/31 20:44:41  phherlin
 * Continuing refactoring
 *
 * Revision 1.1  2005/05/07 13:32:04  phherlin
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
public abstract class AbstractDefaultCommand extends AbstractProcessableCommand {
    private static final Logger log = Logger.getLogger(AbstractDefaultCommand.class);

    private boolean readOnly;
    private boolean outputProperties;
    private boolean readyToExecute;
    private String description;
    private String name;
    private IProgressMonitor monitor;
    private int stepsCount;
    private boolean userInitiated;

    /**
     * @return Returns the readOnly.
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @param readOnly The readOnly to set.
     */
    public void setReadOnly(final boolean readOnly) {
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
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @param outputProperties The outputProperties to set.
     */
    public void setOutputProperties(final boolean outputProperties) {
        this.outputProperties = outputProperties;
    }

    /**
     * @return Returns the outputProperties.
     */
    public boolean hasOutputProperties() {
        return outputProperties;
    }

    /**
     * @return Returns the readyToExecute.
     */
    public boolean isReadyToExecute() {
        return readyToExecute;
    }

    /**
     * @param readyToExecute The readyToExecute to set.
     */
    public void setReadyToExecute(final boolean readyToExecute) {
        this.readyToExecute = readyToExecute;
    }

    /**
     * @return Returns the number of steps for that command
     */
    public int getStepsCount() {
        return stepsCount;
    }
    
    /**
     * @param stepsCount The number of steps for that command
     */
    public void setStepsCount(final int stepsCount) {
        this.stepsCount = stepsCount;
    }
    
    /**
     * @return Returns the userInitiated.
     */
    public boolean isUserInitiated() {
        return userInitiated;
    }

    /**
     * @param userInitiated The userInitiated to set.
     */
    public void setUserInitiated(boolean userInitiated) {
        this.userInitiated = userInitiated;
    }

    /**
     * @return Returns the monitor.
     */
    public IProgressMonitor getMonitor() {
        return this.monitor;
    }
    
    /**
     * @param monitor The monitor to set.
     */
    public void setMonitor(final IProgressMonitor monitor) {
        this.monitor = monitor;
    }
    
    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public abstract void execute() throws CommandException;

    /**
     * @see name.herlin.command.Command#reset()
     */
    public abstract void reset();
    
    /**
     * delegate method for monitor.beginTask
     * @see org.eclipse.core.runtime.IProgressMonitor#beginTask
     */
    protected void beginTask(final String name, final int totalWork) {
        if (this.monitor != null) {
            this.monitor.beginTask(name, totalWork);
        }
    }
    
    /**
     * delegate method to monitor.done()
     * @see org.eclipse.core.runtime.IProgressMonitor#done
     */
    protected void done() {
        if (this.monitor != null) {
            this.monitor.done();
        }
        
        this.setTerminated(true);
    }
    
    /**
     * deletegate method for monitor.isCanceled()
     * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled
     */
    protected boolean isCanceled() {
        return this.monitor == null ? false : this.monitor.isCanceled();
    }
    
    /**
     * delegate method for monitor.setTaskName()
     * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName
     */
    protected void setTaskName(final String name) {
        if (this.monitor != null) {
            this.monitor.setTaskName(name);
        }
    }
    
    /**
     * delegate method for monitor.subTask()
     * @see org.eclipse.core.runtime.IProgressMonitor#subTask
     */
    protected void subTask(final String name) {
        if (this.monitor != null) {
            this.monitor.subTask(name);
        }
    }
    
    /**
     * delegate method for monitor.worked()
     * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled
     */
    protected void worked(final int work) {
        if (this.monitor != null) {
            this.monitor.worked(work);
        }
    }
    
    /**
     * Return a PMD Engine for that project. The engine is parameterized
     * according to the target JDK of that project.
     * 
     * @param project
     * @return
     */
    protected PMD getPmdEngineForProject(final IProject project) throws CommandException {
        final IJavaProject javaProject = JavaCore.create(project);
        final PMD pmdEngine = new PMD();

        if (javaProject.exists()) {
            final String compilerCompliance = javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
            log.debug("compilerCompliance = " + compilerCompliance);
            final SourceType s = SourceType.getSourceTypeForId("java " + compilerCompliance);
            if (s != null) {
                pmdEngine.setJavaVersion(s);
            } else {
                throw new CommandException("The target JDK, " + compilerCompliance + " is not yet supported"); // TODO NLS
            }
            pmdEngine.setClassLoader(new JavaProjectClassLoader(pmdEngine.getClassLoader(), javaProject));
        } else {
            throw new CommandException("The project " + project.getName() + " is not a Java project"); // TODO NLS
        }
        return pmdEngine;
    }
}