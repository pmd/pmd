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
package net.sourceforge.pmd.eclipse.cpd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.cpd.CPD;
import net.sourceforge.pmd.cpd.LanguageFactory;
import net.sourceforge.pmd.cpd.Renderer;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;
import net.sourceforge.pmd.eclipse.cmd.AbstractDefaultCommand;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * This command produces a report of the Cut And Paste detector
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/05/31 23:04:11  phherlin
 * Fix Bug 1190624: refactor CPD integration
 *
 */
public class DetectCutAndPasteCmd extends AbstractDefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.DetectCutAndPasteCmd");
    private IProject project;
    private Renderer renderer;
    private String reportName;

    /**
     * Default Constructor
     */
    public DetectCutAndPasteCmd() {
        super();
        this.setDescription("Detect Cut & paste for a project");
        this.setName("DetectCutAndPaste");
        this.setOutputProperties(false);
        this.setReadOnly(false);
        this.setTerminated(false);
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            final Iterator matches = this.detectCutAndPaste();
            
            log.debug("Rendering CPD report");
            final String reportString = this.renderer.render(matches);

            // Create the report folder if not already existing
            log.debug("Create the report folder");
            final IFolder folder = this.project.getFolder(PMDPluginConstants.REPORT_FOLDER);
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
        } finally {
            this.setTerminated(true);
        }
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.setProject(null);
        this.setRenderer(null);
        this.setTerminated(false);
    }

    /**
     * @param project
     *            The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
    }

    /**
     * @param renderer
     *            The renderer to set.
     */
    public void setRenderer(final Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * @param reportName
     *            The reportName to set.
     */
    public void setReportName(final String reportName) {
        this.reportName = reportName;
    }

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return (this.project != null) && (this.renderer != null) && (this.reportName != null);
    }

    /**
     * Run the cut and paste detector
     * @return matches an iterator to CPD matches
     * @throws CoreException
     */
    private Iterator detectCutAndPaste() throws CoreException {
        log.debug("Searching for project files");
        final int minTileSize = PMDPlugin.getDefault().getPreferenceStore().getInt(PMDPlugin.MIN_TILE_SIZE_PREFERENCE);
        final CPD cpd = new CPD(minTileSize, new LanguageFactory().createLanguage(LanguageFactory.JAVA_KEY));
        final CPDVisitor visitor = new CPDVisitor(cpd);
        this.project.accept(visitor);

        log.debug("Performing CPD");
        cpd.go();

        return cpd.getMatches();
    }
}