/*
 * Created on 3 déc. 2004
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import name.herlin.command.CommandException;
import name.herlin.command.CommandProcessor;
import name.herlin.command.ProcessableCommand;
import name.herlin.command.UnsetInputPropertiesException;
import net.sourceforge.pmd.eclipse.PMDPlugin;

/**
 * This is a particular processor for Eclipse in order to handle long running
 * commands.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2004/12/03 00:22:42  phherlin
 * Continuing the refactoring experiment.
 * Implement the Command framework.
 * Refine the MVC pattern usage.
 *
 *
 */
public class JobCommandProcessor implements CommandProcessor {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.JobCommandProcessor");

    /**
     * @see name.herlin.command.CommandProcessor#processCommand(name.herlin.command.ProcessableCommand)
     */
    public void processCommand(final ProcessableCommand aCommand) throws CommandException {
        log.debug("Begining a job command");

        if (!aCommand.isReadyToExecute()) throw new UnsetInputPropertiesException();
        
        Job job = new Job(aCommand.getName()) {
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    ((DefaultCommand) aCommand).setMonitor(monitor);
                    aCommand.execute();
                } catch (CommandException e) {
                    PMDPlugin.getDefault().logError("Error executing command " + aCommand.getName(), e);
                }
                
                return Status.OK_STATUS;
            }
        };
        
        job.schedule();
        log.debug("Ending a job command");
    }

}
