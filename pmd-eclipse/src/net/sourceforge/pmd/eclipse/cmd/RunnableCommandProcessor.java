/*
 * Created on 3 d�c. 2004
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
import name.herlin.command.CommandProcessor;
import name.herlin.command.AbstractProcessableCommand;
import name.herlin.command.UnsetInputPropertiesException;
import net.sourceforge.pmd.eclipse.PMDPlugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * This is a particular processor for Eclipse in order to handle long running
 * commands. It runs commands in a Workspace Runnable
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.1  2004/12/03 00:22:42  phherlin
 * Continuing the refactoring experiment.
 * Implement the Command framework.
 * Refine the MVC pattern usage.
 *
 *
 */
public class RunnableCommandProcessor implements CommandProcessor, IWorkspaceRunnable {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.RunnableCommandProcessor");
    private AbstractProcessableCommand command;

    /**
     * @see name.herlin.command.CommandProcessor#processCommand(name.herlin.command.AbstractProcessableCommand)
     */
    public void processCommand(final AbstractProcessableCommand aCommand) throws CommandException {
        log.debug("Begining workspace runnable command " + aCommand.getName());

        try {
            if (!aCommand.isReadyToExecute()) {
                throw new UnsetInputPropertiesException();
            }
            
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            this.command = aCommand;
            workspace.run(this, workspace.getRoot(), IWorkspace.AVOID_UPDATE, ((AbstractDefaultCommand) aCommand).getMonitor());
        } catch (CoreException e) {
            throw new CommandException(e);
        } finally {
            log.debug("Ending workspace runnable command " + aCommand.getName());
        }

    }

    /**
     * @see name.herlin.command.CommandProcessor#waitCommandToFinish(name.herlin.command.AbstractProcessableCommand)
     */
    public void waitCommandToFinish(final AbstractProcessableCommand aCommand) throws CommandException {
        // do noting
    }
    
    /**
     * Process the command as a batch
     * @param monitor
     * @throws CoreException
     */
    public void run(final IProgressMonitor monitor) throws CoreException {
        try {
            this.command.execute();
        } catch (CommandException e) {
            PMDPlugin.getDefault().logError("Error executing command " + this.command.getName(), e);
            throw new CoreException(new Status(IStatus.ERROR, PMDPlugin.PLUGIN_ID, 0, e.getMessage(), e));
        }
    }
}
