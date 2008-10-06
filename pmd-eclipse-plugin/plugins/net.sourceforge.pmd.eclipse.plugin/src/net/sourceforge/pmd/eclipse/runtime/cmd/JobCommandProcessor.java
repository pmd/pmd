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
package net.sourceforge.pmd.eclipse.runtime.cmd;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import name.herlin.command.AbstractProcessableCommand;
import name.herlin.command.CommandException;
import name.herlin.command.CommandProcessor;
import name.herlin.command.Timer;
import name.herlin.command.UnsetInputPropertiesException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This is a particular processor for Eclipse in order to handle long running
 * commands.
 * 
 * @author Philippe Herlin
 *
 */
public class JobCommandProcessor implements CommandProcessor {
    private static final Logger log = Logger.getLogger(JobCommandProcessor.class);
    private final Map jobs = Collections.synchronizedMap(new HashMap());

    /**
     * @see name.herlin.command.CommandProcessor#processCommand(name.herlin.command.AbstractProcessableCommand)
     */
    public void processCommand(final AbstractProcessableCommand aCommand) throws CommandException {
        log.debug("Begining job command " + aCommand.getName());

        if (!aCommand.isReadyToExecute()) {
            throw new UnsetInputPropertiesException();
        }
        
        final Job job = new Job(aCommand.getName()) {
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    if (aCommand instanceof AbstractDefaultCommand) {
                        ((AbstractDefaultCommand) aCommand).setMonitor(monitor);
                    }
                    Timer timer = new Timer();
                    aCommand.execute();
                    timer.stop();
                    PMDPlugin.getDefault().logInformation("Command " + aCommand.getName() + " excecuted in " + timer.getDuration() + "ms");
                } catch (CommandException e) {
                    PMDPlugin.getDefault().logError("Error executing command " + aCommand.getName(), e);
                }
                
                return Status.OK_STATUS;
            }
        };
        
        if (aCommand instanceof AbstractDefaultCommand) {
            job.setUser(((AbstractDefaultCommand) aCommand).isUserInitiated());
        }

        job.schedule();
        this.addJob(aCommand, job);
        log.debug("Ending job command " + aCommand.getName());
    }

    /**
     * @see name.herlin.command.CommandProcessor#waitCommandToFinish(name.herlin.command.AbstractProcessableCommand)
     */
    public void waitCommandToFinish(final AbstractProcessableCommand aCommand) throws CommandException {
        final Job job = (Job) this.jobs.get(aCommand);
        if (job != null) {
            try {
                job.join();
            } catch (InterruptedException e) {
                throw new CommandException(e);
            }
        }

    }
    
    /**
     * Add a job to the map. Also, clear all finished jobs
     * @param command for which to keep the job 
     * @param job a job to keep until it is finished
     */
    private void addJob(final AbstractProcessableCommand command, final Job job) {
        this.jobs.put(command, job);
        
        // clear terminated command
        final Iterator i = this.jobs.keySet().iterator();
        while (i.hasNext()) {
            final AbstractProcessableCommand aCommand = (AbstractProcessableCommand) i.next();
            final Job aJob = (Job) this.jobs.get(aCommand);
            if ((aJob == null) || (aJob.getResult() != null)) {
                this.jobs.remove(aCommand);
            }
        }
    }
}
