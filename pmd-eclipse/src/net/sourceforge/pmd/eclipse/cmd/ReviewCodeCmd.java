/*
 * Created on 12 avr. 2005
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.TargetJDK1_3;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.eclipse.MarkerInfo;
import net.sourceforge.pmd.eclipse.PMDConstants;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.model.ModelException;
import net.sourceforge.pmd.eclipse.model.ModelFactory;
import net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * This command executes the PMD engine on a specified resource
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.5  2005/06/30 23:24:19  phherlin
 * Add the JDK 1.5 support
 *
 * Revision 1.4  2005/06/07 18:38:14  phherlin
 * Move classes to limit packages cycle dependencies
 *
 * Revision 1.3  2005/05/31 20:44:41  phherlin
 * Continuing refactoring
 *
 * Revision 1.2  2005/05/10 21:49:18  phherlin
 * Fix new violations detected by PMD 3.1
 *
 * Revision 1.1  2005/05/07 13:32:04  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 *  
 */
public class ReviewCodeCmd extends AbstractDefaultCommand {
    private static final Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.cmd.ReviewCodeCmd");
    private IResource resource;
    private IResourceDelta resourceDelta;
    private Map markers = new HashMap();
    private boolean taskMarker = false;

    /**
     * Default constructor
     */
    public ReviewCodeCmd() {
        super();
        this.setDescription("Run PMD on a workbench resource");
        this.setName("ReviewCode");
        this.setOutputProperties(true);
        this.setReadOnly(true);
        this.setTerminated(false);
    }

