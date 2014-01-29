/*
 * Created on 24 nov. 2004
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
package net.sourceforge.pmd.eclipse.runtime.properties.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;
import net.sourceforge.pmd.eclipse.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.eclipse.runtime.writer.WriterException;
import net.sourceforge.pmd.eclipse.util.IOUtil;
import net.sourceforge.pmd.util.StringUtil;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.ui.IWorkingSet;

/**
 * Implementation of a project properties information structure
 *
 * @author Philippe Herlin
 *
 */
public class ProjectPropertiesImpl implements IProjectProperties {
    private static final Logger log = Logger.getLogger(ProjectPropertiesImpl.class);

    private static final String PROJECT_RULESET_FILE = ".ruleset";

    private final IProjectPropertiesManager projectPropertiesManager;
    private final IProject project;
    private boolean needRebuild;
    private boolean pmdEnabled;
    private boolean ruleSetStoredInProject;
    private String ruleSetFile;
    private RuleSet projectRuleSet;
    private IWorkingSet projectWorkingSet;
    private boolean includeDerivedFiles;
    private boolean violationsAsErrors = true;
    private boolean fullBuildEnabled  = true; // default in case didn't come from properties
    private Set<String> buildPathExcludePatterns = new HashSet<String>();
    private Set<String> buildPathIncludePatterns = new HashSet<String>();
    
    /**
     * The default constructor takes a project as an argument
     */
    public ProjectPropertiesImpl(final IProject project, IProjectPropertiesManager projectPropertiesManager) {
        super();
        this.project = project;
        this.projectPropertiesManager = projectPropertiesManager;
        this.projectRuleSet = PMDPlugin.getDefault().getPreferencesManager().getRuleSet();
        determineBuildPathIncludesExcludes();
    }

