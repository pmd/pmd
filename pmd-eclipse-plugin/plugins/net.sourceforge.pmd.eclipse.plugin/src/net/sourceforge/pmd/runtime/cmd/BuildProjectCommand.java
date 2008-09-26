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
package net.sourceforge.pmd.runtime.cmd;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.PropertiesException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;

/**
 * Rebuild a project to force PMD to be run on that project.
 *
 * @author Philippe Herlin
 * @version $Revision$
 *
 * $Log$
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.6  2005/06/07 18:38:14  phherlin
 * Move classes to limit packages cycle dependencies
 *
 * Revision 1.5  2005/05/31 20:44:41  phherlin
 * Continuing refactoring
 *
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
public class BuildProjectCommand extends AbstractDefaultCommand {

    private static final long serialVersionUID = 1L;

    private IProject project;

    /**
     * @param name
     */
    public BuildProjectCommand() {
        super();
        this.setReadOnly(false);
        this.setOutputProperties(false);
        this.setTerminated(false);
        this.setName("BuildProject");
        this.setDescription("Rebuild a project.");
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            this.project.build(IncrementalProjectBuilder.FULL_BUILD, this.getMonitor());

            final IProjectProperties properties = PMDPlugin.getDefault().loadProjectProperties(this.project);
            properties.setNeedRebuild(false);
        } catch (CoreException e) {
            throw new CommandException(e);
        } catch (PropertiesException e) {
            throw new CommandException(e);
        } finally {
            this.setTerminated(true);
        }
    }

    /**
     * @param project The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
        setReadyToExecute(project != null);
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.setProject(null);
        this.setTerminated(false);
    }
}
