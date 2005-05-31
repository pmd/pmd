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
package net.sourceforge.pmd.eclipse.cmd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;
import net.sourceforge.pmd.renderers.Renderer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * This command produce a HTML report for a project
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.3  2005/05/31 20:44:41  phherlin
 * Continuing refactoring
 *
 * Revision 1.2  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.1  2005/04/20 23:16:20  phherlin
 * Implement reports generation RFE#1177802
 *
 *
 */
public class RenderReportCmd extends AbstractDefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.RenderReportCmd");
    private IProject project;
    private Renderer renderer;
    private String reportName;

    /**
     * Default Constructor
     */
    public RenderReportCmd() {
        super();
        this.setDescription("Produce a HTML report for a project");
        this.setName("RenderReport");
        this.setOutputProperties(false);
        this.setReadOnly(false);
        this.setTerminated(false);
    }
    
    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            log.debug("Starting RenderReport command");
            log.debug("   Create a report object");
            final Report report = this.createReport(this.project);
            
            log.debug("   Render the report");
            final String reportString = this.renderer.render(report);

            log.debug("   Getting the report folder");
            final IFolder folder = this.project.getFolder(PMDPluginConstants.REPORT_FOLDER);
            if (!folder.exists()) {
                folder.create(true, true, this.getMonitor());
            }
            
            log.debug("   Creating the report file");
            final IFile reportFile = folder.getFile(this.reportName);
            final InputStream contentsStream = new ByteArrayInputStream(reportString.getBytes()); 
            if (reportFile.exists()) {
                reportFile.setContents(contentsStream, true, false, this.getMonitor());
            } else {
                reportFile.create(contentsStream, true, this.getMonitor());
            }
            reportFile.refreshLocal(IResource.DEPTH_INFINITE, this.getMonitor());
            contentsStream.close();
        } catch (CoreException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } catch (IOException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } finally {
            log.debug("End of RenderReport command");
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
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return (this.project != null) && (this.renderer != null) && (this.reportName != null);
    }
    
    /**
     * Create a Report object from the markers of a project
     * @param project
     * @return
     */
    private Report createReport(final IProject project) throws CoreException {
        final Report report = new Report();
        
        final IMarker[] markers = project.findMarkers(PMDPluginConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
        for (int i = 0; i < markers.length; i++) {
            final String ruleName = markers[i].getAttribute(PMDPluginConstants.KEY_MARKERATT_RULENAME, "");
            final Rule rule = PMDPlugin.getDefault().getRuleSet().getRuleByName(ruleName);
            final int lineNumber = markers[i].getAttribute(IMarker.LINE_NUMBER, 0);
            final String message = markers[i].getAttribute(IMarker.MESSAGE, "");

            // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops: by Herlin on 01/05/05 19:15
            final RuleContext ruleContext = new RuleContext();
            ruleContext.setSourceCodeFilename(markers[i].getResource().getProjectRelativePath().toString());

            // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops: by Herlin on 01/05/05 19:14
            final RuleViolation ruleViolation = new RuleViolation(rule, lineNumber, message, ruleContext);
            report.addRuleViolation(ruleViolation);
        }
        
        return report;
    }
}
