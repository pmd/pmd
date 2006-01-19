/*  
 * <copyright>  
 *  Copyright 1997-2003 PMD for Eclipse Development team
 *  under sponsorship of the Defense Advanced Research Projects  
 *  Agency (DARPA).  
 *   
 *  This program is free software; you can redistribute it and/or modify  
 *  it under the terms of the Cougaar Open Source License as published by  
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).   
 *   
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS   
 *  PROVIDED "AS IS" WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR   
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF   
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT   
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT   
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL   
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,   
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR   
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.   
 *   
 * </copyright>
 */
package net.sourceforge.pmd.eclipse.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.dfa.DaaRule;
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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.ResourceWorkingSetFilter;

/**
 * Factor some usefull features for visitors
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.7  2006/01/19 22:00:14  phherlin
 * Fix BUG#1357798 Source file utf-8 charset problem
 *
 * Revision 1.6  2005/12/30 17:30:21  phherlin
 * Upgrade to PMD v3.4 -> RuleViolation interface has changed!
 *
 * Revision 1.5  2005/10/24 22:40:33  phherlin
 * Integrating Sebastian Raffel's work
 * Revision 1.4 2005/07/04 21:00:52 phherlin Oops!
 * forgot to use properties model to get project working set Revision 1.3
 * 2005/05/31 20:44:41 phherlin Continuing refactoring
 * 
 * Revision 1.2 2005/05/10 21:49:26 phherlin Fix new violations detected by PMD
 * 3.1
 * 
 * Revision 1.1 2005/05/07 13:32:04 phherlin Continuing refactoring Fix some PMD
 * violations Fix Bug 1144793 Fix Bug 1190624 (at least try)
 * 
 */
public class BaseVisitor {
    private static final Log log = LogFactory.getLog("net.sourceforce.pmd.cmd.BaseVisitor");
    private final ModelFactory modelFactory = ModelFactory.getFactory();
    private IProgressMonitor monitor;
    private boolean useTaskMarker = false;
    private Map accumulator;
    private PMD pmdEngine;
    private RuleSet ruleSet;
    protected RuleSet hiddenRules;

    /**
     * The constructor is protected to avoid illegal instanciation
     * 
     */
    protected BaseVisitor() {
        super();

        this.hiddenRules = new RuleSet();
        if (PMDPlugin.getDefault().useDFA()) {
            DaaRule daaRule = new DaaRule();
            daaRule.setUsesDFA();
            this.hiddenRules.addRule(daaRule);
        }
    }

    /**
     * Returns the useTaskMarker.
     * 
     * @return boolean
     */
    public boolean isUseTaskMarker() {
        return this.useTaskMarker;
    }

    /**
     * Sets the useTaskMarker.
     * 
     * @param useTaskMarker
     *            The useTaskMarker to set
     */
    public void setUseTaskMarker(final boolean useTaskMarker) {
        this.useTaskMarker = useTaskMarker;
    }

    /**
     * Returns the accumulator.
     * 
     * @return Map
     */
    public Map getAccumulator() {
        return this.accumulator;
    }

    /**
     * Sets the accumulator.
     * 
     * @param accumulator
     *            The accumulator to set
     */
    public void setAccumulator(final Map accumulator) {
        this.accumulator = accumulator;
    }

    /**
     * @return
     */
    public IProgressMonitor getMonitor() {
        return this.monitor;
    }

