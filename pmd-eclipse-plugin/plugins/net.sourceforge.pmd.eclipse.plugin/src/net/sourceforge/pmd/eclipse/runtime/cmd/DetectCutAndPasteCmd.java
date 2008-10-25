/*
 * Created on 14 avr. 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.Language;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.Renderer;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPropertyListener;

/**
 * This command produces a report of the Cut And Paste detector
 *
 * @author Philippe Herlin, Sven Jacob
 *
 */
public class DetectCutAndPasteCmd extends AbstractDefaultCommand {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(DetectCutAndPasteCmd.class);
    private IProject project;
    private Language language;
    private int minTileSize;
    private Renderer renderer;
    private String reportName;
    private boolean createReport;
    private List<IPropertyListener> listenerList;

    /**
     * Default Constructor
     */
    public DetectCutAndPasteCmd() {
        super();
        this.setDescription("Detect Cut & paste for a project");
        this.setName("DetectCutAndPaste");
        this.setOutputProperties(true);
        this.setReadOnly(false);
        this.setTerminated(false);
        this.listenerList = new ArrayList<IPropertyListener>();
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    @Override
    public void execute() throws CommandException {
        try {
            // find the files
            final List<File> files = findFiles();

            if (files.size() == 0) {
                PMDPlugin.getDefault().logInformation("No files found for specified language.");
            } else {
                PMDPlugin.getDefault().logInformation("Found " + files.size() + " files for the specified language. Performing CPD.");
            }
            setStepsCount(files.size());
            beginTask("Finding suspect Cut And Paste", getStepsCount()*2);

            if (!isCanceled()) {
                // detect cut and paste
                final CPD cpd = detectCutAndPaste(files);

                if (!isCanceled()) {
                    // if the command was not canceled
                    if (this.createReport) {
                        // create the report optionally
                        this.renderReport(cpd.getMatches());
                    }

                    // trigger event propertyChanged for all listeners
                    Display.getDefault().asyncExec(new Runnable() {
                        public void run() {
                            final Iterator<IPropertyListener> listenerIterator = listenerList.iterator();
                            while (listenerIterator.hasNext()) {
                                final IPropertyListener listener = listenerIterator.next();
                                listener.propertyChanged(cpd.getMatches(), PMDRuntimeConstants.PROPERTY_CPD);
                            }
                        }
                    });
                }
            }
        } catch (CoreException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } catch (PropertiesException e) {
            log.debug("Properties Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } finally {
            this.setTerminated(true);
        }
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    @Override
    public void reset() {
        this.setProject(null);
        this.setTerminated(false);
        this.setReportName(null);
        this.setRenderer(null);
        this.setLanguage("java");
        this.setMinTileSize(PMDPlugin.getDefault().loadPreferences().getMinTileSize());
        this.setCreateReport(false);
        this.addPropertyListener(null);
        this.listenerList = new ArrayList<IPropertyListener>();
    }

    /**
     * @param language The language to set.
     */
    public void setLanguage(final String language) {
        this.language = new LanguageFactory().createLanguage(language);
    }

    /**
     * @param tilesize The tilesize to set.
     */
    public void setMinTileSize(final int tilesize) {
        this.minTileSize = tilesize;
    }

    /**
     * @param project The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
    }

    /**
     * @param renderer The renderer to set.
     */
    public void setRenderer(final Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * @param reportName The reportName to set.
     */
    public void setReportName(final String reportName) {
        this.reportName = reportName;
    }

    /**
     * @param render render a report or not.
     */
    public void setCreateReport(final boolean render) {
        this.createReport = render;
    }

    /**
     * Adds an object that wants to get an event after the command is finished.
     * @param listener the property listener to set.
     */
    public void addPropertyListener(IPropertyListener listener) {
        this.listenerList.add(listener);
    }

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    @Override
    public boolean isReadyToExecute() {
        return this.project != null
            && this.language != null
            && (!this.createReport // need a renderer and reportName if a report should be created
                    || this.renderer != null && this.reportName != null);
    }

    /**
     * Finds all files in a project based on a language.
     * Uses internally the CPDVisitor.
     * @return List of files
     * @throws PropertiesException
     * @throws CoreException
     */
    private List<File> findFiles() throws PropertiesException, CoreException {
        final IProjectProperties properties = PMDPlugin.getDefault().loadProjectProperties(project);
        final CPDVisitor visitor = new CPDVisitor();
        visitor.setWorkingSet(properties.getProjectWorkingSet());
        visitor.setIncludeDerivedFiles(properties.isIncludeDerivedFiles());
        visitor.setLanguage(language);
        visitor.setFiles(new ArrayList<File>());
        this.project.accept(visitor);
        return visitor.getFiles();
    }

    /**
     * Run the cut and paste detector. At first all files have to be added
     * to the cpd. Then the CPD can be executed.
     * @param files List of files to be checked.
     * @return the CPD itself for retrieving the matches.
     * @throws CoreException
     */
    private CPD detectCutAndPaste(final List<File> files) {
        log.debug("Searching for project files");
        final CPD cpd = new CPD(minTileSize, language);

        subTask("Adding files for the CPD");
        final Iterator<File> fileIterator = files.iterator();
        while (fileIterator.hasNext() && !isCanceled()) {
            final File file = fileIterator.next();
            try {
                cpd.add(file);
                worked(1);
            } catch (IOException e) {
                log.warn("IOException when adding file " + file.getName() + " to CPD. Continuing.", e);
            }
        }

        if (!isCanceled()) {
            subTask("Performing CPD");
            log.debug("Performing CPD");
            cpd.go();
            worked(getStepsCount());
        }

        return cpd;
    }

    /**
     * Renders a report using the matches of the CPD. Creates a report folder
     * and report file.
     * @param matches matches of the CPD
     * @throws CommandException
     */
    private void renderReport(Iterator<Match> matches) throws CommandException {
        try {
            log.debug("Rendering CPD report");
            subTask("Rendering CPD report");
            final String reportString = this.renderer.render(matches);

            // Create the report folder if not already existing
            log.debug("Create the report folder");
            final IFolder folder = this.project.getFolder(PMDRuntimeConstants.REPORT_FOLDER);
            if (!folder.exists()) {
                folder.create(true, true, this.getMonitor());
            }

            // Create the report file
            log.debug("Create the report file");
            final IFile reportFile = folder.getFile(this.reportName);
            final InputStream contentsStream = new ByteArrayInputStream(reportString.getBytes());
            if (reportFile.exists()) {
                log.debug("   Overwritting the report file");
                reportFile.setContents(contentsStream, true, false, this.getMonitor());
            } else {
                log.debug("   Creating the report file");
                reportFile.create(contentsStream, true, this.getMonitor());
            }
            reportFile.refreshLocal(IResource.DEPTH_INFINITE, this.getMonitor());
            contentsStream.close();
        } catch (CoreException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } catch (IOException e) {
            log.debug("IO Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        }
    }
}