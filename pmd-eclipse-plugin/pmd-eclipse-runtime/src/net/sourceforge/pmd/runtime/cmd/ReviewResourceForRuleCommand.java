/*
 * Created on 05.11.2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPropertyListener;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.runtime.PMDRuntimeConstants;

/**
 * This command reviews a resource - a file - for a specific rule.
 * 
 * @author Sven
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/11/16 16:54:40  holobender
 * - changed command for the new cpd view
 * - possibility to set the number of maxviolations per file over the rule-properties
 *
 *
 */

public class ReviewResourceForRuleCommand extends AbstractDefaultCommand {
    private static final Logger log = Logger.getLogger(ReviewResourceForRuleCommand.class);
    private IResource resource;
    private RuleContext context;
    private Rule rule; 
    private List listenerList;
    
    public ReviewResourceForRuleCommand() {
        super();
        this.setDescription("Review a resource for a specific rule.");
        this.setName("ReviewResourceForRuleCommand");
        this.setOutputProperties(true);
        this.setReadOnly(true);
        this.setTerminated(false);
        this.listenerList = new ArrayList();
    }
    
    public void setResource(IResource resource) {
        this.resource = resource;
    }
    
    public void setRule(Rule rule) {
        this.rule = rule;
    }
    
    /**
     * Adds an object that wants to get an event after the command is finished.
     * @param listener the property listener to set.
     */
    public void addPropertyListener(IPropertyListener listener) {
        this.listenerList.add(listener);
    }
          
    public boolean isReadyToExecute() {
        return (resource != null) && (rule != null);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.runtime.cmd.AbstractDefaultCommand#reset()
     */
    public void reset() {
        setResource(null);
        setRule(null);
        this.listenerList = new ArrayList();
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
        final PMD pmdEngine = new PMD();

        if (javaProject.exists()) {
            final String compilerCompliance = javaProject.getOption(JavaCore.COMPILER_COMPLIANCE, true);
            log.debug("compilerCompliance = " + compilerCompliance);
            if (JavaCore.VERSION_1_3.equals(compilerCompliance)) {
                pmdEngine.setJavaVersion(SourceType.JAVA_13);
            } else if (JavaCore.VERSION_1_4.equals(compilerCompliance)) {
                pmdEngine.setJavaVersion(SourceType.JAVA_14);
            } else if (JavaCore.VERSION_1_5.equals(compilerCompliance)) {
                pmdEngine.setJavaVersion(SourceType.JAVA_15);
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
    
    /* (non-Javadoc)
     * @see net.sourceforge.pmd.runtime.cmd.AbstractDefaultCommand#execute()
     */
    public void execute() throws CommandException {
        final IProject project = resource.getProject();
        final IFile file = (IFile) resource.getAdapter(IFile.class);
        beginTask("PMD Checking for specific rule...", 1);
        
        if ((file != null) 
                && (file.getFileExtension() != null) 
                && (file.getFileExtension().equals("java"))) {
            final RuleSet ruleSet = new RuleSet();
            ruleSet.addRule(rule);
            final PMD pmdEngine = getPmdEngineForProject(project);

            try {
                this.context = new RuleContext();
                this.context.setSourceCodeFilename(file.getName());
                this.context.setReport(new Report());
                
                final Reader input = new InputStreamReader(file.getContents(), file.getCharset());
                pmdEngine.processFile(input, ruleSet, this.context);
                input.close();
            } catch (CoreException e) {
                throw new CommandException(e);
            } catch (PMDException e) {
                throw new CommandException(e);
            } catch (IOException e) {
                throw new CommandException(e);
            }
                
            // trigger event propertyChanged for all listeners
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    final Iterator listenerIterator = listenerList.iterator();
                    while (listenerIterator.hasNext()) {
                        final IPropertyListener listener = (IPropertyListener) listenerIterator.next();
                        listener.propertyChanged(context.getReport().iterator(), PMDRuntimeConstants.PROPERTY_REVIEW);
                    }
                }
            });
        }
    }   
}