    /**
     * @param monitor
     */
    public void setMonitor(final IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Tell whether the user has required to cancel the operation
     * 
     * @return
     */
    public boolean isCanceled() {
        return getMonitor() == null ? false : getMonitor().isCanceled();
    }

    /**
     * Begin a subtask
     * 
     * @param name
     *            the task name
     */
    public void subTask(final String name) {
        if (getMonitor() != null) {
            getMonitor().subTask(name);
        }
    }

    /**
     * Inform of the work progress
     * 
     * @param work
     */
    public void worked(final int work) {
        if (getMonitor() != null) {
            getMonitor().worked(work);
        }
    }

    /**
     * @return Returns the pmdEngine.
     */
    public PMD getPmdEngine() {
        return this.pmdEngine;
    }

    /**
     * @param pmdEngine
     *            The pmdEngine to set.
     */
    public void setPmdEngine(final PMD pmdEngine) {
        this.pmdEngine = pmdEngine;
    }

    /**
     * @return Returns the ruleSet.
     */
    public RuleSet getRuleSet() {
        return this.ruleSet;
    }

    /**
     * @param ruleSet
     *            The ruleSet to set.
     */
    public void setRuleSet(final RuleSet ruleSet) {
        ruleSet.addRuleSet(hiddenRules);
        this.ruleSet = ruleSet;
    }

    /**
     * Run PMD against a resource
     * 
     * @param resource
     *            the resource to process
     */
    protected final void reviewResource(final IResource resource) {
        final IFile file = (IFile) resource.getAdapter(IFile.class);
        if ((file != null) && (file.getFileExtension() != null) && (file.getFileExtension().equals("java"))) {

            try {
                if (isFileInWorkingSet(file)) {
                    subTask(PMDPlugin.getDefault().getMessage(PMDConstants.MSGKEY_MONITOR_CHECKING_FILE) + " " + file.getName());

                    final RuleContext context = new RuleContext();
                    context.setSourceCodeFilename(file.getName());
                    context.setReport(new Report());

                    final Reader input = new InputStreamReader(file.getContents(), file.getCharset());
                    getPmdEngine().processFile(input, getRuleSet(), context);
                    input.close();

                    file.deleteMarkers(PMDPlugin.PMD_MARKER, true, IResource.DEPTH_INFINITE);
                    updateMarkers(file, context, isUseTaskMarker(), getAccumulator());

                    worked(1);
                } else {
                    log.debug("The file " + file.getName() + " is not in the working set");
                }

            } catch (CoreException e) {
                log.error("Core exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            } catch (PMDException e) {
                log.error("PMD exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            } catch (IOException e) {
                log.error("IO exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            } catch (ModelException e) {
                log.error("Model exception visiting " + file.getName(), e); // TODO:
                                                                            // complete
                                                                            // message
            }

        }
    }

    /**
     * Test if a file is in the PMD working set
     * 
     * @param file
     * @return true if the file should be checked
     */
    private boolean isFileInWorkingSet(final IFile file) throws ModelException {
        boolean fileInWorkingSet = true;
        final ProjectPropertiesModel model = this.modelFactory.getProperiesModelForProject(file.getProject());
        final IWorkingSet workingSet = model.getProjectWorkingSet();
        if (workingSet != null) {
            final ResourceWorkingSetFilter filter = new ResourceWorkingSetFilter();
            filter.setWorkingSet(workingSet);
            fileInWorkingSet = filter.select(null, null, file);
        }

        return fileInWorkingSet;
    }

    /**
     * Update markers list for the specified file
     * 
     * @param file
     *            the file for which markes are to be updated
     * @param context
     *            a PMD context
     * @param fTask
     *            indicate if a task marker should be created
     * @param accumulator
     *            a map that contains impacted file and marker informations
     */
    private void updateMarkers(final IFile file, final RuleContext context, final boolean fTask, final Map accumulator)
            throws CoreException {
        final Set markerSet = new HashSet();
        final List reviewsList = findReviewedViolations(file);
        final Review review = new Review();
        final Iterator iter = context.getReport().iterator();
        while (iter.hasNext()) {
            final RuleViolation violation = (RuleViolation) iter.next();
            review.ruleName = violation.getRule().getName();
            review.lineNumber = violation.getNode().getBeginLine();

            if (reviewsList.contains(review)) {
                log.debug("Ignoring violation of rule " + violation.getRule().getName() + " at line " + violation.getNode().getBeginLine()
                        + " because of a review.");
            } else {
                if (PMDPlugin.getDefault().useDFA() && violation.getRule().usesDFA()) {
                    markerSet.add(getMarkerInfo(violation, PMDPlugin.PMD_DFA_MARKER));
                } else {
                    markerSet.add(getMarkerInfo(violation, fTask ? PMDPlugin.PMD_TASKMARKER : PMDPlugin.PMD_MARKER));
                }
                log.debug("Adding a violation for rule " + violation.getRule().getName() + " at line " + violation.getNode().getBeginLine());
            }
        }

        if (accumulator != null) {
            log.debug("Adding markerSet to accumulator for file " + file.getName());
            accumulator.put(file, markerSet);
        }
    }

    /**
     * Search for reviewed violations in that file
     * 
     * @param file
     */
    private List findReviewedViolations(final IFile file) {
        final List reviewsList = new ArrayList();
        try {
            int lineNumber = 0;
            boolean findLine = false;
            boolean comment = false;
            final Stack pendingReviews = new Stack();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line != null) {
                    line = line.trim();
                    lineNumber++;
                    if (line.startsWith("/*")) {
                        comment = line.indexOf("*/") == -1;
                    } else if (comment && (line.indexOf("*/") != -1)) {
                        comment = false;
                    } else if (!comment && line.startsWith(PMDPlugin.REVIEW_MARKER)) {
                        final String tail = line.substring(17);
                        final String ruleName = tail.substring(0, tail.indexOf(':'));
                        pendingReviews.push(ruleName);
                        findLine = true;
                    } else if (!comment && findLine && !line.equals("") && !line.startsWith("//")) {
                        findLine = false;
                        while (!pendingReviews.empty()) {
                            // @PMD:REVIEWED:AvoidInstantiatingObjectsInLoops:
                            // by Herlin on 01/05/05 18:36
                            final Review review = new Review();
                            review.ruleName = (String) pendingReviews.pop();
                            review.lineNumber = lineNumber;
                            reviewsList.add(review);
                        }
                    }
                }
            }

            // if (log.isDebugEnabled()) {
            // for (int i = 0; i < reviewsList.size(); i++) {
            // final Review review = (Review) reviewsList.get(i);
            // log.debug("Review : rule " + review.ruleName + ", line " +
            // review.lineNumber);
            // }
            // }

        } catch (CoreException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_CORE_EXCEPTION, e);
        } catch (IOException e) {
            PMDPlugin.getDefault().logError(PMDConstants.MSGKEY_ERROR_IO_EXCEPTION, e);
        }

        return reviewsList;
    }

    /**
     * Create a marker info object from a violation
     * 
     * @param violation
     *            a PMD violation
     * @param type
     *            a marker type
     * @return markerInfo a markerInfo object
     */
    private MarkerInfo getMarkerInfo(final RuleViolation violation, final String type) {
        final MarkerInfo markerInfo = new MarkerInfo();

        markerInfo.setType(type);

        final List attributeNames = new ArrayList();
        final List values = new ArrayList();

        attributeNames.add(IMarker.MESSAGE);
        values.add(violation.getDescription());

        attributeNames.add(IMarker.LINE_NUMBER);
        values.add(new Integer(violation.getNode().getBeginLine()));

        attributeNames.add(PMDPlugin.KEY_MARKERATT_LINE2);
        values.add(new Integer(violation.getNode().getEndLine()));

        attributeNames.add(PMDPlugin.KEY_MARKERATT_VARIABLE);
        values.add(violation.getVariableName());

        attributeNames.add(PMDPlugin.KEY_MARKERATT_RULENAME);
        values.add(violation.getRule().getName());

        attributeNames.add(PMDPlugin.KEY_MARKERATT_PRIORITY);
        values.add(new Integer(violation.getRule().getPriority()));

        switch (violation.getRule().getPriority()) {
        case 1:
            attributeNames.add(IMarker.PRIORITY);
            values.add(new Integer(IMarker.PRIORITY_HIGH));
        case 2:
            attributeNames.add(IMarker.SEVERITY);
            values.add(new Integer(IMarker.SEVERITY_ERROR));
            break;

        case 5:
            attributeNames.add(IMarker.SEVERITY);
            values.add(new Integer(IMarker.SEVERITY_INFO));
            break;

        case 3:
            attributeNames.add(IMarker.PRIORITY);
            values.add(new Integer(IMarker.PRIORITY_HIGH));
        case 4:
        default:
            attributeNames.add(IMarker.SEVERITY);
            values.add(new Integer(IMarker.SEVERITY_WARNING));
            break;
        }

        markerInfo.setAttributeNames((String[]) attributeNames.toArray(new String[attributeNames.size()]));
        markerInfo.setAttributeValues(values.toArray());

        return markerInfo;
    }

    /**
     * Private inner type to handle reviews
     */
    private class Review {
        public String ruleName;
        public int lineNumber;

        public boolean equals(final Object obj) {
            boolean result = false;
            if (obj instanceof Review) {
                final Review reviewObj = (Review) obj;
                result = (this.ruleName.equals(reviewObj.ruleName)) && (this.lineNumber == reviewObj.lineNumber);
            }
            return result;
        }

        public int hashCode() {
            return ruleName.hashCode() + lineNumber * lineNumber;
        }

    }
}
