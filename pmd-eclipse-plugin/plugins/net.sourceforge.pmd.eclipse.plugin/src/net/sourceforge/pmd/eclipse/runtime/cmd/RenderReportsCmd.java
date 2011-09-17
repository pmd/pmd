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
import java.util.Map;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.PMDRuntimeConstants;
import net.sourceforge.pmd.eclipse.runtime.builder.MarkerUtil;
import net.sourceforge.pmd.eclipse.util.IOUtil;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.util.StringUtil;

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
 *
 */
public class RenderReportsCmd extends AbstractProjectCommand {

    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(RenderReportsCmd.class);

    /**
     * Table containing the renderers indexed by the file name.
     */
    private Map<String, Renderer> renderers = new HashMap<String, Renderer>();

    /**
     * Default Constructor
     */
    public RenderReportsCmd() {
        super("RenderReport", "Produce reports for a project");

        setOutputProperties(false);
        setReadOnly(false);
        setTerminated(false);
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
     * 
     * @param report
     * @param folder
     * @param reportName
     * @param renderer
     * @throws IOException
     * @throws CoreException
     */
    private void render(Report report, IFolder folder, String reportName, Renderer renderer) throws IOException, CoreException {
    	
        StringWriter writer = new StringWriter();
        String reportString = null;
        
        try {
	        renderer.setWriter(writer);
	        renderer.start();
	        renderer.renderFileReport(report);
	        renderer.end();
	
	        reportString = writer.toString();
	        } finally {
	        	IOUtil.closeQuietly(writer);
	        }
	        
	    if (StringUtil.isEmpty(reportString)) {
	    	log.debug("Missing content for report: " + reportName);
	    	return;
	    }
	        
        log.debug("   Creating the report file");
        final IFile reportFile = folder.getFile(reportName);
        final InputStream contentsStream = new ByteArrayInputStream(reportString.getBytes());
        if (reportFile.exists()) {
            reportFile.setContents(contentsStream, true, false, getMonitor());
        } else {
            reportFile.create(contentsStream, true, getMonitor());
        }
        reportFile.refreshLocal(IResource.DEPTH_INFINITE, getMonitor());
        contentsStream.close();    	
    }
    
    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    @Override
    public void execute() throws CommandException {

        try {
            log.debug("Starting RenderReport command");
            log.debug("   Create a report object");
            final Report report = createReport(project());

            log.debug("   Getting the report folder");
            final IFolder folder = getProjectFolder(PMDRuntimeConstants.REPORT_FOLDER);
            if (!folder.exists()) {
                folder.create(true, true, getMonitor());
            }

            for (Map.Entry<String, Renderer> entry: renderers.entrySet()) {
                String reportName = entry.getKey();
                Renderer renderer = entry.getValue();
                log.debug("   Render the report");
                render(report, folder, reportName, renderer);
            }
        } catch (CoreException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } catch (IOException e) {
            log.debug("Core Exception: " + e.getMessage(), e);
            throw new CommandException(e);
        } finally {        	
            log.debug("End of RenderReport command");
            setTerminated(true);
        }
    }

    /**
     * @see name.herlin.command.Command#reset()
     */
    @Override
    public void reset() {
       setProject(null);
       renderers = new HashMap<String, Renderer>();
       setTerminated(false);
    }

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    @Override
    public boolean isReadyToExecute() {
        return super.isReadyToExecute() && !this.renderers.isEmpty();
    }

    /**
     * Create a Report object from the markers of a project
     * @param project
     * @return
     */
    private Report createReport(final IProject project) throws CoreException {
        final Report report = new Report();

        final IMarker[] markers = MarkerUtil.findAllMarkers(project);
        final RuleSet ruleSet = PMDPlugin.getDefault().getPreferencesManager().getRuleSet();
        
        for (IMarker marker : markers) {
            final String ruleName = marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_RULENAME, "");
            final Rule rule = ruleSet.getRuleByName(ruleName);

            // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops: by Herlin on 01/05/05 19:14
            final FakeRuleViolation ruleViolation = new FakeRuleViolation(rule);

            // Fill in the rule violation object before adding it to the report
            ruleViolation.setBeginLine(marker.getAttribute(IMarker.LINE_NUMBER, 0));
            ruleViolation.setEndLine(marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_LINE2, 0));
            ruleViolation.setVariableName(marker.getAttribute(PMDRuntimeConstants.KEY_MARKERATT_LINE2, ""));
            ruleViolation.setFilename(marker.getResource().getProjectRelativePath().toString());
            ruleViolation.setDescription(marker.getAttribute(IMarker.MESSAGE, rule.getMessage()));

            if (marker.getResource() instanceof IFile) {
                final ICompilationUnit unit = JavaCore.createCompilationUnitFrom((IFile) marker.getResource());
                final IPackageDeclaration[] packages = unit.getPackageDeclarations();
                if (packages.length > 0) {
                    ruleViolation.setPackageName(packages[0].getElementName());
                } else {
                    ruleViolation.setPackageName("(default)");
                }

                IType[] types = unit.getAllTypes();
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
