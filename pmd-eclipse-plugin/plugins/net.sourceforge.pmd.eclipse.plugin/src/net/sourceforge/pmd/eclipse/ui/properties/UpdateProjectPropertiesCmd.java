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
package net.sourceforge.pmd.eclipse.ui.properties;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.runtime.cmd.AbstractProjectCommand;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;

import org.eclipse.ui.IWorkingSet;

/**
 * Save updated project properties. This is a composite command.
 *
 * @author Philippe Herlin
 *
 */
public class UpdateProjectPropertiesCmd extends AbstractProjectCommand {

    private static final long serialVersionUID = 1L;

//    private IProject project;
    private boolean pmdEnabled;
    private IWorkingSet projectWorkingSet;
    private RuleSet projectRuleSet;
    private boolean ruleSetStoredInProject;
    private String ruleSetFile;
    private boolean needRebuild;
    private boolean ruleSetFileExists;
    private boolean includeDerivedFiles;
    private boolean violationsAsErrors = true;

    /**
     * Default constructor. Initializes command attributes
     *
     */
    public UpdateProjectPropertiesCmd() {
        super("UpdateProjectProperties", "Update a project PMD specific properties.");
        this.setReadOnly(false);
        this.setOutputProperties(true);
        this.setTerminated(false);
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            final IProjectProperties properties = projectProperties();
            properties.setPmdEnabled(this.pmdEnabled);
            properties.setProjectRuleSet(this.projectRuleSet);
            properties.setProjectWorkingSet(this.projectWorkingSet);
            properties.setRuleSetStoredInProject(this.ruleSetStoredInProject);
            properties.setRuleSetFile(this.ruleSetFile);
            properties.setIncludeDerivedFiles(this.includeDerivedFiles);
            properties.setViolationsAsErrors(this.violationsAsErrors);
            properties.sync();
            this.needRebuild = properties.isNeedRebuild();
            this.ruleSetFileExists = !properties.isRuleSetFileExist();

        } catch (PropertiesException e) {
            throw new CommandException(e.getMessage(), e);
        } finally {
            this.setTerminated(true);
        }
    }

    /**
     * @param pmdEnabled The pmdEnabled to set.
     */
    public void setPmdEnabled(final boolean pmdEnabled) {
        this.pmdEnabled = pmdEnabled;
    }

    /**
     * @param projectRuleSet The projectRuleSet to set.
     */
    public void setProjectRuleSet(final RuleSet projectRuleSet) {
        this.projectRuleSet = projectRuleSet;
    }

    /**
     * @param projectWorkingSet The projectWorkingSet to set.
     */
    public void setProjectWorkingSet(final IWorkingSet projectWorkingSet) {
        this.projectWorkingSet = projectWorkingSet;
    }

    /**
     * @param ruleSetStoredInProject The ruleSetStoredInProject to set.
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) {
        this.ruleSetStoredInProject = ruleSetStoredInProject;
    }

    /**
     * @param ruleSetFile The ruleSetFile to set.
     */
    public void setRuleSetFile(String ruleSetFile) {
		this.ruleSetFile = ruleSetFile;
	}

    /**
     * @param includeDerivedFiles The includeDerivedFiles to set.
     */
    public void setIncludeDerivedFiles(boolean includeDerivedFiles) {
        this.includeDerivedFiles = includeDerivedFiles;
    }

    /**
     * @param violationsAsErrors The violationsAsErrors to set.
     */
    public void setViolationsAsErrors(boolean violationsAsErrors) {
        this.violationsAsErrors = violationsAsErrors;
    }

    /**
     * @return Returns the needRebuild.
     */
    public boolean isNeedRebuild() {
        return this.needRebuild;
    }

    /**
     * @return Returns the ruleSetFileExists.
     */
    public boolean isRuleSetFileExists() {
        return this.ruleSetFileExists;
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.setProject(null);
        this.setPmdEnabled(false);
        this.setProjectRuleSet(null);
        this.setRuleSetStoredInProject(false);
        this.setRuleSetFile(null);
        this.setIncludeDerivedFiles(false);
        this.setViolationsAsErrors(false);
        this.setTerminated(false);
    }

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return super.isReadyToExecute() && this.projectRuleSet != null;
    }
}