    /**
     * @see name.herlin.command.AbstractProcessableCommand#execute()
     */
    public void execute() throws CommandException {
        try {
            if (this.resource == null) {
                this.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), this.getStepsCount());
                this.processResourceDelta();
            } else {
                this.beginTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_PMD_PROCESSING), this.getStepsCount());
                this.processResource();
            }

            applyMarkers();

        } finally {
            this.setTerminated(true);
        }

    }

    /**
     * @return Returns the file markers
     */
    public Map getMarkers() {
        return this.markers;
    }

    /**
     * @param resource
     *            The resource to set.
     */
    public void setResource(final IResource resource) {
        this.resource = resource;
    }

    /**
     * @param resourceDelta
     *            The resourceDelta to set.
     */
    public void setResourceDelta(final IResourceDelta resourceDelta) {
        this.resourceDelta = resourceDelta;
    }

    /**
     * @param taskMarker The taskMarker to set.
     */
    public void setTaskMarker(final boolean taskMarker) {
        this.taskMarker = taskMarker;
    }
    /**
     * @see name.herlin.command.Command#reset()
     */
    public void reset() {
        this.setResource(null);
        this.markers = new HashMap();
        this.setTerminated(false);
    }

    /**
     * @see name.herlin.command.Command#isReadyToExecute()
     */
    public boolean isReadyToExecute() {
        return (this.resource != null) || (this.resourceDelta != null);
    }

    /**
     * Return a PMD Engine for that project. The engine is parameterized
     * according to the target JDK of that project.
     * 
     * @param project
     * @return
     */
    private PMD getPmdEngineForProject(final IProject project) throws CommandException {
        final IJavaProject javaProject = JavaCore.create(project);
        PMD pmdEngine = null;

        if (javaProject.exists()) {
            final String compilerCompliance = javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
            log.debug("compilerCompliance = " + compilerCompliance);
            if (JavaCore.VERSION_1_3.equals(compilerCompliance)) {
                pmdEngine = new PMD(new TargetJDK1_3());
            } else if (JavaCore.VERSION_1_4.equals(compilerCompliance)) {
                pmdEngine = new PMD(new TargetJDK1_4());
            } else if (JavaCore.VERSION_1_5.equals(compilerCompliance)) {
                pmdEngine = new PMD(new TargetJDK1_5());
            } else {
                throw new CommandException("The target JDK, " + compilerCompliance + " is not yet supported"); // TODO:
                                                                                                               // NLS
            }
        } else {
            throw new CommandException("The project " + project.getName() + " is not a Java project"); // TODO:
                                                                                                       // NLS
        }
        return pmdEngine;
    }

    /**
     * Review a resource
     */
    private void processResource() throws CommandException {
        try {
            final IProject project = this.resource.getProject();
            final ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(project);
            final RuleSet ruleSet = model.getProjectRuleSet();
            final PMD pmdEngine = this.getPmdEngineForProject(project);
            this.setStepsCount(this.countResourceElement(this.resource));
            log.debug("Visit of resource " + this.resource.getName() + " : " + this.getStepsCount());

            final ResourceVisitor visitor = new ResourceVisitor();
            visitor.setMonitor(this.getMonitor());
            visitor.setRuleSet(ruleSet);
            visitor.setPmdEngine(pmdEngine);
            visitor.setAccumulator(this.markers);
            visitor.setUseTaskMarker(this.taskMarker);
            this.resource.accept(visitor);
        } catch (ModelException e) {
            throw new CommandException(e);
        } catch (CoreException e) {
            throw new CommandException(e);
        }
    }

    /**
     * Review a resource delta
     */
    private void processResourceDelta() throws CommandException {
        try {
            final IProject project = this.resourceDelta.getResource().getProject();
            final ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(project);
            final RuleSet ruleSet = model.getProjectRuleSet();
            final PMD pmdEngine = this.getPmdEngineForProject(project);
            this.setStepsCount(this.countDeltaElement(this.resourceDelta));
            log.debug("Visit of resource delta : " + this.getStepsCount());

            final DeltaVisitor visitor = new DeltaVisitor();
            visitor.setMonitor(this.getMonitor());
            visitor.setRuleSet(ruleSet);
            visitor.setPmdEngine(pmdEngine);
            visitor.setAccumulator(this.markers);
            visitor.setUseTaskMarker(this.taskMarker);
            this.resourceDelta.accept(visitor);
        } catch (ModelException e) {
            throw new CommandException(e);
        } catch (CoreException e) {
            throw new CommandException(e);
        }
    }

    /**
     * Apply PMD markers after the review
     *
     */
    private void applyMarkers() {
        log.info("Processing marker directives");

        try {
            final Set filesSet = this.markers.keySet();
            final Iterator i = filesSet.iterator();
            while (i.hasNext()) {
                final IFile file = (IFile) i.next();

                final Set markerInfoSet = (Set) this.markers.get(file);
                file.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                final Iterator j = markerInfoSet.iterator();
                while (j.hasNext()) {
                    final MarkerInfo markerInfo = (MarkerInfo) j.next();
                    final IMarker marker = file.createMarker(markerInfo.getType());
                    marker.setAttributes(markerInfo.getAttributeNames(), markerInfo.getAttributeValues());
                }

            }
        } catch (CoreException e) {
            log.warn("CoreException when setting marker info for resource " + this.resource.getName() + " : " + e.getMessage()); // TODO:
                                                                                                                                 // NLS
        }

    }

    /**
     * Count the number of sub-resources of a resource
     * @param resource a project
     * @return the element count
     */
    private int countResourceElement(final IResource resource) {
        final CountVisitor visitor = new CountVisitor();

        try {
            resource.accept(visitor);
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError("Exception when counting elements of a project", e);
        }

        return visitor.count;
    }

    /**
     * Count the number of sub-resources of a delta
     * @param delta a resource delta
     * @return the element count
     */
    private int countDeltaElement(final IResourceDelta delta) {
        final CountVisitor visitor = new CountVisitor();

        try {
            delta.accept(visitor);
        } catch (CoreException e) {
            PMDPlugin.getDefault().logError("Exception counting elemnts in a delta selection", e);
        }

        return visitor.count;
    }

    /**
     * Private inner class to count a number of resources or delta elements
     */
    private final class CountVisitor implements IResourceVisitor, IResourceDeltaVisitor {
        public int count = 0;
        public boolean visit(final IResource resource) {
            boolean fVisitChildren = true;
            count++;

            if ((resource instanceof IFile)
                && (((IFile) resource).getFileExtension() != null)
                && ((IFile) resource).getFileExtension().equals("java")) {

                fVisitChildren = false;
            }

            return fVisitChildren;
        }
        
        // @PMD:REVIEWED:UnusedFormalParameter: by Herlin on 10/05/05 23:46
        public boolean visit(final IResourceDelta delta) {
            count++;
            return true;
        }
    };

}