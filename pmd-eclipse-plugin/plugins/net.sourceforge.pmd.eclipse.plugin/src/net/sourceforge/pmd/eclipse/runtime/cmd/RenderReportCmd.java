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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;

/**
 * This command produce a HTML report for a project
 *
 * @author Philippe Herlin
 * @version $Revision$
 *
 * $Log$
 * Revision 1.1  2006/05/22 21:37:34  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.7  2006/04/11 21:02:16  phherlin
 * Use the new IRuleViolation interface to generate reports
 * Fix default package issue
 *
 * Revision 1.6  2006/04/10 20:55:32  phherlin
 * Update to PMD 3.6
 *
 * Revision 1.5  2006/01/17 21:27:37  phherlin
 * Create a fake node instead of using SimpleNode
 *
 * Revision 1.4  2005/12/30 17:30:21  phherlin
 * Upgrade to PMD v3.4 -> RuleViolation interface has changed!
 *
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

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(RenderReportCmd.class);
    private IProject project;

    /**
     * Table containing the renderers indexed by the file name.
     */
    private HashMap renderers = new HashMap();

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
     * Register a renderer and its associated file for processing.
     *
     * @param renderer the renderer
     * @param reportFile the file name where the report will be saved
     */
    public void registerRenderer(Renderer renderer, String reportFile) {
        renderers.put(reportFile, renderer);
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            log.debug("Starting RenderReport command");
            log.debug("   Create a report object");
            final Report report = this.createReport(this.project);

            log.debug("   Getting the report folder");
            final IFolder folder = this.project.getFolder(PMDRuntimeConstants.REPORT_FOLDER);
            if (!folder.exists()) {
                folder.create(true, true, this.getMonitor());
            }

            Iterator i = renderers.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry entry = (Map.Entry) i.next();

                final String reportName = (String) entry.getKey();
                final Renderer renderer = (Renderer) entry.getValue();

                log.debug("   Render the report");
                final StringWriter w = new StringWriter();
                renderer.setWriter(w);
                renderer.start();
                renderer.renderFileReport(report);
                renderer.end();

                final String reportString = w.toString();

                log.debug("   Creating the report file");
                final IFile reportFile = folder.getFile(reportName);
                final InputStream contentsStream = new ByteArrayInputStream(reportString.getBytes());
                if (reportFile.exists()) {
                    reportFile.setContents(contentsStream, true, false, this.getMonitor());
                } else {
                    reportFile.create(contentsStream, true, this.getMonitor());
                }
                reportFile.refreshLocal(IResource.DEPTH_INFINITE, this.getMonitor());
                contentsStream.close();
            }
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
       this.renderers = new HashMap();
       this.setTerminated(false);
    }

    /**
     * @param project The project to set.
     */
    public void setProject(final IProject project) {
        this.project = project;
    }

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return this.project != null && !this.renderers.isEmpty();
    }

    /**
     * Create a Report object from the markers of a project
     * @param project
     * @return
     */
    private Report createReport(final IProject project) throws CoreException {
        final Report report = new Report();

        final IMarker[] markers = project.findMarkers(PMDRuntimeConstants.PMD_MARKER, true, IResource.DEPTH_INFINITE);
        for (int i = 0; i < markers.length; i++) {
            IMarker marker = markers[i];
            final String ruleName = marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_RULENAME, "");
            final Rule rule = PMDPlugin.getDefault().getPreferencesManager().getRuleSet().getRuleByName(ruleName);

            // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops: by Herlin on 01/05/05 19:14
            final FakeRuleViolation ruleViolation = new FakeRuleViolation(rule);

            // Fill in the rule violation object before adding it to the report
            ruleViolation.setBeginLine(marker.getAttribute(IMarker.LINE_NUMBER, 0));
            ruleViolation.setEndLine(marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_LINE2, 0));
            ruleViolation.setVariableName(marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_LINE2, ""));
            ruleViolation.setFilename(marker.getResource().getProjectRelativePath().toString());
            ruleViolation.setDescription(marker.getAttribute(IMarker.MESSAGE, rule.getMessage()));

            if (markers[i].getResource() instanceof IFile) {
                final ICompilationUnit unit = JavaCore.createCompilationUnitFrom((IFile) markers[i].getResource());
                final IPackageDeclaration packages[] = unit.getPackageDeclarations();
                if (packages.length > 0) {
                    ruleViolation.setPackageName(packages[0].getElementName());
                } else {
                    ruleViolation.setPackageName("(default)");
                }

                IType types[] = unit.getAllTypes();
                if (types.length > 0) {
                    ruleViolation.setClassName(types[0].getElementName());
                } else {
                    ruleViolation.setClassName(marker.getResource().getName());
                }
            }

            report.addRuleViolation(ruleViolation);
        }

        return report;
    }
}