    /**
     * Determines the included and excluded paths configured for the build path of this eclipse project.
     */
    private void determineBuildPathIncludesExcludes() {
        IClasspathEntry source = PMDPlugin.buildSourceClassPathEntryFor(project);
        if (source != null) {
            try {
               String basePath = new File(project.getWorkspace().getRoot().getLocation().toOSString()
                        + java.io.File.separator + source.getPath().toOSString()).getCanonicalPath();
               if (!basePath.endsWith(File.separator)) {
                   basePath += File.separator;
               }
               if (source.getExclusionPatterns() != null) {
                   for (IPath path : source.getExclusionPatterns()) {
                       String pathString = path.toOSString();
                       if (!pathString.endsWith(File.separator)) {
                           pathString += File.separator;
                       }
                       buildPathExcludePatterns.add(basePath + pathString + ".*");
                   }
               }
               if (source.getInclusionPatterns() != null) {
                   for (IPath path : source.getInclusionPatterns()) {
                       String pathString = path.toOSString();
                       if (!pathString.endsWith(File.separator)) {
                           pathString += File.separator;
                       }
                       buildPathIncludePatterns.add(basePath + pathString + ".*");
                   }
               }
            } catch (IOException e) {
               log.error("Couldn't determine build class path", e);
            }
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#getProject()
     */
    public IProject getProject() {
        return this.project;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#isPmdEnabled()
     */
    public boolean isPmdEnabled()  {
        return this.pmdEnabled;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setPmdEnabled(boolean)
     */
    public void setPmdEnabled(final boolean pmdEnabled) {
        log.debug("Enable PMD for project " + this.project.getName() + ": " + this.pmdEnabled);
        if (this.pmdEnabled != pmdEnabled) {
            this.pmdEnabled = pmdEnabled;
            this.needRebuild |= pmdEnabled;
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#getProjectRuleSet()
     */
    public RuleSet getProjectRuleSet() throws PropertiesException {
        return cloneRuleSet();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setProjectRuleSet(net.sourceforge.pmd.RuleSet)
     */
    public void setProjectRuleSet(final RuleSet projectRuleSet) throws PropertiesException {
        log.debug("Set a rule set for project " + this.project.getName());
        if (projectRuleSet == null) {
            throw new PropertiesException("Setting a project rule set to null"); // TODO NLS
        }

        this.needRebuild |= !this.projectRuleSet.getRules().equals(projectRuleSet.getRules());
        this.projectRuleSet = projectRuleSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#isRuleSetStoredInProject()
     */
    public boolean isRuleSetStoredInProject() {
        return this.ruleSetStoredInProject;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setRuleSetStoredInProject(boolean)
     */
    public void setRuleSetStoredInProject(final boolean ruleSetStoredInProject) throws PropertiesException {
        log.debug("Set rule set stored in project for project " + this.project.getName() + ": " + ruleSetStoredInProject);
        this.needRebuild |= this.ruleSetStoredInProject != ruleSetStoredInProject;
        this.ruleSetStoredInProject = ruleSetStoredInProject;
        if (this.ruleSetStoredInProject && !isRuleSetFileExist()) {
            throw new PropertiesException("The project ruleset file cannot be found for project " + this.project.getName()); // TODO NLS
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#getRuleSetFile()
     */
    public String getRuleSetFile() {
    	
        return StringUtil.isEmpty(ruleSetFile) ? PROJECT_RULESET_FILE : ruleSetFile;
	}

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setRuleSetFile(String)
     */
	public void setRuleSetFile(String ruleSetFile) throws PropertiesException {
        log.debug("Set rule set file for project " + project.getName() + ": " + ruleSetFile);
        needRebuild |= this.ruleSetFile == null || !this.ruleSetFile.equals(ruleSetFile);
        this.ruleSetFile = ruleSetFile;
        if (ruleSetStoredInProject && !isRuleSetFileExist()) {
            throw new PropertiesException("The project ruleset file cannot be found for project " + project.getName()); // TODO NLS
        }
	}

	/**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#getProjectWorkingSet()
     */
    public IWorkingSet getProjectWorkingSet() {
        return this.projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setProjectWorkingSet(org.eclipse.ui.IWorkingSet)
     */
    public void setProjectWorkingSet(final IWorkingSet projectWorkingSet) {
        log.debug("Set working set for project " + project.getName() + ": "
                + (projectWorkingSet == null ? "none" : projectWorkingSet.getName()));

        needRebuild |= projectWorkingSet == null ? projectWorkingSet != null:!this.projectWorkingSet.equals(projectWorkingSet);
        this.projectWorkingSet = projectWorkingSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#isNeedRebuild()
     */
    public boolean isNeedRebuild() {
        log.debug("Query if project " + project.getName() + " need rebuild : " + (pmdEnabled && needRebuild));
        log.debug("   PMD Enabled = " + pmdEnabled);
        log.debug("   Project need rebuild = " + needRebuild);
        return pmdEnabled && needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setNeedRebuild()
     */
    public void setNeedRebuild(final boolean needRebuild) {
        log.debug("Set if rebuild is needed for project " + project.getName() + ": " + needRebuild);
        this.needRebuild = needRebuild;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#isRuleSetFileExist()
     */
    public final boolean isRuleSetFileExist() {
    	return getResolvedRuleSetFile().exists();
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#getResolvedRuleSetFile()
     */
    public File getResolvedRuleSetFile() {
        // Check as project-relative path
        IFile file = project.getFile(getRuleSetFile());
        File f = getExistingFileOrNull(file);
        if (f == null) {
            // Check as workspace-relative path
            IWorkspaceRoot workspaceRoot = project.getWorkspace().getRoot();
            try {
                IFile workspaceFile = workspaceRoot.getFile(new Path(getRuleSetFile()));
                f = getExistingFileOrNull(workspaceFile);
            } catch (IllegalArgumentException e) {
                // Fall back to below
            }
            if (f == null) {
                // Fall back to file system path
                f = new File(getRuleSetFile());
            }
        }
        return f;
    }

    private File getExistingFileOrNull(IFile file) {
        boolean exists = file.exists() && file.isAccessible();
        File result = null;
        if (exists) {
            File f = new File(file.getLocation().toOSString());
            // For some reason IFile says exists when it doesn't!  So double check.
            if (f.exists()) {
                result = f;
            }
        }
        return result;
    }

    /**
     * Create a project ruleset file from the current configured rules
     *
     */
    public void createDefaultRuleSetFile() throws PropertiesException {
        log.info("Create a default rule set file for project " + this.project.getName());
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        try {
            IRuleSetWriter writer = PMDPlugin.getDefault().getRuleSetWriter();
            baos = new ByteArrayOutputStream();
            writer.write(baos, projectRuleSet);

            final IFile file = project.getFile(PROJECT_RULESET_FILE);
            if (file.exists() && file.isAccessible()) {
                throw new PropertiesException("Project ruleset file already exists");
            } else {
                bais = new ByteArrayInputStream(baos.toByteArray());
                file.create(bais, true, null);
            }
        } catch (WriterException e) {
            throw new PropertiesException(e);
        } catch (CoreException e) {
            throw new PropertiesException(e);
        } finally {
        	IOUtil.closeQuietly(baos);
        	IOUtil.closeQuietly(bais);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#isIncludeDerivedFiles()
     */
    public boolean isIncludeDerivedFiles() {
        return includeDerivedFiles;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setIncludeDerivedFiles(boolean)
     */
    public void setIncludeDerivedFiles(boolean includeDerivedFiles) {
        log.debug("Set if derived files should be included: " + includeDerivedFiles);
        this.needRebuild |= this.includeDerivedFiles != includeDerivedFiles;
        this.includeDerivedFiles = includeDerivedFiles;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.model.PMDPluginModel#sync()
     */
    public void sync() throws PropertiesException {
        log.info("Commit properties for project " + project.getName());
        projectPropertiesManager.storeProjectProperties(this);
    }

    /**
     * Clone the PMD ruleset.
     * @return a pmd ruleSetClone.
     */
    private RuleSet cloneRuleSet() {
        final RuleSet clonedRuleSet = new RuleSet();

        for (Rule rule: projectRuleSet.getRules()) {
            clonedRuleSet.addRule(rule);
        }
        clonedRuleSet.addExcludePatterns(projectRuleSet.getExcludePatterns());
        clonedRuleSet.addIncludePatterns(projectRuleSet.getIncludePatterns());

        return clonedRuleSet;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#violationsAsErrors()
     */
    public boolean violationsAsErrors() throws PropertiesException {
        return this.violationsAsErrors;
    }

    public void setViolationsAsErrors(boolean violationsAsErrors) throws PropertiesException {
        log.debug("Set to handle violations as errors: " + violationsAsErrors);
        needRebuild |= this.violationsAsErrors != violationsAsErrors;
        this.violationsAsErrors = violationsAsErrors;
    }

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#isFullBuildEnabled()
     */
	public boolean isFullBuildEnabled() throws PropertiesException {
		return fullBuildEnabled;
	}

    /**
     * @see net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties#setFullBuildEnabled(boolean)
     */
	public void setFullBuildEnabled(boolean fullBuildEnabled)
			throws PropertiesException {
        log.debug("Set if run at full build for project " + project.getName() + ": " + fullBuildEnabled);
		if (this.fullBuildEnabled != fullBuildEnabled){
			this.fullBuildEnabled = fullBuildEnabled;
			if (this.fullBuildEnabled){
				needRebuild = true;
			}
		}
		
	}
	
	/**
	 * Provide some help to folks using the debugger and logging
	 */
	public String toString(){
		String projectName = "n/a";
		String projectRuleSetName = "n/a";
		String projectWorkingSetName = "n/a";
		if (project != null ){ projectName = project.getName();}
		if (projectRuleSet!= null){ projectRuleSetName = projectRuleSet.getName();}
		if (projectWorkingSet!=null){ projectWorkingSetName = projectWorkingSet.getName();}
		
		return "fullBuildEnabled:"+fullBuildEnabled 
		+ " includeDerivedFiles:"+includeDerivedFiles+" pmdEnabled:"+pmdEnabled
		+ " project:"+projectName+ " projectRuleSet:"+projectRuleSetName
		+ " projectWorkingSet:"+projectWorkingSetName + " ruleSetFile:"+ruleSetFile
		+ " ruleSetStoredInProject:"+ruleSetStoredInProject 
		+ " violationsAsErrors: "+violationsAsErrors;
	}

	public Set<String> getBuildPathExcludePatterns() {
	    return buildPathExcludePatterns;
	}

	public Set<String> getBuildPathIncludePatterns() {
	    return buildPathIncludePatterns;
	}
}